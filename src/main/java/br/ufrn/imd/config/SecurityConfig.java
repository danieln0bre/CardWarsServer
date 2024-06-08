package br.ufrn.imd.config;

import br.ufrn.imd.service.UserService;
import br.ufrn.imd.config.CustomAuthenticationFilter;
import br.ufrn.imd.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final UserService userService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl, UserService userService) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(authenticationConfiguration);
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager, userService);
        customAuthenticationFilter.setFilterProcessesUrl("/api/users/login");

        http
            .csrf().disable()  // Disable CSRF for simplicity; enable it in production
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/users/login", "/api/users/register/player", "/api/users/register/manager","/api/players/{id}/events/add" ,"/public/**").permitAll() // Allow access to login endpoints
                .requestMatchers("/api/events/createEvent", "/api/events/{id}/update", "/api/events/{id}/delete",
                        "/api/events/{eventId}/finalize", "/api/events/{eventId}/start", "/api/events/{eventId}/generatePairings",
                        "/api/events/{eventId}/finalizeRound").hasRole("MANAGER")
                .anyRequest().authenticated() // All other requests need to be authenticated
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PlainTextPasswordEncoder(); // Use a suitable password encoder for your application
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new CookieHttpSessionIdResolver();
    }
}
