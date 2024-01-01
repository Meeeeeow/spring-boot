package com.example.demo.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerListParams;
import com.stripe.param.PaymentIntentCreateParams;

import net.authorize.api.contract.v1.CreateCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
@Service
public class StripePaymentService implements PaymentGatewayService{
	@Value("${stripe.api.key}")
	private String stripeApiKey;
	

	@Override
	public String makePayment(String customerProfileId,String customerPaymentProfileId,BigDecimal purchasedAmount) throws StripeException, InterruptedException{
		try {
			long amountInCents = purchasedAmount.setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).longValue();
			System.out.println(amountInCents);
		
			String returnUrl = "https://yourwebsite.com/checkout/success";
	        
	        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
	                .setAmount(amountInCents)
	                .setCurrency("USD")
	                .setPaymentMethod("pm_card_visa")
	                .setConfirmationMethod(PaymentIntentCreateParams.ConfirmationMethod.MANUAL) 
	                .setConfirm(true)
	                .setReturnUrl(returnUrl)  // Specify the return URL
	                .build();

	        System.out.println(params);
	        PaymentIntent paymentIntent = PaymentIntent.create(params);

	        // Introduce a delay (e.g., 5 seconds)
	        Thread.sleep(5000);
	        System.out.println(paymentIntent.getStatus());
	        if ("requires_action".equals(paymentIntent.getStatus())) {
	            // Confirm the PaymentIntent after the delay
	            PaymentIntent confirmedPaymentIntent = paymentIntent.confirm();
	            System.out.println(confirmedPaymentIntent);
	            // Check the status of the PaymentIntent after confirmation
	            if ("requires_action".equals(confirmedPaymentIntent.getStatus())) {
	                // Further action is needed, redirect the customer to the hosted payment page
	                return "Further action is needed. Redirect the customer to the hosted payment page.";
	            } else if ("succeeded".equals(confirmedPaymentIntent.getStatus())) {
	                // Payment succeeded, show success message
	                System.out.println("Payment succeeded! PaymentIntent ID: " + confirmedPaymentIntent.getId());
	                return confirmedPaymentIntent.getId();
	            } else {
	                // Payment failed, show appropriate error message
	                System.out.println("Payment failed! PaymentIntent status: " + confirmedPaymentIntent.getStatus());
	                return confirmedPaymentIntent.getStatus();
	            }
	        } else if ("succeeded".equals(paymentIntent.getStatus())) {
	            // Payment has already succeeded, show success message
	            System.out.println("Payment process has succeeded! PaymentIntent ID: " + paymentIntent.getId());
	            return paymentIntent.getId();
	        } else {
	            // Payment failed, show appropriate error message
	            System.out.println("Payment failed! PaymentIntent status: " + paymentIntent.getStatus());
	            return "Payment failed! Please check your payment details and try again.";
	        }
		}catch (StripeException e) {
	        // Handle StripeException appropriately
	        e.printStackTrace();
	        return "Payment failed! An error occurred: " + e.getMessage();
	    } catch (InterruptedException e) {
	        // Handle InterruptedException
	        e.printStackTrace();
	        return "Payment failed! An error occurred: " + e.getMessage();
	    }
		
	}

	@Override
	public CreateTransactionResponse voidPayment(String transactionID) {
		return null;
		
	}

	@Override
	public void refundPayment(String transactionID, BigDecimal amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValidGateway(String gatewayID) {
		// TODO Auto-generated method stub
		System.out.println("Stripe validation");
		try {
	        // With connect and API key set globally
	        RequestOptions requestOptions = RequestOptions.builder()
	          .setStripeAccount(gatewayID)
	          .setApiKey(stripeApiKey)
	          .build();
	        // CustomerListParams params = CustomerListParams.builder().build();
	        // CustomerCollection customers = Customer.list(params, requestOptions);
	       System.out.println(requestOptions.getStripeAccount());
	       System.out.println(requestOptions.hashCode());
	       CustomerListParams params = CustomerListParams.builder().build();
	       CustomerCollection customers = Customer.list(params, requestOptions);
	       System.out.println(customers);
	      } catch(Exception e) {
	        System.out.println(e);
	        return false;
	      }
		return true;
	}

	@Override
	public String getCustomerProfileId(String cardName, String cardEmail) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CreateCustomerPaymentProfileResponse getCustomerPaymentProfileId(String cardNumber, String customerId,String expirationMonth, String expirationYear, String cardCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean settleBatchStatusDaily(String timeZone, String cutoffTime) {
		// TODO Auto-generated method stub
		return true;
		
	}

}
