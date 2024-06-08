package br.ufrn.imd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import br.ufrn.imd.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, UserService userService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        setFilterProcessesUrl("/api/users/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> credentials = objectMapper.readValue(request.getInputStream(), Map.class);

            String username = credentials.get("username");
            String password = credentials.get("password");

            System.out.println("Attempting authentication for user: " + username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException {
        System.out.println("Authentication successful for user: " + authResult.getName());

        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String userId = userService.getUserIdByUsername(userDetails.getUsername()).orElse(null);

        String role = userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .findFirst()
            .orElse(null);

        System.out.println("User ID: " + userId + ", Role: " + role);

        Map<String, Object> userData = new HashMap<>();
        userData.put("username", userDetails.getUsername());
        userData.put("role", role);
        userData.put("id", userId);

        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(userData)); // Return user details as JSON
        response.setStatus(HttpServletResponse.SC_OK);

        // Set authentication in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authResult);

        // Ensure the session is created and set
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        System.out.println("Authentication failed for user: " + request.getParameter("username") + ", error: " + failed.getMessage());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Login failed: " + failed.getMessage() + "\"}");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
