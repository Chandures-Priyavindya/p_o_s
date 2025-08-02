package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.Customer;

import java.util.List;

public interface CustomerService {
    Boolean addCustomer(Customer customer);

    Boolean editCustomer(Customer customer);

    Customer getCustomerById(Integer customerId);

    // NEW: Get all customers
    List<Customer> getAllCustomers();

    // NEW: Delete customer by ID
    Boolean deleteCustomer(Integer customerId);
}

