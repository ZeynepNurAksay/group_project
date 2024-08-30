package group07.group.allocation.validator;

import group07.group.allocation.model.account.UserSignup;
import group07.group.allocation.repos.UserRepo;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Objects;

public class SignupValidator implements Validator {

    private final UserRepo userRepo;

    public SignupValidator(UserRepo repo){
        this.userRepo = repo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserSignup.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "", "First Name Required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "secondName", "", "Second Name Required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "", "Email is required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "", "Password Required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rePassword", "", "Confirm Password Required");

        if (errors.hasErrors()){
            return;
        }
        UserSignup user = (UserSignup) target;

        if (EmailValidation.patternMatches(user.getEmail())){
            errors.rejectValue("email", "", "Please enter a valid email address");
        }

        if (userRepo.findByEmail(user.getEmail())!=null){
            errors.rejectValue("email", "", "Email is already associated with an account!");
        }
        if (!Objects.equals(user.password, user.rePassword)){
            errors.rejectValue("rePassword", "", "Passwords do not match!");
        }
        if (user.password.length()<8){
            errors.rejectValue("password", "", "Passwords must be at least 8 characters!");
        }
    }

}
