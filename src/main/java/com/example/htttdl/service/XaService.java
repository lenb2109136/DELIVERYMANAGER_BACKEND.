package com.example.htttdl.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.htttdl.modal.Xa;
import com.example.htttdl.repository.XaRepository;

@Service
public class XaService {
	@Autowired
	private XaRepository xaService;

	public List<Xa> getXaByHuyen(int id) {
		return xaService.getXaByHuyen(id);
	}
}
