package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.LOAINHANVIEN;
@Repository
public interface LoaiNhanVienRepository extends JpaRepository<LOAINHANVIEN, Integer>{

}
