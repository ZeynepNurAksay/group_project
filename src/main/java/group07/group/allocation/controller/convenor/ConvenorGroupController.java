package group07.group.allocation.controller.convenor;

import group07.group.allocation.controller.MainController;
import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.group.Group;
import group07.group.allocation.model.question.answers.DTO.PreferenceSetDTO;
import group07.group.allocation.model.question.answers.PreferenceSet;

import group07.group.allocation.model.question.answers.PreferenceSetStatus;
import group07.group.allocation.model.question.answers.StudentAnswer;
import group07.group.allocation.model.question.set.MultipleChoice;
import group07.group.allocation.model.question.set.PriorityList;
import group07.group.allocation.model.question.set.QuestionSet;
import group07.group.allocation.model.question.set.TrueFalse;
import group07.group.allocation.repos.*;
import group07.group.allocation.tasks.CloseStudentSubmissionsOnDeadline;
import group07.group.allocation.service.EmailManager;
import group07.group.allocation.validator.PreferenceSetValidator;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static group07.group.allocation.controller.MainController.*;
import static group07.group.allocation.controller.convenor.ConvenorController.*;

/**
 * Allows the convenor to allocate questions to students and view outstanding group preference tasks
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/convenor/")  //all convenor paths are controlled here
public class ConvenorGroupController {

    @Autowired
    ModuleRepo moduleRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    GroupRepository groupRepo;

    @Autowired
    QuestionSetRepo questionSetRepo;

    @Autowired
    PreferenceSetRepo preferenceSetRepo;

    @Autowired
    StudentAnswerRepo studentAnswerRepo;

    @Autowired
    EmailManager emailManager;

    @Autowired
    ThreadPoolTaskScheduler taskScheduler;


    @InitBinder("preferenceSetDTO")
    protected void initPreferenceSetBinder(WebDataBinder binder) {
        binder.addValidators(new PreferenceSetValidator(moduleRepo, questionSetRepo));
    }

    /**
     * Displays the mange groups dashboard
     */
    @GetMapping("/manage-groups")
    public String mangeGroups() {
        return "convenor/manage-groups";
    }

    /**
     * Displays the add preference set page
     */
    @GetMapping("/add-preference-set")
    public String addPreferenceSet(@ModelAttribute("preferenceSetDTO") PreferenceSetDTO preferenceSetDTO, Model model, Principal principal) {
        model.addAttribute("preferenceSetDTO", new PreferenceSetDTO());
        model.addAttribute("modules", getConvenorsModules(principal.getName(), moduleRepo, userRepo));
        model.addAttribute("questionSets", getConvenorsQuestionSets(principal.getName(), userRepo, questionSetRepo));
        return "convenor/add-preference-set";
    }

    /**
     * Processes the add preference set form emails students to notify them that they can submit preferences.
     */

    @PostMapping("/add-preference-set")
    public String doAddPreferenceSet(@Valid @ModelAttribute("preferenceSetDTO") PreferenceSetDTO preferenceSetDTO, BindingResult result, Model model, Principal principal, HttpServletRequest request, Authentication authentication) throws MalformedURLException, MessagingException {
        PreferenceSet thePreference = new PreferenceSet();

        Optional<Module> module = moduleRepo.findById(Integer.parseInt(preferenceSetDTO.getModuleID()));
        Optional<QuestionSet> questionSet = questionSetRepo.findById(Integer.parseInt(preferenceSetDTO.getQuestionSetID()));

        if (module.isPresent() && !module.get().getConvenor().getEmail().equals(principal.getName())) {
            result.rejectValue("moduleID", "", "You dont own that module");
        }
        if (questionSet.isPresent() && !questionSet.get().getConvenor().getEmail().equals(principal.getName())) {
            result.rejectValue("questionSetID", "", "You dont own that questionSet");
        }

        if (result.hasErrors()) {
            model.addAttribute("modules", getConvenorsModules(principal.getName(), moduleRepo, userRepo));
            model.addAttribute("questionSets", getConvenorsQuestionSets(principal.getName(), userRepo, questionSetRepo));
            return "convenor/add-preference-set";
        }


        if (module.isPresent() && questionSet.isPresent()) {
            thePreference.setName(preferenceSetDTO.getName());
            thePreference.setDeadline(LocalDateTime.parse(preferenceSetDTO.getDeadline()));
            thePreference.setModule(module.get());
            thePreference.setQuestionSet(questionSet.get());

            thePreference.setGroupMaxNumber(Integer.parseInt(preferenceSetDTO.getGroupMaxNumber()));
            thePreference.setGroupMinNumber(Integer.parseInt(preferenceSetDTO.getGroupMinNumber()));
            thePreference.setStatus(PreferenceSetStatus.AWAITING_DEADLINE);

            URL requestURL = new URL(request.getRequestURL().toString());

            Context context = new Context(); //create the variables for the registration email:
            context.setVariable("convenor_name", authentication.getName());
            context.setVariable("css_URL", "http://" + requestURL.getAuthority() + "/css/styles.css");
            context.setVariable("logo_URL", "http://" + requestURL.getAuthority() + "/images/user-group.svg");

            for (Student student : thePreference.getModule().getStudents()) {
                context.setVariable("studentName", student.getFirstName());
                context.setVariable("preferenceSetName", thePreference.getName());
                context.setVariable("moduleName", thePreference.getModule().getName());
                context.setVariable("moduleCode", thePreference.getModule().getCode());
                context.setVariable("deadline", thePreference.getDeadline());
                emailManager.sendTemplateEmail(student.getEmail(), "There is a new preference set you need to fill in! - Group Allocator", "email/preferenceSet.html", context);
                thePreference.getStudentAnswers().add(new StudentAnswer(student));
            }
            thePreference = preferenceSetRepo.save(thePreference);
            taskScheduler.schedule(new CloseStudentSubmissionsOnDeadline(thePreference, preferenceSetRepo, studentAnswerRepo), java.sql.Timestamp.valueOf(thePreference.getDeadline()));
            MainController.setSuccessMessageSession(model, request, "Success, you have created a preference set. Students will now be emailed to submit their preferences");
            return "redirect:/convenor/preference-sets";
        } else {
            MainController.errorMessage(model, "Unknown error occurred. Please try again");
            return "convenor/add-preference-set";
        }

    }

    /**
     * Delete the convenor's preference set
     */
    @GetMapping("/delete/preference-set/{id}")
    public String delete(Principal principal, @PathVariable String id) {
        List<PreferenceSet> set = getConvenorsPreferenceSets(principal.getName(), userRepo, preferenceSetRepo); // Collect a list of preference sets owned by convenor
        Optional<PreferenceSet> item = set.stream().filter(preferenceSet -> preferenceSet.getId() == Integer.parseInt(id)).findFirst(); // Filter to find the matching set
        item.ifPresent(preferenceSet -> preferenceSetRepo.delete(preferenceSet)); // Delete the matching set if the provided id is correct
        return "redirect:/convenor/preference-sets";
    }

    /**
     * Returns the view of preferences sets belonging to the convenor with their status'
     */
    @GetMapping("/preference-sets")
    public String preferenceSets(Model model, Principal principal, HttpSession session) {
        model.addAttribute("preferenceSets", getConvenorsPreferenceSets(principal.getName(), userRepo, preferenceSetRepo));
        getErrorMessageSession(model, session);
        return "convenor/view-preference-sets";
    }

    /**
     * View the allocated groups of a preference set
     * Only the convenor who owns the preference set can view this
     * Convenor can only view the allocated groups once they have been allocated
     */
    @GetMapping("preference-sets/{id}")
    public String viewGroups(Principal principal, @PathVariable String id, Model model, HttpServletRequest request, HttpSession session) {

        Optional<PreferenceSet> dbPreferenceSet = preferenceSetRepo.findById(Integer.parseInt(id));

        if (dbPreferenceSet.isPresent() && dbPreferenceSet.get().getModule().getConvenor().getEmail().equals(principal.getName())) {
            PreferenceSet preferenceSet = dbPreferenceSet.get();
            if (preferenceSet.getStatus() == PreferenceSetStatus.AWAITING_DEADLINE || preferenceSet.getStatus() == PreferenceSetStatus.ALLOCATING_GROUPS) {
                setErrorMessageSession(model, request, "Groups have not been allocated yet, please check back later");
                return "redirect:/convenor/preference-sets";
            }
            List<Group> groups = preferenceSet.getAllocatedGroups();
            model.addAttribute("groups", groups);
            Iterable<StudentAnswer> answers = preferenceSet.getStudentAnswers();
            model.addAttribute("answers", answers);
            model.addAttribute("preferenceSetId", id);
            model.addAttribute("preferenceSet", preferenceSet);
            getErrorMessageSession(model, session);
            return "convenor/viewGroups";
        }

        setErrorMessageSession(model, request, "Invalid preference set ID or the preference set does not belong to you.");
        return "redirect:/convenor/preference-sets";

    }

    @GetMapping("student-preferences/{email}/{pid}")
    public String viewStudentPreferences(Principal principal, @PathVariable String email, @PathVariable String pid, Model model, HttpServletRequest request) {
        Student student = (Student) userRepo.findByEmail(email);
        Optional<PreferenceSet> dbPreferenceSet = preferenceSetRepo.findById(Integer.parseInt(pid));
        if (dbPreferenceSet.isEmpty() || !dbPreferenceSet.get().getModule().getConvenor().getEmail().equals(principal.getName())) {
            setErrorMessageSession(model, request, "Invalid preference set ID or the preference set does not belong to you.");
            return "redirect:/convenor/preference-sets/" + pid;
        }
        PreferenceSet preferenceSet = dbPreferenceSet.get();

        QuestionSet questionSet = preferenceSet.getQuestionSet();
        List<TrueFalse> trueFalseQuestions = questionSet.getTrueFalseQuestions();
        List<PriorityList> priorityListQuestions = questionSet.getPriorityListQuestions();
        List<MultipleChoice> multipleChoiceQuestions = questionSet.getMultipleChoiceQuestions();
        StudentAnswer studentAnswer = preferenceSet.getStudentAnswers().stream().filter(s -> Objects.equals(s.getStudent().getEmail(), email)).findFirst().orElse(null);
        if (studentAnswer == null) {
            setErrorMessageSession(model, request, "Invalid student email address, either they don't exist or do not belong to this preference set");
            return "redirect:/convenor/preference-sets/" + pid;
        }

        model.addAttribute("student", student);
        model.addAttribute("trueFalseQuestions", trueFalseQuestions);
        model.addAttribute("priorityListQuestions", priorityListQuestions);
        model.addAttribute("multipleChoiceQuestions", multipleChoiceQuestions);
        model.addAttribute("studentTrueFalseAnswers", studentAnswer.getTrueFalseAnswers());
        model.addAttribute("studentPriorityListAnswers", studentAnswer.getPriorityListAnswers());
        model.addAttribute("studentMultipleChoiceAnswers", studentAnswer.getMultipleChoiceAnswers());


        return "convenor/viewStudentPreferences";
    }

    /**
     * View the allocated group's answers from an allocated group
     * Only the convenor who owns the preference set can view this
     * The convenor can only view this page once the groups have been allocated
     */
    @GetMapping("preference-sets/{preferenceSetId}/{groupId}")
    public String viewAllocatedGroupPreferences(Principal principal, @PathVariable int preferenceSetId, @PathVariable int groupId, Model model, HttpServletRequest request) {
        Optional<PreferenceSet> dbPreferenceSet = preferenceSetRepo.findById(preferenceSetId);
        if (dbPreferenceSet.isEmpty() || !dbPreferenceSet.get().getModule().getConvenor().getEmail().equals(principal.getName())) {
            setErrorMessageSession(model, request, "Invalid preference set ID or the preference set does not belong to you");
            return "redirect:/convenor/preference-sets/" + preferenceSetId;
        }
        PreferenceSet preferenceSet = dbPreferenceSet.get();
        Group allocatedGroup = preferenceSet.getAllocatedGroups().stream().filter(s -> s.getId() == groupId).findFirst().orElse(null);

        if (allocatedGroup == null) {
            setErrorMessageSession(model, request, "Invalid group ID.");
            return "redirect:/convenor/preference-sets/" + preferenceSetId;
        }

        if ((preferenceSet.getStatus() == PreferenceSetStatus.AWAITING_APPROVAL || preferenceSet.getStatus() == PreferenceSetStatus.COMPLETE) && preferenceSet.getAllocatedGroups().contains(allocatedGroup)) {
            model.addAttribute("preferenceSet", preferenceSet);
            model.addAttribute("allocatedGroup", allocatedGroup);
            return "convenor/view-group-preferences";
        }

        setErrorMessageSession(model, request, "Groups have not been allocated yet, please try again later.");
        return "redirect:/convenor/preference-sets/" + preferenceSetId;

    }

    /**
     * Get the convenor's preference set and set the status to confirmed
     * Email the students their groups and set the status of the preference set to be completed
     * This will allow the students to view their groups on the website too
     */
    @GetMapping("confirm/preference-sets/{preferenceSetId}")
    public String conformGroupAllocation(Principal principal, @PathVariable int preferenceSetId, Model model, HttpServletRequest request, Authentication authentication) throws MalformedURLException, MessagingException {
        Optional<PreferenceSet> dbPreferenceSet = preferenceSetRepo.findById(preferenceSetId);
        if (dbPreferenceSet.isEmpty() || !dbPreferenceSet.get().getModule().getConvenor().getEmail().equals(principal.getName())) {
            setErrorMessageSession(model, request, "Invalid preference set ID or the preference set does not belong to you");
            return "redirect:/convenor/preference-sets/" + preferenceSetId;
        }

        PreferenceSet preferenceSet = dbPreferenceSet.get();
        preferenceSet.setStatus(PreferenceSetStatus.COMPLETE);
        URL requestURL = new URL(request.getRequestURL().toString());


        int counter = 1;
        for (Group group : preferenceSet.getAllocatedGroups()){

            for (StudentAnswer studentAnswer : group.getStudentAnswers()){
                //Todo: add convenors actual name and student actual name
                Context context = new Context(); //create the variables for the registration email:
                context.setVariable("convenor_name", authentication.getName());
                context.setVariable("css_URL", "http://" + requestURL.getAuthority() + "/css/styles.css");
                context.setVariable("logo_URL", "http://" + requestURL.getAuthority() + "/images/user-group.svg");
                context.setVariable("moduleName", preferenceSet.getModule().getName());
                context.setVariable("moduleCode", preferenceSet.getModule().getCode());
                context.setVariable("preferenceSetName", preferenceSet.getName());
                context.setVariable("groupNumber", counter);
                context.setVariable("groupMembers", group.getStudentAnswers());
                context.setVariable("studentName", studentAnswer.getStudent().getEmail());

                emailManager.sendTemplateEmail(studentAnswer.getStudent().getEmail(), "Your new group has been allocated! - Group Allocator", "email/allocated-group.html", context);
            }
            counter ++;
        }
        setSuccessMessageSession(model, request, "Success! Students will now receive their groups via email, this may take some time.");
        preferenceSetRepo.save(preferenceSet);
        return "redirect:/convenor/preference-sets/";

    }

}
