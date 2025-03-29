package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.TrangThai;
@Repository
public interface TrangThaiRepository extends JpaRepository<TrangThai, Integer> {

}
