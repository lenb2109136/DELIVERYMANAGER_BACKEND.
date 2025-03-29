package com.example.htttdl.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.KhachHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.repository.KhachHangRepository;
import com.example.htttdl.repository.NhanVuenRepository;

@Service
public class AuthService {
    @Autowired
    NhanVuenRepository accountRepo;

    @Autowired
    KhachHangRepository khachHangRepository;

    public NHANVIEN getUserByEmail(String email, String password) {
        return accountRepo.getNhanVienByEmailAndpassword(email, password).orElse(null);
    }

    public NHANVIEN getUserBySDT(String email, String password) {
        return accountRepo.getNhanVienBySDT(email, password).orElse(null);
    }

    public KhachHang getKhachHangBySdtAndPassword(String email, String password) {
        return khachHangRepository.getKhachHangBySdtAndPassword(email, password).orElse(null);
    }

    public KhachHang getKhachHangBySDT(String email, String password) {
        return khachHangRepository.getKhachHangById(email).orElse(null);
    }
}
