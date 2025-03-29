package com.example.htttdl.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DINHMUCKHOANGCACH")
public class DinhMucKhoangCach {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DMKC_ID")
    private int id;
    @Column(name = "DMKC_GIOIHANDUOI")
    private float gioiHanDuoi;
    @Column(name = "DMKC_GIOIHANTREN")
    private float gioiHanTren;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

	public float getGioiHanDuoi() {
		return gioiHanDuoi;
	}

	public void setGioiHanDuoi(float gioiHanDuoi) {
		this.gioiHanDuoi = gioiHanDuoi;
	}

	public float getGioiHanTren() {
		return gioiHanTren;
	}

	public void setGioiHanTren(float gioiHanTren) {
		this.gioiHanTren = gioiHanTren;
	}

    
}
