package group07.group.allocation.service;

import group07.group.allocation.model.account.Role;
import group07.group.allocation.model.account.User;
import group07.group.allocation.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (Objects.equals(username, "")){
            throw new UsernameNotFoundException("Invalid Username/Password");
        }
        User user = userRepo.findByEmail(username);
        if (user == null){
            throw new UsernameNotFoundException("Invalid Username/Password");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : user.getUserRoles()){
            authorities.add(new SimpleGrantedAuthority("ROLE_"+role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), true, true, true, true, authorities);
    }
}
