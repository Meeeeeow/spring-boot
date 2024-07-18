package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.AccountCustomer;
import com.example.demo.repository.AccountCustomerRepository;


@Service
public class CustomerService {
	
	@Autowired
	private AccountCustomerRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public String addUser(AccountCustomer userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "user added to system ";
    }


	public List<AccountCustomer> getUsers() {
		// TODO Auto-generated method stub
		return repository.findAll();
		
	}
}
