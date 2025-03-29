package com.example.htttdl.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "DONHANGLAYNHAN")
public class DonHangLayNHan extends DonHang {
	// @ManyToOne(fetch = FetchType.EAGER)
	// @JoinColumn(name = "PX_ID")
	// private Xa xa;
	// @Column(name = "DHLN_DIACHI")
	// private String diChi;

	// public Xa getXa() {
	// return xa;
	// }

	// public void setXa(Xa xa) {
	// this.xa = xa;
	// }

	// public String getDiChi() {
	// return diChi;
	// }

	// public void setDiChi(String diChi) {
	// this.diChi = diChi;
	// }

}
