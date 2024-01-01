package com.example.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import net.authorize.Environment;

import net.authorize.api.contract.v1.ArrayOfBatchDetailsType;
import net.authorize.api.contract.v1.AuthenticateTestRequest;
import net.authorize.api.contract.v1.BatchDetailsType;
import net.authorize.api.contract.v1.CreateCustomerPaymentProfileRequest;
import net.authorize.api.contract.v1.CreateCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.CreateCustomerProfileRequest;
import net.authorize.api.contract.v1.CreateCustomerProfileResponse;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CreditCardType;
import net.authorize.api.contract.v1.CustomerPaymentProfileType;
import net.authorize.api.contract.v1.CustomerProfilePaymentType;
import net.authorize.api.contract.v1.CustomerProfileType;
import net.authorize.api.contract.v1.GetMerchantDetailsRequest;
import net.authorize.api.contract.v1.GetMerchantDetailsResponse;
import net.authorize.api.contract.v1.GetSettledBatchListRequest;
import net.authorize.api.contract.v1.GetSettledBatchListResponse;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.PaymentProfile;
import net.authorize.api.contract.v1.PaymentType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.contract.v1.ValidationModeEnum;
import net.authorize.api.controller.CreateCustomerPaymentProfileController;
import net.authorize.api.controller.CreateCustomerProfileController;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.GetMerchantDetailsController;
import net.authorize.api.controller.GetSettledBatchListController;
import net.authorize.api.controller.base.ApiOperationBase;

@Service
public class AuthorizeNetPaymentService implements PaymentGatewayService{
	
	@Value("${authorize.net.api.login.id}")
    private String apiLoginId;

    @Value("${authorize.net.transaction.key}")
    private String transactionKey;
    
	

	@Override
	public String makePayment(String customerProfileIdAuthorize,String customerPaymentProfileIdAuthorize,BigDecimal purchasedAmount) {
		System.out.println("This is customerProfileIdAuthorize " + customerProfileIdAuthorize);
		System.out.println("This is customerPaymentProfileIdAuthorize " + customerPaymentProfileIdAuthorize);
		System.out.println(purchasedAmount);
		setupEnvironment();
		ApiOperationBase.setMerchantAuthentication(getAuth());
		
		CustomerProfilePaymentType profileToCharge = new CustomerProfilePaymentType();
		profileToCharge.setCustomerProfileId(customerProfileIdAuthorize);
		 
		PaymentProfile paymentProfile = new PaymentProfile();
	    paymentProfile.setPaymentProfileId(customerPaymentProfileIdAuthorize);
	    profileToCharge.setPaymentProfile(paymentProfile);
	    System.out.println(paymentProfile);
	 // Create the payment transaction request
	    TransactionRequestType txnRequest = new TransactionRequestType();
	    txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
	    txnRequest.setProfile(profileToCharge);
	    txnRequest.setAmount(purchasedAmount.setScale(2, RoundingMode.CEILING));
		
	    CreateTransactionRequest apiRequest = new CreateTransactionRequest();
	    apiRequest.setTransactionRequest(txnRequest);
	    CreateTransactionController controller = new CreateTransactionController(apiRequest);
	    controller.execute();
	    
	    System.out.println(controller.getApiResponse());
	    
	    CreateTransactionResponse response = controller.getApiResponse();
	    System.out.println(response.getTransactionResponse().getTransId());
	    System.out.println(response.getTransactionResponse().getResponseCode());
	    System.out.println(response.getTransactionResponse().getRawResponseCode());
//	    System.out.println(response.getTransactionResponse().getTransId());
	    
	    if(response != null)
	    	{
	    		return response.getTransactionResponse().getTransId();
	    	}
	    
	    return "There was an issue with payment. Payment failed!!";
	}
	
	//void payment
	@Override
	public CreateTransactionResponse voidPayment(String transactionID) {
		setupEnvironment();
		
		//merchant auth
		ApiOperationBase.setMerchantAuthentication(getAuth());
		
		//create payment transaction request
		TransactionRequestType txnRequest = new TransactionRequestType();
		txnRequest.setTransactionType(TransactionTypeEnum.VOID_TRANSACTION.value());
		txnRequest.setRefTransId(transactionID);
		
		//Make the API request
		CreateTransactionRequest apiRequest = new CreateTransactionRequest();
		apiRequest.setTransactionRequest(txnRequest);
		CreateTransactionController controller = new CreateTransactionController(apiRequest);
		controller.execute();
		
		
		CreateTransactionResponse response = controller.getApiResponse();
		return response;
	}

