package com.example.htttdl.config;

import com.example.htttdl.modal.NHANVIEN;

public class AminBean {
    private Integer buuCucId;
    NHANVIEN nhanVien = new NHANVIEN();

    public AminBean(Integer id) {
        this.buuCucId = id;
        nhanVien.setId(1);
    }

    public AminBean() {

    }

    public Integer getBuuCucId() {
        return buuCucId;
    }

    public void setBuuCucId(Integer buuCucId) {
        this.buuCucId = buuCucId;
        nhanVien.setId(1);
    }

    public NHANVIEN getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NHANVIEN nhanVien) {
        this.nhanVien = nhanVien;
    }

}
