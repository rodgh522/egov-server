package com.example.egov.domain.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUserId(String userId);

    /**
     * Find user by userId and tenantId using native query (bypasses Hibernate tenant filter)
     * Used for login authentication
     */
    @Query(value = "SELECT * FROM USERS WHERE USER_ID = :userId AND TENANT_ID = :tenantId AND USE_AT = 'Y'",
            nativeQuery = true)
    Optional<User> findByUserIdAndTenantIdNative(@Param("userId") String userId,
                                                  @Param("tenantId") String tenantId);

    /**
     * Check if userId already exists
     */
    boolean existsByUserId(String userId);

    /**
     * Find all active users with pagination
     */
    Page<User> findByUseAt(String useAt, Pageable pageable);

    /**
     * Find user by esntlId and check if active
     */
    Optional<User> findByIdAndUseAt(String id, String useAt);
}
