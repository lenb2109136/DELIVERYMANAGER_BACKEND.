package com.example.htttdl.DTO;

import com.example.htttdl.modal.NHANVIEN;

public class LoginResponse {
    private String token;
    private String role;

    public LoginResponse(String token, String role, Object nhanVien) {
        this.token = token;
        this.role = role;
        this.nhanVien = nhanVien;
    }

    private Object nhanVien;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Object getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NHANVIEN nhanVien) {
        this.nhanVien = nhanVien;
    }

}
