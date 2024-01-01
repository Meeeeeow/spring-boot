package com.example.demo.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.demo.dto.PaymentDTO;
import com.stripe.exception.StripeException;

import net.authorize.api.contract.v1.CreateCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;


public interface PaymentGatewayService {
	String makePayment(String customerProfileIdAuthorize,String customerPaymentProfileIdAuthorize,BigDecimal purchasedAmount) throws StripeException, InterruptedException;
	
	CreateTransactionResponse voidPayment(String transactionID);
	
	void refundPayment(String transactionID, BigDecimal amount);
	
//	boolean isVendorUp(Vendor vendor);
//	boolean validateGatway();
	
	boolean isValidGateway(String gatewayID);

	String getCustomerProfileId(String cardName, String cardEmail);
	CreateCustomerPaymentProfileResponse getCustomerPaymentProfileId(String cardNumber, String customerId,String expirationMonth, String expirationYear, String cardCode);
	boolean settleBatchStatusDaily(String timeZone, String cutoffTime);
}
