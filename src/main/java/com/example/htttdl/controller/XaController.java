package com.example.htttdl.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.htttdl.response.response;
import com.example.htttdl.service.XaService;

@RestController
@RequestMapping("xa")
@CrossOrigin("*")
public class XaController {
	@Autowired
	private XaService xaService;

	@GetMapping("/getxabyhuyen")
	public ResponseEntity<response> getXaByHuyen(int id) {
		return new ResponseEntity<response>(new response(HttpStatus.OK, "ok", xaService.getXaByHuyen(id)),
				HttpStatus.OK);
	}
}
