package group07.group.allocation.controller.convenor;

import group07.group.allocation.Group07Application;
import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.DTO.AddStudentDTO;
import group07.group.allocation.model.account.DTO.ImportUsersDTO;
import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.account.User;
import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.StudentAnswer;
import group07.group.allocation.repos.ModuleRepo;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.UserRepo;
import group07.group.allocation.service.EmailManager;
import group07.group.allocation.validator.AddStudentValidator;
import group07.group.allocation.validator.EmailValidation;
import group07.group.allocation.validator.ImportStudentsValidator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static group07.group.allocation.Group07Application.studentRole;
import static group07.group.allocation.controller.SecurityController.generateSecureRandomPassword;
import static group07.group.allocation.controller.MainController.errorMessage;
import static group07.group.allocation.controller.convenor.ConvenorController.getConvenorsModules;

/**
 * Allows the convenor to add / remove students
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/convenor/")  //all convenor paths are controlled here
public class ConvenorStudentController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModuleRepo moduleRepo;

    @Autowired
    EmailManager emailManager;

    @Autowired
    Environment environment;

    @Autowired
    PreferenceSetRepo preferenceSetRepo;

    @InitBinder("addStudentDTO")
    protected void initAddStudentBinder(WebDataBinder binder) {
        binder.addValidators(new AddStudentValidator(moduleRepo));
    }

    @InitBinder("importUsersDTO")
    protected void initImportUsersBinder(WebDataBinder binder) {
        binder.addValidators(new ImportStudentsValidator(moduleRepo));
    }

    /**
     * Remove a student from the convenors module
     *
     * @param module_id  id of the module
     * @param student_id id of the student
     * @param principal  logged in user
     */
    @GetMapping("/removeStudent/{module_id}/{student_id}")
    public String removeStudent(@PathVariable int module_id, @PathVariable String student_id, Principal principal, HttpServletRequest request) {
        List<Module> modules = getConvenorsModules(principal.getName(), moduleRepo, userRepo);
        for (Module module : modules) {
            if (module.getId() == module_id) { //if the convenor has access to the module:
                List<Student> students = module.getStudents();
                students.removeIf(student -> String.valueOf(student.getID()).equals(student_id)); //remove the specified student from the module
                module.setStudents(students);
                moduleRepo.save(module);
                return "redirect:/convenor/module/" + module_id;
            }
        }
        request.getSession().setAttribute("Error", "You do not have permission to remove students in this module or the module/student does not exist");
        return "redirect:/convenor/module/" + module_id; // These students or module do not belong to this convenor
    }

    /**
     * Show the manage students screen
     */
    @GetMapping("/students")
    public String manageStudents() {
        return "convenor/manage-students";
    }

    /**
     * Display the page that allows convenors to import students
     */
    @GetMapping("/add-students/{module_id}")
    public String addStudents(@ModelAttribute AddStudentDTO addStudentDTO, @PathVariable(required = false) String module_id, Model model,
                              Authentication authentication) {
        Convenor convenor = (Convenor) userRepo.findByEmail(authentication.getName());
        List<Module> modules = moduleRepo.findModuleByConvenor(convenor);
        model.addAttribute("modules", modules);
        if (module_id != null){
            addStudentDTO.setModule_id(module_id);
        }
        model.addAttribute("addStudentDTO", addStudentDTO);
        return "convenor/add-students";
    }

    /**
     * Display the page that allows convenors to import students
     */
    @GetMapping("/add-students")
    public String addStudents(@ModelAttribute AddStudentDTO addStudentDTO, Model model, Authentication authentication) {
        Convenor convenor = (Convenor) userRepo.findByEmail(authentication.getName());
        model.addAttribute("addStudentDTO", new AddStudentDTO());
        List<Module> modules = moduleRepo.findModuleByConvenor(convenor);
        model.addAttribute("modules", modules);
        return "convenor/add-students";
    }


    @PostMapping("/add-students")
    public String processAddStudents(@Valid @ModelAttribute("addStudentDTO") AddStudentDTO addStudentDTO, BindingResult result, Model model, HttpServletRequest request, Authentication authentication) throws MalformedURLException, MessagingException {
        Convenor convenor = (Convenor) userRepo.findByEmail(authentication.getName());
        Optional<Module> dbModule = moduleRepo.findById(Integer.parseInt(addStudentDTO.getModule_id()));
        if (result.hasErrors() || dbModule.isEmpty()) {
            errorMessage(model, "Please check the errors below and try again");
            List<Module> modules = moduleRepo.findModuleByConvenor(convenor);
            model.addAttribute("modules", modules);
            Integer selectedModuleID = Integer.parseInt(addStudentDTO.getModule_id());
            model.addAttribute("lastSelected", selectedModuleID);
            return "convenor/add-students";
        }
        Module module = dbModule.get();
        Student existingStudent = (Student) userRepo.findByEmail(addStudentDTO.getEmail());
        if (existingStudent == null) {
            Student student = (Student) this.newUser(addStudentDTO.getEmail(), addStudentDTO.getFirstName(), addStudentDTO.getLastName(), request, authentication);
            module.addStudent(student); //add user to DB and send welcome email
            addToModulePreferenceSets(student, module);
        } else {
            module.addStudent(existingStudent); // student already exists
            addToModulePreferenceSets(existingStudent, module);
        }

        moduleRepo.save(module); //add the student to the module
        List<Module> modules = moduleRepo.findModuleByConvenor(convenor);
        model.addAttribute("bannerColor", "banner-color-green");
        model.addAttribute("message", "Success, student added to module. If they are new to the system they will receive a registration email");
        model.addAttribute("hidden", "show");
        model.addAttribute("addStudentDTO", new AddStudentDTO());
        model.addAttribute("modules", modules);
        return "convenor/add-students";
    }

    /**
     * Display the page that allows convenors to import students
     */
    @RequestMapping("/import-students")
    public String chooseFile(@ModelAttribute ImportUsersDTO importUsersDTO, Model model, Authentication authentication) {
        Convenor convenor = (Convenor) userRepo.findByEmail(authentication.getName());
        model.addAttribute("importUsersDTO", new ImportUsersDTO());
        List<Module> modules = moduleRepo.findModuleByConvenor(convenor);
        model.addAttribute("modules", modules);
        return "convenor/importStudents";
    }

    /**
     * Process the CSV file to import a list of students
     */
    @PostMapping("/import-students")
    public String uploadedSuccessfully(@Valid @ModelAttribute("importUsersDTO") ImportUsersDTO importUsersDTO, BindingResult result, Model model, HttpServletRequest request, Authentication authentication) throws IOException, MessagingException {
        Convenor convenor = (Convenor) userRepo.findByEmail(authentication.getName());
        Optional<Module> dbModule = moduleRepo.findById(Integer.parseInt(importUsersDTO.getSelectedModuleID()));
        if (result.hasErrors() || dbModule.isEmpty()) { // form had errors:
            this.rejectImport(model, "Please check the errors below and try again", importUsersDTO, convenor);
            return "convenor/importStudents";
        }

        MultipartFile csvFile = (MultipartFile) importUsersDTO.getCsvFile();
        Reader in = new BufferedReader(new InputStreamReader(csvFile.getInputStream()));
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

        Module module = dbModule.get();
        for (CSVRecord record : records) {
            if (EmailValidation.patternMatches(record.get("Email"))) {
                String message = "Error importing email: " + record.get("Email") + ". Invalid email, please try again";
                this.rejectImport(model, message, importUsersDTO, convenor);
                return "convenor/importStudents";
            }
            try {
                if (record.get("Current_marks") != null && !Objects.equals(record.get("Current_marks"), "")) {
                    int currentMark = Integer.parseInt(record.get("Current_marks"));
                    if (!(currentMark >= 1 && currentMark <= 100)) { //check percent is between two values
                        String message = "Error importing email: " + record.get("Email") + ". Could not process current mark, please enter a percentage between 1 and 100 and try again";
                        this.rejectImport(model, message, importUsersDTO, convenor);
                        return "convenor/importStudents";
                    }
                }

            } catch (IllegalArgumentException e) {
                //Carry on, this field can be left blank
            } catch (Exception e) {
                String message = "Error importing email: " + record.get("Email") + ". Could not process current mark, please enter an integer and try again";
                this.rejectImport(model, message, importUsersDTO, convenor);
                return "convenor/importStudents";
            }

            Student existingStudent = (Student) userRepo.findByEmail(record.get("Email"));
            if (existingStudent == null) {
                Student student = (Student) this.newUser(record.get("Email"), record.get("F.name"), record.get("L.name"), request, authentication);
                module.addStudent(student);
                addToModulePreferenceSets(student, module);
            } else {
                module.addStudent(existingStudent); //student already exists, add them to the list of students to add to the module
                addToModulePreferenceSets(existingStudent, module);
            }


        }
        moduleRepo.save(module); //add the students
        return "convenor/importStudentSuccess";
    }

    public void addToModulePreferenceSets(Student student, Module module){
        List<PreferenceSet> preferenceSets = preferenceSetRepo.findPreferenceSetByModule(module);
        for (PreferenceSet preferenceSet : preferenceSets){
            preferenceSet.getStudentAnswers().add(new StudentAnswer(student));
            preferenceSetRepo.save(preferenceSet);
        }
    }

    /**
     *Method to reject the imported spreadsheet and remember the choice of the selected module
     */
    public void rejectImport(Model model, String message, ImportUsersDTO importUsersDTO, Convenor convenor) {
        errorMessage(model, message);
        List<Module> modules = moduleRepo.findModuleByConvenor(convenor);
        model.addAttribute("modules", modules);
        Integer selectedModuleID = Integer.parseInt(importUsersDTO.getSelectedModuleID());
        model.addAttribute("lastSelected", selectedModuleID);
    }

    /**
     * Stores new user in the database and sends them a welcome email with their generated password
     */
    public User newUser(String email, String firstName, String lastName, HttpServletRequest request, Authentication authentication) throws MalformedURLException, MessagingException {
        URL requestURL = new URL(request.getRequestURL().toString());

        Context context = new Context(); //create the variables for the registration email:
        context.setVariable("convenor_name", authentication.getName());
        context.setVariable("login_URL", "http://" + requestURL.getAuthority() + "/login");
        context.setVariable("css_URL", "http://" + requestURL.getAuthority() + "/css/styles.css");
        context.setVariable("logo_URL", "http://" + requestURL.getAuthority() + "/images/user-group.svg");

        Student user = new Student();
        String password;
        if (Arrays.asList(environment.getActiveProfiles()).contains("Test")){ //If we are running the test, set the password to something we know
            password = "12345678";
        } else {
            password = generateSecureRandomPassword();
        }
        user.setPassword(Group07Application.passwordEncoder().encode(password));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.getUserRoles().add(studentRole);
        user = userRepo.save(user);
        context.setVariable("student_name", user.getFirstName());
        context.setVariable("student_email", user.getEmail());
        context.setVariable("student_password", password);
        emailManager.sendTemplateEmail(user.getEmail(), "You have been registered! - Group Allocator", "email/register.html", context);
        return user;
    }

}
