package com.example.egov.domain.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * FileDetail - represents individual file details.
 *
 * This is a detail entity that belongs to FileEntity.
 * Does NOT extend BaseEntity as it doesn't have its own TENANT_ID.
 * Tenant isolation is handled through the parent FileEntity relationship.
 */
@Entity
@Table(name = "FILE_DETAILS")
@Getter @Setter
@NoArgsConstructor
public class FileDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DETAIL_ID")
    private Long detailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_FILE_ID")
    private FileEntity fileEntity;

    @Column(name = "FILE_SEQUENCE")
    private Integer fileSequence;

    @Column(name = "FILE_STORED_PATH", length = 2000)
    private String fileStoredPath;

    @Column(name = "STORED_FILE_NAME", length = 255)
    private String storedFileName;

    @Column(name = "ORIGINAL_FILE_NAME", length = 255)
    private String originalFileName;

    @Column(name = "FILE_EXTENSION", length = 20)
    private String fileExtension;

    @Column(name = "FILE_CONTENT", columnDefinition = "TEXT")
    private String fileContent;

    @Column(name = "FILE_SIZE")
    private Long fileSize;
}
