package group07.group.allocation.controller;

import com.github.mkopylec.recaptcha.validation.RecaptchaValidator;
import group07.group.allocation.model.account.*;
import group07.group.allocation.model.account.DTO.ChangePasswordDTO;
import group07.group.allocation.model.account.DTO.ForgotPasswordRequestDTO;
import group07.group.allocation.model.account.DTO.ResetPasswordDTO;
import group07.group.allocation.repos.ResetTokenRepo;
import group07.group.allocation.repos.UserRepo;
import group07.group.allocation.service.EmailManager;
import group07.group.allocation.validator.ChangePasswordValidator;
import group07.group.allocation.validator.ForgotPasswordValidator;
import group07.group.allocation.validator.SignupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static group07.group.allocation.Group07Application.convenorRole;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Controller
public class SecurityController {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ResetTokenRepo resetTokenRepo;
    @Autowired
    private EmailManager emailManager;
    @Autowired
    private RecaptchaValidator recaptchaValidator;

    @InitBinder("passwordDTO")
    protected void initUpdatePasswordBinder(WebDataBinder binder, Authentication authentication) {
        binder.addValidators(new ChangePasswordValidator(userRepo, authentication.getName()));
    }

    @InitBinder("passwordDto")
    protected void initForgotPasswordBinder(WebDataBinder binder) {
        binder.addValidators(new ForgotPasswordValidator(userRepo));
    }

    @InitBinder("userRegister")
    protected void initRegisterUserBinder(WebDataBinder binder){
        binder.addValidators(new SignupValidator(userRepo));
    }

    @GetMapping("/register")
    public String registrationForm(@ModelAttribute UserSignup userRegister, Model model) {
        model.addAttribute("userRegister", new UserSignup());
        return "security/signup";
    }

    @RequestMapping("/registerUser")
    public String registerUser (@Valid @ModelAttribute("userRegister") UserSignup userRegister, BindingResult result, Model model, HttpServletRequest request){
        if (result.hasErrors()){
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", "Please fix the errors below and try again:");
            model.addAttribute("hidden", "show");
            return "security/signup";
        } else if (recaptchaValidator.validate(request).isFailure()){
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", "Please complete the captcha:");
            model.addAttribute("hidden", "show");
            return "security/signup";
        } else {

            String encodedPassword = encodePassword(userRegister.getPassword());
            userRegister.setPassword(encodedPassword);
            Convenor newUser = new Convenor();
            newUser.setEmail(userRegister.getEmail());
            newUser.setFirstName(userRegister.getFirstName());
            newUser.setLastName(userRegister.getSecondName());
            newUser.setPassword(userRegister.getPassword());
            newUser.setUserRoles(List.of(convenorRole));

            userRepo.save(newUser);
            model.addAttribute("bannerColor", "banner-color-green");
            model.addAttribute("message", "Account created, please log in to continue");
            model.addAttribute("hidden", "show");
            return "security/login";
        }
    }

    @GetMapping("/login")
    public String loginForm (@RequestParam("redirect") Optional<Boolean> redirect,
                             @RequestParam("error") Optional<Boolean> error,
                             Model model){
        if (redirect.isPresent()){
            model.addAttribute("bannerColor", "banner-color-green");
            model.addAttribute("message", "Please log in to continue");
            model.addAttribute("hidden", "show");
        } else if (error.isPresent()){
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", "Invalid email/password. Please try again");
            model.addAttribute("hidden", "show");
        }
        return "security/login";
    }

    @GetMapping("/login-success")
    public String showDashboard(HttpServletRequest request, Principal principal) {
        User sessionUser = new User();
        User dbUser = userRepo.findByEmail(principal.getName());
        sessionUser.setFirstName(dbUser.getFirstName());
        sessionUser.setLastName(dbUser.getLastName());
        request.getSession().setAttribute("USER_SESSION", sessionUser);
        if (request.isUserInRole("ROLE_STUDENT")){
            return "redirect:/student/dashboard";
        } else if (request.isUserInRole("ROLE_CONVENOR")){
            return "redirect:/convenor/dashboard";
        } else{
            return "security/denied";
        }
    }

    /**
     * Check user authentication
     * @param principal an authenticated user
     * @return true if the user is logged in otherwise false
     */
    @GetMapping("/user/isLoggedIn")
    @ResponseBody // return a response rather than a view
    public Boolean checkAuth(Principal principal){
        return principal != null;
    }

