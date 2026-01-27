package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PositionRepository extends JpaRepository<Position, String> {

    boolean existsByPositionId(String positionId);

    boolean existsByPositionCode(String positionCode);

    Optional<Position> findByPositionCode(String positionCode);

    List<Position> findByTenantId(String tenantId);

    List<Position> findByUseAt(String useAt);

    List<Position> findByTenantIdAndUseAt(String tenantId, String useAt);

    List<Position> findAllByOrderByPositionLevelAsc();
}
