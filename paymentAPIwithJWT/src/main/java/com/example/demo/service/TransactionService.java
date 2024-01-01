package com.example.demo.service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.enums.TransactionStatus;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.Transaction;
import com.example.demo.repository.TransactionRepository;

@Service
public class TransactionService {
	@Autowired
	private TransactionRepository transactionRepository;

	public Transaction saveTransaction(Transaction transaction) {
		return transactionRepository.save(transaction);	
	}
	public Optional<Transaction> findLatestTransactionByCardNumberAndVendorName(String cardNumber, String vendorName) {
		System.out.println("Searching for transactions with card number: " + cardNumber);
		System.out.println("Searching for transactions with vendor name: " + vendorName);
	    return transactionRepository.findTopByCardNumberAndVendorNameOrderByTransactionDateDesc(cardNumber, vendorName);
	}
//	public Transaction findLatestTransactionByCardNumber(String cardNumber) {
//		
//        return transactionRepository.findLatestTransactionByCardNumber(cardNumber)
//                .orElseThrow(() -> new EntityNotFoundException("No transaction found for card number: " + cardNumber));
//    }
	public void updateTransactionStatus(String transactionId, TransactionStatus newStatus) {
        transactionRepository.updateTransactionStatus(transactionId, newStatus);
    }
	public void findTransactionsToUpdateStatusSuccess(LocalDateTime startDate, LocalDateTime endDate) {
		List<Transaction> transactionsToUpdate = transactionRepository.findTransactionsToUpdateStatus(startDate, endDate);
		System.out.println("Here is a list of transactions to update: " + transactionsToUpdate);
		for (Transaction transaction : transactionsToUpdate) {
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        }
		transactionRepository.saveAll(transactionsToUpdate);
	}
	
}
