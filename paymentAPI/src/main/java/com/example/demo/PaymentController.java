package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/abc")
public class PaymentController {
	@Autowired
	PaymentService paymentService; 
	
	
	@GetMapping("/submission")
	public String startPage() {
		return "submitForm";
	}
	@PostMapping("/submitForms")
	public String checkExistingAccount(@RequestParam("name") String name,
			@RequestParam("email") String email) {
		boolean accountExists = paymentService.existsByCustomerNameAndCustomerEmail(name, email);
		System.out.println("Customer account is: " + name + "\\nEmail is: " + email);
		System.out.println(accountExists);
		if(accountExists)
		{
			 return "<script>alert('Customer " + name + " is present in the database.');</script>";
		}else {
			return "<script>alert('Customer " + name + " is not present in the database.');</script>";
		}
		
	}
}
