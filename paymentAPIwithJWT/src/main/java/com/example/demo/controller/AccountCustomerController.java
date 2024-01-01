package com.example.demo.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.el.stream.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.FieldError;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.RefreshTokenRequest;
import com.example.demo.exception.DuplicateCustomerException;
import com.example.demo.exception.JwtTokenExpiredException;
import com.example.demo.model.AccountCustomer;
import com.example.demo.model.RefreshToken;
import com.example.demo.service.CustomerService;
import com.example.demo.service.JwtService;
import com.example.demo.service.RefreshTokenService;

import jakarta.validation.Valid;



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
//	@GetMapping("/authenticated")
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
//	public String authenticate() {
//		return "This will be authenticated onlu by admin";
//	}
//	
//	@GetMapping("/authenticated_user")
//	
//	public String authenticateUser() {
//		return "This will be authenticated only by user";
//	}
//	@PostMapping("/addRoles")
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
//	public String addRoles() {
//		rolesService.addRole("ROLE_USER");
//		return "Role added successfully to database";
//	}
	@PostMapping("/addNew")
//	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> addNewUser(@Valid @RequestBody AccountCustomer accountCustomer) {
		 try {
			 String result = service.addUser(accountCustomer);
	         return ResponseEntity.ok(result);
	     } catch (DuplicateCustomerException ex) {
	         return ResponseEntity.badRequest().body(ex.getMessage());
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	        		 .body("User already added to the system!!");
	    }
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
	
	@GetMapping("/getAllUsers")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<?> getUsers() {
	    try {
	        List<AccountCustomer> users = service.getUsers();
	        return ResponseEntity.ok().body(users);
	    } catch (JwtTokenExpiredException ex) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Collections.singletonMap("error", "JWT Token has expired"));
	    }
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
	    try {
	        // Authenticate the user
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

	        if (authentication.isAuthenticated()) {
	            // If authentication is successful, generate tokens
	            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());

	            return ResponseEntity.ok(JwtResponse.builder()
	                    .accessToken(jwtService.generateToken(authRequest.getUsername()))
	                    .refreshTokenUid(refreshToken.getToken())
	                    .build());
	        }
	    } catch (DuplicateKeyException ex) {
	        // Handle duplicate key violation (for example, duplicate entry in refresh_table)
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                .body(Collections.singletonMap("error", "Duplicate key violation. User already authenticated."));
	    } catch (AuthenticationException ex) {
	        // Handle authentication failure
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
	                .body(Collections.singletonMap("error", "Invalid credentials"));
	    }

	    // Handle unexpected cases
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body(Collections.singletonMap("error", "Unexpected error during authentication"));
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
