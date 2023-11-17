package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DependencyDemoApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DependencyDemoApplication.class, args);
		Customers c = context.getBean(Customers.class);
		DataCustomer d = context.getBean(DataCustomer.class);
		c.display();
		c.setCustID(0);
		c.setCustName("Abdul");
		c.setAge(23);
		d.helloCustomer(c.getCustName());
		System.out.println(c.getCustID() +  c.getCustName() + c.getAge());
		
		c.setCustID(1);
		c.setCustName("Rarim");
		c.setAge(25);
		System.out.println(c.getCustID() + c.getCustName() + c.getAge());
		d.helloCustomer(c.getCustName());
	}

}
