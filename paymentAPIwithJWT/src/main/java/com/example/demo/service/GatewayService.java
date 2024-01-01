package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Gateway;
import com.example.demo.repository.GatewayRepository;



@Service
public class GatewayService {
	@Autowired
	private GatewayRepository gatewayRepository;
	public Gateway saveGateway(Gateway gateway)
	{
		return gatewayRepository.save(gateway);
	}
}
