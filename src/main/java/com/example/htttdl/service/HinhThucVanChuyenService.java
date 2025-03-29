package com.example.htttdl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.HinhThucVanChuyen;
import com.example.htttdl.repository.HinhThucVanChuyenRepository;

@Service
public class HinhThucVanChuyenService {
	@Autowired
	private HinhThucVanChuyenRepository hinhThucVanChuyenRepository;
	public List<HinhThucVanChuyen> getall() {
		return hinhThucVanChuyenRepository.findAll();
	}
}
