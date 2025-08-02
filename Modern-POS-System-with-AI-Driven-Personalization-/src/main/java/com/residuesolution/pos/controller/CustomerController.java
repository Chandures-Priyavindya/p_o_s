package com.residuesolution.pos.controller;

import com.residuesolution.pos.dto.Customer;
import com.residuesolution.pos.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // ADMIN: Full CRUD - Can add customers
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> addCustomer(@RequestBody Customer customer){
        customerService.addCustomer(customer);
        return ResponseEntity.ok("Customer Added Successfully");
    }

    // ADMIN: Full CRUD - Can edit/update customers
    @PutMapping("/edit/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

    // ADMIN & MANAGER & CASHIER: All can view individual customers
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<Customer> getCustomer(@PathVariable Integer customerId) {
        Customer customer = customerService.getCustomerById(customerId);

        if (customer != null) {
            return ResponseEntity.ok(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ADMIN & MANAGER & CASHIER: All can view all customers
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_CASHIER')")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();

        if (customers.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no customers found
        }

        return ResponseEntity.ok(customers);
    }

    // ADMIN: Full CRUD - Only admin can delete customers
    @DeleteMapping("/delete/{customerId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCustomer(@PathVariable Integer customerId) {
        Boolean isDeleted = customerService.deleteCustomer(customerId);

        if (isDeleted) {
            return ResponseEntity.ok("Customer Deleted Successfully");
        } else {
            return ResponseEntity.notFound().build(); // 404 if customer not found
        }
    }

    // MANAGER: Special endpoint for managers to update their own customer details
    // Note: This would require authentication context to get current user's customer info
    @PutMapping("/update-own-details")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<String> updateOwnCustomerDetails(@RequestBody Customer customer) {
        // This endpoint would need additional logic to ensure managers can only update
        // their own associated customer details. You'd need to implement user-customer relationship.

        // For now, this is a placeholder - you'll need to add logic to:
        // 1. Get current authenticated user
        // 2. Find their associated customer record
        // 3. Allow them to update only their own details

        Boolean isUpdated = customerService.editCustomer(customer);

        if (isUpdated) {
            return ResponseEntity.ok("Your Customer Details Updated Successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // CASHIER: Special endpoint for cashiers to create new customers (if allowed by business logic)
    @PostMapping("/create-new")
    @PreAuthorize("hasAuthority('ROLE_CASHIER')")
    public ResponseEntity<String> createNewCustomer(@RequestBody Customer customer){
        // Additional business logic can be added here to restrict when cashiers can create customers
        customerService.addCustomer(customer);
        return ResponseEntity.ok("New Customer Created Successfully by Cashier");
    }
    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Authentication is working!");
    }
}