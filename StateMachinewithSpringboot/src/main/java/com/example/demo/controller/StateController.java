package com.example.demo.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.enums.PaymentEvent;
import com.example.demo.enums.PaymentStatus;
import com.example.demo.model.PaymentState;
import com.example.demo.service.PaymentStateService;

@RestController
@RequestMapping("/api")
public class StateController {
	@Autowired
	private PaymentStateService stateService;
	@GetMapping("/welcome-state")
	public String welcome() {
		return "This is the state machine page.";
	}
	
	@PostMapping("/initialize-payment")
	public ResponseEntity<String> initializePayment(@RequestBody Map<String, Long> requestBody) {
		Long id = requestBody.get("id");
		System.out.println(id);
		return stateService.initializeState(id);
	}
//	@PostMapping("/update-payment-state")
//	public ResponseEntity<String> updatePaymentState(@RequestBody Map<String, Object> requestBody) {
//	    Long txnId = Long.valueOf(requestBody.get("txnId").toString());
//	    String action = (String) requestBody.get("action");
//
//	    // Determine the new state based on the action
//	    PaymentEvent newEvent;
//	      try{
//	    	  newEvent = PaymentEvent.valueOf(action);
//	          System.out.println(newEvent);
//	      } catch (Exception ex) {
//	    	  return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action: " + action);
//	      }
////	    switch (action) {
////	        case "action1":
////	            newState = PaymentStatus.INITIALIZED;
////	            break;
////	        case "action2":
////	            newState = PaymentStatus.PROCESSING;
////	            break;
////	        case "action3":
////	            newState = PaymentStatus.COMPLETED;
////	            break;
////	        case "action4":
////	            newState = PaymentStatus.FAILED;
////	            break;
////	        default:
////	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action: " + action);
////	    }
//
//	    return stateService.updateState(txnId, newEvent);
//	}
	@PostMapping("/update-payment-state")
	public ResponseEntity<String> updatePaymentState(@RequestBody Map<String, Object> requestBody) {
	    Long txnId = Long.valueOf(requestBody.get("txnId").toString());
	    String action = (String) requestBody.get("action");

	    PaymentEvent newEvent;
	    try {
	        newEvent = PaymentEvent.valueOf(action);
	    } catch (IllegalArgumentException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action: " + action);
	    }

	    return stateService.updateState(txnId, newEvent);
	}
	@GetMapping("/get-payment-state")
	public ResponseEntity<String> getPaymentStatus(@RequestBody Map<String, Long> requestBody) {
		Long txnId = Long.valueOf(requestBody.get("txnId").toString());
		return stateService.getStatus(txnId);
	}
	
}
