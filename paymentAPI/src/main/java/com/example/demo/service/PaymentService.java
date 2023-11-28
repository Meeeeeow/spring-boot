package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.CustomerRepository;

@Service
public class PaymentService {
	@Autowired
	CustomerRepository customersRepo;
	
	public boolean existsByCustomerNameAndCustomerEmail(String name, String email) {
		
		return customersRepo.existsByCustomerNameAndCustomerEmail(name, email);
	}
}
