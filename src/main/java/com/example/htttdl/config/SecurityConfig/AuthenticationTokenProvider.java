package com.example.htttdl.config.SecurityConfig;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.htttdl.Utils.TokenUtil;
import com.example.htttdl.config.AminBean;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.service.AuthService;

@Service
public class AuthenticationTokenProvider implements AuthenticationProvider {

    @Autowired
    AuthService authService;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    AminBean adminBean;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AuthenticationCustome auth = (AuthenticationCustome) authentication;
        System.err.println("XIN CHÀO BẠN ƠI");
        if (auth.getToken() != null) {
            String sdt = tokenUtil.extractUsername(auth.getToken());
            NHANVIEN nhanVien = authService.getUserBySDT(sdt, sdt);
            if (nhanVien != null) {
                System.out.println("HELLO ADMIN");
                auth.setAuthenticated(true);
                adminBean.setBuuCucId(nhanVien.getDiemNhanHang().getId());
                adminBean.setNhanVien(nhanVien);
                auth.setAuthorities(Collections.singletonList(
                        new SimpleGrantedAuthority(
                                nhanVien.getLoainhanvien().getId() == 1 ? "ROLE_ADMIN" : "ROLE_SHIPPER")));
                return auth;
            }
            return auth;
        }
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(AuthenticationCustome.class);
    }

}
