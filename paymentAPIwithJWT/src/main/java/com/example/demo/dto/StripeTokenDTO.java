package com.example.demo.dto;

import lombok.Data;

@Data
public class StripeTokenDTO {
	private String cardNumber;
	private String expirationMonth;
	private String expirationYear;
	private String cvv;
	private String token;
	private boolean success;
}
