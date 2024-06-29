package br.ufrn.imd.controller;

import br.ufrn.imd.auth.AuthenticationResponse;
import br.ufrn.imd.model.Manager;
import br.ufrn.imd.model.Player;
import br.ufrn.imd.model.User;
import br.ufrn.imd.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ManagerService managerService;

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    private final JwtService jwtService;

    public UserController(UserService userService, ManagerService managerService, UserDetailsServiceImpl userDetailsService, UserDetailsServiceImpl userDetailsService1, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.managerService = managerService;
        this.userDetailsService = userDetailsService1;
        this.jwtService = jwtService;
    }

    @PostMapping("/register/player")
    public ResponseEntity<?> registerPlayer(@RequestBody Player player) {
        try {
            //COMMENT System.out.println("teste");
            player.setPassword(passwordEncoder.encode(player.getPassword()));
            Player savedPlayer = userService.savePlayer(player);

            final User userDetails = (User) userDetailsService.loadUserByUsername(savedPlayer.getEmail());

            final String jwt = jwtService.generateToken(userDetails);

            System.out.println(jwt);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwt);
            return ResponseEntity.ok(authenticationResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao registrar o jogador");
        }
    }

    @PostMapping("/register/manager")
    public ResponseEntity<?> registerManager(@RequestBody Manager manager) {
        try {
            //COMMENT System.out.println("teste");
            manager.setPassword(passwordEncoder.encode(manager.getPassword()));
            Manager savedManager = userService.saveManager(manager);

            final User userDetails = (User) userDetailsService.loadUserByUsername(savedManager.getEmail());

            final String jwt = jwtService.generateToken(userDetails);

            System.out.println(jwt);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(jwt);
            return ResponseEntity.ok(authenticationResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao registrar o jogador");
        }
    }




    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id, @RequestParam String userType) {
        return userService.getUserById(id, userType)
                          .map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/manager/{id}")
    public ResponseEntity<Manager> getManagerById(@PathVariable String id) {
        return managerService.getManagerById(id)
                          .map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }
}
