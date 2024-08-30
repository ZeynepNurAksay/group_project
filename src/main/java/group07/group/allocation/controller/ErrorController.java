package group07.group.allocation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController{

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model){
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()){
                //Return 404 Error
                model.addAttribute("errorTitle", HttpStatus.NOT_FOUND.value() + " " + HttpStatus.NOT_FOUND.getReasonPhrase() + " - Group Allocation System");
                model.addAttribute("errorHeading", "Error: " + HttpStatus.NOT_FOUND.value() + "<br>Sorry, we couldn't find the page you were looking for :(");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
                //500 error
                model.addAttribute("errorTitle", HttpStatus.INTERNAL_SERVER_ERROR.value() + " " + HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase() + " - Group Allocation System");
                model.addAttribute("errorHeading", "Error: " + HttpStatus.INTERNAL_SERVER_ERROR.value() + "<br>Sorry, we have experienced an internal server error :(");
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                //401 error
                model.addAttribute("errorTitle", HttpStatus.UNAUTHORIZED.value() + " " + HttpStatus.UNAUTHORIZED.getReasonPhrase() + " - Group Allocation System");
                model.addAttribute("errorHeading", "Error: " + HttpStatus.UNAUTHORIZED.value() + "<br>Sorry, you need to be logged in to do that :(");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()){
                //403 error
                model.addAttribute("errorTitle", HttpStatus.FORBIDDEN.value() + " " + HttpStatus.FORBIDDEN.getReasonPhrase() + " - Group Allocation System");
                model.addAttribute("errorHeading", "Error: " + HttpStatus.FORBIDDEN.value() + "<br>Sorry, you don't have permission to do that :(");
            }
            else{
                model.addAttribute("errorTitle", "Unknown Error - Group Allocation System");
                model.addAttribute("errorHeading", "Oops! Something went wrong.");
            }
        } else {
            model.addAttribute("errorTitle", "Unknown Error - Group Allocation System");
            model.addAttribute("errorHeading", "Oops! Something went wrong.");
        }
        return "error";
    }
}
