package com.example.htttdl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.Huyen;

@Repository
public interface HuyenRepository extends JpaRepository<Huyen, Integer> {
	@Query(value = "SELECT * FROM quanhuyen WHERE t_id=:id", nativeQuery = true)
	public List<Huyen> getHuyenByTinh(@Param("id") int id);
}
