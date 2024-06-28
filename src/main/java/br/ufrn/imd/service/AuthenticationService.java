package br.ufrn.imd.service;

import br.ufrn.imd.dto.LoginUserDto;
import br.ufrn.imd.model.User;
import br.ufrn.imd.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    public User authenticate(LoginUserDto input) {
        System.out.println(input.getEmail());
        var email = input.getEmail();
        var password = input.getPassword();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email, password
                    )
            );

        } catch (AuthenticationException e) {
            //COMMENT System.out.println(input.getUsername());
            //COMMENT System.out.println(input.getPassword());
            System.out.println("Authentication error: " + e.getMessage());
            throw new RuntimeException("Invalid login credentials");
        }

        //COMMENT System.out.println(input.getPassword());
        //COMMENT System.out.println(input.getUsername());


        return userService.getUserByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public UserService getUserService() {
        return userService;
    }
}
