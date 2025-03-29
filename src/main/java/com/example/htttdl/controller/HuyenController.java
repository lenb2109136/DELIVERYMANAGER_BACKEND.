package com.example.htttdl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.htttdl.response.response;
import com.example.htttdl.service.QuanHuyenService;

@Controller
@CrossOrigin("*")
@RequestMapping("/huyen")
public class HuyenController {
	@Autowired
	private QuanHuyenService quanHuyenService;

	@GetMapping("/getbytinh")
	public ResponseEntity<response> getall(@RequestParam("id") int id) {
		return new ResponseEntity<response>(new response(HttpStatus.OK, "ok", quanHuyenService.getAllByTinh(id)),
				HttpStatus.OK);
	}
}
