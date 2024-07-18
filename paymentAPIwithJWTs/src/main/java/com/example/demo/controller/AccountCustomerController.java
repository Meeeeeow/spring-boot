package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.model.AccountCustomer;
import com.example.demo.model.RefreshToken;
import com.example.demo.service.CustomerService;
import com.example.demo.service.JwtService;
import com.example.demo.service.RefreshTokenService;




@RestController
@RequestMapping("/api")
public class AccountCustomerController {
	
	@Autowired
	private CustomerService service;
	
	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
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
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String addNewUser(@RequestBody AccountCustomer accountCustomer) {
		return service.addUser(accountCustomer);
	}
	
	@GetMapping("/getAllUsers")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<List<AccountCustomer>> getUsers(){
		return ResponseEntity.ok().body(service.getUsers());
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
	
	@PostMapping("/refreshToken")
	public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return refreshTokenService.findByToken(refreshTokenRequest.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getName());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .refreshTokenUid(refreshTokenRequest.getToken())
                            .build();
                }).orElseThrow(() -> new RuntimeException(
                        "Refresh token is not in database!"));
	}
}
