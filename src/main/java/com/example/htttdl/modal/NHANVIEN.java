package com.example.htttdl.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "NHANVIEN")
public class NHANVIEN {
	@Id
	@Column(name = "NV_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@ManyToOne
	@JoinColumn(name = "LNV_ID")
	private LOAINHANVIEN loainhanvien;
	@ManyToOne
	@JoinColumn(name = "DNH_ID")
	private DiemNhanHang diemNhanHang;
	@Column(name = "NV_TEN")
	private String ten;
	@Column(name = "NV_GIOITINHNAM")
	private Boolean gioitinhnam;
	@Column(name = "NV_SODIENTHOAI")
	private String sdt;

	@Column(name = "NV_MATKHAU")
	private String matKhau;

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		this.matKhau = matKhau;
	}

	@Column(name = "NV_VIDO")
	private Float kinhDo;
	@Column(name = "NV_KINHDO")
	private Float viDo;

	public Float getKinhDo() {
		return kinhDo;
	}

	public void setKinhDo(Float kinhDo) {
		this.kinhDo = kinhDo;
	}

	public Float getViDo() {
		return viDo;
	}

	public void setViDo(Float viDo) {
		this.viDo = viDo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LOAINHANVIEN getLoainhanvien() {
		return loainhanvien;
	}

	public void setLoainhanvien(LOAINHANVIEN loainhanvien) {
		this.loainhanvien = loainhanvien;
	}

	public DiemNhanHang getDiemNhanHang() {
		return diemNhanHang;
	}

	public void setDiemNhanHang(DiemNhanHang diemNhanHang) {
		this.diemNhanHang = diemNhanHang;
	}

	public String getTen() {
		return ten;
	}

	public void setTen(String ten) {
		this.ten = ten;
	}

	public Boolean getGioitinhnam() {
		return gioitinhnam;
	}

	public void setGioitinhnam(Boolean gioitinhnam) {
		this.gioitinhnam = gioitinhnam;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

}
