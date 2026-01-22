package com.example.egov.domain.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * FileEntity - represents file metadata in the system.
 *
 * Contains file attachment information and related file details.
 * Tenant filtering is applied via Hibernate filter.
 */
@Entity
@Table(name = "FILES")

@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class FileEntity {

    @Id
    @Column(name = "ATTACHMENT_FILE_ID", length = 20)
    private String attachmentFileId;

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt;

    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;

    @CreatedDate
    @Column(name = "CREATED_DATETIME", nullable = false, updatable = false)
    private LocalDateTime createdDateTime;

    @OneToMany(mappedBy = "fileEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileDetail> fileDetails = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.useAt == null) {
            this.useAt = "Y";
        }
    }
}
