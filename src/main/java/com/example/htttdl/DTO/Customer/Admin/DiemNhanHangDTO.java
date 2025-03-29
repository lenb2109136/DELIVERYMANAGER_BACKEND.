package com.example.htttdl.DTO.Customer.Admin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiemNhanHangDTO {
    private Object diemNhanHangId;
    private Object diChiChiTiet;
    private Object khoangCachDuTinh;
    private Object khoangCachDuTinhNeChuyenTiep;
    private Object orderId;
    private Set<Integer> orderIds;

    public DiemNhanHangDTO() {
    }

    public DiemNhanHangDTO(Object orderId, Object diemNhanHangId, Object diChiChiTiet, Object khoangCachDuTinh,
            Object khoangCachNeuChuyenTiep) {
        this.diemNhanHangId = diemNhanHangId;
        this.diChiChiTiet = diChiChiTiet;
        this.khoangCachDuTinh = khoangCachDuTinh;
        this.khoangCachDuTinhNeChuyenTiep = khoangCachNeuChuyenTiep;
        this.orderId = orderId;
    }

    public void setDiemNhanHangId(Object diemNhanHangId) {
        this.diemNhanHangId = diemNhanHangId;
    }

    public void setDiChiChiTiet(Object diChiChiTiet) {
        this.diChiChiTiet = diChiChiTiet;
    }

    public void setKhoangCachDuTinh(Object khoangCachDuTinh) {
        this.khoangCachDuTinh = khoangCachDuTinh;
    }

    public Object getKhoangCachDuTinhNeChuyenTiep() {
        return khoangCachDuTinhNeChuyenTiep;
    }

    public void setKhoangCachDuTinhNeChuyenTiep(Object khoangCachDuTinhNeChuyenTiep) {
        this.khoangCachDuTinhNeChuyenTiep = khoangCachDuTinhNeChuyenTiep;
    }

    public Object getDiemNhanHangId() {
        return diemNhanHangId;
    }

    public void setDiemNhanHangId(Integer diemNhanHangId) {
        this.diemNhanHangId = diemNhanHangId;
    }

    public Object getDiChiChiTiet() {
        return diChiChiTiet;
    }

    public void setDiChiChiTiet(String diChiChiTiet) {
        this.diChiChiTiet = diChiChiTiet;
    }

    public Object getKhoangCachDuTinh() {
        return khoangCachDuTinh;
    }

    public void setKhoangCachDuTinh(Float khoangCachDuTinh) {
        this.khoangCachDuTinh = khoangCachDuTinh;
    }

    public Object getOrderId() {
        return orderId;
    }

    public void setOrderId(Object orderId) {
        this.orderId = orderId;
    }

}
