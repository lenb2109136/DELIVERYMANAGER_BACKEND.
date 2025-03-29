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
import com.example.htttdl.modal.KhachHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.service.AuthService;

@Service
public class AuthenticationTokenProviderCustomer implements AuthenticationProvider {

    @Autowired
    AuthService authService;

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    KhachHang adminBean;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CustomerAuthentication auth = (CustomerAuthentication) authentication;
        if (auth.getToken() != null) {
            String sdt = tokenUtil.extractUsername(auth.getToken());
            KhachHang khachhang = authService.getKhachHangBySDT(sdt, sdt);
            if (khachhang != null) {
                auth.setAuthenticated(true);
                adminBean.setId(khachhang.getId());
                adminBean.setTen(khachhang.getTen());
                auth.setAuthorities(Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_CUSTOMER")));
                System.out.println("DA XAC THUC THANH CONG");
                return auth;
            }
            return auth;
        }
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(CustomerAuthentication.class);
    }

}
