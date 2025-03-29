package com.example.htttdl.response;

import org.springframework.http.HttpStatus;

public class response {
	private HttpStatus status;
	private String message;
	private Object data;

	public response(HttpStatus status, String message, Object l) {
		this.status = status;
		this.message = message;
		this.data = l;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
