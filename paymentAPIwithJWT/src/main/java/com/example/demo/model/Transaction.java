package com.example.demo.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.enums.TransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transaction_table")
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_id")
	private Long id;
	
	@Column(name="amount_purchased")
	@NotNull(message = "Amount cannot be null.")
	@DecimalMin(value = "0.01", message = "Amount must be greater than or equal to 0.01")
	@Digits(integer = 10, fraction = 2, message = "Invalid amount format")
	private BigDecimal amount;
//	
//	@Column(name="amount_possession")
//	@NotBlank(message="Total amount of money can not be blank.")
//	private double totalAmount;
	
	@Column(name="customer_profile_id")
	private String customerProfileId;
	
	@Column(name="customer_payment_profile_id")
	private String customerPaymentProfileId;
	
	@Column(name="transaction_status")
	@NotNull(message = "Transaction status cannot be null")
	@Enumerated(EnumType.STRING)
	private TransactionStatus transactionStatus;
	
	@Column(name="card_holder_name")
    private String cardHolderName;
    
    @Column(name="card_holder_email")
    private String cardHolderEmail;
    
    @Column(name="card_number")
    @NotNull(message = "Card Number cannot be null")
    private String cardNumber;
    
    @Column(name="transaction_id")
    private String transactionId;
    
    @Column(name="user_name")
    private String userName;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name="vendor_name")
    private String vendorName;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCustomerProfileId() {
		return customerProfileId;
	}

	public void setCustomerProfileId(String customerProfileId) {
		this.customerProfileId = customerProfileId;
	}

	public String getCustomerPaymentProfileId() {
		return customerPaymentProfileId;
	}

	public void setCustomerPaymentProfileId(String customerPaymentProfileId) {
		this.customerPaymentProfileId = customerPaymentProfileId;
	}

	public  TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(TransactionStatus pending) {
		this.transactionStatus = pending;
	}

	public String getCardHolderName() {
		return cardHolderName;
	}

	public void setCardHolderName(String cardHolderName) {
		this.cardHolderName = cardHolderName;
	}

	public String getCardHolderEmail() {
		return cardHolderEmail;
	}

	public void setCardHolderEmail(String cardHolderEmail) {
		this.cardHolderEmail = cardHolderEmail;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	
}
