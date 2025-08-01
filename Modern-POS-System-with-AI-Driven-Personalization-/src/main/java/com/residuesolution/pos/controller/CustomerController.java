package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.Customer;
import com.residuesolution.pos.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
@RequiredArgsConstructor

public class CustomerController {

    private final CustomerService customerService;
    @PostMapping("/add")
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer){
          customerService.addCustomer(customer);
          return ResponseEntity.ok("Customer Added Successfully");
    }
    // NEW: Edit customer endpoint
    @PutMapping("/edit/{customerId}")
    public ResponseEntity<String> editCustomer(
            @PathVariable Integer customerId,
            @RequestBody Customer customer) {

        // Set the ID from path parameter to ensure we're updating the correct customer
        customer.setCustomer_id(customerId);

        Boolean isUpdated = customerService.editCustomer(customer);

        if (isUpdated) {
            return ResponseEntity.ok("Customer Updated Successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // BONUS: Get customer by ID (useful for edit forms)
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomer(@PathVariable Integer customerId) {
        Customer customer = customerService.getCustomerById(customerId);

        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

