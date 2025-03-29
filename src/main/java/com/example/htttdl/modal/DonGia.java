package com.example.htttdl.modal;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "DONGIA")
public class DonGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DG_ID")
    private Integer id;
    @Column(name = "DG_GIA")
    private Float gia;
    @Column(name = "DG_THOIDIEMBATDAU")
    private Date thoiDiemBatDau;
    @Column(name = "DG_THOIDIEMKETTHUC")
    private Date thoiDiemKetThuc;
    @ManyToOne
    @JoinColumn(name = "DMKC_ID")

    private DinhMucKhoangCach dinhMucKhoangCach;
    @ManyToOne
    @JoinColumn(name = "DMKL_ID")
    private DinhMucKhoiLuong dinhMucKhoiLuong;
    @ManyToOne
    @JoinColumn(name = "HTVC_ID")
    private HinhThucVanChuyen hinhThucVanChuyen;
    @ManyToOne
    @JoinColumn(name = "LH_ID")
    private LoaiHang loaiHang;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Float getGia() {
        return gia;
    }

    public void setGia(Float gia) {
        this.gia = gia;
    }

    public Date getThoiDiemBatDau() {
        return thoiDiemBatDau;
    }

    public void setThoiDiemBatDau(Date thoiDiemBatDau) {
        this.thoiDiemBatDau = thoiDiemBatDau;
    }

    public Date getThoiDiemKetThuc() {
        return thoiDiemKetThuc;
    }

    public void setThoiDiemKetThuc(Date thoiDiemKetThuc) {
        this.thoiDiemKetThuc = thoiDiemKetThuc;
    }

    public DinhMucKhoangCach getDinhMucKhoangCach() {
        return dinhMucKhoangCach;
    }

    public void setDinhMucKhoangCach(DinhMucKhoangCach dinhMucKhoangCach) {
        this.dinhMucKhoangCach = dinhMucKhoangCach;
    }

    public DinhMucKhoiLuong getDinhMucKhoiLuong() {
        return dinhMucKhoiLuong;
    }

    public void setDinhMucKhoiLuong(DinhMucKhoiLuong dinhMucKhoiLuong) {
        this.dinhMucKhoiLuong = dinhMucKhoiLuong;
    }

    public HinhThucVanChuyen getHinhThucVanChuyen() {
        return hinhThucVanChuyen;
    }

    public void setHinhThucVanChuyen(HinhThucVanChuyen hinhThucVanChuyen) {
        this.hinhThucVanChuyen = hinhThucVanChuyen;
    }

    public LoaiHang getLoaiHang() {
        return loaiHang;
    }

    public void setLoaiHang(LoaiHang loaiHang) {
        this.loaiHang = loaiHang;
    }

}
