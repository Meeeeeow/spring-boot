package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepo extends JpaRepository<Customers, Long>{  //jpa repository extends crud repository
	
}
