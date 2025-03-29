package com.example.htttdl.DTO.Customer.Admin;

import java.util.List;

import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.NHANVIEN;

public class PhanCongDTO {
    private List<DonHang> orders;
    private NHANVIEN nhanVien;

    private Double khoangCachTrungTamCum;

    public PhanCongDTO() {
    }

    public PhanCongDTO(List<DonHang> orders, NHANVIEN nhanVien, Double khoangCach) {
        this.orders = orders;
        this.nhanVien = nhanVien;
        this.khoangCachTrungTamCum = khoangCach;
    }

    public PhanCongDTO(List<DonHang> orders, NHANVIEN nhanVien) {
        this.orders = orders;
        this.nhanVien = nhanVien;
    }

    public List<DonHang> getOrders() {
        return orders;
    }

    public void setOrders(List<DonHang> orders) {
        this.orders = orders;
    }

    public NHANVIEN getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NHANVIEN nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Double getKhoangCachTrungTamCum() {
        return khoangCachTrungTamCum;
    }

    public void setKhoangCachTrungTamCum(Double khoangCachTrungTamCum) {
        this.khoangCachTrungTamCum = khoangCachTrungTamCum;
    }

}