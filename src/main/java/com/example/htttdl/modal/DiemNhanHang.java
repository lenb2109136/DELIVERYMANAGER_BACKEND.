package com.example.htttdl.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DIEMNHANHANG")
public class DiemNhanHang {
	@Id
	@Column(name = "DNH_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "PX_ID")
	private int px_id;
	@Column(name = "DNH_DIACHICHITIET")
	private String diachichitiet;
	@Column(name = "DNH_SDT")
	private String sdt;
	@Column(name = "DNH_KINHDO")
	private float kinhKo;
	@Column(name = "DNH_VIDO")
	private float viDo;

	public DiemNhanHang(Integer diemNhanHangId) {
		this.id = diemNhanHangId;
	}

	public DiemNhanHang() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPx_id() {
		return px_id;
	}

	public void setPx_id(int px_id) {
		this.px_id = px_id;
	}

	public String getDiachichitiet() {
		return diachichitiet;
	}

	public void setDiachichitiet(String diachichitiet) {
		this.diachichitiet = diachichitiet;
	}

	public String getSdt() {
		return sdt;
	}

	public void setSdt(String sdt) {
		this.sdt = sdt;
	}

	public float getKinhKo() {
		return kinhKo;
	}

	public void setKinhKo(float kinhKo) {
		this.kinhKo = kinhKo;
	}

	public float getViDo() {
		return viDo;
	}

	public void setViDo(float viDo) {
		this.viDo = viDo;
	}

	public double haversineDistance(double lat, double lon) {
		final double R = 6371.0;
		double dLat = Math.toRadians(lat - this.viDo);
		double dLon = Math.toRadians(lon - this.kinhKo);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
				Math.cos(Math.toRadians(this.viDo)) * Math.cos(Math.toRadians(lat)) *
						Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return R * c;
	}

}
