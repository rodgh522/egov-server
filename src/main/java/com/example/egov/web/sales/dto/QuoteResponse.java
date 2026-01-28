package com.example.egov.web.sales.dto;

import com.example.egov.domain.sales.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteResponse {

    private String quoteId;
    private String quoteNumber;
    private String opportunityId;
    private String opportunityName;
    private String customerId;
    private String customerName;
    private String contactId;
    private String contactName;
    private String quoteStatus;
    private LocalDate quoteDate;
    private LocalDate validUntil;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentTerms;
    private String deliveryTerms;
    private String notes;
    private String assignedUserId;
    private String assignedUserName;
    private String tenantId;
    private String useAt;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private List<QuoteItemResponse> items;

    public static QuoteResponse from(Quote quote) {
        if (quote == null) {
            return null;
        }

        return QuoteResponse.builder()
                .quoteId(quote.getQuoteId())
                .quoteNumber(quote.getQuoteNumber())
                .opportunityId(quote.getOpportunity() != null ? quote.getOpportunity().getOpportunityId() : null)
                .opportunityName(quote.getOpportunity() != null ? quote.getOpportunity().getOpportunityName() : null)
                .customerId(quote.getCustomer() != null ? quote.getCustomer().getCustomerId() : null)
                .customerName(quote.getCustomer() != null ? quote.getCustomer().getCustomerName() : null)
                .contactId(quote.getContact() != null ? quote.getContact().getContactId() : null)
                .contactName(quote.getContact() != null ? quote.getContact().getContactName() : null)
                .quoteStatus(quote.getQuoteStatus())
                .quoteDate(quote.getQuoteDate())
                .validUntil(quote.getValidUntil())
                .subtotal(quote.getSubtotal())
                .discountAmount(quote.getDiscountAmount())
                .taxAmount(quote.getTaxAmount())
                .totalAmount(quote.getTotalAmount())
                .currency(quote.getCurrency())
                .paymentTerms(quote.getPaymentTerms())
                .deliveryTerms(quote.getDeliveryTerms())
                .notes(quote.getNotes())
                .assignedUserId(quote.getAssignedUser() != null ? quote.getAssignedUser().getId() : null)
                .assignedUserName(quote.getAssignedUser() != null ? quote.getAssignedUser().getUserName() : null)
                .tenantId(quote.getTenantId())
                .useAt(quote.getUseAt())
                .createdDate(quote.getCreatedDate())
                .updatedDate(quote.getUpdatedDate())
                .items(QuoteItemResponse.fromList(quote.getQuoteItems()))
                .build();
    }

    public static List<QuoteResponse> fromList(List<Quote> quotes) {
        if (quotes == null) {
            return null;
        }
        return quotes.stream()
                .map(QuoteResponse::from)
                .collect(Collectors.toList());
    }
}
