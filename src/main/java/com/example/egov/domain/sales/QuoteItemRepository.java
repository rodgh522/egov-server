package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteItemRepository extends JpaRepository<QuoteItem, Long> {

    @Query("SELECT qi FROM QuoteItem qi WHERE qi.quote.quoteId = :quoteId ORDER BY qi.itemOrder ASC")
    List<QuoteItem> findByQuoteIdOrderByItemOrder(@Param("quoteId") String quoteId);

    @Query("SELECT qi FROM QuoteItem qi WHERE qi.product.productId = :productId")
    List<QuoteItem> findByProductId(@Param("productId") String productId);

    @Query("DELETE FROM QuoteItem qi WHERE qi.quote.quoteId = :quoteId")
    void deleteByQuoteId(@Param("quoteId") String quoteId);

    List<QuoteItem> findByTenantId(String tenantId);
}
