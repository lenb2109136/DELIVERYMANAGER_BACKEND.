package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.HinhThucVanChuyen;
@Repository
public interface HinhThucVanChuyenRepository extends JpaRepository<HinhThucVanChuyen, Integer> {

}
