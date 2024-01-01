package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//@ExpDate
@Table(name="credit_card_table")
public class CreditCard {
	

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="card_id")
    private Long cardId;
    
//	@Embedded
//    @Valid
//    private ExpDateFields expDateFields;
	
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Invalid card number")
    @Column(name="card_number")
    private String cardNumber;

    @NotBlank(message = "Card holder name is required")
    @Size(min = 2, max = 50, message = "Card holder name must be between 2 and 50 characters")
    @Column(name="card_name")
    private String cardHolderName;

    @NotBlank(message = "Card holder email is required")
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "Invalid email format")
    @Column(name="card_email")
    private String cardHolderEmail;

    @NotBlank(message = "Expiration month is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Invalid expiration month")
    @Column(name="exp_month")
    private String expirationMonth;

    @NotBlank(message = "Expiration year is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Invalid expiration year")
    @Column(name="exp_year")
    private String expirationYear;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    @Column(name="cvv")
    private String cvv;

    @NotBlank(message = "Current balance is required")
    @Column(name="current_balance")
    private String currentBalance;

    @NotBlank(message = "Vendor name is required")
    @Column(name = "vendor_name")
    private String vendorName;
    
    @ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="customer_id")
	private AccountCustomer UserInfo;
    
    @Column(name="user_name")
    private String userName;
    
    
}
