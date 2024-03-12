package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;

import com.example.demo.enums.PaymentStatus;
import com.example.demo.enums.PaymentEvent;
import com.example.demo.model.PaymentState;
import com.example.demo.repository.PaymentStateRepository;


@Service
public class PaymentStateService {
	@Autowired
	private PaymentStateRepository stateRepository;
	
	@Autowired
    private StateMachineFactory<PaymentStatus, PaymentEvent> stateMachineFactory;
	
	public ResponseEntity<String> initializeState(Long txnId) {
        try {
            PaymentState statePayment = new PaymentState();
            statePayment.setTxnId(txnId);
            statePayment.setStateName(PaymentStatus.INITIALIZED);
            PaymentState savedStatePayment = stateRepository.save(statePayment);
            
            // Construct response message
            String responseMessage = "Id is: "+ savedStatePayment.getTxnId() + " and Updated State is: " + savedStatePayment.getStateName();
            
            return ResponseEntity.ok(responseMessage);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: txn_id already exists");
        }
    }
	
	public ResponseEntity<String> updateState(Long txnId, PaymentEvent newEvent) {
	    Optional<PaymentState> optionalState = stateRepository.findByTxnId(txnId);
	    System.out.println(optionalState);
	    System.out.println(optionalState.isPresent());
	    if (optionalState.isPresent()) {
	        PaymentState currentState = optionalState.get();

	        // Create a state machine
	        StateMachine<PaymentStatus, PaymentEvent> stateMachine = stateMachineFactory.getStateMachine();
	        stateMachine.start();
	        stateMachine.getStateMachineAccessor().doWithAllRegions(access -> {
	            access.resetStateMachine(new DefaultStateMachineContext<>(currentState.getStateName(), null, null, null));
	        });

	        // Send the event to the state machine
	        boolean transitionPossible = stateMachine.sendEvent(MessageBuilder
	                .withPayload(newEvent)
	                .setHeader("txnId", txnId)
	                .build());
	        System.out.println(transitionPossible);
	        System.out.println(currentState);
	        if (transitionPossible) {
	        	System.out.println("I am from state machine" + currentState);       	
	        	currentState.setStateName(stateMachine.getState().getId());
	        	stateRepository.save(currentState);
	            return ResponseEntity.ok("State updated to: " + stateMachine.getState().getId());
	        } else {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transition not possible");
	        }
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction with ID " + txnId + " not found");
	    }
	}

	
	public ResponseEntity<String> getStatus(Long txnId){
		 Optional<PaymentState> optionalState = stateRepository.findByTxnId(txnId);
		    if (optionalState.isPresent()) {
		        PaymentState state = optionalState.get();
		        return ResponseEntity.ok("Transaction ID: " + txnId + ", Status: " + state.getStateName());
		    } else {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction with ID " + txnId + " not found");
		    }
	}
	
//	private boolean isValidTransition(PaymentStatus currentState, PaymentStatus newState) {
//        switch (currentState) {
//            case INITIALIZED:
//                return newState == PaymentStatus.PROCESSING;
//            case PROCESSING:
//                return newState == PaymentStatus.COMPLETED || newState == PaymentStatus.FAILED || newState == PaymentStatus.TIMEDOUT;
//            case COMPLETED:
//            case TIMEDOUT:
//            	return newState == PaymentStatus.COMPLETED || newState == PaymentStatus.FAILED;
//            case FAILED:
//                return false; // No further transitions allowed from COMPLETED or FAILED states
//            default:
//                return false; // Handle any other cases or undefined states
//        }
//    }
	
}
