package com.example.demo.exceptionHandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.example.demo.exception.DuplicateCustomerException;
import com.example.demo.exception.DuplicateVendorException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.exception.GatewayValidationException;
import com.example.demo.exception.JwtTokenExpiredException;
import com.example.demo.exception.RefreshTokenExpiredException;

import io.jsonwebtoken.ExpiredJwtException;
@ControllerAdvice(basePackages = "com.example")
public class CustomExceptionHandler extends ResponseEntityExceptionHandler{
	@ExceptionHandler(DuplicateCustomerException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateCustomerException(DuplicateCustomerException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
	
	 @ExceptionHandler(JwtTokenExpiredException.class)
	    public ResponseEntity<Map<String, String>> handleJwtTokenExpiredException(JwtTokenExpiredException ex) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	 }
	 @ExceptionHandler(DuplicateVendorException.class)
	    public ResponseEntity<Map<String, String>> handleDuplicateVendorException(DuplicateVendorException ex) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	    }
	 @ExceptionHandler(GatewayValidationException.class)
	    public ResponseEntity<String> handleGatewayValidationException(GatewayValidationException ex) {
	        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	 }
	 @ExceptionHandler(RefreshTokenExpiredException.class)
	    public ResponseEntity<Map<String, String>> handleRefreshTokenExpiredException(RefreshTokenExpiredException ex) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	    }
	 
	 @ExceptionHandler(EntityNotFoundException.class)
	    public ResponseEntity<Map<String, String>> handleEntityNotFoundException(EntityNotFoundException ex) {
	        Map<String, String> errorResponse = new HashMap<>();
	        errorResponse.put("error", ex.getMessage());
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	    }
	
}
