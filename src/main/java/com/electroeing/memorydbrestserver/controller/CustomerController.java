package com.electroeing.memorydbrestserver.controller;

import com.electroeing.memorydbrestserver.entities.Customer;
import com.electroeing.memorydbrestserver.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customers")
public class CustomerController {
    private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    CustomerRepository customerRepository;

    @GetMapping
    public List<Customer> getCustomerList() {
        logger.info("Customers request received");
        return customerRepository.findAll();
    }

    @PostMapping("create")
    public Customer createCustomer(Customer customer) {
        logger.info("Creating customer: {}", customer);
        return customerRepository.save(customer);
    }
}
