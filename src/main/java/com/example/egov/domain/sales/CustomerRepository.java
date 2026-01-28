package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {

    Optional<Customer> findByCustomerId(String customerId);

    Optional<Customer> findByCustomerCode(String customerCode);

    boolean existsByCustomerCode(String customerCode);

    List<Customer> findByUseAt(String useAt);

    List<Customer> findByCustomerType(String customerType);

    List<Customer> findByIndustry(String industry);

    @Query("SELECT c FROM Customer c WHERE c.assignedUser.id = :userId")
    List<Customer> findByAssignedUserId(@Param("userId") String userId);

    @Query("SELECT c FROM Customer c WHERE c.branch.branchId = :branchId")
    List<Customer> findByBranchId(@Param("branchId") String branchId);

    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:keyword% OR c.customerCode LIKE %:keyword%")
    List<Customer> searchByKeyword(@Param("keyword") String keyword);

    List<Customer> findByTenantId(String tenantId);
}