    @GetMapping("/access-denied")
    public String deniedAccess(){
        return "security/denied";
    }

    @GetMapping("/logout-success")
    public String logoutSuccess(Model model){
        model.addAttribute("bannerColor", "banner-color-green");
        model.addAttribute("message", "Logout successful!");
        model.addAttribute("hidden", "show");

        return "security/login";
    }

    /**
     * Used to request a reset password link
     */
    @GetMapping("/forgot-password")
    public String forgotPasswordForm(@ModelAttribute ForgotPasswordRequestDTO passwordDto, Model model){
        model.addAttribute("password", new ForgotPasswordRequestDTO());
        return "security/forgotPassword";
    }

    /**
     * Send a reset link to the user if their email exists in the database
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("passwordDto") ForgotPasswordRequestDTO passwordDto, BindingResult result, Model model, HttpServletRequest request) throws MalformedURLException, MessagingException {
        if (recaptchaValidator.validate(request).isFailure()) {
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", "Please complete the captcha:");
            model.addAttribute("hidden", "show");
        } else {
            if (!result.hasErrors()) {
                User user = userRepo.findByEmail(passwordDto.getEmail());
                String token = UUID.randomUUID().toString();
                PasswordResetToken resetToken = new PasswordResetToken(user, token);
                resetTokenRepo.save(resetToken);

                String path = request.getContextPath() + "/password-reset?token=" + token;
                URL requestURL = new URL(request.getRequestURL().toString());
                String resetURL = "http://" + requestURL.getAuthority() + path;

                Context context = new Context();
                context.setVariable("token_URL", resetURL);
                context.setVariable("css_URL", "http://" + requestURL.getAuthority() + "/css/styles.css");
                context.setVariable("logo_URL", "http://" + requestURL.getAuthority() + "/images/user-group.svg");
                emailManager.sendTemplateEmail(passwordDto.getEmail(), "Reset Password - Group Allocator", "email/forgot-password.html", context);
                System.out.println("RESET URL: " + resetURL);

                model.addAttribute("bannerColor", "banner-color-green");
                model.addAttribute("message", "Please check your inbox. If you have an account with us you can use the link sent to your email");

            } else {
                model.addAttribute("bannerColor", "banner-color-red");
                model.addAttribute("message", "Email is not associated with an account!");
            }
            model.addAttribute("hidden", "show");
        }
        model.addAttribute("password", passwordDto);
        return "security/forgotPassword";
    }

    /**
     * Used to reset the user's password from a link sent to their email
     */
    @GetMapping("/password-reset")
    public String resetPasswordForm(Model model, @RequestParam("token") String token){
        boolean tokenCheck = this.validatePasswordResetToken(token);
        if (!tokenCheck){
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", "Invalid password reset link, please request another one");
            model.addAttribute("hidden", "show");
            model.addAttribute("password", new ForgotPasswordRequestDTO());
            return "security/forgotPassword";
        } else{
            model.addAttribute("resetPassword", new ResetPasswordDTO(token));
            return "security/resetPassword";
        }
    }

    @PostMapping("/password-reset")
    public String resetPassword(@ModelAttribute ResetPasswordDTO passwordDTO, Model model){
        boolean tokenCheck = this.validatePasswordResetToken(passwordDTO.getToken());
        if (tokenCheck){
            Optional<PasswordResetToken> token = Optional.ofNullable(resetTokenRepo.findByToken(passwordDTO.getToken()));
            if (token.isEmpty()) {
                model.addAttribute("bannerColor", "banner-color-red");
                model.addAttribute("message", "Invalid password reset link, please request another one");
                model.addAttribute("hidden", "show");
                model.addAttribute("password", new ForgotPasswordRequestDTO());
                return "security/forgotPassword";

            } else{
                User user = token.get().getUser();
                user.setPassword(encodePassword(passwordDTO.getPassword1()));
                userRepo.save(user);
                model.addAttribute("bannerColor", "banner-color-green");
                model.addAttribute("message", "Password reset success");
                model.addAttribute("hidden", "show");
                return "security/login";
            }
        }
        model.addAttribute("bannerColor", "banner-color-red");
        model.addAttribute("message", "Invalid password reset link, please request another one");
        model.addAttribute("hidden", "show");
        model.addAttribute("password", new ForgotPasswordRequestDTO());
        return "security/forgotPassword";
    }

