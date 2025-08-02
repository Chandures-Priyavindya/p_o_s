package com.residuesolution.pos.service.impl;

import com.residuesolution.pos.dto.Customer;
import com.residuesolution.pos.entity.CustomerEntity;
import com.residuesolution.pos.repository.CustomerRepository;
import com.residuesolution.pos.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper mapper;

    @Override
    public Boolean addCustomer(Customer customer) {
        CustomerEntity save = customerRepository.save(mapper.map(customer, CustomerEntity.class));
        if(save != null) {
            return true;
        }
        return false;
    }

    // NEW: Edit customer implementation
    @Override
    public Boolean editCustomer(Customer customer) {
        // Check if customer exists
        Optional<CustomerEntity> existingCustomer = customerRepository.findById(customer.getCustomer_id());

        if (existingCustomer.isPresent()) {
            // Map the updated data and save
            CustomerEntity customerToUpdate = mapper.map(customer, CustomerEntity.class);
            CustomerEntity updatedCustomer = customerRepository.save(customerToUpdate);

            return updatedCustomer != null;
        }

        return false; // Customer not found
    }

    // BONUS: Get customer by ID implementation
    @Override
    public Customer getCustomerById(Integer customerId) {
        Optional<CustomerEntity> customerEntity = customerRepository.findById(customerId);

        if (customerEntity.isPresent()) {
            return mapper.map(customerEntity.get(), Customer.class);
        }

        return null; // Customer not found
    }

    // NEW: Get all customers implementation
    @Override
    public List<Customer> getAllCustomers() {
        List<CustomerEntity> customerEntities = customerRepository.findAll();

        // Convert List of Entities to List of DTOs
        return customerEntities.stream()
                .map(customerEntity -> mapper.map(customerEntity, Customer.class))
                .toList();
    }

    // NEW: Delete customer implementation
    @Override
    public Boolean deleteCustomer(Integer customerId) {
        // Check if customer exists
        if (customerRepository.existsById(customerId)) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false; // Customer not found
    }
}