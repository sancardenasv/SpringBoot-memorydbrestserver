package com.electroeing.memorydbrestserver.controller;

import com.electroeing.memorydbrestserver.entities.Customer;
import com.electroeing.memorydbrestserver.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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

    @PutMapping("{dni}")
    public Customer updateCustomer(@PathVariable String dni, @RequestBody Customer customer, HttpServletResponse response) {
        logger.info("Updating customer: dni={}, customer={}", dni, customer);
        Customer currentCustomer = customerRepository.getOne(dni);
        if (currentCustomer != null){
            return customerRepository.save(customer);
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }
}
