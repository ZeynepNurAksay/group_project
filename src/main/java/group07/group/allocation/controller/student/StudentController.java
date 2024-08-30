package group07.group.allocation.controller.student;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.account.User;
import group07.group.allocation.model.question.answers.PreferenceSet;
import group07.group.allocation.model.question.answers.PreferenceSetStatus;
import group07.group.allocation.model.question.answers.StudentAnswer;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/student/") //all student paths are controlled here
public class StudentController {
    //TODO: view allocated groups
    //TODO: choose groups

    @GetMapping(value = {"/dashboard", "/"})
    public String showDashboard(HttpSession session, Model model){
        User user = (User) session.getAttribute("USER_SESSION");
        model.addAttribute("name", user.getFullName());
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model){
        User user = (User) session.getAttribute("USER_SESSION");
        model.addAttribute("name", user.getFullName());
        return "student/profile";
    }

    /**
     * Get the preferenceSets associated with a student
     */
    public static List<PreferenceSet> getStudentsPreferenceSets(String email, UserRepo userRepo, PreferenceSetRepo preferenceSetRepo, PreferenceSetStatus status){
        return preferenceSetRepo.findPreferenceSetByModuleStudentsContainingAndStatus((Student) userRepo.findByEmail(email), status);

    }

    /**
     * Get a preferenceSet by ID that is associated with a student and has not exceeded the deadline
     */
    public static PreferenceSet getStudentsPreferenceSetByIdBeforeDeadline(int preferenceSetId, String email, UserRepo userRepo, PreferenceSetRepo preferenceSetRepo, PreferenceSetStatus status){
        return preferenceSetRepo.findPreferenceSetByIdAndModuleStudentsContainingAndStatus(preferenceSetId, (Student) userRepo.findByEmail(email), status);
               //preferenceSetRepo.findPreferenceSetByIdAndModuleStudentsContainingAndStatus(33, (Student) userRepo.findByEmail(principal.getName()), PreferenceSetStatus.AWAITING_DEADLINE);
    }

    /**
     * Get the StudentAnswer from a preference set that is associated with a student
     */
    public static StudentAnswer findStudentAnswerByPreferenceSetAndStudent(PreferenceSet preferenceSet, Student student){
        return preferenceSet.getStudentAnswers().stream().filter(studentAnswer -> student.getID() == studentAnswer.getStudent().getID()).findFirst().orElse(null);
    }
}
