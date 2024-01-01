package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.AccountCustomer;
import com.example.demo.repository.AccountCustomerRepository;

import jakarta.validation.Valid;


@Service
public class CustomerService {
	
	@Autowired
	private AccountCustomerRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	public String addUser(@Valid AccountCustomer userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "user added to system!!";
    }


	public List<AccountCustomer> getUsers() {
		return repository.findAll();	
	}
	
	public Optional<AccountCustomer> userExists(String username) {
	    return repository.findByName(username);
	}

}
