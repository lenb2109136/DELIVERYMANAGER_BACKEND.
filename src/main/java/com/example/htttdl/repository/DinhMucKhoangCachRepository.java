package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.DinhMucKhoangCach;
@Repository
public interface DinhMucKhoangCachRepository extends JpaRepository<DinhMucKhoangCach, Integer> {

}
