package com.electroeing.memorydbrestserver.controller;

import com.electroeing.memorydbrestserver.entities.Customer;
import com.electroeing.memorydbrestserver.repository.CustomerRepository;
import com.electroeing.memorydbrestserver.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/customers")
public class CustomerController {
    private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private CustomerRepository customerRepository;

    @Autowired
    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Customer> getCustomerList() {
        logger.info("Customers request received");
        return customerRepository.findAll();
    }

    @PutMapping("{dni}")
    public Response updateCustomer(@PathVariable String dni, @RequestBody Customer customer, HttpServletResponse response) {
        logger.info("Updating customer: dni={}, customer={}", dni, customer);
        String reason = "";
        if (customer.getAge() < 18) {
            Customer currentCustomer = customerRepository.getOne(dni);
            if (currentCustomer != null){
                Customer cu = customerRepository.save(customer);
                Response rs = new Response(true, reason);
                rs.addBodyObject("customer", cu);
                return rs;
            }
            reason = String.format("No customer with dni %s found to be edited.", dni);
        } else {
            reason = String.format("Customer age invalid: %s. Must be under 18.", customer.getAge());
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new Response(false, reason);
    }

    @PostMapping("")
    public Response createCustomer(@RequestBody Customer customer, HttpServletResponse response) {
        logger.info("Creating customer: customer={}", customer);
        String reason = "";
        Optional<Customer> currentCustomer = customerRepository.findById(customer.getDni());
        if (!currentCustomer.isPresent()){
            Customer cu = customerRepository.save(customer);
            Response rs = new Response(true, reason);
            rs.addBodyObject("customer", cu);
            return rs;
        }
        reason = String.format("Customer with dni %s already exists.", customer.getDni());

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new Response(false, reason);
    }

    @DeleteMapping("{dni}")
    public Response deleteCustomer(@PathVariable String dni, HttpServletResponse response) {
        logger.info("Requested customer DELETE: dni={}", dni);
        String reason = "";
        Optional<Customer> currentCustomer = customerRepository.findById(dni);
        if (currentCustomer.isPresent()){
            customerRepository.deleteById(dni);
            return new Response(true, reason);
        }
        reason = String.format("Customer with dni %s does not exists.", dni);

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return new Response(false, reason);
    }
}
