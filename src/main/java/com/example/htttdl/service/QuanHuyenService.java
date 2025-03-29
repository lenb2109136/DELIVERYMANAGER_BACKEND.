package com.example.htttdl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.Huyen;
import com.example.htttdl.repository.HuyenRepository;

@Service
public class QuanHuyenService {
	@Autowired
	private HuyenRepository huyenRepository;

	public List<Huyen> getAllByTinh(int id) {
		return huyenRepository.getHuyenByTinh(id);
	}
}
