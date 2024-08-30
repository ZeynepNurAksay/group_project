package group07.group.allocation.controller;

import group07.group.allocation.repos.UserRepo;
import group07.group.allocation.service.EmailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
public class MainController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    EmailManager emailManager;

    /**
     * Add the error message to the view
     */
    public static void getErrorMessageSession(Model model, HttpSession session) {
        if (session.getAttribute("Error") != null) {
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", session.getAttribute("Error"));
            model.addAttribute("hidden", "show");
            session.removeAttribute("Error");
        }
    }

    /**
     * Add the success message to the view
     */
    public static void getSuccessMessageSession(Model model, HttpSession session){
        if (session.getAttribute("Success") != null) {
            model.addAttribute("bannerColor", "banner-color-green");
            model.addAttribute("message", session.getAttribute("Success"));
            model.addAttribute("hidden", "show");
            session.removeAttribute("Success");
        }
    }

    /**
     * Add the error message to the session for another get mapping to receive it
     */
    public static void setErrorMessageSession(Model model, HttpServletRequest request, String message){
        request.getSession().setAttribute("Error", message);
    }


    /**
     * Add the success message to the session for another mapping to receive it;
     */
    public static void setSuccessMessageSession(Model model, HttpServletRequest request, String message){
        request.getSession().setAttribute("Success", message);
    }

    /**
     * Add the error message to the view
     */
    public static void errorMessage(Model model, String message) {
        model.addAttribute("bannerColor", "banner-color-red");
        model.addAttribute("message", message);
        model.addAttribute("hidden", "show");
    }

    public static void successMessage(Model model, String message){
        model.addAttribute("bannerColor", "banner-color-green");
        model.addAttribute("message", message);
        model.addAttribute("hidden", "show");
    }

    @GetMapping("/")
    public String homePage(HttpServletRequest request, Model model) {
        if (request.isUserInRole("ROLE_STUDENT")){
            model.addAttribute("header", "student-header");
        } else if (request.isUserInRole("ROLE_CONVENOR")){
            model.addAttribute("header", "convenor-header");
        } else{
            model.addAttribute("header", "header");
        }
        return "home";
    }


}
