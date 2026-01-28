package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Customer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private String customerId;
    private String customerName;
    private String customerCode;
    private String customerType;
    private String industry;
    private String companySize;
    private String website;
    private String phone;
    private String email;
    private String address;
    private BigDecimal annualRevenue;
    private Integer employeeCount;
    private String assignedUserId;
    private String assignedUserName;
    private String branchId;
    private String branchName;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static CustomerResponse from(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerCode(customer.getCustomerCode())
                .customerType(customer.getCustomerType())
                .industry(customer.getIndustry())
                .companySize(customer.getCompanySize())
                .website(customer.getWebsite())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .annualRevenue(customer.getAnnualRevenue())
                .employeeCount(customer.getEmployeeCount())
                .assignedUserId(customer.getAssignedUser() != null ? customer.getAssignedUser().getId() : null)
                .assignedUserName(customer.getAssignedUser() != null ? customer.getAssignedUser().getUserName() : null)
                .branchId(customer.getBranch() != null ? customer.getBranch().getBranchId() : null)
                .branchName(customer.getBranch() != null ? customer.getBranch().getBranchName() : null)
                .tenantId(customer.getTenantId())
                .useAt(customer.getUseAt())
                .createdDate(customer.getCreatedDate())
                .updatedDate(customer.getUpdatedDate())
                .build();
    }

    public static List<CustomerResponse> fromList(List<Customer> customers) {
        if (customers == null) {
            return null;
        }
        return customers.stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }
}
