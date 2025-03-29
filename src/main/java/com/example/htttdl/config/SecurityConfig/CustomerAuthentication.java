package com.example.htttdl.config.SecurityConfig;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class CustomerAuthentication implements Authentication {
    private final String token;
    private boolean authenticated;
    private Collection<? extends GrantedAuthority> authorities;

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    private Object principal;
    private Object credentials;
    private Object details;

    public CustomerAuthentication(String token) {
        this.token = token;
        this.authenticated = false;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_cuimia"));
    }

    public CustomerAuthentication(String token, Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.authenticated = true;
        this.authorities = authorities != null ? authorities
                : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return (principal != null) ? principal.toString() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    public String getToken() {
        return token;
    }
}
