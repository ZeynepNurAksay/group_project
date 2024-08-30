package group07.group.allocation.controller.student;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.group.Group;
import group07.group.allocation.model.question.answers.*;
import group07.group.allocation.model.question.answers.DTO.*;
import group07.group.allocation.model.question.set.ChoiceOption;
import group07.group.allocation.model.question.set.MultipleChoice;
import group07.group.allocation.model.question.set.PriorityList;
import group07.group.allocation.model.question.set.TrueFalse;
import group07.group.allocation.repos.GroupRepository;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.StudentAnswerRepo;
import group07.group.allocation.repos.UserRepo;
import group07.group.allocation.validator.StudentAnswerValidator;
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
import java.util.ArrayList;
import java.util.List;

import static group07.group.allocation.controller.MainController.*;
import static group07.group.allocation.controller.student.StudentController.*;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
@RequestMapping("/student/") //all student paths are controlled here
public class StudentPreferencesController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PreferenceSetRepo preferenceSetRepo;

    @Autowired
    StudentAnswerRepo studentAnswerRepo;

    @Autowired
    GroupRepository groupRepo;

    @InitBinder("studentAnswerDTO")
    protected void studentAnswerBinder(WebDataBinder binder, Principal principal) {
        binder.addValidators(new StudentAnswerValidator(userRepo, preferenceSetRepo, principal));
    }

    @GetMapping("/group-preferences")
    public String groupPreferences(Model model, Principal principal, HttpSession session) {
        //Only show students preferences are still accepting responses
        List<PreferenceSet> preferenceSets = getStudentsPreferenceSets(principal.getName(), userRepo, preferenceSetRepo, PreferenceSetStatus.AWAITING_DEADLINE);
        //List<PreferenceSetDTO> preferenceSetDTOS;
        int i = 0;
        for (PreferenceSet preferenceSet : preferenceSets) {
            StudentAnswer studentAnswer = findStudentAnswerByPreferenceSetAndStudent(preferenceSet, (Student) userRepo.findByEmail(principal.getName()));
            preferenceSet.setCompleted(studentAnswer.isCompleted());
            preferenceSets.set(i, preferenceSet);
            i++;
        }
        model.addAttribute("studentsPreferenceSets", preferenceSets);
        getSuccessMessageSession(model, session);
        getErrorMessageSession(model, session);
        return "student/group-preferences";
    }

    @GetMapping("/answer-questions/{preferenceSetID}")
    public String answerQuestions(Model model, @PathVariable int preferenceSetID, Principal principal, HttpServletRequest request) {
        PreferenceSet dbPreferenceSet = getStudentsPreferenceSetByIdBeforeDeadline(preferenceSetID, principal.getName(), userRepo, preferenceSetRepo, PreferenceSetStatus.AWAITING_DEADLINE);
        if (dbPreferenceSet == null) {
            setErrorMessageSession(model, request, "Invalid preference id, either the deadline has passed or the preference set does not belong to you.");
            return "redirect:/student/group-preferences";
        }
        StudentAnswer studentAnswer = findStudentAnswerByPreferenceSetAndStudent(dbPreferenceSet, (Student) userRepo.findByEmail(principal.getName()));
        StudentAnswerDTO studentAnswerDTO = new StudentAnswerDTO();

        for (Student student : studentAnswer.getPreferredGroupMembers()) {
            studentAnswerDTO.getPreferredGroupMembers().add(student.getEmail());
        }

        studentAnswerDTO = getTrueFalseQuestions(studentAnswerDTO, dbPreferenceSet, studentAnswer);
        studentAnswerDTO = getMultipleChoiceQuestions(studentAnswerDTO, dbPreferenceSet, studentAnswer);
        studentAnswerDTO = getPriorityListQuestions(studentAnswerDTO, dbPreferenceSet, studentAnswer);

        model.addAttribute("preferenceSet", dbPreferenceSet);
        model.addAttribute("studentAnswerDTO", studentAnswerDTO);
        model.addAttribute("studentsEmail", principal.getName());
        return "student/answer-preference-set";

    }

    @PostMapping("/answer-questions/{preferenceSetID}")
    public String submitPreferenceSetAnswers(@Valid @ModelAttribute StudentAnswerDTO studentAnswerDTO, BindingResult result, Model model, @PathVariable int preferenceSetID, Principal principal, HttpServletRequest request) {
        PreferenceSet dbPreferenceSet = getStudentsPreferenceSetByIdBeforeDeadline(preferenceSetID, principal.getName(), userRepo, preferenceSetRepo, PreferenceSetStatus.AWAITING_DEADLINE);
        if (dbPreferenceSet == null) {
            setErrorMessageSession(model, request, "Invalid preference id, either the deadline has passed or you don't have access to this preference set.");
            return "redirect:/student/group-preferences";
        }
        if (result.hasErrors()) {
            model.addAttribute("preferenceSet", dbPreferenceSet);
            model.addAttribute("studentsEmail", principal.getName());
            errorMessage(model, "Please fix the errors below and try again");
            return "student/answer-preference-set";
        }

        Student student = (Student) userRepo.findByEmail(principal.getName());
        StudentAnswer studentAnswer = findStudentAnswerByPreferenceSetAndStudent(dbPreferenceSet, student);

        //Clear the existing values to replace with the latest form results.
        studentAnswer.setMultipleChoiceAnswers(new ArrayList<>());
        studentAnswer.setPriorityListAnswers(new ArrayList<>());
        studentAnswer.setPreferredGroupMembers(new ArrayList<>());
        studentAnswer.setTrueFalseAnswers(new ArrayList<>());

        for (String studentDTO : studentAnswerDTO.getPreferredGroupMembers()) {
            Student otherStudent = (Student) userRepo.findByEmail(studentDTO);
            studentAnswer.getPreferredGroupMembers().add(otherStudent);
        }

        for (TrueFalseAnswerDTO trueFalseAnswerDTO : studentAnswerDTO.getTrueFalseAnswers()) {
            TrueFalseAnswer trueFalseAnswer = new TrueFalseAnswer();
            trueFalseAnswer.setAnswer(Boolean.parseBoolean(trueFalseAnswerDTO.getAnswer()));
            studentAnswer.getTrueFalseAnswers().add(trueFalseAnswer);
        }

        int questionNumber = 0;
        for (MultipleChoice multipleChoiceQuestion : dbPreferenceSet.getQuestionSet().getMultipleChoiceQuestions()) {
            MultipleChoiceAnswer multipleChoiceAnswer = new MultipleChoiceAnswer();
            int questionAnswerOption = 0;
            for (ChoiceOption ignored : multipleChoiceQuestion.getOptions()) {
                MultipleChoiceOptionAnswer answer = new MultipleChoiceOptionAnswer();
                //Only selected options are submitted so need to check how many were submitted
                if (questionAnswerOption >= 0 && questionAnswerOption < studentAnswerDTO.getMultipleChoiceAnswers().get(questionNumber).getSelectedAnswers().size()) {
                    answer.setSelected(Boolean.parseBoolean(studentAnswerDTO.getMultipleChoiceAnswers().get(questionNumber).getSelectedAnswers().get(questionAnswerOption).getSelected()));
                } else {
                    answer.setSelected(false); //set the answer to false if it has not been sent to the server (i.e. the user didn't select the checkbox)
                }
                answer.setChoiceName(dbPreferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(questionNumber).getOptions().get(questionAnswerOption).getChoiceName());
                multipleChoiceAnswer.getMultipleChoiceAnswers().add(answer);
                questionAnswerOption++;
            }
            questionNumber++;
            studentAnswer.getMultipleChoiceAnswers().add(multipleChoiceAnswer);
        }

        questionNumber = 0;
        for (PriorityList priorityListQuestion : dbPreferenceSet.getQuestionSet().getPriorityListQuestions()) {
            PriorityListAnswer priorityListAnswer = new PriorityListAnswer();
            int questionAnswerOption = 0;
            for (ChoiceOption ignored : priorityListQuestion.getOptions()) {
                PriorityListOptionAnswer answer = new PriorityListOptionAnswer();
                if (questionAnswerOption >= 0 && questionAnswerOption < studentAnswerDTO.getPriorityListAnswers().get(questionNumber).getPriorityListAnswers().size()) {
                    answer.setOrderInList(Integer.parseInt(studentAnswerDTO.getPriorityListAnswers().get(questionNumber).getPriorityListAnswers().get(questionAnswerOption).getOrderInList()));
                } else {
                    answer.setOrderInList(questionAnswerOption);
                }
                answer.setChoiceName(dbPreferenceSet.getQuestionSet().getPriorityListQuestions().get(questionNumber).getOptions().get(questionAnswerOption).getChoiceName());
                priorityListAnswer.getPriorityListAnswers().add(answer);
                questionAnswerOption++;
            }
            questionNumber++;
            studentAnswer.getPriorityListAnswers().add(priorityListAnswer);
        }

        studentAnswer.setCompleted(true);
        dbPreferenceSet.getStudentAnswers().set(dbPreferenceSet.getStudentAnswers().indexOf(studentAnswer), studentAnswer);
        preferenceSetRepo.save(dbPreferenceSet); //save or update the student answer
        setSuccessMessageSession(model, request, "You have submitted your preferences. You can edit your answers up until the deadline");
        return "redirect:/student/group-preferences";
    }

    public StudentAnswerDTO getTrueFalseQuestions(StudentAnswerDTO studentAnswerDTO, PreferenceSet dbPreferenceSet, StudentAnswer studentAnswer) {
        int i = 0;
        for (TrueFalse ignored : dbPreferenceSet.getQuestionSet().getTrueFalseQuestions()) {
            TrueFalseAnswerDTO trueFalseAnswerDTO = new TrueFalseAnswerDTO();
            if (i >= 0 && i < studentAnswer.getTrueFalseAnswers().size()) {
                trueFalseAnswerDTO.setAnswer(String.valueOf(studentAnswer.getTrueFalseAnswers().get(i).isAnswer()));
            }

            trueFalseAnswerDTO.setOption1(dbPreferenceSet.getQuestionSet().getTrueFalseQuestions().get(i).getOption1());
            trueFalseAnswerDTO.setOption2(dbPreferenceSet.getQuestionSet().getTrueFalseQuestions().get(i).getOption2());
            trueFalseAnswerDTO.setQuestionText(dbPreferenceSet.getQuestionSet().getTrueFalseQuestions().get(i).getQuestionText());
            trueFalseAnswerDTO.setQuestionDescription(dbPreferenceSet.getQuestionSet().getTrueFalseQuestions().get(i).getQuestionDescription());
            studentAnswerDTO.getTrueFalseAnswers().add(trueFalseAnswerDTO);
            i++;
        }
        return studentAnswerDTO;
    }

    public StudentAnswerDTO getMultipleChoiceQuestions(StudentAnswerDTO studentAnswerDTO, PreferenceSet dbPreferenceSet, StudentAnswer studentAnswer) {
        int i = 0;
        for (MultipleChoice multipleChoiceQuestions : dbPreferenceSet.getQuestionSet().getMultipleChoiceQuestions()) {
            MultipleChoiceAnswerDTO multipleChoiceAnswer = new MultipleChoiceAnswerDTO();
            int j = 0;
            for (ChoiceOption multipleChoiceOption : multipleChoiceQuestions.getOptions()) {
                MultipleChoiceOptionAnswerDTO answer = new MultipleChoiceOptionAnswerDTO();
                if (i >= 0 && i < studentAnswer.getMultipleChoiceAnswers().size()) {
                    answer.setSelected(String.valueOf(studentAnswer.getMultipleChoiceAnswers().get(i).getMultipleChoiceAnswers().get(j).isSelected()));
                } else {
                    answer.setSelected("false");
                }
                j++;
                answer.setChoiceName(multipleChoiceOption.getChoiceName());
                multipleChoiceAnswer.getSelectedAnswers().add(answer);
            }
            i++;
            studentAnswerDTO.getMultipleChoiceAnswers().add(multipleChoiceAnswer);
        }
        return studentAnswerDTO;
    }

    public StudentAnswerDTO getPriorityListQuestions(StudentAnswerDTO studentAnswerDTO, PreferenceSet dbPreferenceSet, StudentAnswer studentAnswer) {
        int i = 0;
        for (PriorityList priorityListQuestion : dbPreferenceSet.getQuestionSet().getPriorityListQuestions()) {
            PriorityListAnswerDTO priorityListAnswerDTO = new PriorityListAnswerDTO();
            int j = 0;
            for (ChoiceOption ignored : priorityListQuestion.getOptions()) {
                PriorityListOptionAnswerDTO answer = new PriorityListOptionAnswerDTO();
                if (i >= 0 && i < studentAnswer.getPriorityListAnswers().size()) {
                    answer.setOrderInList(String.valueOf(studentAnswer.getPriorityListAnswers().get(i).getPriorityListAnswers().get(j).getOrderInList()));
                } else {
                    answer.setOrderInList(String.valueOf(j));
                }
                j++;
                priorityListAnswerDTO.getPriorityListAnswers().add(answer);
            }
            i++;
            studentAnswerDTO.getPriorityListAnswers().add(priorityListAnswerDTO);
        }
        return studentAnswerDTO;
    }

    @GetMapping("preference-sets")
    public String viewPreferenceSets(Principal principal, Model model) {
        Student student = (Student) userRepo.findByEmail(principal.getName());
        List<Group> groups = groupRepo.findGroupByStudentAnswersStudentAndPreferenceSetStatus(student, PreferenceSetStatus.COMPLETE);
        model.addAttribute("groups", groups);
        return "student/groups";
    }

}
