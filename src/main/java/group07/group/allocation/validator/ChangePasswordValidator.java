package group07.group.allocation.validator;

import group07.group.allocation.model.account.DTO.ChangePasswordDTO;
import group07.group.allocation.repos.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

public class ChangePasswordValidator implements Validator {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserRepo userRepo;
    private final String email;

    public ChangePasswordValidator(UserRepo repo, String email) {
        this.userRepo = repo;
        this.email = email;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return ChangePasswordDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ChangePasswordDTO user = (ChangePasswordDTO) target;

        if (!passwordEncoder.matches(user.getPassword(), userRepo.findByEmail(email).getPassword())) {
            errors.rejectValue("password", "", "Incorrect password! Please try again.");
        }
        if (!(Objects.equals(user.getNewPassword(), user.getReNewPassword()))){
            errors.rejectValue("reNewPassword", "", "Passwords do not match!");
        }

    }

}
