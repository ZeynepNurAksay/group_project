package group07.group.allocation.controller.convenor;

import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.User;
import group07.group.allocation.repos.ModuleRepo;
import group07.group.allocation.repos.UserRepo;
import group07.group.allocation.validator.AddModuleValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static group07.group.allocation.controller.MainController.*;
import static group07.group.allocation.controller.convenor.ConvenorController.getConvenorsModules;

/**
 * Allows the convenor to add / remove questionSets
 */
@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/convenor/")  //all convenor paths are controlled here
public class ConvenorModuleController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    ModuleRepo moduleRepo;

    @InitBinder("module")
    protected void studentAnswerBinder(WebDataBinder binder){
        binder.addValidators(new AddModuleValidator());
    }

    /**
     * Display the logged in convenors modules
     */
    @GetMapping("/modules")
    public String viewModules(Principal principal, Model model, HttpSession session) {
        getErrorMessageSession(model, session);
        User user = userRepo.findByEmail(principal.getName());
        model.addAttribute("name", user.getFullName());
        Convenor convenor = (Convenor) user;
        model.addAttribute("modules", moduleRepo.findModuleByConvenor(convenor));
        getSuccessMessageSession(model, session);
        return "convenor/view-modules";
    }

    /**
     * Display the students that belong to the convenors module
     *
     * @param id module ID
     */
    @GetMapping("/module/{id}")
    public String displayModule(@PathVariable String id, Model model, Principal principal, HttpServletRequest request, HttpSession session) {
        if (session.getAttribute("Error") != null) {
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", session.getAttribute("Error"));
            model.addAttribute("hidden", "show");
            session.removeAttribute("Error");
        }

        List<Module> modules = getConvenorsModules(principal.getName(), moduleRepo, userRepo);
        for (Module module : modules) {
            if (module.getId() == Integer.parseInt(id)) {
                model.addAttribute("module", module);
                return "convenor/view-module-students";
            }
        }
        request.getSession().setAttribute("Error", "You do not have permission to view students in this module or the module does not exist");
        return "redirect:/convenor/modules";

    }

    /**
     * Show the add module page
     */
    @GetMapping("/addModule")
    public String addModule(@ModelAttribute("module") Module module, Model model, Principal principal, HttpSession session) {
        model.addAttribute("convenor", principal.getName());
        model.addAttribute("module", new Module());
        getSuccessMessageSession(model, session);

        return "convenor/addModule";
    }

    /**
     * Add the module to the database and to the convenors list of modules
     */
    @PostMapping("/addModule")
    public String doAddModule(@Valid @ModelAttribute("module") Module module, BindingResult result, Principal principal, Model model, HttpServletRequest request) {
        if (result.hasErrors()){
            model.addAttribute("convenor", principal.getName());
            errorMessage(model, "Please fix the errors below and try again");
            return "convenor/addModule";
        }
        Module theModule = new Module();
        theModule.setCode(module.getCode());
        theModule.setName(module.getName());
        theModule.setDescription(module.getDescription());

        theModule.setConvenor((Convenor) userRepo.findByEmail(principal.getName()));

        moduleRepo.save(theModule);

        setSuccessMessageSession(model, request, "Module created successfully!");
        return "redirect:/convenor/modules/";
    }

    /**
     * Remove a specified convenors module
     *
     * @param id module id to remove
     */
    @GetMapping("/delete/module/{id}")
    public String removeModule(@PathVariable int id, Principal principal, HttpServletRequest request) {
        List<Module> modules = getConvenorsModules(principal.getName(), moduleRepo, userRepo);
        for (Module module : modules) {
            if (module.getId() == id) {
                moduleRepo.deleteById(id);
                return "redirect:/convenor/modules/";
            }
        }
        request.getSession().setAttribute("Error", "The module does not exist or you do not have permission to remove this module");
        return "redirect:/convenor/modules/";
    }

}
