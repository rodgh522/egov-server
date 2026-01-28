package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private String contactId;
    private String customerId;
    private String customerName;
    private String contactName;
    private String contactTitle;
    private String department;
    private String phone;
    private String mobile;
    private String email;
    private String isPrimary;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public static ContactResponse from(Contact contact) {
        if (contact == null) {
            return null;
        }

        return ContactResponse.builder()
                .contactId(contact.getContactId())
                .customerId(contact.getCustomer() != null ? contact.getCustomer().getCustomerId() : null)
                .customerName(contact.getCustomer() != null ? contact.getCustomer().getCustomerName() : null)
                .contactName(contact.getContactName())
                .contactTitle(contact.getContactTitle())
                .department(contact.getDepartment())
                .phone(contact.getPhone())
                .mobile(contact.getMobile())
                .email(contact.getEmail())
                .isPrimary(contact.getIsPrimary())
                .tenantId(contact.getTenantId())
                .useAt(contact.getUseAt())
                .createdDate(contact.getCreatedDate())
                .updatedDate(contact.getUpdatedDate())
                .build();
    }

    public static List<ContactResponse> fromList(List<Contact> contacts) {
        if (contacts == null) {
            return null;
        }
        return contacts.stream()
                .map(ContactResponse::from)
                .collect(Collectors.toList());
    }
}
