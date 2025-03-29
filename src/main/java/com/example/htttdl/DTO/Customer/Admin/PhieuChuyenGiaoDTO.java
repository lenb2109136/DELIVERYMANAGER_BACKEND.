package com.example.htttdl.DTO.Customer.Admin;

import java.util.List;

// import com.example.htttdl.controller.Admin.PhieuChuyenGiao;
import com.example.htttdl.modal.DonHang;
import com.example.htttdl.modal.PhieuChuyenGiao;

public class PhieuChuyenGiaoDTO {
    private PhieuChuyenGiao phieuChuyenGiao;
    private List<DonHang> orderList;

    public PhieuChuyenGiaoDTO() {

    }

    public PhieuChuyenGiaoDTO(PhieuChuyenGiao p, List<DonHang> o) {
        this.phieuChuyenGiao = p;
        this.orderList = o;
    }

    public PhieuChuyenGiao getPhieuChuyenGiao() {
        return phieuChuyenGiao;
    }

    public void setPhieuChuyenGiao(PhieuChuyenGiao phieuChuyenGiao) {
        this.phieuChuyenGiao = phieuChuyenGiao;
    }

    public List<DonHang> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<DonHang> orderList) {
        this.orderList = orderList;
    }

}
