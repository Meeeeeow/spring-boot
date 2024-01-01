package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class PaymentDTO {
	private String cardNumber;
	private String expirationMonth;
    private String expirationYear;
    private String cvv;
    private String cardHolderName;
    private String cardHolderEmail;
    private String vendorName; 
    private String currentBalance;
    private BigDecimal purchasedAmount;
    private String userName;
    private String token;
    private boolean success;
}
