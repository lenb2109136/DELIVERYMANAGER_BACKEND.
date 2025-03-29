package com.example.htttdl.service;

import java.util.List;

import com.example.htttdl.modal.DonHang;

public class ToaDoDTO {
    private ToaDo toaDoGoc;
    private List<DonHang> dsdonhang;

    public ToaDo getToaDoGoc() {
        return toaDoGoc;
    }

    public void setToaDoGoc(ToaDo toaDoGoc) {
        this.toaDoGoc = toaDoGoc;
    }

    public List<DonHang> getDsdonhang() {
        return dsdonhang;
    }

    public void setDsdonhang(List<DonHang> dsdonhang) {
        this.dsdonhang = dsdonhang;
    }

}
