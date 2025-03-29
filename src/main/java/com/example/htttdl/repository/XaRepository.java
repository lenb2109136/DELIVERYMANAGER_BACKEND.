package com.example.htttdl.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.Xa;

@Repository
public interface XaRepository extends JpaRepository<Xa, Integer> {
    @Query(value = " SELECT * FROM phuongxa WHERE QH_ID=:id", nativeQuery = true)
    public List<Xa> getXaByHuyen(@Param("id") int id);
}
