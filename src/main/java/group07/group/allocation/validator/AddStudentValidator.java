package group07.group.allocation.validator;

import group07.group.allocation.model.account.DTO.AddStudentDTO;
import group07.group.allocation.model.Module;
import group07.group.allocation.repos.ModuleRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

public class AddStudentValidator implements Validator {

    private final ModuleRepo moduleRepo;

    public AddStudentValidator(ModuleRepo moduleRepo) {
        this.moduleRepo = moduleRepo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return AddStudentDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddStudentDTO addStudentForm = (AddStudentDTO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "First name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "", "Last name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email is required");

        if(errors.hasErrors()){
            return;
        }

        if (EmailValidation.patternMatches(addStudentForm.getEmail())){
            errors.rejectValue("email", "", "Enter a valid email address");
        }

        try {
            if (addStudentForm.getCurrent_marks() != null && !Objects.equals(addStudentForm.getCurrent_marks(), "")){
                int currentMark = Integer.parseInt(addStudentForm.getCurrent_marks());
                if (!(currentMark >= 1 && currentMark <= 100)){ //check percent is between two values
                    errors.rejectValue("current_marks", "", "Please enter a percentage between 1 and 100");
                }
            }

        } catch (Exception e){
            errors.rejectValue("current_marks", "", "Please enter an integer");
        }

        try {
            Integer moduleID = Integer.parseInt(addStudentForm.getModule_id());
            Optional<Module> dbModule = moduleRepo.findById(moduleID);
            if (dbModule.isEmpty()){
                errors.rejectValue("selectedModule", "", "Your module does not exist, please create your module first using the button above");
            }
        } catch (Exception e){
            errors.rejectValue("selectedModule", "", "Error, processing selected module, please logout and try again");
        }
    }
}
