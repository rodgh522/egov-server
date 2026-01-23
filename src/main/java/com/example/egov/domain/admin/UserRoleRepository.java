package com.example.egov.domain.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUser_Id(String userId); // using User.id (esntlId)
}
