package com.example.htttdl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.Tinh;
import com.example.htttdl.repository.TinhRepository;

@Service
public class TinhService {
	@Autowired
	private TinhRepository tinhRepository;
	
	public List<Tinh> getall(){
		return tinhRepository.findAll();
	}
}
