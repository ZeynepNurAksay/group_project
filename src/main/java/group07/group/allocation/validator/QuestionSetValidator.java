package group07.group.allocation.validator;

import group07.group.allocation.model.question.set.DTO.MultipleChoiceDTO;
import group07.group.allocation.model.question.set.DTO.PriorityListDTO;
import group07.group.allocation.model.question.set.DTO.QuestionSetDTO;
import group07.group.allocation.model.question.set.DTO.TrueFalseDTO;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.regex.Pattern;

public class QuestionSetValidator implements Validator {

    private final Pattern isStringIntegerRegex = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Override
    public boolean supports(Class<?> clazz) {
        return QuestionSetDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        QuestionSetDTO questionSetDTO = (QuestionSetDTO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "", "Question set name is required");

        if (questionSetDTO.getStudentChooseOwnGroup() == null) {
            errors.rejectValue("studentChooseOwnGroup", "", "Please select if students can choose their own groups");
        }

        //validation for true/false questions
        for (int i = 0; i < questionSetDTO.getTrueFalse().size(); i++) {
            TrueFalseDTO currentTrueFalse = questionSetDTO.getTrueFalse().get(i);
            if (Objects.equals(currentTrueFalse.getQuestionText(), "") || currentTrueFalse.getQuestionText() == null) {
                errors.rejectValue("trueFalse[" + i + "].questionText", "", "Please create a True/False question to ask your students");
            }
            if (Objects.equals(currentTrueFalse.getOption1(), "") || currentTrueFalse.getOption1() == null) {
                errors.rejectValue("trueFalse[" + i + "].option1", "", "True/false question must have an option to choose from");
            }
            if (Objects.equals(currentTrueFalse.getOption2(), "") || currentTrueFalse.getOption2() == null) {
                errors.rejectValue("trueFalse[" + i + "].option2", "", "True/false question must have a second option to choose from");
            }
            if (currentTrueFalse.getGroupAnswersBySimilar() == null) {
                errors.rejectValue("trueFalse[" + i + "].groupAnswersBy", "", "Please select how to group students");
            }
            if (isNotInteger(currentTrueFalse.getQuestionWeight())) {
                errors.rejectValue("trueFalse[" + i + "].questionWeight", "", "Please enter a number for how important this question is, 1 = not very, 10 = very " +
                        "important");

            } else {

                if (checkWeight(currentTrueFalse.getQuestionWeight())) {
                    errors.rejectValue("trueFalse[" + i + "].questionWeight", "", "Please select how important this question is, 1 = not very, 10 = very important");
                }
            }
        }

        //validation for priority list questions
        for (int i = 0; i < questionSetDTO.getPriorityList().size(); i++) {
            PriorityListDTO currentPriorityList = questionSetDTO.getPriorityList().get(i);
            if (Objects.equals(currentPriorityList.getQuestionText(), "") || currentPriorityList.getQuestionText() == null){
                errors.rejectValue("priorityList["+i+"].questionText", "", "Priority list question text is required");
            }

            for (int j = 0; j < currentPriorityList.getOptions().size(); j++){
                if (Objects.equals(currentPriorityList.getOptions().get(j).getChoiceName(), "") || currentPriorityList.getOptions().get(j).getChoiceName() == null){
                    errors.rejectValue("priorityList["+i+"].options["+j+"]", "", "Priority list option text is required. Please enter text or remove the option");
                }
            }

            if (currentPriorityList.getGroupAnswersBySimilar() == null) {
                errors.rejectValue("priorityList[" + i + "].groupAnswersBy", "", "Please select how to group students");
            }

            if (isNotInteger(currentPriorityList.getQuestionWeight())) {
                errors.rejectValue("priorityList[" + i + "].questionWeight", "", "Please enter a number for how important this question is, 1 = not very, 10 = very important");
            }
            else if (checkWeight(currentPriorityList.getQuestionWeight())) {
                    errors.rejectValue("priorityList[" + i + "].questionWeight", "", "Please select how important this question is, 1 = not very, 10 = very important");
            }
        }


        //validation for multiple choice questions
        for (int i = 0; i < questionSetDTO.getMultipleChoice().size(); i++) {
            MultipleChoiceDTO currentMultipleChoice = questionSetDTO.getMultipleChoice().get(i);
            if (Objects.equals(currentMultipleChoice.getQuestionText(), "") || currentMultipleChoice.getQuestionText() == null){
                errors.rejectValue("multipleChoice["+i+"].questionText", "", "Multiple choice question text is required");
            }

            for (int j = 0; j < currentMultipleChoice.getOptions().size(); j++){
                if (Objects.equals(currentMultipleChoice.getOptions().get(j).getChoiceName(), "") || currentMultipleChoice.getOptions().get(j).getChoiceName() == null){
                    errors.rejectValue("multipleChoice["+i+"].options["+j+"]", "", "Multiple choice option text is required. Please enter text or remove the option");
                }
            }

            if (currentMultipleChoice.getGroupAnswersBySimilar() == null) {
                errors.rejectValue("multipleChoice[" + i + "].groupAnswersBy", "", "Please select how to group students");
            }

            if(!isNotInteger(currentMultipleChoice.getMinimumSelection()) && !isNotInteger((currentMultipleChoice.getMaximumSelection()))) {
                if (Integer.parseInt(currentMultipleChoice.getMinimumSelection()) > Integer.parseInt(currentMultipleChoice.getMaximumSelection())) {
                    errors.rejectValue("multipleChoice[" + i + "].minimumSelection", "", "Multiple choice minimum selection should be less than the maximum selection");
                }

                if (Integer.parseInt(currentMultipleChoice.getMaximumSelection()) > currentMultipleChoice.getOptions().size()) {
                    errors.rejectValue("multipleChoice[" + i + "].maximumSelection", "", "Multiple choice maximum selection should not be more than the number of options");
                }

                if (Integer.parseInt(currentMultipleChoice.getMaximumSelection()) < 1){
                    errors.rejectValue("multipleChoice["+i+"].maximumSelection", "", "Multiple choice maximum selection should be at least 1");
                }

                if (Integer.parseInt(currentMultipleChoice.getMinimumSelection()) < 0){
                    errors.rejectValue("multipleChoice["+i+"].minimumSelection", "", "Multiple choice minimum selection should not be less than 0");
                }
            }
            else {
                if(isNotInteger(currentMultipleChoice.getMinimumSelection())){
                    errors.rejectValue("multipleChoice[" + i + "].minimumSelection", "", "Multiple choice minimum selection must be a number");
                }
                if(isNotInteger(currentMultipleChoice.getMaximumSelection())){
                    errors.rejectValue("multipleChoice[" + i + "].maximumSelection", "", "Multiple choice maximum selection must be a number");
                }
            }

            if (isNotInteger(currentMultipleChoice.getQuestionWeight())) {
                errors.rejectValue("multipleChoice[" + i + "].questionWeight", "", "Please enter a number for how important this question is, 1 = not very, 10 = very important");
            }
            else if (checkWeight(currentMultipleChoice.getQuestionWeight())) {
                    errors.rejectValue("multipleChoice[" + i + "].questionWeight", "", "Please select how important this question is, 1 = not very, 10 = very important");
            }
        }
    }

    public boolean checkWeight(String string_number) {
        int number = Integer.parseInt(string_number);
        return number < 1 || number > 10;
    }

    public boolean isNotInteger(String string_number) {
        if (string_number == null) {
            return true;
        }
        return !isStringIntegerRegex.matcher(string_number).matches();
    }


}
