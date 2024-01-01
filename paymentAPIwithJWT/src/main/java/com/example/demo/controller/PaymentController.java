package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.GatewayDTO;
import com.example.demo.dto.PaymentDTO;
import com.example.demo.dto.StripeTokenDTO;
import com.example.demo.dto.VendorDTO;
import com.example.demo.enums.TransactionStatus;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.exception.GatewayValidationException;
import com.example.demo.model.AccountCustomer;
import com.example.demo.model.CreditCard;
import com.example.demo.model.Gateway;
import com.example.demo.model.Transaction;
import com.example.demo.model.TransactionKey;
import com.example.demo.model.Vendor;
import com.example.demo.repository.AccountCustomerRepository;
import com.example.demo.service.CreditCardService;
import com.example.demo.service.GatewayService;
import com.example.demo.service.PaymentGatewayService;
import com.example.demo.service.TransactionService;
import com.example.demo.service.VendorService;

import net.authorize.api.contract.v1.CreateCustomerPaymentProfileResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class PaymentController {
	@Value("${transaction.cutoff.time}")
	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	private String cutoffTime;
	@Value("${transaction.timezone}")
	private String timeZone;
	@Value("${stripe.api.key}")
	private String stripeApiKey;
	
	@Autowired
	private VendorService vendorService;
	@Autowired
	private GatewayService gatewayService;
	@Autowired
	private CreditCardService creditCardService;
	
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private AccountCustomerRepository customerRepository;
	
	@Autowired
    private HealthEndpoint healthEndpoint;
	
	 private final PaymentGatewayService authorizeNetPaymentService;
	 private final PaymentGatewayService stripePaymentService;
	 private StripeTokenDTO model;
	 
	 private final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	 public PaymentController(
	            @Qualifier("authorizeNetPaymentService") PaymentGatewayService authorizeNetPaymentService,
	            @Qualifier("stripePaymentService") PaymentGatewayService stripePaymentService) {
			this.authorizeNetPaymentService = authorizeNetPaymentService;
	        this.stripePaymentService = stripePaymentService;
	}
	 @PostConstruct
	    public void init(){

	        Stripe.apiKey = stripeApiKey;
	    }

	//save the vendor
	@PostMapping("/saveVendor")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public ResponseEntity<String> saveVendor(@RequestBody VendorDTO vendorDTO){
		Vendor vendor = new Vendor();
		vendor.setVendorName(vendorDTO.getVendorName());
		vendorService.saveVendor(vendor);
		
		return new ResponseEntity<>("Vendor has been created successfully!!", HttpStatus.CREATED);
	}
	//validate the gateway
		@PostMapping("/validateGateway")
		@PreAuthorize("hasAuthority('ROLE_ADMIN')")
		public ResponseEntity<String> validateGateway(@RequestBody GatewayDTO gatewayDTO){
			String vendorName = gatewayDTO.getVendorName();
			String gatewayID = gatewayDTO.getMerchantGatewayId();
			String merchantName = gatewayDTO.getMerchantName();
			System.out.println(vendorName.toLowerCase());
			
			logger.info("Received Gateway validation request. Vendor: {}, Gateway ID: {}, Merchant Name: {}", vendorName, gatewayID, merchantName);
			 
			try {
		        switch (vendorName.toLowerCase()) {
		            case "authorizenet":
		            	try {
		            		boolean isValidAuthorizeNet = authorizeNetPaymentService.isValidGateway(gatewayID);
			                logger.info("AuthorizeNet Gateway validation result: {}", isValidAuthorizeNet);
			                Gateway gateway = new Gateway();
		                	gateway.setMerchantName(merchantName);
		                	gateway.setMerchantGatewayId(gatewayID);
		                	gateway.setVendor(vendorService.findVendorByName(vendorName));
		                	
		                	Gateway savedGateway = gatewayService.saveGateway(gateway);
			                if (isValidAuthorizeNet && savedGateway != null) {
			                	logger.info("AuthorizeNet Gateway validation successful. Gateway saved.");
			                    return new ResponseEntity<>("Gateway validation successful and AuthorizeNet gateway saved", HttpStatus.CREATED);
			                } else {
			                    throw new GatewayValidationException("Invalid AuthorizeNet gateway. Already exists");
			                }
		            	}catch(GatewayValidationException e) {
		            		logger.error("Error during Gateway validation: {}", e.getMessage());
		    		        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
		            	}
		                
		            case "stripe":
		            	try {
		            		 boolean isValidStripe = stripePaymentService.isValidGateway(gatewayID);
				                logger.info("Stripe Gateway validation result: {}", isValidStripe);
				                Gateway gatewayStripe = new Gateway();
				                gatewayStripe.setMerchantName(merchantName);
				                gatewayStripe.setMerchantGatewayId(gatewayID);
				                gatewayStripe.setVendor(vendorService.findVendorByName(vendorName));
			                	
			                	Gateway savedGatewayStripe = gatewayService.saveGateway(gatewayStripe);
				                if(isValidStripe && savedGatewayStripe != null) {
				                	logger.info("Stripe Gateway validation successful. Gateway saved.");
				                	return new ResponseEntity<>("Gateway validation successful and Stripe gateway saved", HttpStatus.CREATED);
				                }else {
				                	return new ResponseEntity<>("Stripe Gateway already exists", HttpStatus.FORBIDDEN);
				                }
		            	}catch(GatewayValidationException e) {
		            		logger.error("Error during Gateway validation: {}", e.getMessage());
		    		        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		            	}
		               
		            default:
		            	logger.warn("No such gateway found");
		    			return new ResponseEntity<>("No such gateway found", HttpStatus.NOT_FOUND);
		        }
		    } catch (GatewayValidationException e) {
		    	logger.error("Error during Gateway validation: {}", e.getMessage());
		        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		    }
			
		}
		
		// make payment endpoint
		@PostMapping("/makePayment")
		public ResponseEntity<String> makePayment(@RequestBody PaymentDTO paymentDTO) {
		    try {
		    	String vendorName = paymentDTO.getVendorName();
			    String cardNumber = paymentDTO.getCardNumber();
			    String cardName = paymentDTO.getCardHolderName();
			    String cardHolderEmail = paymentDTO.getCardHolderEmail();
			    String expirationMonth = paymentDTO.getExpirationMonth();
			    String expirationYear = paymentDTO.getExpirationYear();
			    String currentBalance = paymentDTO.getCurrentBalance();
			    String cvv = paymentDTO.getCvv();
			    BigDecimal purchasedAmount = paymentDTO.getPurchasedAmount();
			    String userName = paymentDTO.getUserName();
			    System.out.println("User name calling:" + userName);
			    System.out.println("card number calling: " + cardNumber);
			    //get current date
			    LocalDate currentDate =  LocalDate.now();
			    int currentYear = currentDate.getYear();
			    int currentMonth = currentDate.getMonthValue();
			    
			    //parse string year and month
			    int parsedYear = Integer.parseInt(expirationYear);
			    int parsedMonth = Integer.parseInt(expirationMonth);
			    
			    //valid exp date
			    if(parsedYear > currentYear || (parsedYear == currentYear && parsedMonth >= currentMonth))
			    {
			    	System.out.println("Payment is valid");
			    	 if (creditCardService.isCreditCardNumberExistsForVendor(cardNumber, vendorName) && vendorService.findVendorByName(vendorName) != null) {
					        // Case: Credit card exists
					        System.out.println("Credit card exists");
					        return handleExistingCreditCardTransaction(cardNumber, purchasedAmount, userName, vendorName,cardName, cardHolderEmail,cvv,expirationMonth, expirationYear);
					    } else {
					        // Case: Save in credit card table
					        return handleNewCreditCardTransaction(cardNumber, cardName, cardHolderEmail, cvv, expirationMonth, expirationYear,
					                currentBalance, vendorName, purchasedAmount, userName);
					    }
			    //invalid exp date
			    }else {
			    	System.out.println("Payment is expired");
			    	return new ResponseEntity<>("Payment is expired", HttpStatus.BAD_REQUEST);
			    }
		    }catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("Error processing payment make.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
			
		}

		private ResponseEntity<String> handleExistingCreditCardTransaction(String cardNumber, BigDecimal purchasedAmount,
		                                                                  String userName, String vendorName,String cardName, String cardHolderEmail, String cvv,String expirationMonth,String expirationYear) {
		    try {
		    	
			    switch (vendorName.toLowerCase()) {
			        case "authorizenet":
			        	Optional<Transaction> latestTransaction = transactionService.findLatestTransactionByCardNumberAndVendorName(cardNumber, vendorName);
					    System.out.println(latestTransaction);
					    if (latestTransaction.isPresent()) {
					    	Transaction transaction = latestTransaction.get();
					    	String profileID = transaction.getCustomerProfileId();
						    String paymentProfileID = transaction.getCustomerPaymentProfileId();
						    System.out.println(profileID);
						    System.out.println(paymentProfileID);
						    return authorizeNetPayment(profileID, paymentProfileID, purchasedAmount, cardNumber, cardName, cardHolderEmail, userName, vendorName);
					    }else {
			                // Handle the case when there is no latest transaction
			                return new ResponseEntity<>("No latest transaction found for the specified card and vendor.", HttpStatus.NOT_FOUND);
			            }
			            
			        case "stripe":
			        	return stripePayment(purchasedAmount,cardName,cardHolderEmail,userName,cardNumber, cvv,expirationMonth, expirationYear,vendorName);
			        default:
			            return new ResponseEntity<>("Unsupported vendor: " + vendorName, HttpStatus.BAD_REQUEST);
			    }
		    }catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("Error processing existing card.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
			
		}

		private ResponseEntity<String> handleNewCreditCardTransaction(String cardNumber, String cardName,
		                                                              String cardHolderEmail, String cvv,
		                                                              String expirationMonth, String expirationYear,
		                                                              String currentBalance, String vendorName,
		                                                              BigDecimal purchasedAmount, String userName) {
		    // Save in credit card table
			try {
					AccountCustomer userInfo = customerRepository.findByName(userName)
			            .orElseThrow(() -> new EntityNotFoundException("User not found with name: " + userName));
			        switch (vendorName.toLowerCase()) {
			            case "authorizenet":
			            	
			            	String profileID = authorizeNetPaymentService.getCustomerProfileId(cardName, cardHolderEmail);
						    
						    CreateCustomerPaymentProfileResponse paymentProfileResponse = authorizeNetPaymentService.getCustomerPaymentProfileId(cardNumber,
				                    profileID, expirationMonth, expirationYear, cvv);
						    
			                String customerPaymentProfileId = paymentProfileResponse.getCustomerPaymentProfileId();
			                System.out.println("I am from profile: " + profileID);
			               if (profileID != null && paymentProfileResponse != null) {
			            	   creditCardService.saveCreditCardInfo(buildCreditCard(cardNumber, cardName,
							            cardHolderEmail, cvv, expirationMonth, expirationYear, currentBalance, vendorName, userInfo, userName));
			               }else {
						        return new ResponseEntity<>("Error saving credit card information. Please check your information carefully. ", HttpStatus.BAD_REQUEST);
						    }
			                System.out.println("I am from paymentProfile: " + customerPaymentProfileId);
			                return authorizeNetPayment(profileID, customerPaymentProfileId, purchasedAmount, cardNumber, cardName, cardHolderEmail, userName, vendorName);
			            case "stripe":
			            	creditCardService.saveCreditCardInfo(buildCreditCard(cardNumber, cardName,
						            cardHolderEmail, cvv, expirationMonth, expirationYear, currentBalance, vendorName, userInfo, userName));
			            	return stripePayment(purchasedAmount,cardName,cardHolderEmail,userName,cardNumber, cvv,expirationMonth, expirationYear,vendorName);
			            default:
			                return new ResponseEntity<>("Unsupported vendor: " + vendorName, HttpStatus.BAD_REQUEST);
			        }
			}catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("Error processing new credit card", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
			
			
		}

		private ResponseEntity<String> authorizeNetPayment(String profileID, String paymentProfileID, BigDecimal purchasedAmount,
		                                                   String cardNumber, String cardName, String cardHolderEmail, String userName, String vendorName) {
		    try {
		    	String transactionIdFromAuthorize = authorizeNetPaymentService.makePayment(profileID, paymentProfileID, purchasedAmount);
			    
			    Transaction transaction = new Transaction();
			    transaction.setCardNumber(cardNumber);
			    transaction.setCardHolderName(cardName);
			    transaction.setCardHolderEmail(cardHolderEmail);
			    transaction.setAmount(purchasedAmount);
			    transaction.setCustomerProfileId(profileID);
			    transaction.setCustomerPaymentProfileId(paymentProfileID);
			    transaction.setTransactionStatus(TransactionStatus.PENDING);
			    transaction.setTransactionId(transactionIdFromAuthorize);
			    transaction.setUserName(userName);
			    transaction.setTransactionDate(LocalDateTime.now());
			    transaction.setVendorName(vendorName.toLowerCase());		    
			    Transaction transactionDone = transactionService.saveTransaction(transaction);

			    if (transactionDone != null) {
			        return new ResponseEntity<>("Credit Card Info Saved successfully for AuthorizeNet. Transaction waiting for approval.", HttpStatus.CREATED);
			    } else {
			        return new ResponseEntity<>("There was an error in saving transaction data for " + userName + ". Please try again!",
			                HttpStatus.BAD_REQUEST);
			    }
		    }catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseEntity<>("Error processing payment via Authorize.Net.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
			
		}
		public boolean doesMessageContainAlphanumeric(String message) {
	        // Use regular expression to check if the message contains alphanumeric characters
	        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+");
	        Matcher matcher = pattern.matcher(message);

	        return matcher.find();
	}
		private ResponseEntity<String> stripePayment(BigDecimal purchasedAmount,
				String cardName,String cardHolderEmail,String userName,String cardNumber,String  cvv,String expirationMonth,String  expirationYear,String vendorName) throws StripeException, InterruptedException{
		        String message = stripePaymentService.makePayment(null, null, purchasedAmount);
		        System.out.println(cardNumber);
		        System.out.println("I am Stripe user: "+ userName);
		        if(message == "Further action is needed. Redirect the customer to the hosted payment page.") {
		        	return new ResponseEntity<>("Further action is needed. Redirect the customer to the hosted payment page.", HttpStatus.OK);
		        }else if(doesMessageContainAlphanumeric(message)){
		        	
		        	Transaction transaction = new Transaction();
				    transaction.setCardNumber((String)(cardNumber));
				    transaction.setCardHolderName((String)cardName);
				    transaction.setCardHolderEmail((String)cardHolderEmail);
				    transaction.setAmount(purchasedAmount);
				    transaction.setCustomerProfileId(null);
				    transaction.setCustomerPaymentProfileId(null);
				    transaction.setTransactionStatus(TransactionStatus.SUCCESS);
				    transaction.setTransactionId(message);
				    transaction.setUserName((String)userName);
				    transaction.setTransactionDate(LocalDateTime.now());
				    transaction.setVendorName(vendorName);				    
				    Transaction transactionDone = transactionService.saveTransaction(transaction);

				    if (transactionDone != null) {
				        return new ResponseEntity<>("Credit Card Info Saved successfully for Stripe. Transaction succeeded.", HttpStatus.CREATED);
				    } else {
				        return new ResponseEntity<>("There was an error in saving transaction data for " + userName + ". Please try again!",
				                HttpStatus.BAD_REQUEST);
				    }
		        	
		        }else if(message.contains("Payment failed! Please check your payment details and try again.")){
		        	return new ResponseEntity<>("Payment failed! Please check your payment details and try again.", HttpStatus.BAD_REQUEST);
		        }else {
		        	return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		        }
		       
			
		}
		private CreditCard buildCreditCard(String cardNumber, String cardName, String cardHolderEmail, String cvv,
		                                   String expirationMonth, String expirationYear, String currentBalance,
		                                   String vendorName, AccountCustomer userInfo, String userName) {
		    CreditCard creditCard = new CreditCard();
		    creditCard.setCardNumber(cardNumber);
		    creditCard.setCardHolderName(cardName);
		    creditCard.setCardHolderEmail(cardHolderEmail);
		    creditCard.setCvv(cvv);
		    creditCard.setExpirationMonth(expirationMonth);
		    creditCard.setExpirationYear(expirationYear);
		    creditCard.setCurrentBalance(currentBalance);
		    creditCard.setVendorName(vendorName);
		    creditCard.setUserInfo(userInfo);
		    creditCard.setUserName(userName);
		    return creditCard;
		}
		@PostMapping("/voidPayment")
		@PreAuthorize("hasAuthority('ROLE_ADMIN')")
		public ResponseEntity<String> voidPayment(@RequestBody Map<String, Object> requestBodyMap) {
			try {
	            // Extract the transactionID from the request body
	            String transactionID = (String) requestBodyMap.get("transactionID");
	            System.out.println(transactionID);

	            // Call the service method to void the payment
	            CreateTransactionResponse response = authorizeNetPaymentService.voidPayment(transactionID);

	            if (response != null && response.getMessages().getResultCode() == MessageTypeEnum.OK) {
	                TransactionResponse result = response.getTransactionResponse();
	                // Void payment successful
	                if (result.getMessages() != null) {
	                    System.out.println("Successfully voided transaction with Transaction ID: " + result.getTransId());
	                    System.out.println("Response Code: " + result.getResponseCode());
	                    System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
	                    System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
	                    System.out.println("Auth Code: " + result.getAuthCode());

	                    // Update the transaction status to VOIDED
	                    transactionService.updateTransactionStatus(transactionID, TransactionStatus.VOIDED);

	                    return new ResponseEntity<>("Transaction status updated to VOIDED successfully.", HttpStatus.OK);
	                } else {
	                    System.out.println("Failed Transaction");
	                    return new ResponseEntity<>("Failed to void transaction. Please check the response.", HttpStatus.BAD_REQUEST);
	                }
	            } else {
	                System.out.println("Failed Transaction");
	                return new ResponseEntity<>("Failed to void transaction. Please check the response.", HttpStatus.BAD_REQUEST);
	            }
	        } catch (Exception e) {
	            // Log the exception for further investigation
	            e.printStackTrace();
	            return new ResponseEntity<>("Error voiding payment. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
		}
		@PostMapping("/settlePayment")
//		@PreAuthorize("hasAuthority('ROLE_ADMIN')")
		public ResponseEntity<String>settlePayment(){
			try {
				// Get the current date and time in the default timezone
		        ZonedDateTime currentDateTime = ZonedDateTime.now();
		        // Get the timezone ID
		        ZoneId currentZone = currentDateTime.getZone();
		        int currentHour = currentDateTime.getHour();
		        System.out.println("Current Timezone: " + currentZone);
		        System.out.println("Current Time:  " + currentDateTime);
		        System.out.println("Current Hour: " + currentHour);   
		        // Parse cutoff time and create a LocalTime object
		        LocalTime transactioncutoffTime = LocalTime.parse(cutoffTime, DateTimeFormatter.ofPattern("HH:mm"));
		        System.out.println(transactioncutoffTime);
		     // Get the current date
		        LocalDate currentDate = LocalDate.now();
		        
		        ZonedDateTime cutoffTimeInPST = ZonedDateTime.of(
		        		currentDate.minusDays(1),
		                transactioncutoffTime,
		                ZoneId.of(timeZone));
		     // Get the current system time zone
		        ZoneId systemTimeZone = ZoneId.systemDefault();
		        ZonedDateTime cutoffTimeInLocal = cutoffTimeInPST.withZoneSameInstant(systemTimeZone);
		        int cutOffTimeInLocalHour= cutoffTimeInLocal.getHour();
		        System.out.println("cutoff time lin local hour:" + cutOffTimeInLocalHour);
		        if(currentHour < (cutOffTimeInLocalHour + 5)) {
		        	String message = "Captured payment not yet settled. Try again after " + (cutOffTimeInLocalHour + 5) + ".";
		            System.out.println(message);
		            return new ResponseEntity<>(message, HttpStatus.OK);
//		        	authorizeNetPaymentService.settleBatchStatusDaily(timeZone, cutoffTime);
		        }else {
		        	System.out.println("Moving on to batch testing!!");
		        	boolean settled = authorizeNetPaymentService.settleBatchStatusDaily(timeZone, cutoffTime);
		        	 if (settled) {
		        		 LocalDate yesterday = LocalDate.now().minusDays(1);
		                 LocalDate today = LocalDate.now();
		                 LocalTime cutOffTimeInLocalTime = cutoffTimeInLocal.toLocalTime();
		                 LocalDateTime yesterdayCutoff = LocalDateTime.of(yesterday, cutOffTimeInLocalTime);
		                 LocalDateTime todayCutoff = LocalDateTime.of(today, cutOffTimeInLocalTime);
		                 System.out.println("Yesterdays date: " + yesterdayCutoff);
		                 System.out.println("todays date:" + todayCutoff);
		                 transactionService.findTransactionsToUpdateStatusSuccess(yesterdayCutoff, todayCutoff);
		                 return new ResponseEntity<>("Batch settled successfully.", HttpStatus.OK);
		             } else {
		                 return new ResponseEntity<>("Failed to settle batch. Please check the logs for details.", HttpStatus.INTERNAL_SERVER_ERROR);
		             }
		        }
			}catch (Exception e) {
		        e.printStackTrace();
		        return new ResponseEntity<>("Error settling payment. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
		    }
		}
		@GetMapping("/health")
	    public ResponseEntity<String> healthCheck() {
			HealthComponent health = healthEndpoint.health();
		    String healthStatus = "Health Check: " + health.getStatus();
	        return ResponseEntity.ok(healthStatus);
	    }
}
