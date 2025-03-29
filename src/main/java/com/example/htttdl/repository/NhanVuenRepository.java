package com.example.htttdl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.NHANVIEN;

@Repository
public interface NhanVuenRepository extends JpaRepository<NHANVIEN, Integer> {
    @Query("select p from NHANVIEN p where p.sdt=:sdt and p.matKhau=:password")
    public Optional<NHANVIEN> getNhanVienByEmailAndpassword(@Param("sdt") String sdt,
            @Param("password") String password);

    @Query("select p from NHANVIEN p where p.sdt=:sdt ")
    public Optional<NHANVIEN> getNhanVienBySDT(@Param("sdt") String sdt,
            @Param("password") String password);
}
