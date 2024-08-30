package group07.group.allocation.validator;

import group07.group.allocation.model.account.Student;
import group07.group.allocation.model.question.answers.*;
import group07.group.allocation.model.question.answers.DTO.*;
import group07.group.allocation.repos.PreferenceSetRepo;
import group07.group.allocation.repos.UserRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.security.Principal;
import java.util.Locale;
import java.util.Objects;

import static group07.group.allocation.controller.student.StudentController.findStudentAnswerByPreferenceSetAndStudent;
import static group07.group.allocation.controller.student.StudentController.getStudentsPreferenceSetByIdBeforeDeadline;

@SuppressWarnings("NullableProblems")
public class StudentAnswerValidator implements Validator {

    public static PreferenceSetRepo preferenceSetRepo;
    public static UserRepo userRepo;
    public static Principal principal;

    public StudentAnswerValidator(UserRepo userRepo, PreferenceSetRepo preferenceSetRepo, Principal principal) {
        StudentAnswerValidator.userRepo = userRepo;
        StudentAnswerValidator.preferenceSetRepo = preferenceSetRepo;
        StudentAnswerValidator.principal = principal;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return StudentAnswerDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        StudentAnswerDTO studentAnswerDTO = (StudentAnswerDTO) target;
        PreferenceSet dbPreferenceSet = getStudentsPreferenceSetByIdBeforeDeadline(Integer.parseInt(studentAnswerDTO.getPreferenceSetId()), principal.getName(), userRepo, preferenceSetRepo, PreferenceSetStatus.AWAITING_DEADLINE);
        Student student = (Student) userRepo.findByEmail(principal.getName());
        if (dbPreferenceSet == null){
            errors.rejectValue("submit", "", "Invalid preference id, either the deadline has passed or the preference set does not belong to you.");
            return;
        }
        StudentAnswer studentAnswer = findStudentAnswerByPreferenceSetAndStudent(dbPreferenceSet, student);
        if (studentAnswer == null) {
            errors.rejectValue("submit", "", "Invalid preference id, either the deadline has passed or the preference set does not belong to you.");
            return;
        }

        //Validate student emails:
        int studentCount = 0;
        for (String otherStudentEmail: studentAnswerDTO.getPreferredGroupMembers()) {
            //if (Objects.equals(otherStudentEmail, "") || otherStudentEmail == null){
            if (studentAnswerDTO.getPreferredGroupMembers().stream().allMatch(item -> Objects.equals(item, ""))) { //if all the group member emails were ""
                break;
            }
            if (Objects.equals(otherStudentEmail, "") || otherStudentEmail == null){
                errors.rejectValue("preferredGroupMembers[" + studentCount + "]", "", "Please fill in this field");
            } else{
                Student otherStudent = (Student) userRepo.findByEmail(otherStudentEmail);
                if (otherStudent == null || !dbPreferenceSet.getModule().containsStudent(dbPreferenceSet.getModule().getStudents(), otherStudent)){
                    errors.rejectValue("preferredGroupMembers[" + studentCount + "]", "", "This student does not exist or is not part of this module");
                }
            }
            studentCount++;
        }

        //Validate True False:
        for (int questionIndex = 0; questionIndex < dbPreferenceSet.getQuestionSet().getTrueFalseQuestions().size(); questionIndex++){
            try{
                TrueFalseAnswerDTO trueFalseAnswer = studentAnswerDTO.getTrueFalseAnswers().get(questionIndex);
                if (!trueFalseAnswer.getAnswer().equalsIgnoreCase("true") && !trueFalseAnswer.getAnswer().equalsIgnoreCase("false")){
                    errors.rejectValue("trueFalseAnswers["+questionIndex+"]", "", "Please enter a true or false value");
                }
            } catch (IndexOutOfBoundsException | NullPointerException e){
                errors.rejectValue("trueFalseAnswers["+questionIndex+"]", "", "Please answer this question");
            }
        }

        //Validate Multiple Choice:
        for (int questionIndex = 0; questionIndex < dbPreferenceSet.getQuestionSet().getMultipleChoiceQuestions().size(); questionIndex++){
            try {
                MultipleChoiceAnswerDTO multipleChoiceAnswer = studentAnswerDTO.getMultipleChoiceAnswers().get(questionIndex);
                int numSelected = 0;
                for (MultipleChoiceOptionAnswerDTO option : multipleChoiceAnswer.getSelectedAnswers()){
                    if (option.getSelected() != null && !option.getSelected().equalsIgnoreCase("true")){
                        errors.rejectValue("multipleChoiceAnswers["+questionIndex+"]", "", "Please enter a true or false value");
                        break;
                    }
                    if (option.getSelected() != null){
                        if (option.getSelected().equalsIgnoreCase("true")) {
                            numSelected++;
                        }
                    }
                }
                if (numSelected < dbPreferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(questionIndex).getMinimumSelection()){
                    errors.rejectValue("multipleChoiceAnswers["+questionIndex+"]", "", "You have selected too few option(s)");
                }
                if (numSelected > dbPreferenceSet.getQuestionSet().getMultipleChoiceQuestions().get(questionIndex).getMaximumSelection()){
                    errors.rejectValue("multipleChoiceAnswers["+questionIndex+"]", "", "You have selected too many option(s)");
                }
            } catch (IndexOutOfBoundsException e){
                errors.rejectValue("multipleChoiceAnswers["+questionIndex+"]", "", "Please answer this question");
            }

        }

        //validate priorityList:
        for (int questionIndex = 0; questionIndex < dbPreferenceSet.getQuestionSet().getPriorityListQuestions().size(); questionIndex++){
            try {
                PriorityListAnswerDTO priorityListAnswer = studentAnswerDTO.getPriorityListAnswers().get(questionIndex);
                if (priorityListAnswer.getPriorityListAnswers().size() != dbPreferenceSet.getQuestionSet().getPriorityListQuestions().get(questionIndex).getOptions().size()) {
                    errors.rejectValue("priorityListAnswers[" + questionIndex + "]", "", "Invalid number of options, please reload the page");
                    continue;
                }
                for (PriorityListOptionAnswerDTO option : priorityListAnswer.getPriorityListAnswers()) {
                    if (option.getOrderInList() == null || Objects.equals(option.getOrderInList(), "")) {
                        errors.rejectValue("priorityListAnswers[" + questionIndex + "]", "", "Invalid order number, please reload the page");
                        continue;
                    }
                    int number = Integer.parseInt(option.getOrderInList());
                }
            } catch (IndexOutOfBoundsException e){
                errors.rejectValue("priorityListAnswers[" + questionIndex + "]", "", "Did not send answers to the server, please reload and try again");
            } catch (NumberFormatException e){
                errors.rejectValue("priorityListAnswers[" + questionIndex + "]", "", "Could not convert order number to an integer, please reload and try again");
            }
        }

    }
}
