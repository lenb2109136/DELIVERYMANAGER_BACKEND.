package com.example.htttdl.controller;

import java.net.ResponseCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.htttdl.response.response;
import com.example.htttdl.service.HinhThucVanChuyenService;

@RestController
@CrossOrigin("*")
@RequestMapping("/hinhthuc")
public class HinhThucVanChuyenController {
	@Autowired
	private HinhThucVanChuyenService hinhThucVanChuyenService;

	@GetMapping("/getall")
	public ResponseEntity<response> laydanhsach() {
		System.out.println("đã vào đây rồi bạn");
		return new ResponseEntity<response>(new response(HttpStatus.OK, "ok", hinhThucVanChuyenService.getall()),
				HttpStatus.OK);
	}
}
