package br.ufrn.imd.controller;

import br.ufrn.imd.auth.LoginResponse;
import br.ufrn.imd.dto.LoginUserDto;
import br.ufrn.imd.model.User;
import br.ufrn.imd.service.AuthenticationService;
import br.ufrn.imd.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {



    private final JwtService jwtService;

    private final  AuthenticationService authenticationService;

    public AuthController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        //COMMENT System.out.println("loginn");
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setExpiresIn(jwtService.getExpirationTime());



        return ResponseEntity.ok(loginResponse);
    }
}
