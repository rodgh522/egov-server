package com.example.egov.domain.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "USERS") // Simplified from COMTNUSER
@Getter @Setter
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "ESNTL_ID", length = 20) // eGov standard User ID field name
    private String id;

    @Column(name = "USER_ID", length = 20, unique = true)
    private String userId;

    @Column(name = "PASSWORD", length = 200)
    private String password;

    @Column(name = "USER_NAME", length = 60)
    private String userNm;

    @Column(name = "EMAIL", length = 50)
    private String email;

    @Column(name = "ZIP_CODE", length = 6)
    private String userZip;

    @Column(name = "USER_ADDRESS", length = 100)
    private String userAdres;

    @Column(name = "USER_EMAIL", length = 50)
    private String userEmail;

    @Column(name = "GROUP_ID", length = 20)
    private String groupId; 

    @Column(name = "ORGANIZATION_ID", length = 20)
    private String orgnztId;

    @Column(name = "INSTITUTION_CODE", length = 8)
    private String pstinstCode;

    @Column(name = "STATUS_CODE", length = 15)
    private String emplyrSttusCode; // P: Portal, S: System

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    private Role role;

    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;

    @Column(name = "SUBSCRIBED_DATE")
    private LocalDateTime sbscrbDe;
    
    @PrePersist
    public void prePersist() {
        this.sbscrbDe = LocalDateTime.now();
    }
}
