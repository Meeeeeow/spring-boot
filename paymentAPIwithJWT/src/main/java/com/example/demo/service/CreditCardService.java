package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.CreditCard;
import com.example.demo.repository.CreditCardRepository;

import jakarta.validation.Valid;

@Service
public class CreditCardService {
	@Autowired
	private CreditCardRepository creditCardRepository;
	
	public boolean isCreditCardNumberExistsForVendor(String cardNumber, String vendorName) {
	    System.out.println("Calling service");
	    return creditCardRepository.existsByCardNumberAndVendorName(cardNumber, vendorName);
	}

	public void saveCreditCardInfo(@Valid CreditCard creditCard) {
		creditCardRepository.save(creditCard);
	}
}
