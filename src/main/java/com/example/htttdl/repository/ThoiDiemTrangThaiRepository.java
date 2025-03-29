package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.ThoiDiemTrangThai;
@Repository
public interface ThoiDiemTrangThaiRepository extends JpaRepository<ThoiDiemTrangThai, Integer> {

}
