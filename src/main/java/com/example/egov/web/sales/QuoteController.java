package com.example.egov.web.sales;

import com.example.egov.domain.sales.Quote;
import com.example.egov.service.sales.QuoteService;
import com.example.egov.web.sales.dto.QuoteCreateRequest;
import com.example.egov.web.sales.dto.QuoteResponse;
import com.example.egov.web.sales.dto.QuoteUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody QuoteCreateRequest request) {
        log.info("Creating quote: {}", request.getQuoteNumber());
        Quote quote = quoteService.createQuote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(QuoteResponse.from(quote));
    }

    @PutMapping("/{quoteId}")
    public ResponseEntity<QuoteResponse> updateQuote(
            @PathVariable String quoteId,
            @Valid @RequestBody QuoteUpdateRequest request) {
        log.info("Updating quote: {}", quoteId);
        Quote quote = quoteService.updateQuote(quoteId, request);
        return ResponseEntity.ok(QuoteResponse.from(quote));
    }

    @GetMapping("/{quoteId}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable String quoteId) {
        log.debug("Fetching quote: {}", quoteId);
        return quoteService.getQuote(quoteId)
                .map(quote -> ResponseEntity.ok(QuoteResponse.from(quote)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<QuoteResponse>> getAllQuotes() {
        log.debug("Fetching all quotes");
        List<Quote> quotes = quoteService.getAllQuotes();
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<QuoteResponse>> getQuotesByCustomer(@PathVariable String customerId) {
        log.debug("Fetching quotes for customer: {}", customerId);
        List<Quote> quotes = quoteService.getQuotesByCustomer(customerId);
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<List<QuoteResponse>> getQuotesByOpportunity(@PathVariable String opportunityId) {
        log.debug("Fetching quotes for opportunity: {}", opportunityId);
        List<Quote> quotes = quoteService.getQuotesByOpportunity(opportunityId);
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<QuoteResponse>> getQuotesByStatus(@PathVariable String status) {
        log.debug("Fetching quotes by status: {}", status);
        List<Quote> quotes = quoteService.getQuotesByStatus(status);
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @GetMapping("/assigned/{userId}")
    public ResponseEntity<List<QuoteResponse>> getQuotesByAssignedUser(@PathVariable String userId) {
        log.debug("Fetching quotes assigned to user: {}", userId);
        List<Quote> quotes = quoteService.getQuotesByAssignedUser(userId);
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @GetMapping("/expired")
    public ResponseEntity<List<QuoteResponse>> getExpiredQuotes() {
        log.debug("Fetching expired quotes");
        List<Quote> quotes = quoteService.getExpiredQuotes();
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuoteResponse>> searchQuotes(@RequestParam String keyword) {
        log.debug("Searching quotes with keyword: {}", keyword);
        List<Quote> quotes = quoteService.searchQuotes(keyword);
        return ResponseEntity.ok(QuoteResponse.fromList(quotes));
    }

    @DeleteMapping("/{quoteId}")
    public ResponseEntity<Void> deleteQuote(@PathVariable String quoteId) {
        log.info("Deleting quote: {}", quoteId);
        quoteService.deleteQuote(quoteId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/number/{quoteNumber}")
    public ResponseEntity<Boolean> existsByQuoteNumber(@PathVariable String quoteNumber) {
        log.debug("Checking if quote number exists: {}", quoteNumber);
        return ResponseEntity.ok(quoteService.existsByQuoteNumber(quoteNumber));
    }
}
