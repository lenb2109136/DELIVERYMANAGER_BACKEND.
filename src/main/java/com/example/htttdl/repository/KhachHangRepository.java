package com.example.htttdl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.KhachHang;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    @Query("select p from KhachHang p where p.sdt=:sdt ")
    public Optional<KhachHang> getKhachHangById(@Param("sdt") String sdt);

    @Query("select p from KhachHang p where p.sdt=:sdt and p.matKhau=:password ")
    public Optional<KhachHang> getKhachHangBySdtAndPassword(@Param("sdt") String sdt,
            @Param("password") String password);
}
