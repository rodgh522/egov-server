package com.example.egov.domain.file;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

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
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "tenantId", type = String.class))
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter @Setter
@NoArgsConstructor
public class FileEntity extends BaseEntity {

    @Id
    @Column(name = "ATTACHMENT_FILE_ID", length = 20)
    private String attachmentFileId;

    @Column(name = "USE_AT", length = 1)
    private String useAt;

    @OneToMany(mappedBy = "fileEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileDetail> fileDetails = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.useAt == null) {
            this.useAt = "Y";
        }
    }
}
