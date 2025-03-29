package com.example.htttdl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.htttdl.DTO.LoginData;
import com.example.htttdl.DTO.LoginResponse;
import com.example.htttdl.Utils.TokenUtil;
import com.example.htttdl.modal.KhachHang;
import com.example.htttdl.modal.NHANVIEN;
import com.example.htttdl.repository.NhanVuenRepository;
import com.example.htttdl.response.ObjectRespone;
import com.example.htttdl.service.AuthService;

@RestController
@CrossOrigin("*")
public class Login {

    @Autowired
    AuthService authService;

    @Autowired
    TokenUtil tokenUtil;

    @PostMapping("/login/nhanvien")
    public ResponseEntity<Object> login(@RequestBody LoginData loginRequest) {
        if (loginRequest.getSdt() != null || loginRequest.getPassword() != null) {
            NHANVIEN nhanVien = authService.getUserByEmail(loginRequest.getSdt(), loginRequest.getPassword());
            if (nhanVien != null) {
                String token = tokenUtil.generateToken(nhanVien.getSdt());
                LoginResponse lr = new LoginResponse(token,
                        nhanVien.getLoainhanvien().getId() == 1 ? "ADMIN" : "SHIPPER",
                        nhanVien);
                return new ResponseEntity<>(new ObjectRespone("success", lr), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ObjectRespone("SDT hoặc mật khẩu không chính xác", loginRequest),
                    HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(new ObjectRespone("Thông tin đăng nhập không hợp lệ", loginRequest),
                HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/login/customer")
    public ResponseEntity<Object> loginCustomer(@RequestBody LoginData loginRequest) {
        if (loginRequest.getSdt() != null || loginRequest.getPassword() != null) {
            KhachHang nhanVien = authService.getKhachHangBySdtAndPassword(loginRequest.getSdt(),
                    loginRequest.getPassword());
            if (nhanVien != null) {
                String token = tokenUtil.generateToken(nhanVien.getSdt());
                LoginResponse lr = new LoginResponse(token, "CUSTOMER", nhanVien);
                return new ResponseEntity<>(new ObjectRespone("success", lr), HttpStatus.OK);
            }
            return new ResponseEntity<>(new ObjectRespone("SDT hoặc mật khẩu không chính xác", loginRequest),
                    HttpStatus.BAD_REQUEST);

        }
        return new ResponseEntity<>(new ObjectRespone("Thông tin đăng nhập không hợp lệ", loginRequest),
                HttpStatus.BAD_REQUEST);
    }
}
