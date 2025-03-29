package com.example.htttdl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.example.htttdl.config.FireBaseInit;

@SpringBootApplication
public class HtttdlApplication {

	public static void main(String[] args) throws Exception {
		FireBaseInit.initFirebase();
		SpringApplication.run(HtttdlApplication.class, args);
	}

}
