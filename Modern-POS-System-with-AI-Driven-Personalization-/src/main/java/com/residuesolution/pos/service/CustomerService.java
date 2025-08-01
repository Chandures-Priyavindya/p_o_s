package com.residuesolution.pos.service;

import com.residuesolution.pos.dto.Customer;

public interface CustomerService {
    Boolean addCustomer(Customer customer);

    Boolean editCustomer(Customer customer);

    Customer getCustomerById(Integer customerId);
}

