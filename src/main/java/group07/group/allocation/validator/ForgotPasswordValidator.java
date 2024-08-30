package group07.group.allocation.validator;

import group07.group.allocation.model.account.DTO.ForgotPasswordRequestDTO;
import group07.group.allocation.repos.UserRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


public class ForgotPasswordValidator implements Validator {

    private final UserRepo userRepo;

    public ForgotPasswordValidator(UserRepo repo) {
        this.userRepo = repo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ForgotPasswordRequestDTO.class.equals(clazz);
    }

    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email is required");

        if (errors.hasErrors()){
            return;
        }

        ForgotPasswordRequestDTO user = (ForgotPasswordRequestDTO) target;

        if (userRepo.findByEmail(user.getEmail())==null){
            errors.rejectValue("email", "", "Email is not associated with an account!");
        }
    }

}
