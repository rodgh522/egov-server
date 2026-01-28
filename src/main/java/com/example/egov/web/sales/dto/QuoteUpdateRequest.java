package com.example.egov.web.sales.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class QuoteUpdateRequest {

    @Size(max = 50, message = "Quote number must not exceed 50 characters")
    private String quoteNumber;

    @Size(max = 20, message = "Quote status must not exceed 20 characters")
    private String quoteStatus;

    private LocalDate quoteDate;

    private LocalDate validUntil;

    private BigDecimal discountAmount;

    @Size(max = 3, message = "Currency must not exceed 3 characters")
    private String currency;

    @Size(max = 200, message = "Payment terms must not exceed 200 characters")
    private String paymentTerms;

    @Size(max = 200, message = "Delivery terms must not exceed 200 characters")
    private String deliveryTerms;

    private String notes;

    private String contactId;

    private String assignedUserId;

    @Pattern(regexp = "^[YN]$", message = "useAt must be Y or N")
    private String useAt;

    private List<QuoteItemRequest> items;
}
