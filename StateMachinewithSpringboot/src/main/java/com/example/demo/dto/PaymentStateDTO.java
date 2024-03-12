package com.example.demo.dto;

import com.example.demo.enums.PaymentStatus;

import lombok.Data;

@Data
public class PaymentStateDTO {
	private Long id;
	private Long txnId;
    private PaymentStatus state;
}
