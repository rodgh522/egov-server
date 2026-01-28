package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalesTargetRepository extends JpaRepository<SalesTarget, String> {

    Optional<SalesTarget> findByTargetId(String targetId);

    List<SalesTarget> findByTargetYear(Integer targetYear);

    List<SalesTarget> findByTargetYearAndTargetMonth(Integer targetYear, Integer targetMonth);

    List<SalesTarget> findByTargetType(String targetType);

    @Query("SELECT st FROM SalesTarget st WHERE st.user.id = :userId")
    List<SalesTarget> findByUserId(@Param("userId") String userId);

    @Query("SELECT st FROM SalesTarget st WHERE st.branch.branchId = :branchId")
    List<SalesTarget> findByBranchId(@Param("branchId") String branchId);

    @Query("SELECT st FROM SalesTarget st WHERE st.user.id = :userId AND st.targetYear = :year")
    List<SalesTarget> findByUserIdAndYear(
            @Param("userId") String userId,
            @Param("year") Integer year);

    @Query("SELECT st FROM SalesTarget st WHERE st.branch.branchId = :branchId AND st.targetYear = :year")
    List<SalesTarget> findByBranchIdAndYear(
            @Param("branchId") String branchId,
            @Param("year") Integer year);

    @Query("SELECT st FROM SalesTarget st WHERE st.user IS NULL AND st.branch IS NULL AND st.targetYear = :year")
    List<SalesTarget> findCompanyTargetsByYear(@Param("year") Integer year);

    List<SalesTarget> findByTenantId(String tenantId);
}
