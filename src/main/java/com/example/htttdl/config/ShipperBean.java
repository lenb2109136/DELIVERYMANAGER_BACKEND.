package com.example.htttdl.config;

import com.example.htttdl.modal.DiemNhanHang;
import com.example.htttdl.modal.NHANVIEN;

public class ShipperBean {

    private NHANVIEN nhanVien;

    public ShipperBean() {
        this.nhanVien = new NHANVIEN();
        nhanVien.setTen("Nguyễn Văn A");
        nhanVien.setId(2);
        nhanVien.setDiemNhanHang(new DiemNhanHang(1));
    }

    public NHANVIEN getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NHANVIEN nhanVien) {
        this.nhanVien = nhanVien;
    }
}