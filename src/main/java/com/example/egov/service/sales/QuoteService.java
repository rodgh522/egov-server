package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.admin.User;
import com.example.egov.domain.admin.UserRepository;
import com.example.egov.domain.sales.*;
import com.example.egov.web.sales.dto.QuoteCreateRequest;
import com.example.egov.web.sales.dto.QuoteItemRequest;
import com.example.egov.web.sales.dto.QuoteUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final QuoteRepository quoteRepository;
    private final QuoteItemRepository quoteItemRepository;
    private final CustomerRepository customerRepository;
    private final ContactRepository contactRepository;
    private final OpportunityRepository opportunityRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyQuoteAccess(Quote quote) {
        if (!isSystemTenant() && !quote.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to quote: " + quote.getQuoteId());
        }
    }

    @Transactional
    public Quote createQuote(QuoteCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        if (quoteRepository.existsByQuoteNumber(request.getQuoteNumber())) {
            throw new IllegalArgumentException("Quote number already exists: " + request.getQuoteNumber());
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + request.getCustomerId()));

        String quoteId = generateQuoteId();
        log.info("Creating quote: {} for tenant: {}", quoteId, tenantId);

        Quote quote = new Quote();
        quote.setQuoteId(quoteId);
        quote.setQuoteNumber(request.getQuoteNumber());
        quote.setCustomer(customer);
        quote.setQuoteStatus(request.getQuoteStatus() != null ? request.getQuoteStatus() : "DRAFT");
        quote.setQuoteDate(request.getQuoteDate() != null ? request.getQuoteDate() : LocalDate.now());
        quote.setValidUntil(request.getValidUntil());
        quote.setCurrency(request.getCurrency() != null ? request.getCurrency() : "KRW");
        quote.setPaymentTerms(request.getPaymentTerms());
        quote.setDeliveryTerms(request.getDeliveryTerms());
        quote.setNotes(request.getNotes());
        quote.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        quote.setTenantId(tenantId);

        if (request.getOpportunityId() != null) {
            Opportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                    .orElseThrow(() -> new IllegalArgumentException("Opportunity not found: " + request.getOpportunityId()));
            quote.setOpportunity(opportunity);
        }

        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                    .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + request.getContactId()));
            quote.setContact(contact);
        }

        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            quote.setAssignedUser(assignedUser);
        }

        Quote savedQuote = quoteRepository.save(quote);

        // Add quote items if provided
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (int i = 0; i < request.getItems().size(); i++) {
                QuoteItemRequest itemRequest = request.getItems().get(i);
                addQuoteItem(savedQuote, itemRequest, i);
            }
            calculateQuoteTotals(savedQuote);
            savedQuote = quoteRepository.save(savedQuote);
        }

        log.info("Quote created successfully: {}", savedQuote.getQuoteId());
        return savedQuote;
    }

    @Transactional
    public Quote updateQuote(String quoteId, QuoteUpdateRequest request) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found: " + quoteId));

        verifyQuoteAccess(quote);
        log.info("Updating quote: {}", quoteId);

        if (request.getQuoteNumber() != null && !request.getQuoteNumber().equals(quote.getQuoteNumber())) {
            if (quoteRepository.existsByQuoteNumber(request.getQuoteNumber())) {
                throw new IllegalArgumentException("Quote number already exists: " + request.getQuoteNumber());
            }
            quote.setQuoteNumber(request.getQuoteNumber());
        }
        if (request.getQuoteStatus() != null) {
            quote.setQuoteStatus(request.getQuoteStatus());
        }
        if (request.getQuoteDate() != null) {
            quote.setQuoteDate(request.getQuoteDate());
        }
        if (request.getValidUntil() != null) {
            quote.setValidUntil(request.getValidUntil());
        }
        if (request.getDiscountAmount() != null) {
            quote.setDiscountAmount(request.getDiscountAmount());
        }
        if (request.getCurrency() != null) {
            quote.setCurrency(request.getCurrency());
        }
        if (request.getPaymentTerms() != null) {
            quote.setPaymentTerms(request.getPaymentTerms());
        }
        if (request.getDeliveryTerms() != null) {
            quote.setDeliveryTerms(request.getDeliveryTerms());
        }
        if (request.getNotes() != null) {
            quote.setNotes(request.getNotes());
        }
        if (request.getUseAt() != null) {
            quote.setUseAt(request.getUseAt());
        }
        if (request.getContactId() != null) {
            Contact contact = contactRepository.findById(request.getContactId())
                    .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + request.getContactId()));
            quote.setContact(contact);
        }
        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Assigned user not found: " + request.getAssignedUserId()));
            quote.setAssignedUser(assignedUser);
        }

        // Update items if provided
        if (request.getItems() != null) {
            quote.getQuoteItems().clear();
            for (int i = 0; i < request.getItems().size(); i++) {
                QuoteItemRequest itemRequest = request.getItems().get(i);
                addQuoteItem(quote, itemRequest, i);
            }
        }

        calculateQuoteTotals(quote);
        Quote updatedQuote = quoteRepository.save(quote);
        log.info("Quote updated successfully: {}", updatedQuote.getQuoteId());

        return updatedQuote;
    }

    private void addQuoteItem(Quote quote, QuoteItemRequest itemRequest, int order) {
        Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemRequest.getProductId()));

        QuoteItem item = new QuoteItem();
        item.setQuote(quote);
        item.setProduct(product);
        item.setItemOrder(order);
        item.setQuantity(itemRequest.getQuantity() != null ? itemRequest.getQuantity() : 1);
        item.setUnitPrice(itemRequest.getUnitPrice() != null ? itemRequest.getUnitPrice() : product.getUnitPrice());
        item.setDiscountRate(itemRequest.getDiscountRate() != null ? itemRequest.getDiscountRate() : BigDecimal.ZERO);
        item.setTaxRate(itemRequest.getTaxRate() != null ? itemRequest.getTaxRate() : product.getTaxRate());
        item.setDescription(itemRequest.getDescription());
        item.setTenantId(quote.getTenantId());

        // Calculate item amounts
        BigDecimal subtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        BigDecimal discountAmount = subtotal.multiply(item.getDiscountRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        item.setDiscountAmount(discountAmount);
        BigDecimal afterDiscount = subtotal.subtract(discountAmount);
        BigDecimal taxAmount = afterDiscount.multiply(item.getTaxRate()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        item.setTaxAmount(taxAmount);
        item.setTotalAmount(afterDiscount.add(taxAmount));

        quote.getQuoteItems().add(item);
    }

    private void calculateQuoteTotals(Quote quote) {
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;

        for (QuoteItem item : quote.getQuoteItems()) {
            BigDecimal itemSubtotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                    .subtract(item.getDiscountAmount());
            subtotal = subtotal.add(itemSubtotal);
            taxAmount = taxAmount.add(item.getTaxAmount());
        }

        quote.setSubtotal(subtotal);
        quote.setTaxAmount(taxAmount);
        BigDecimal totalAmount = subtotal.add(taxAmount).subtract(
                quote.getDiscountAmount() != null ? quote.getDiscountAmount() : BigDecimal.ZERO);
        quote.setTotalAmount(totalAmount);
    }

    public Optional<Quote> getQuote(String quoteId) {
        Optional<Quote> quote = quoteRepository.findById(quoteId);
        quote.ifPresent(this::verifyQuoteAccess);
        return quote;
    }

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

    public List<Quote> getQuotesByCustomer(String customerId) {
        return quoteRepository.findByCustomerId(customerId);
    }

    public List<Quote> getQuotesByOpportunity(String opportunityId) {
        return quoteRepository.findByOpportunityId(opportunityId);
    }

    public List<Quote> getQuotesByStatus(String status) {
        return quoteRepository.findByQuoteStatus(status);
    }

    public List<Quote> getQuotesByAssignedUser(String userId) {
        return quoteRepository.findByAssignedUserId(userId);
    }

    public List<Quote> getExpiredQuotes() {
        return quoteRepository.findExpiredQuotes(LocalDate.now());
    }

    public List<Quote> searchQuotes(String keyword) {
        return quoteRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteQuote(String quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Quote not found: " + quoteId));

        verifyQuoteAccess(quote);

        log.info("Deleting quote: {}", quoteId);
        quoteRepository.delete(quote);
        log.info("Quote deleted successfully: {}", quoteId);
    }

    public boolean existsByQuoteNumber(String quoteNumber) {
        return quoteRepository.existsByQuoteNumber(quoteNumber);
    }

    private String generateQuoteId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "QTE" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
