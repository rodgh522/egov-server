package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.Branch;
import com.example.egov.domain.admin.BranchRepository;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.sales.Customer;
import com.example.egov.domain.sales.CustomerRepository;
import com.example.egov.web.sales.dto.CustomerCreateRequest;
import com.example.egov.web.sales.dto.CustomerUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyCustomerAccess(Customer customer) {
        if (!isSystemTenant() && !customer.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to customer: " + customer.getCustomerId());
        }
    }

    @Transactional
    public Customer createCustomer(CustomerCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
            throw new IllegalArgumentException("Customer code already exists: " + request.getCustomerCode());
        }

        String customerId = generateCustomerId();
        log.info("Creating customer: {} for tenant: {}", customerId, tenantId);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);
        customer.setCustomerName(request.getCustomerName());
        customer.setCustomerCode(request.getCustomerCode());
        customer.setCustomerType(request.getCustomerType() != null ? request.getCustomerType() : "COMPANY");
        customer.setIndustry(request.getIndustry());
        customer.setCompanySize(request.getCompanySize());
        customer.setWebsite(request.getWebsite());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setAnnualRevenue(request.getAnnualRevenue());
        customer.setEmployeeCount(request.getEmployeeCount());
        customer.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        customer.setTenantId(tenantId);

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            customer.setAssignedUser(assignedUser);
        }

        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            customer.setBranch(branch);
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer created successfully: {}", savedCustomer.getCustomerId());

        return savedCustomer;
    }

    @Transactional
    public Customer updateCustomer(String customerId, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        verifyCustomerAccess(customer);
        log.info("Updating customer: {}", customerId);

        if (request.getCustomerName() != null) {
            customer.setCustomerName(request.getCustomerName());
        }
        if (request.getCustomerCode() != null && !request.getCustomerCode().equals(customer.getCustomerCode())) {
            if (customerRepository.existsByCustomerCode(request.getCustomerCode())) {
                throw new IllegalArgumentException("Customer code already exists: " + request.getCustomerCode());
            }
            customer.setCustomerCode(request.getCustomerCode());
        }
        if (request.getCustomerType() != null) {
            customer.setCustomerType(request.getCustomerType());
        }
        if (request.getIndustry() != null) {
            customer.setIndustry(request.getIndustry());
        }
        if (request.getCompanySize() != null) {
            customer.setCompanySize(request.getCompanySize());
        }
        if (request.getWebsite() != null) {
            customer.setWebsite(request.getWebsite());
        }
        if (request.getPhone() != null) {
            customer.setPhone(request.getPhone());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        if (request.getAnnualRevenue() != null) {
            customer.setAnnualRevenue(request.getAnnualRevenue());
        }
        if (request.getEmployeeCount() != null) {
            customer.setEmployeeCount(request.getEmployeeCount());
        }
        if (request.getUseAt() != null) {
            customer.setUseAt(request.getUseAt());
        }
        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            customer.setAssignedUser(assignedUser);
        }
        if (request.getBranchId() != null) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found: " + request.getBranchId()));
            customer.setBranch(branch);
        }

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Customer updated successfully: {}", updatedCustomer.getCustomerId());

        return updatedCustomer;
    }

    public Optional<Customer> getCustomer(String customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        customer.ifPresent(this::verifyCustomerAccess);
        return customer;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public List<Customer> getActiveCustomers() {
        return customerRepository.findByUseAt("Y");
    }

    public List<Customer> getCustomersByType(String customerType) {
        return customerRepository.findByCustomerType(customerType);
    }

    public List<Customer> getCustomersByIndustry(String industry) {
        return customerRepository.findByIndustry(industry);
    }

    public List<Customer> getCustomersByAssignedUser(String userId) {
        return customerRepository.findByAssignedUserId(userId);
    }

    public List<Customer> getCustomersByBranch(String branchId) {
        return customerRepository.findByBranchId(branchId);
    }

    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteCustomer(String customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));

        verifyCustomerAccess(customer);

        log.info("Deleting customer: {}", customerId);
        customerRepository.delete(customer);
        log.info("Customer deleted successfully: {}", customerId);
    }

    public boolean existsByCustomerCode(String customerCode) {
        return customerRepository.existsByCustomerCode(customerCode);
    }

    private String generateCustomerId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CUS" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
