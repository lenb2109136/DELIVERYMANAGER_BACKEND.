package com.example.htttdl.modal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "DONHANG")
@Inheritance(strategy = InheritanceType.JOINED)
public class DonHang {
	@Id
	@Column(name = "DH_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@ManyToOne
	@JoinColumn(name = "DNH_ID")
	private DiemNhanHang diemNhanHang;
	@ManyToOne
	@NotNull(message = "Chưa chọn loại hàng")
	@JoinColumn(name = "LH_ID")
	private LoaiHang loaiHang;

	@ManyToOne()
	@JoinColumn(name = "DH_STATUS")
	private TrangThai trangThai;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
	// @JsonIgnore
	List<PhanCong> phanCongs;

	public DonHang() {
		this.setLatSend(0.3f);
		this.setLatSend(0.3f);
		this.setDiaChiNguoiGui("dcdcd");
	}

	public DonHang(Integer id, Float kinhDo, Float viDo) {
		this.id = id;
		this.kinhDo = kinhDo;
		this.viDo = viDo;
	}

	public DonHang(Integer orderId) {
		this.id = orderId;
	}

	// CHỖ NÀY Á NHA BẠN ƠI
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "PCG_ID")
	private PhieuChuyenGiao phieuChuyenGiao;
	// @NotNull(message = "Vui lòng chọn xã cho đơn hàng.")
	@ManyToOne
	@JoinColumn(name = "PX_ID")
	private Xa xa;
	@ManyToOne
	@JoinColumn(name = "KH_ID")
	private KhachHang khachHang;
	@ManyToOne
	@JoinColumn(name = "HTVC_ID")
	@NotNull(message = "Chưa chọn hình thức vận chuyển")
	private HinhThucVanChuyen hinhThucVanChuyen;
	@Column(name = "DH_TENNGUOINHAN")
	@NotNull(message = "Chưa điền tên người nhận")
	@NotBlank(message = "Chưa điền tên người nhận")
	private String tenNguoiNhan;
	@Column(name = "DH_DIENTHOAI")
	@NotNull(message = "Chưa điền SDT người nhận")
	@NotBlank(message = "Chưa điền SDT người nhận")
	@Pattern(regexp = "^(\\+84|0)[3-9]\\d{8}$", message = "Số điện thoại không đúng định dạng Việt Nam")
	private String SDTnguoiNhan;
	@Column(name = "DH_DIACHICHITIET")
	@NotNull(message = "Chưa điền địa chỉ chi tiết")
	@NotBlank(message = "Chưa điền địa chỉ chi tiết")
	private String diaChiChiTiet;
	@Column(name = "DH_TRONGLUONG")
	@NotNull(message = "Không được null")
	@Min(value = 0, message = "Trọng lượng không được < 0")
	private float trongLuong;
	@Column(name = "DH_KHOANGCACHDUTINH")
	private float khoangCachDuTinh;
	@NotNull(message = "Không được để trống kinh độ")
	@Min(value = 0, message = "Kinh độ không hợp lệ")
	@Column(name = "DH_KINHDO")
	private float kinhDo;
	@NotNull(message = "Không được để trống vĩ độ")
	@Min(value = 0, message = "Vĩ độ không hợp lệ")
	@Column(name = "DH_VIDO")
	private float viDo;
	@Column(name = "DH_LAYTIENTAICHO")
	private boolean layTienTaiCho;
	@Transient
	// @NotNull(message = "Chưa nhập tọa độ người gửi")
	private Float latSend;
	@Transient
	// @NotNull(message = "Chưa nhập tọa độ người gửi")
	private Float longSend;
	@Transient
	// @NotBlank(message = "Ko được bỏ trống địa chỉ người gửi")
	private String diaChiNguoiGui;

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDiaChiNguoiGui() {
		return diaChiNguoiGui;
	}

	public void setDiaChiNguoiGui(String diaChiNguoiGui) {
		this.diaChiNguoiGui = diaChiNguoiGui;
	}

	public Float getLatSend() {
		return latSend;
	}

	public void setLatSend(Float latSend) {
		this.latSend = latSend;
	}

	public Float getLongSend() {
		return longSend;
	}

	public void setLongSend(Float longSend) {
		this.longSend = longSend;
	}

	@Column(name = "DH_FEE")
	private Double fee;

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DiemNhanHang getDiemNhanHang() {
		return diemNhanHang;
	}

	public void setDiemNhanHang(DiemNhanHang diemNhanHang) {
		this.diemNhanHang = diemNhanHang;
	}

	public LoaiHang getLoaiHang() {
		return loaiHang;
	}

	public void setLoaiHang(LoaiHang loaiHang) {
		this.loaiHang = loaiHang;
	}

	public PhieuChuyenGiao getPhieuChuyenGiao() {
		return phieuChuyenGiao;
	}

	public void setPhieuChuyenGiao(PhieuChuyenGiao phieuChuyenGiao) {
		this.phieuChuyenGiao = phieuChuyenGiao;
	}

	public Xa getXa() {
		return xa;
	}

	public void setXa(Xa xa) {
		this.xa = xa;
	}

	public KhachHang getKhachHang() {
		return khachHang;
	}

	public void setKhachHang(KhachHang khachHang) {
		this.khachHang = khachHang;
	}

	public HinhThucVanChuyen getHinhThucVanChuyen() {
		return hinhThucVanChuyen;
	}

	public void setHinhThucVanChuyen(HinhThucVanChuyen hinhThucVanChuyen) {
		this.hinhThucVanChuyen = hinhThucVanChuyen;
	}

	public String getTenNguoiNhan() {
		return tenNguoiNhan;
	}

	public void setTenNguoiNhan(String tenNguoiNhan) {
		this.tenNguoiNhan = tenNguoiNhan;
	}

	public String getSDTnguoiNhan() {
		return SDTnguoiNhan;
	}

	public void setSDTnguoiNhan(String sDTnguoiNhan) {
		SDTnguoiNhan = sDTnguoiNhan;
	}

	public String getDiaChiChiTiet() {
		return diaChiChiTiet;
	}

	public void setDiaChiChiTiet(String diaChiChiTiet) {
		this.diaChiChiTiet = diaChiChiTiet;
	}

	public float getTrongLuong() {
		return trongLuong;
	}

	public void setTrongLuong(float trongLuong) {
		this.trongLuong = trongLuong;
	}

	public float getKhoangCachDuTinh() {
		return khoangCachDuTinh;
	}

	public void setKhoangCachDuTinh(float khoangCachDuTinh) {
		this.khoangCachDuTinh = khoangCachDuTinh;
	}

	public float getKinhDo() {
		return kinhDo;
	}

	public void setKinhDo(float kinhDo) {
		this.kinhDo = kinhDo;
	}

	public float getViDo() {
		return viDo;
	}

	public void setViDo(float viDo) {
		this.viDo = viDo;
	}

	public boolean isLayTienTaiCho() {
		return layTienTaiCho;
	}

	public void setLayTienTaiCho(boolean layTienTaiCho) {
		this.layTienTaiCho = layTienTaiCho;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public TrangThai getTrangThai() {
		return trangThai;
	}

	public void setTrangThai(TrangThai trangThai) {
		this.trangThai = trangThai;
	}

	public List<PhanCong> getPhanCongs() {
		return phanCongs;
	}

	public void setPhanCongs(List<PhanCong> phanCongs) {
		this.phanCongs = phanCongs;
	}

}
