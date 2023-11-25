package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
	boolean existsByCustomerNameAndCustomerEmail(String customerName, String customerEmail);
}
