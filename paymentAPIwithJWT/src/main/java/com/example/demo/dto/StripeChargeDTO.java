package com.example.demo.dto;

import lombok.Data;

@Data
public class StripeChargeDTO {
	private String stripeToken;
	private String username;
	private String amount;
	private boolean success;
	private String message;
	private String chargeId;
}
