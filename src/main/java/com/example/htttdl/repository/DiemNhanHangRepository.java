package com.example.htttdl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.DiemNhanHang;

@Repository
public interface DiemNhanHangRepository extends JpaRepository<DiemNhanHang, Integer> {

    @Query(value = """
                SELECT * FROM diemNhanHang
                ORDER BY ST_Distance_Sphere(point(:longitude, :latitude), point(dnh_kinhdo, dnh_vido))
                LIMIT 2
            """, nativeQuery = true)
    List<DiemNhanHang> findTop2Nearest(@Param("latitude") double latitude, @Param("longitude") double longitude);

}
