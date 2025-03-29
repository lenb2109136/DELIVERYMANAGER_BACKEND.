package com.example.htttdl.modal;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "THOIDIEMTRANGTHAI")
public class ThoiDiemTrangThai {
	@Id
	@Column(name = "TTTT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "TTTT_THOIGIAN")
	private LocalDateTime thoiDiem;
	@ManyToOne
	@JoinColumn(name = "TT_ID")
	private TrangThai trangThai;
	@ManyToOne
	@JoinColumn(name = "DH_ID")
	private DonHang donHang;
	@ManyToOne
	@JoinColumn(name = "NV_ID")
	private NHANVIEN nhanvien;
	@ManyToOne
	@JoinColumn(name = "DNH_ID")
	private DiemNhanHang diemNhanHang;

	public ThoiDiemTrangThai() {

	}

	public ThoiDiemTrangThai(NHANVIEN nhanVien, DonHang donHang, TrangThai trangThai) {
		this.nhanvien = nhanVien;
		this.donHang = donHang;
		this.trangThai = trangThai;
		this.thoiDiem = LocalDateTime.now();
	}

	public ThoiDiemTrangThai(NHANVIEN nhanVien, DonHang donHang, TrangThai trangThai, DiemNhanHang diemNhanHang) {
		this.nhanvien = nhanVien;
		this.donHang = donHang;
		this.trangThai = trangThai;
		this.thoiDiem = LocalDateTime.now();
		this.diemNhanHang = diemNhanHang;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getThoiDiem() {
		return thoiDiem;
	}

	public void setThoiDiem(LocalDateTime thoiDiem) {
		this.thoiDiem = thoiDiem;
	}

	public TrangThai getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(TrangThai trangThai) {
		this.trangThai = trangThai;
	}

	public DonHang getDonHang() {
		return donHang;
	}

	public void setDonHang(DonHang donHang) {
		this.donHang = donHang;
	}

	public NHANVIEN getNhanvien() {
		return nhanvien;
	}

	public void setNhanvien(NHANVIEN nhanvien) {
		this.nhanvien = nhanvien;
	}

	public DiemNhanHang getDiemNhanHang() {
		return diemNhanHang;
	}

	public void setDiemNhanHang(DiemNhanHang diemNhanHang) {
		this.diemNhanHang = diemNhanHang;
	}

}
