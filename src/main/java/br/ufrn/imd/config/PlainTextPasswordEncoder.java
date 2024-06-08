package br.ufrn.imd.config;

import org.springframework.security.crypto.password.PasswordEncoder;

public class PlainTextPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        System.out.println("Encoding password: " + rawPassword);
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        System.out.println("Checking password: " + rawPassword + " against stored password: " + encodedPassword);
        return rawPassword.toString().equals(encodedPassword);
    }
}
