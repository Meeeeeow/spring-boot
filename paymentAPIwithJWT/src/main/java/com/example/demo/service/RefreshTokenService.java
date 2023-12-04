package com.example.demo.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.RefreshToken;
import com.example.demo.repository.AccountCustomerRepository;
import com.example.demo.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	
	@Autowired
	private AccountCustomerRepository customerRepo;
	
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
	
	public RefreshToken createRefreshToken(String username) {
		RefreshToken refreshToken = RefreshToken.builder()
				.userInfo(customerRepo.findByName(username).get())
				.token(UUID.randomUUID().toString())
				.expiryDate(Instant.now().plusMillis(600000))
				.build();
		return refreshTokenRepository.save(refreshToken);
	}

	public Optional<RefreshToken> findByToken(String token) {
		return refreshTokenRepository.findByToken(token);
	}
	
	 public RefreshToken verifyExpiration(RefreshToken token) {
	        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
	            refreshTokenRepository.delete(token);
	            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
	        }
	        return token;
	    }
}
