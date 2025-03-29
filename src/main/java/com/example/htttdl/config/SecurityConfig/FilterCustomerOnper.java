package com.example.htttdl.config.SecurityConfig;

import java.io.IOException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterCustomerOnper extends OncePerRequestFilter {
    private AuthenticationManager authManager;

    public FilterCustomerOnper(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null) {
            token = token.substring(7);
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication auth = new CustomerAuthentication(token);
            auth = authManager.authenticate(auth);
            System.out.println("IS AUTHEN " + auth.isAuthenticated());
            context.setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

}