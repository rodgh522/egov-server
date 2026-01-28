package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, String> {

    Optional<Opportunity> findByOpportunityId(String opportunityId);

    @Query("SELECT o FROM Opportunity o WHERE o.customer.customerId = :customerId")
    List<Opportunity> findByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT o FROM Opportunity o WHERE o.stage.stageId = :stageId")
    List<Opportunity> findByStageId(@Param("stageId") String stageId);

    @Query("SELECT o FROM Opportunity o WHERE o.assignedUser.id = :userId")
    List<Opportunity> findByAssignedUserId(@Param("userId") String userId);

    @Query("SELECT o FROM Opportunity o WHERE o.branch.branchId = :branchId")
    List<Opportunity> findByBranchId(@Param("branchId") String branchId);

    List<Opportunity> findByUseAt(String useAt);

    @Query("SELECT o FROM Opportunity o WHERE o.expectedCloseDate BETWEEN :startDate AND :endDate")
    List<Opportunity> findByExpectedCloseDateBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Opportunity o WHERE o.stage.isWon = 'Y'")
    List<Opportunity> findWonOpportunities();

    @Query("SELECT o FROM Opportunity o WHERE o.stage.isLost = 'Y'")
    List<Opportunity> findLostOpportunities();

    @Query("SELECT o FROM Opportunity o WHERE o.stage.isWon = 'N' AND o.stage.isLost = 'N'")
    List<Opportunity> findOpenOpportunities();

    @Query("SELECT o FROM Opportunity o WHERE o.opportunityName LIKE %:keyword%")
    List<Opportunity> searchByKeyword(@Param("keyword") String keyword);

    List<Opportunity> findByTenantId(String tenantId);
}
