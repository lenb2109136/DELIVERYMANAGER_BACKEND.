package com.example.htttdl.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.LoaiHang;
import com.example.htttdl.repository.LoaiHangRepository;

@Service
public class LoaiHangService {
	@Autowired
	private LoaiHangRepository loaiHangRepository;

	public List<LoaiHang> getall() {
		return loaiHangRepository.findAll();
	}
}
