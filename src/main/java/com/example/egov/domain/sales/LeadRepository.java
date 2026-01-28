package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, String> {

    Optional<Lead> findByLeadId(String leadId);

    List<Lead> findByLeadStatus(String leadStatus);

    List<Lead> findByLeadSource(String leadSource);

    List<Lead> findByUseAt(String useAt);

    @Query("SELECT l FROM Lead l WHERE l.assignedUser.id = :userId")
    List<Lead> findByAssignedUserId(@Param("userId") String userId);

    @Query("SELECT l FROM Lead l WHERE l.leadName LIKE %:keyword% OR l.companyName LIKE %:keyword% OR l.email LIKE %:keyword%")
    List<Lead> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT l FROM Lead l WHERE l.leadStatus NOT IN ('CONVERTED', 'LOST')")
    List<Lead> findActiveLeads();

    @Query("SELECT l FROM Lead l WHERE l.convertedCustomer IS NOT NULL")
    List<Lead> findConvertedLeads();

    List<Lead> findByTenantId(String tenantId);
}
