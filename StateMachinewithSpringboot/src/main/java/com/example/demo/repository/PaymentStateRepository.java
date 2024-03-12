package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.PaymentState;

@Repository
public interface PaymentStateRepository  extends JpaRepository<PaymentState, Long>{
	Optional<PaymentState> findByTxnId(Long txnId);
}
