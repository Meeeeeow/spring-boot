package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
	@Autowired
	private  CustomerRepository customersRepo;
	
	public boolean existsByCustomerNameAndCustomerEmail(String name, String email) {
		
		return customersRepo.existsByCustomerNameAndCustomerEmail(name, email);
	}
}
