package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.AuthRequest;
import com.example.demo.model.AccountCustomer;
import com.example.demo.service.CustomerService;
import com.example.demo.service.JwtService;



@RestController
@RequestMapping("/api")
public class AccountCustomerController {
	
	@Autowired
	private CustomerService service;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@GetMapping("/welcome")
	public String welcome() {
		return "This is the welcome page. Can be accessed by everyone."; 
	}
	@GetMapping("/authenticated")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String authenticate() {
		return "This will be authenticated onlu by admin";
	}
	
	@GetMapping("/authenticated_user")
	
	public String authenticateUser() {
		return "This will be authenticated only by user";
	}
//	@PostMapping("/addRoles")
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
//	public String addRoles() {
//		rolesService.addRole("ROLE_USER");
//		return "Role added successfully to database";
//	}
	@PostMapping("/addNew")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String addNewUser(@RequestBody AccountCustomer accountCustomer) {
		return service.addUser(accountCustomer);
	}
	
	@PostMapping("/authenticate")
	public String authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		if(authentication.isAuthenticated()) {
			return jwtService.generateToken(authRequest.getUsername());
		}else {
			throw new UsernameNotFoundException("Invalid user request!");
		}
		
	}
}
