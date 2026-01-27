package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {

    boolean existsByTenantId(String tenantId);

    List<Tenant> findByUseAt(String useAt);
}