	@Override
	public void refundPayment(String transactionID, BigDecimal amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValidGateway(String gatewayID) {
		System.out.println(gatewayID);
		setupEnvironment();
		AuthenticateTestRequest apiRequest = new AuthenticateTestRequest();
        apiRequest.setMerchantAuthentication(getAuth());
        System.out.println(apiRequest.getMerchantAuthentication());
        GetMerchantDetailsRequest getRequest = new GetMerchantDetailsRequest();
        getRequest.setMerchantAuthentication(apiRequest.getMerchantAuthentication());
       
        GetMerchantDetailsController controller = new GetMerchantDetailsController(getRequest);
        controller.execute();
        GetMerchantDetailsResponse getResponse = controller.getApiResponse();
        
        System.out.println(getResponse.getMerchantName());
        System.out.println(getResponse.getGatewayId());
        return getResponse != null && getResponse.getMerchantName() != null && gatewayID.equals(getResponse.getGatewayId());
	}
	private MerchantAuthenticationType getAuth() {

		
	    MerchantAuthenticationType merchantAuthenticationType = new MerchantAuthenticationType();
	    merchantAuthenticationType.setName(apiLoginId);
	    merchantAuthenticationType.setTransactionKey(transactionKey);
	    return merchantAuthenticationType;
	}

	@Override
	public String getCustomerProfileId(String cardName, String cardEmail) {
		System.out.println(cardName);
		
      try {
    	// Set customer profile data
          CustomerProfileType customerProfileType = new CustomerProfileType();
          customerProfileType.setMerchantCustomerId(cardName);
          customerProfileType.setEmail(cardEmail);
          
       // Create the API request and set the parameters for this specific request
          CreateCustomerProfileRequest apiRequest = new CreateCustomerProfileRequest();
          apiRequest.setMerchantAuthentication(getAuth());
          apiRequest.setProfile(customerProfileType);
          
       // Call the controller
          CreateCustomerProfileController controller = new CreateCustomerProfileController(apiRequest);
          controller.execute(Environment.SANDBOX);

       // Get the response
          CreateCustomerProfileResponse response;
          response = controller.getApiResponse();
          
          // Parse the response to determine results
          System.out.println(response);
          if (response != null && response.getMessages().getResultCode() == MessageTypeEnum.ERROR) {
        	  System.out.println("Getting error message");
              return response.getMessages().getMessage().get(0).getText();
          } else {
        	  System.out.println("I am getCustomerProfileId: "+ response.getCustomerProfileId());
              return response.getCustomerProfileId();
          }
      }catch (Exception e) {
          e.printStackTrace();
          return "Error getting customer profile ID: " + e.getMessage();
      }
	}

	@Override
	public CreateCustomerPaymentProfileResponse getCustomerPaymentProfileId(String cardNumber, String customerId,
            String expirationMonth, String expirationYear,
            String cardCode) {
		try {
			// Create the API request and set the parameters for adding a payment profile
			CreateCustomerPaymentProfileRequest apiRequest = new CreateCustomerPaymentProfileRequest();
			apiRequest.setMerchantAuthentication(getAuth());
			apiRequest.setCustomerProfileId(customerId);
			apiRequest.setValidationMode(ValidationModeEnum.TEST_MODE);
			
			// Set credit card details
			CreditCardType creditCard = new CreditCardType();
			creditCard.setCardNumber(cardNumber);
			creditCard.setExpirationDate(expirationYear.substring(2) + "-" + expirationMonth);
			creditCard.setCardCode(cardCode);
			
			CustomerPaymentProfileType profile = new CustomerPaymentProfileType();
			PaymentType paymentType = new PaymentType();
			paymentType.setCreditCard(creditCard);
			profile.setPayment(paymentType);
			
			apiRequest.setPaymentProfile(profile);
			
			CreateCustomerPaymentProfileController controller = new CreateCustomerPaymentProfileController(apiRequest);
			controller.execute(Environment.SANDBOX);
			CreateCustomerPaymentProfileResponse response;
			response = controller.getApiResponse();
			System.out.println("I am response from paymentProfile: " + response);
			System.out.println("I am from paymentProfileAfter" + response.getCustomerPaymentProfileId());
			if (response != null && response.getMessages().getResultCode() == MessageTypeEnum.ERROR) {
				// Handle error case
				// You might want to throw an exception or handle it accordingly
				throw new RuntimeException("Error getting customer payment profile ID: " +
				response.getMessages().getMessage().get(0).getText());
			} else {
				System.out.println("I am getCustomerPaymentProfileId: " + response.getCustomerPaymentProfileId());
				return response;
			}
		} catch (Exception e) {
		// Handle any exceptions that might occur during processing
		e.printStackTrace();
		throw new RuntimeException("Error getting customer payment profile ID: " + e.getMessage());
		}
	}
	private void setupEnvironment() {
        ApiOperationBase.setEnvironment(Environment.SANDBOX);
    }
	@Override
	public boolean settleBatchStatusDaily(String timeZone, String cutoffTime) {
		setupEnvironment();
		
		ApiOperationBase.setMerchantAuthentication(getAuth());
		GetSettledBatchListRequest getRequest = new GetSettledBatchListRequest();
        getRequest.setMerchantAuthentication(getAuth());
        try {
        	 // Set current date and time in PST
            TimeZone timeZoneTrans = TimeZone.getTimeZone("America/Los_Angeles");
            GregorianCalendar pstCalendar = new GregorianCalendar(timeZoneTrans);
            int pstHour = pstCalendar.get(Calendar.HOUR_OF_DAY);

            // Parse cutoff time and create a LocalTime object
            LocalTime transactionCutoffTime = LocalTime.parse(cutoffTime, DateTimeFormatter.ofPattern("HH:mm"));
            int cutoffHour = transactionCutoffTime.getHour();

            System.out.println("PST Hour: " + pstHour);
            System.out.println("Cutoff Hour: " + cutoffHour);

            // Set first and last settlement dates based on the comparison
            if (pstHour >= cutoffHour) {
                // Set first settlement date to 1 day ago at 16:00 PST
                pstCalendar.add(Calendar.DAY_OF_YEAR, -1);
                pstCalendar.set(Calendar.HOUR_OF_DAY, 16);
                pstCalendar.set(Calendar.MINUTE, 0);
                pstCalendar.set(Calendar.SECOND, 0);
                getRequest.setFirstSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(pstCalendar));
                System.out.println(pstCalendar);

                // Set last settlement date to the current date at 16:00 PST
                GregorianCalendar currentDate = new GregorianCalendar(timeZoneTrans);
                currentDate.set(Calendar.HOUR_OF_DAY, 16);
                currentDate.set(Calendar.MINUTE, 0);
                currentDate.set(Calendar.SECOND, 0);
                System.out.println(currentDate);
                getRequest.setLastSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(currentDate));
            } else {
                // Set first settlement date to 2 days ago at 16:00 PST
                GregorianCalendar pstCalendarPrev = new GregorianCalendar(timeZoneTrans);
                pstCalendarPrev.add(Calendar.DAY_OF_YEAR, -2);
                pstCalendarPrev.set(Calendar.HOUR_OF_DAY, 16);
                pstCalendarPrev.set(Calendar.MINUTE, 0);
                pstCalendarPrev.set(Calendar.SECOND, 0);
                System.out.println(pstCalendarPrev);
                getRequest.setFirstSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(pstCalendarPrev));

                // Set last settlement date to 1 day ago at 16:00 PST
                GregorianCalendar pstCalendarLast = new GregorianCalendar(timeZoneTrans);
                pstCalendarLast.add(Calendar.DAY_OF_YEAR, -1);
                pstCalendarLast.set(Calendar.HOUR_OF_DAY, 16);
                pstCalendarLast.set(Calendar.MINUTE, 0);
                pstCalendarLast.set(Calendar.SECOND, 0);
                System.out.println(pstCalendarLast);
                getRequest.setLastSettlementDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(pstCalendarLast));
            }
        } catch (Exception ex) {
            System.out.println("Error : while setting dates");
            ex.printStackTrace();
        }
        GetSettledBatchListController controller = new GetSettledBatchListController(getRequest);
        controller.execute();
        GetSettledBatchListResponse getResponse = controller.getApiResponse();
        System.out.println(getResponse);
        if (getResponse != null) {

            if (getResponse.getMessages().getResultCode() == MessageTypeEnum.OK) {

                System.out.println(getResponse.getMessages().getMessage().get(0).getCode());
                System.out.println(getResponse.getMessages().getMessage().get(0).getText());

                ArrayOfBatchDetailsType batchList = getResponse.getBatchList();
                if (batchList != null) {
                    System.out.println("List of Settled Transaction :");
                    for (BatchDetailsType batch : batchList.getBatch()) {
                        System.out.println(batch.getBatchId() + " - " + batch.getMarketType() + " - " + batch.getPaymentMethod() + " - " + batch.getProduct() + " - " + batch.getSettlementState()+ " - " + batch.getStatistics() + " - " + batch.getSettlementTimeLocal() + " - " + batch.getSettlementTimeUTC());
                    }
                }
            } else {
                System.out.println("Failed to get settled batch list:  " + getResponse.getMessages().getResultCode());
                System.out.println(getResponse.getMessages().getMessage().get(0).getText());
                return false;
        	}
           }
        return true;
        }
        

}
