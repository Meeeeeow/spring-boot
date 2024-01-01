package com.example.demo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.TransactionStatus;
import com.example.demo.model.Transaction;



public interface TransactionRepository extends JpaRepository<Transaction, Long>{
	 Optional<Transaction> findTopByCardNumberAndVendorNameOrderByTransactionDateDesc(String cardNumber, String vendorName);
	
	Optional<Transaction> findTransactionByTransactionId(String transactionId);
	
	@Transactional
    @Modifying
    @Query("UPDATE Transaction t SET t.transactionStatus = :newStatus WHERE t.transactionId = :transactionId")
    void updateTransactionStatus(String transactionId, TransactionStatus newStatus);
	
	@Query("SELECT t FROM Transaction t " +
	        "WHERE t.transactionDate BETWEEN :yesterday AND :today " +
	        "AND t.transactionStatus = 'PENDING' " +
	        "AND t.vendorName = 'authorizenet'")
	List<Transaction> findTransactionsToUpdateStatus(
	        @Param("yesterday") LocalDateTime yesterday,
	        @Param("today") LocalDateTime today);
}