    public String encodePassword(String plainPassword){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(plainPassword);
    }


    public boolean validatePasswordResetToken(String token){
        final PasswordResetToken passwordResetToken = resetTokenRepo.findByToken(token);
        return isTokenFound(passwordResetToken) && !(isTokenExpired(passwordResetToken));
    }

    private boolean isTokenFound(PasswordResetToken passwordResetToken){
        return passwordResetToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passwordResetToken){
        final Calendar calendar = Calendar.getInstance();
        return passwordResetToken.getExpiryDate().before(calendar.getTime());
    }

    public static Stream<Character> getRandomSpecialChars(int count) {
        Random random = new SecureRandom();
        IntStream specialChars = random.ints(count, 33, 45); //Only special character
        return specialChars.mapToObj(data -> (char) data);
    }

    public static Stream<Character> getRandomNumbers(int count){
        Random random = new SecureRandom();
        IntStream specialChars = random.ints(count, 48, 58); //Only Numbers
        return specialChars.mapToObj(data -> (char) data);
    }

    public static Stream<Character> getRandomAlphabets(int count){
        Random random = new SecureRandom();
        IntStream randomAlphabet = random.ints(count, 65, 91); //Only Letters
        return randomAlphabet.mapToObj(data -> (char) data);
    }


    public static String generateSecureRandomPassword() {
        Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2), //generate 2 random numbers
                        Stream.concat(getRandomSpecialChars(2), //generate 2 special characters
                        Stream.concat(getRandomAlphabets(2), //generate 2 random characters
                                getRandomAlphabets(4)))); //generate 4 random characters
        List<Character> charList = pwdStream.collect(Collectors.toList());
        Collections.shuffle(charList); //randomly shuffle all characters
        return charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }

    @RequestMapping("/test")
    public String showEmail (Model model){
        model.addAttribute("logo_URL", "http://localhost:8080/images/user-group.svg");
        model.addAttribute("css_URL", "http://localhost:8080/css/styles.css");
        model.addAttribute("student_name", "Cameron");
        model.addAttribute("student_password", "123456");
        model.addAttribute("convenor_name", "Jon");
        model.addAttribute("login_URL", "http://localhost:8080/login");

        return "email/register";
    }

    @GetMapping("/profile")
    public String showProfile(HttpServletRequest request) {
        if (request.isUserInRole("ROLE_CONVENOR")) {
            return "redirect:/convenor/profile";
        } else {
            return "redirect:/student/profile";
        }
    }

    @GetMapping("/changePassword")
    public String changePassword(@ModelAttribute ChangePasswordDTO passwordDTO, HttpServletRequest request, Model model) {
        model.addAttribute("passwordDTO", new ChangePasswordDTO());

        if (request.isUserInRole("ROLE_STUDENT")) {
            model.addAttribute("header", "student-header");
        } else if (request.isUserInRole("ROLE_CONVENOR")) {
            model.addAttribute("header", "convenor-header");
        } else {
            model.addAttribute("header", "header");
        }

        return "security/changePassword";
    }

    @PostMapping("/changePasswordAction")
    public String changePasswordAction(@Valid @ModelAttribute("passwordDTO") ChangePasswordDTO passwordDTO, BindingResult result, Model model, Principal principal, HttpServletRequest request) {

        if (request.isUserInRole("ROLE_STUDENT")) {
            model.addAttribute("header", "student-header");
        } else if (request.isUserInRole("ROLE_CONVENOR")) {
            model.addAttribute("header", "convenor-header");
        } else {
            model.addAttribute("header", "header");
        }

        if (!result.hasErrors()) {
            model.addAttribute("bannerColor", "banner-color-green");
            model.addAttribute("message", "Password changed successfully!");
            model.addAttribute("hidden", "show");
            User theUser = userRepo.findByEmail(principal.getName());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode(passwordDTO.getNewPassword());
            theUser.setPassword(encodedPassword);
            userRepo.save(theUser);
        }
        else{
            model.addAttribute("bannerColor", "banner-color-red");
            model.addAttribute("message", "Please try again!");
            model.addAttribute("hidden", "show");
        }

        model.addAttribute("passwordDTO", passwordDTO);
        return "security/changePassword";
    }

}
