package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.DonGia;

@Repository
public interface DonGiaRepository extends JpaRepository<DonGia, Integer> {
    @Query(value = "SELECT p.DG_GIA FROM dongia p JOIN dinhmuckhoangcach kc ON kc.DMKC_ID = p.DMKC_ID JOIN dinhmuckhoiluong kl ON kl.DMKL_ID = p.DMKL_ID WHERE p.LH_ID = ?1 AND p.HTVC_ID = ?2 AND  ?3 BETWEEN kc.DMKC_GIOIHANDUOI AND kc.DMKC_GIOIHANTREN AND ?4 BETWEEN kl.DMKL_GIOIHANDUOI AND kl.DMKL_GIOIHANTREN;\r\n"
            + //
            "", nativeQuery = true)
    public Double getPrice(Integer loaiHangId, Integer hinhThucVanChuyenId, float khoiLuong, float khoangCach);

    @Modifying
    @Query("UPDATE DonHang d set trangThai=:trangThai where d.id=:orderId and d.khachHang.id =:customerId")
    public Integer cancelOrder(@Param("orderId") Integer orderId, @Param("customerId") Integer customerId);
}
