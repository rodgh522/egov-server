package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, String> {

    Optional<Quote> findByQuoteId(String quoteId);

    Optional<Quote> findByQuoteNumber(String quoteNumber);

    boolean existsByQuoteNumber(String quoteNumber);

    @Query("SELECT q FROM Quote q WHERE q.customer.customerId = :customerId")
    List<Quote> findByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT q FROM Quote q WHERE q.opportunity.opportunityId = :opportunityId")
    List<Quote> findByOpportunityId(@Param("opportunityId") String opportunityId);

    List<Quote> findByQuoteStatus(String quoteStatus);

    List<Quote> findByUseAt(String useAt);

    @Query("SELECT q FROM Quote q WHERE q.validUntil < :date AND q.quoteStatus = 'SENT'")
    List<Quote> findExpiredQuotes(@Param("date") LocalDate date);

    @Query("SELECT q FROM Quote q WHERE q.assignedUser.id = :userId")
    List<Quote> findByAssignedUserId(@Param("userId") String userId);

    @Query("SELECT q FROM Quote q WHERE q.quoteNumber LIKE %:keyword%")
    List<Quote> searchByKeyword(@Param("keyword") String keyword);

    List<Quote> findByTenantId(String tenantId);
}
