package com.example.demo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name ="payment_customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long customer_id;
	private String customer_name;
	private String customer_email;
	private int amount_present;

	public Long getCustomer_id() {
		return customer_id;
	}
	public void setCustomer_id(Long customer_id) {
		this.customer_id = customer_id;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getCustomer_email() {
		return customer_email;
	}
	public void setCustomer_email(String customer_email) {
		this.customer_email = customer_email;
	}
	public int getAmount_present() {
		return amount_present;
	}
	public void setAmount_present(int amount_present) {
		this.amount_present = amount_present;
	}
//	@Override
//	public String toString() {
//		return "Customer [customer_id=" + customer_id + ", customer_name=" + customer_name + ", customer_email="
//				+ customer_email + ", amount_present=" + amount_present + "]";
//	}

}
