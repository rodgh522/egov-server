package com.example.egov.domain.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "FILES")
@Getter @Setter
@NoArgsConstructor
public class FileEntity {

    @Id
    @Column(name = "ATTACHMENT_FILE_ID", length = 20)
    private String atchFileId;

    @Column(name = "CREATED_DATETIME")
    private LocalDateTime createDt;

    @Column(name = "USE_AT", length = 1)
    private String useAt;

    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;

    @OneToMany(mappedBy = "fileEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileDetail> fileDetails = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createDt = LocalDateTime.now();
        if (this.useAt == null) this.useAt = "Y";
    }
}
