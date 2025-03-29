package com.example.htttdl.config.SecurityConfig;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterOnePer extends OncePerRequestFilter {
    private AuthenticationManager authManager;

    public FilterOnePer(AuthenticationManager authManager) {
        this.authManager = authManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization"); // Lưu ý chữ "A" viết hoa
        System.out.println("Authorization Header: " + token);

        if (token != null && token.startsWith("Bearer ")) {
            System.out.println("VAO ADMIN");
            token = token.substring(7);
            SecurityContext context = SecurityContextHolder.getContext();
            Authentication auth = new AuthenticationCustome(token);
            auth = authManager.authenticate(auth);
            context.setAuthentication(auth);
        } else {
            System.out.println("KHONG CO TOKEN");
        }

        filterChain.doFilter(request, response);
    }

}