package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Vendor;



@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long>{

	boolean existsByVendorName(String vendorName);
	Optional<Vendor> findByVendorName(String vendorName);

}
