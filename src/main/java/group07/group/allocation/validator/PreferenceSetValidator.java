package group07.group.allocation.validator;

import group07.group.allocation.model.Module;
import group07.group.allocation.model.question.answers.DTO.PreferenceSetDTO;
import group07.group.allocation.model.question.set.QuestionSet;
import group07.group.allocation.repos.ModuleRepo;
import group07.group.allocation.repos.QuestionSetRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.Optional;

public class PreferenceSetValidator implements Validator {

    private ModuleRepo mRepo;
    private QuestionSetRepo qRepo;
    public PreferenceSetValidator(ModuleRepo mRepo, QuestionSetRepo qRepo) {
        this.mRepo = mRepo;
        this.qRepo = qRepo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PreferenceSetDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PreferenceSetDTO dto = (PreferenceSetDTO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "","Preference name must not be empty");

        try {
            Optional<Module> m = mRepo.findById(Integer.parseInt(dto.getModuleID())); // Search for module
            if (m.isEmpty()) { // Check if module isn't present
                errors.rejectValue("moduleID", "", "Invalid Module"); // Reject
            }
        }catch (Exception e) {
            errors.rejectValue("moduleID", "", "Invalid Number"); // Reject passed value is not an integer
        }

        try {
            Optional<QuestionSet> m = qRepo.findById(Integer.parseInt(dto.getQuestionSetID())); // Search for question set
            if (m.isEmpty()) { // Check if not present
                errors.rejectValue("questionSetID", "", "Invalid Question Set"); // Reject
            }
        }catch (Exception e) {
            errors.rejectValue("questionSetID", "", "Invalid Number"); // Reject passed value is not an integer
        }

//todo:
//        try {
//            if (dto.getDeadline() != null) {
//                try {
//                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//                    String theDeadline = dto.getDeadline().substring(0, 10);
//                    LocalDate dateTime = LocalDate.parse(theDeadline, formatter);
//                    if (!dateTime.isAfter(LocalDate.now())) {
//                        errors.rejectValue("deadline", "", "Date must be after the current date");
//                    }
//                } catch (Exception e) {
//                    errors.rejectValue("deadline", "", "Date is not valid");
//                }
//            }
//        }
//        catch(Exception e){
//            errors.rejectValue("deadline", "", "Date must be valid");
//        }



        try{
            Integer.parseInt(dto.getGroupMinNumber());
        } catch (NumberFormatException ex){
            errors.rejectValue("groupMinNumber", "", "Preference group minimum number must not be empty");
        }

        try{
            Integer.parseInt(dto.getGroupMaxNumber());
        } catch (NumberFormatException ex){
            errors.rejectValue("groupMaxNumber", "", "Preference group maximum number must not be empty");
        }

        if (errors.hasErrors()){
            return;
        }

        if (Integer.parseInt(dto.getGroupMinNumber()) > Integer.parseInt(dto.getGroupMaxNumber())) {
            errors.rejectValue("groupMinNumber", "", "Minimum number of group members should not exceed maximum.");
        }
        if (Integer.parseInt(dto.getGroupMinNumber()) < 2) {
            errors.rejectValue("groupMinNumber", "", "Minimum number of group members must be no less than 2.");
        }
//        if (Integer.parseInt(dto.getGroupMaxNumber()) < 2) {
//            errors.rejectValue("groupMaxNumber", "", "Maximum number of group members must be no less than 2.");
//        }
    }
}
