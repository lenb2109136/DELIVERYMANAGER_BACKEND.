package com.example.htttdl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.htttdl.response.response;
import com.example.htttdl.service.LoaiHangService;

@RestController
@CrossOrigin("*")
@RequestMapping("/loaihang")
public class LoaiHangController {
	@Autowired
	private LoaiHangService loaiHangService;

	@GetMapping("getall")
	public ResponseEntity<response> getall() {
		return new ResponseEntity<response>(new response(HttpStatus.OK, "ok", loaiHangService.getall()), HttpStatus.OK);
	}
}
