package com.example.demo;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataCustomer {
	@GetMapping("/customer")
	public String helloCustomer(String name){
		return ( "Hello Customers with name " + name);
	}
}
