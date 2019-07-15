package com.electroeing.memorydbrestserver.repository;

import com.electroeing.memorydbrestserver.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {

}
