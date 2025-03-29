package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.htttdl.modal.PhanCong;

public interface PhanCongRepository extends JpaRepository<PhanCong, Integer> {
    @Query(value = "SELECT * \r\n"
            + "FROM phancong WHERE donHangId=:idd AND loaiPhanCong=0 AND  trangthai=0 AND nhanVienId=:idnv\r\n"
            + "ORDER BY ngayPhanCong DESC, id DESC \r\n"
            + "LIMIT 1;\r\n"
            + "", nativeQuery = true)
    public PhanCong getPhanCong(int idd, int idnv);

    @Query(value = "SELECT * \r\n"
            + "FROM phancong WHERE donHangId=:idd AND loaiPhanCong=1 AND  trangthai=0 AND nhanVienId=:idnv\r\n"
            + "ORDER BY ngayPhanCong DESC, id DESC \r\n"
            + "LIMIT 1;\r\n"
            + "", nativeQuery = true)
    public PhanCong getPhanCongsend(int idd, int idnv);
}
