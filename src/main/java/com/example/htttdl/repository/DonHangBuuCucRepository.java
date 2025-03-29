package com.example.htttdl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.htttdl.modal.DonHangBuuCuc;
@Repository
public interface DonHangBuuCucRepository extends JpaRepository<DonHangBuuCuc, Integer> {

}
