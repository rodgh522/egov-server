package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OpportunityProductRepository extends JpaRepository<OpportunityProduct, Long> {

    @Query("SELECT op FROM OpportunityProduct op WHERE op.opportunity.opportunityId = :opportunityId")
    List<OpportunityProduct> findByOpportunityId(@Param("opportunityId") String opportunityId);

    @Query("SELECT op FROM OpportunityProduct op WHERE op.product.productId = :productId")
    List<OpportunityProduct> findByProductId(@Param("productId") String productId);

    @Query("DELETE FROM OpportunityProduct op WHERE op.opportunity.opportunityId = :opportunityId")
    void deleteByOpportunityId(@Param("opportunityId") String opportunityId);

    List<OpportunityProduct> findByTenantId(String tenantId);
}
