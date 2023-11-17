package com.example.demo;

import org.springframework.stereotype.Component;

@Component
public class Customers {
	private int custID;
	private String custName;
	private int age;
	
	public int getCustID() {
		return custID;
	}
	public void setCustID(int custID) {
		this.custID = custID;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public void display() {
		System.out.println("Object returned succesfully!");		
	}
}
