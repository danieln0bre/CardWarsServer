package br.ufrn.imd.service;

import br.ufrn.imd.dto.LoginUserDto;
import br.ufrn.imd.model.User;
import br.ufrn.imd.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
            System.out.println(input.getEmail());
            System.out.println(input.getPassword());
            System.out.println("Authentication error: " + e.getMessage());
            throw new RuntimeException("Invalid login credentials");
        }

        System.out.println(input.getPassword());
        System.out.println(input.getEmail());


        return userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



}
