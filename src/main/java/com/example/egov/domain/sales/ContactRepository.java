package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, String> {

    Optional<Contact> findByContactId(String contactId);

    @Query("SELECT c FROM Contact c WHERE c.customer.customerId = :customerId")
    List<Contact> findByCustomerId(@Param("customerId") String customerId);

    @Query("SELECT c FROM Contact c WHERE c.customer.customerId = :customerId AND c.isPrimary = 'Y'")
    Optional<Contact> findPrimaryContactByCustomerId(@Param("customerId") String customerId);

    List<Contact> findByUseAt(String useAt);

    @Query("SELECT c FROM Contact c WHERE c.contactName LIKE %:keyword% OR c.email LIKE %:keyword%")
    List<Contact> searchByKeyword(@Param("keyword") String keyword);

    List<Contact> findByTenantId(String tenantId);
}
