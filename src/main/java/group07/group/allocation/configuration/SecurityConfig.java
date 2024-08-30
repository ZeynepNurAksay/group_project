package group07.group.allocation.configuration;

import group07.group.allocation.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login?redirect=true")
                .loginProcessingUrl("/process-login")
                .defaultSuccessUrl("/login-success", true)
                .failureUrl("/login?error=true")
                .permitAll()

                .and().logout()
                .invalidateHttpSession(true)
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/logout-success")
                .permitAll()

                .and().authorizeRequests()
                .antMatchers("/convenor/**").hasRole("CONVENOR")
                .antMatchers("/student/**").hasRole("STUDENT")
                .antMatchers("/",
                        "/register",
                        "/registerUser",
                        "/forgot-password",
                        "/password-reset*",
                        "/login",
                        "/login?error=true",
                        "/user/isLoggedIn/",
                        "/access-denied",
                        "/css/**",
                        "/images/**",
                        "/js/**").permitAll() //Allow access to non-authenticated users
                .anyRequest().authenticated() //Any other request must require authentication

                .and().exceptionHandling()
                .accessDeniedPage("/access-denied");
    }

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
