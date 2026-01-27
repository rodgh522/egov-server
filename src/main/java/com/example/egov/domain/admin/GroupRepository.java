package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, String> {

    boolean existsByGroupId(String groupId);

    boolean existsByGroupCode(String groupCode);

    Optional<Group> findByGroupCode(String groupCode);

    List<Group> findByTenantId(String tenantId);

    List<Group> findByUseAt(String useAt);

    List<Group> findByTenantIdAndUseAt(String tenantId, String useAt);

    List<Group> findByBranch(Branch branch);

    List<Group> findByBranch_BranchId(String branchId);
}
