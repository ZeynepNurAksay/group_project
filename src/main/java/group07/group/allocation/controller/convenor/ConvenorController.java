package group07.group.allocation.controller.convenor;

import group07.group.allocation.model.Module;
import group07.group.allocation.model.account.Convenor;
import group07.group.allocation.model.account.User;
import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.set.QuestionSet;
import group07.group.allocation.repos.ModuleRepo;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.QuestionSetRepo;
import group07.group.allocation.repos.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/convenor/")  //all convenor paths are controlled here
public class ConvenorController {

    /**
     * Display the convenors dashboard
     */
    @GetMapping(value = {"/dashboard", "/"})
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("USER_SESSION");
        model.addAttribute("name", user.getFullName());
        return "convenor/dashboard";
    }



    /**
     * Display the logged-in users profile
     */
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("USER_SESSION");
        model.addAttribute("name", user.getFullName());
        return "convenor/profile";
    }

    /**
     * Get the modules associated with a convenor
     *
     * @param email the convenors email
     * @return a list of modules that are associated with the convenors email
     */
    public static List<Module> getConvenorsModules(String email, ModuleRepo module_repo, UserRepo user_repo) {
        return module_repo.findModuleByConvenor((Convenor) user_repo.findByEmail(email));
    }

    /**
     * Get the questionSets associated with a convenor
     * @param email the convenors email
     * @return a list of question sets that belong to the associated email
     */
    public static List<QuestionSet> getConvenorsQuestionSets(String email, UserRepo userRepo, QuestionSetRepo questionSetRepo){
        return questionSetRepo.findQuestionSetByConvenor((Convenor) userRepo.findByEmail(email));
    }

    /**
     * Get the preferenceSets associated with a convenor
     */
    public static List<PreferenceSet> getConvenorsPreferenceSets(String email, UserRepo userRepo, PreferenceSetRepo preferenceSetRepo){
        return preferenceSetRepo.findPreferenceSetByModuleConvenor((Convenor) userRepo.findByEmail(email));
    }
}
