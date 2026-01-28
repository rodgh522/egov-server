package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.sales.Contact;
import com.example.egov.domain.sales.ContactRepository;
import com.example.egov.domain.sales.Customer;
import com.example.egov.domain.sales.CustomerRepository;
import com.example.egov.web.sales.dto.ContactCreateRequest;
import com.example.egov.web.sales.dto.ContactUpdateRequest;
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
public class ContactService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final ContactRepository contactRepository;
    private final CustomerRepository customerRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyContactAccess(Contact contact) {
        if (!isSystemTenant() && !contact.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to contact: " + contact.getContactId());
        }
    }

    @Transactional
    public Contact createContact(ContactCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        String contactId = generateContactId();
        log.info("Creating contact: {} for customer: {}", contactId, request.getCustomerId());

        Contact contact = new Contact();
        contact.setContactId(contactId);
        contact.setCustomer(customer);
        contact.setContactName(request.getContactName());
        contact.setContactTitle(request.getContactTitle());
        contact.setDepartment(request.getDepartment());
        contact.setPhone(request.getPhone());
        contact.setMobile(request.getMobile());
        contact.setEmail(request.getEmail());
        contact.setIsPrimary(request.getIsPrimary() != null ? request.getIsPrimary() : "N");
        contact.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        contact.setTenantId(tenantId);

        Contact savedContact = contactRepository.save(contact);
        log.info("Contact created successfully: {}", savedContact.getContactId());

        return savedContact;
    }

    @Transactional
    public Contact updateContact(String contactId, ContactUpdateRequest request) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));

        verifyContactAccess(contact);
        log.info("Updating contact: {}", contactId);

        if (request.getContactName() != null) {
            contact.setContactName(request.getContactName());
        }
        if (request.getContactTitle() != null) {
            contact.setContactTitle(request.getContactTitle());
        }
        if (request.getDepartment() != null) {
            contact.setDepartment(request.getDepartment());
        }
        if (request.getPhone() != null) {
            contact.setPhone(request.getPhone());
        }
        if (request.getMobile() != null) {
            contact.setMobile(request.getMobile());
        }
        if (request.getEmail() != null) {
            contact.setEmail(request.getEmail());
        }
        if (request.getIsPrimary() != null) {
            contact.setIsPrimary(request.getIsPrimary());
        }
        if (request.getUseAt() != null) {
            contact.setUseAt(request.getUseAt());
        }

        Contact updatedContact = contactRepository.save(contact);
        log.info("Contact updated successfully: {}", updatedContact.getContactId());

        return updatedContact;
    }

    public Optional<Contact> getContact(String contactId) {
        Optional<Contact> contact = contactRepository.findById(contactId);
        contact.ifPresent(this::verifyContactAccess);
        return contact;
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public List<Contact> getContactsByCustomer(String customerId) {
        return contactRepository.findByCustomerId(customerId);
    }

    public Optional<Contact> getPrimaryContactByCustomer(String customerId) {
        return contactRepository.findPrimaryContactByCustomerId(customerId);
    }

    public List<Contact> getActiveContacts() {
        return contactRepository.findByUseAt("Y");
    }

    public List<Contact> searchContacts(String keyword) {
        return contactRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteContact(String contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + contactId));

        verifyContactAccess(contact);

        log.info("Deleting contact: {}", contactId);
        contactRepository.delete(contact);
        log.info("Contact deleted successfully: {}", contactId);
    }

    private String generateContactId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CON" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
