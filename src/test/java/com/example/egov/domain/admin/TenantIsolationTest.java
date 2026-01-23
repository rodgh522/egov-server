package com.example.egov.domain.admin;

import com.example.egov.config.multitenancy.TenantContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class TenantIsolationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @BeforeEach
    public void setup() {
        // Clear tenant context
        TenantContext.clear();

        // Clean up existing test data to ensure clean state
        userRepository.deleteAll();
        tenantRepository.deleteAll();

        // Create Tenants
        Tenant tenantA = new Tenant();
        tenantA.setTenantId("TENANT_A");
        tenantA.setTenantName("Tenant A");
        tenantRepository.save(tenantA);

        Tenant tenantB = new Tenant();
        tenantB.setTenantId("TENANT_B");
        tenantB.setTenantName("Tenant B");
        tenantRepository.save(tenantB);
    }

    @Test
    public void testTenantIsolation() {
        // 1. Create Data for Tenant A
        TenantContext.setCurrentTenantId("TENANT_A");
        User userA = new User();
        userA.setId("USR-001");
        userA.setUserId("user_a");
        userA.setPassword("password");
        userA.setUserName("User A");
        userA.setTenantId("TENANT_A");
        userA.setUseAt("Y");
        userRepository.save(userA);

        // 2. Create Data for Tenant B
        TenantContext.setCurrentTenantId("TENANT_B");
        User userB = new User();
        userB.setId("USR-002");
        userB.setUserId("user_b");
        userB.setPassword("password");
        userB.setUserName("User B");
        userB.setTenantId("TENANT_B");
        userB.setUseAt("Y");
        userRepository.save(userB);

        // 3. Verify Tenant A can only see Tenant A data
        TenantContext.setCurrentTenantId("TENANT_A");
        List<User> usersA = userRepository.findAll();
        Assertions.assertEquals(1, usersA.size(), "Tenant A should see exactly 1 user");
        Assertions.assertEquals("user_a", usersA.get(0).getUserId());

        // 4. Verify Tenant B can only see Tenant B data
        TenantContext.setCurrentTenantId("TENANT_B");
        List<User> usersB = userRepository.findAll();
        Assertions.assertEquals(1, usersB.size(), "Tenant B should see exactly 1 user");
        Assertions.assertEquals("user_b", usersB.get(0).getUserId());

        // 5. Verify No Tenant Context (Should verify what happens when no tenant is set
        // - likely empty or all depending on filter config.
        // Our Aspect *enables* it if tenantId is present. If not present, filter is NOT
        // enabled, so we might see ALL (2).
        TenantContext.clear();
        List<User> usersAll = userRepository.findAll();
        // Assuming default behavior is seeing all if filter is not enabled
        Assertions.assertEquals(2, usersAll.size(), "Without tenant context, should see all users");
    }
}
