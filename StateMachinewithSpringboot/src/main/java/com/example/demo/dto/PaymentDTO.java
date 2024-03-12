package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class PaymentDTO {
	@NotEmpty(message = "Card Number is required")
	private String cardNumber;
	@NotEmpty(message = "Expiration Month is required")
	private String expirationMonth;
	@NotEmpty(message = "Expiration Year is required")
    private String expirationYear;
	@NotEmpty(message = "Cvv is required")
    private String cvv;
	@NotEmpty(message = "Card Holder Name is required")
    private String cardHolderName;
	@NotEmpty(message = "Card Holder Email is required")
    private String cardHolderEmail;
	@NotEmpty(message = "Vendor Name is required")
    private String vendorName; 
	@NotEmpty(message = "Current Balance is required")
    private String currentBalance;
	@NotNull(message = "Purchased Amount is required")
    private BigDecimal purchasedAmount;
	@NotEmpty(message = "User Name is required")
    private String userName;
    private String token;
    private boolean success;
}
