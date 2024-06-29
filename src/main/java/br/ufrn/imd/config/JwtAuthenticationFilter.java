package br.ufrn.imd.config;

import br.ufrn.imd.model.User;
import br.ufrn.imd.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;

    private final JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            HandlerExceptionResolver handlerExceptionResolver
    ) {
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String email = jwtService.extractUsername(jwt);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (email != null && authentication == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(email);
                System.out.println("email : " + userDetails.getEmail());
                System.out.println("autoridades : " + userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            System.out.println("é o jason");
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }




}