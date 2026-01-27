package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, String> {

    boolean existsByBranchId(String branchId);

    boolean existsByBranchCode(String branchCode);

    Optional<Branch> findByBranchCode(String branchCode);

    List<Branch> findByTenantId(String tenantId);

    List<Branch> findByUseAt(String useAt);

    List<Branch> findByTenantIdAndUseAt(String tenantId, String useAt);

    List<Branch> findByParentBranch(Branch parentBranch);
}
