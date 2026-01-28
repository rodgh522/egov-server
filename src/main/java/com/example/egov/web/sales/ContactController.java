package com.example.egov.web.sales;

import com.example.egov.domain.sales.Contact;
import com.example.egov.service.sales.ContactService;
import com.example.egov.web.sales.dto.ContactCreateRequest;
import com.example.egov.web.sales.dto.ContactResponse;
import com.example.egov.web.sales.dto.ContactUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponse> createContact(@Valid @RequestBody ContactCreateRequest request) {
        log.info("Creating contact: {}", request.getContactName());
        Contact contact = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ContactResponse.from(contact));
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<ContactResponse> updateContact(
            @PathVariable String contactId,
            @Valid @RequestBody ContactUpdateRequest request) {
        log.info("Updating contact: {}", contactId);
        Contact contact = contactService.updateContact(contactId, request);
        return ResponseEntity.ok(ContactResponse.from(contact));
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<ContactResponse> getContact(@PathVariable String contactId) {
        log.debug("Fetching contact: {}", contactId);
        return contactService.getContact(contactId)
                .map(contact -> ResponseEntity.ok(ContactResponse.from(contact)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getAllContacts() {
        log.debug("Fetching all contacts");
        List<Contact> contacts = contactService.getAllContacts();
        return ResponseEntity.ok(ContactResponse.fromList(contacts));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ContactResponse>> getContactsByCustomer(@PathVariable String customerId) {
        log.debug("Fetching contacts for customer: {}", customerId);
        List<Contact> contacts = contactService.getContactsByCustomer(customerId);
        return ResponseEntity.ok(ContactResponse.fromList(contacts));
    }

    @GetMapping("/customer/{customerId}/primary")
    public ResponseEntity<ContactResponse> getPrimaryContact(@PathVariable String customerId) {
        log.debug("Fetching primary contact for customer: {}", customerId);
        return contactService.getPrimaryContactByCustomer(customerId)
                .map(contact -> ResponseEntity.ok(ContactResponse.from(contact)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/active")
    public ResponseEntity<List<ContactResponse>> getActiveContacts() {
        log.debug("Fetching active contacts");
        List<Contact> contacts = contactService.getActiveContacts();
        return ResponseEntity.ok(ContactResponse.fromList(contacts));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactResponse>> searchContacts(@RequestParam String keyword) {
        log.debug("Searching contacts with keyword: {}", keyword);
        List<Contact> contacts = contactService.searchContacts(keyword);
        return ResponseEntity.ok(ContactResponse.fromList(contacts));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteContact(@PathVariable String contactId) {
        log.info("Deleting contact: {}", contactId);
        contactService.deleteContact(contactId);
        return ResponseEntity.noContent().build();
    }
}
