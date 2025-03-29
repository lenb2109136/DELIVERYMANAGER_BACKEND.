package com.example.htttdl.modal;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "phancong")
public class PhanCong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "nhanVienId")
    private NHANVIEN nhanVien;
    @ManyToOne

    @JsonIgnore
    @JoinColumn(name = "donHangId")
    private DonHang order;

    private Date ngayPhanCong;

    @Column(name = "loaiPhanCong")
    private Integer loaiPhanCong;

    private Integer trangThai;

    public Integer getLoaiPhanCong() {
        return loaiPhanCong;
    }

    public void setLoaiPhanCong(Integer loaiPhanCong) {
        this.loaiPhanCong = loaiPhanCong;
    }

    public Integer getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Integer trangThai) {
        this.trangThai = trangThai;
    }

    public PhanCong() {

    }

    public PhanCong(NHANVIEN nhanVien, DonHang order, Integer donGiao, Integer trangThai) {
        this.nhanVien = nhanVien;
        this.order = order;
        this.loaiPhanCong = donGiao;
        this.ngayPhanCong = new Date();
        this.trangThai = trangThai;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NHANVIEN getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NHANVIEN nhanVien) {
        this.nhanVien = nhanVien;
    }

    public DonHang getOrder() {
        return order;
    }

    public void setOrder(DonHang order) {
        this.order = order;
    }

    public Date getNgayPhanCong() {
        return ngayPhanCong;
    }

    public void setNgayPhanCong(Date ngayPhanCong) {
        this.ngayPhanCong = ngayPhanCong;
    }

    public Integer getDonGiao() {
        return loaiPhanCong;
    }

    public void setDonGiao(Integer donGiao) {
        this.loaiPhanCong = donGiao;
    }

}
