package com.example.htttdl.DTO.Customer;

import com.example.htttdl.modal.DiemNhanHang;
import com.example.htttdl.modal.HinhThucVanChuyen;
import com.example.htttdl.modal.LoaiHang;
import com.example.htttdl.modal.TrangThai;
import com.example.htttdl.modal.Xa;

public interface OrderDTO {
    Integer getId();

    LoaiHang getLoaiHang();

    DiemNhanHang getDiemNhanHang();

    TrangThai getTrangThai();

    Xa getXa();

    HinhThucVanChuyen getHinhThucVanChuyen();

    String getTenNguoiNhan();

    String getSDTnguoiNhan();

    String getDiaChiChiTiet();

    Float getTrongLuong();

    Float getKhoangCachDuTinh();

    Float getKinhDo();

    Float getViDo();

}