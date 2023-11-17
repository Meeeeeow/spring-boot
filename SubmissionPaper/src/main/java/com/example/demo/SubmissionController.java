package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SubmissionController {
	private final CustomersRepo customersRepo; 
	
	@Autowired
	public SubmissionController(CustomersRepo customersRepo)
	{
		this.customersRepo = customersRepo;
	}
	// entry page
	@GetMapping("submit")
	public String CustomerDetails() {
		return "customers";
	}
	//create 
	@PostMapping("newCustomers")
    public String saveCustomer(HttpServletRequest req) {
        // Save to the database
        savedCustomers(req);

        // Redirect to the customer details page
        return "redirect:/customerDetails";
    }
	//read
	@RequestMapping(value = "customerDetails", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView viewDetails() {
        List<Customers> allCustomers = retrieveFromDatabase();
        ModelAndView modelAndView = new ModelAndView("ViewDetails");
        modelAndView.addObject("customers", allCustomers);
        return modelAndView;
    }
	//save data in database
	private Customers savedCustomers(HttpServletRequest req) {
        Enumeration<String> parameterNames = req.getParameterNames();
        List<String> parameterList = Collections.list(parameterNames);
        Customers customer = new Customers();
        //save in the database
        for (String paramName : parameterList) {
            if ("custid".equals(paramName)) {
                customer.setCustID(Long.parseLong(req.getParameter(paramName)));
            } else if ("cname".equals(paramName)) {
                customer.setCustName(req.getParameter(paramName));
            } else if ("cemail".equals(paramName)) {
                customer.setCustEmail(req.getParameter(paramName));
            }
        }
        return customersRepo.save(customer);
    }
	//get data from database
	private List<Customers> retrieveFromDatabase() {
        return customersRepo.findAll();
    }
	//delete
	@DeleteMapping("deletedCustomers")
    private String deleteCustomers(@RequestParam("customerID") Long customerID) {
        customersRepo.deleteById(customerID);
        return "redirect:/customerDetails";
    }
	//update
	@PutMapping("updateCustomers")
	private String updateCustomers(@RequestParam("customerID") Long customerID,
									@RequestParam("updatedName") String updatedCustName,
									@RequestParam("updatedEmail") String updatedCustEmail)
	{
		Customers existingCustomer = customersRepo.findById(customerID).orElse(null);
		System.out.println("i am" + existingCustomer);
		existingCustomer.setCustName(updatedCustName);
		existingCustomer.setCustEmail(updatedCustEmail);
		
		customersRepo.save(existingCustomer);
		return "redirect:/customerDetails";
	}
}
