package com.example.htttdl.modal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "phuongxa")
public class Xa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PX_ID")
    private Integer id;
    @Column(name = "PX_TEN")
    private String tenXa;

    @Column(name = "PX_vido")
    private Float viDo;
    @Column(name = "PX_KINHDO")
    private Float kinhDo;

    @ManyToOne
    @JoinColumn(name = "QH_ID")
    private Huyen huyen;

    public Float getViDo() {
        return viDo;
    }

    public void setViDo(Float viDo) {
        this.viDo = viDo;
    }

    public Float getKinhDo() {
        return kinhDo;
    }

    public void setKinhDo(Float kinhDo) {
        this.kinhDo = kinhDo;
    }

    public String getTenXa() {
        return tenXa;
    }

    public void setTenXa(String tenXa) {
        this.tenXa = tenXa;
    }

    public Huyen getHuyen() {
        return huyen;
    }

    public void setHuyen(Huyen huyen) {
        this.huyen = huyen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenTinh() {
        return tenXa;
    }

    public void setTenTinh(String tenTinh) {
        this.tenXa = tenTinh;
    }
}
