package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //HTTP request are handled by controller
public class AppConfiguration {
	@RequestMapping("/hello")  // localhost/hello/
	public String hello() {
		return "Hello World!!";
	}
}
