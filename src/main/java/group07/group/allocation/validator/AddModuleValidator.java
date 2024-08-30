package group07.group.allocation.validator;

import group07.group.allocation.model.Module;
import group07.group.allocation.repos.ModuleRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@SuppressWarnings("NullableProblems")
public class AddModuleValidator implements Validator {
    public AddModuleValidator() {

    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Module.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Module addModuleForm = (Module) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "code", "", "Module name is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "", "Module code is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "", "Module description is required");
    }
}
