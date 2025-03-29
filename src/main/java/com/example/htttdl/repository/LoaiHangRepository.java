package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.LoaiHang;
@Repository
public interface LoaiHangRepository extends JpaRepository<LoaiHang, Integer> {

}
