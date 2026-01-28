package com.example.egov.web.sales;

import com.example.egov.domain.sales.Customer;
import com.example.egov.service.sales.CustomerService;
import com.example.egov.web.sales.dto.CustomerCreateRequest;
import com.example.egov.web.sales.dto.CustomerResponse;
import com.example.egov.web.sales.dto.CustomerUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CustomerCreateRequest request) {
        log.info("Creating customer: {}", request.getCustomerName());
        Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(customer));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerUpdateRequest request) {
        log.info("Updating customer: {}", customerId);
        Customer customer = customerService.updateCustomer(customerId, request);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable String customerId) {
        log.debug("Fetching customer: {}", customerId);
        return customerService.getCustomer(customerId)
                .map(customer -> ResponseEntity.ok(CustomerResponse.from(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.debug("Fetching all customers");
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CustomerResponse>> getActiveCustomers() {
        log.debug("Fetching active customers");
        List<Customer> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @GetMapping("/type/{customerType}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByType(@PathVariable String customerType) {
        log.debug("Fetching customers by type: {}", customerType);
        List<Customer> customers = customerService.getCustomersByType(customerType);
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @GetMapping("/industry/{industry}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByIndustry(@PathVariable String industry) {
        log.debug("Fetching customers by industry: {}", industry);
        List<Customer> customers = customerService.getCustomersByIndustry(industry);
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByAssignedUser(@PathVariable String userId) {
        log.debug("Fetching customers assigned to user: {}", userId);
        List<Customer> customers = customerService.getCustomersByAssignedUser(userId);
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<CustomerResponse>> getCustomersByBranch(@PathVariable String branchId) {
        log.debug("Fetching customers by branch: {}", branchId);
        List<Customer> customers = customerService.getCustomersByBranch(branchId);
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam String keyword) {
        log.debug("Searching customers with keyword: {}", keyword);
        List<Customer> customers = customerService.searchCustomers(keyword);
        return ResponseEntity.ok(CustomerResponse.fromList(customers));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("Deleting customer: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{customerCode}")
    public ResponseEntity<Boolean> existsByCustomerCode(@PathVariable String customerCode) {
        log.debug("Checking if customer code exists: {}", customerCode);
        return ResponseEntity.ok(customerService.existsByCustomerCode(customerCode));
    }
}
