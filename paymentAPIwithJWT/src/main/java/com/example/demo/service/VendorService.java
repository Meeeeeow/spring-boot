package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.exception.DuplicateVendorException;
import com.example.demo.exception.EntityNotFoundException;
import com.example.demo.model.Vendor;
import com.example.demo.repository.VendorRepository;


@Service
public class VendorService {
	
	@Autowired
	private  VendorRepository vendorRepository;
	
	public void saveVendor(Vendor vendor)
	{
		 if (vendorRepository.existsByVendorName(vendor.getVendorName())) {
		        throw new DuplicateVendorException("Vendor with the same name already exists.");
		 }
		vendorRepository.save(vendor);
	}

	 public Vendor findVendorByName(String vendorName) {
	        return vendorRepository.findByVendorName(vendorName)
	               .orElseThrow(() -> new EntityNotFoundException("Vendor not found with name: " + vendorName));
	    }
	
}
