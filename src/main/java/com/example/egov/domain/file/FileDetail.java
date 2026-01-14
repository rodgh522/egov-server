package com.example.egov.domain.file;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "FILE_DETAILS")
@Getter @Setter
@NoArgsConstructor
public class FileDetail {
    
    // Composite Key handling in standard eGov is (ATCH_FILE_ID, FILE_SN).
    // For simplicity in JPA, we can use an ID class or a surrogate key. 
    // Using surrogate key 'DETAIL_ID' and mapping ATCH_FILE_ID as ManyToOne.
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DETAIL_ID") 
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_FILE_ID")
    private FileEntity fileEntity;

    @Column(name = "FILE_SEQUENCE")
    private Integer fileSn;

    @Column(name = "FILE_STORED_PATH", length = 2000)
    private String fileStreCours;

    @Column(name = "STORED_FILE_NAME", length = 255)
    private String streFileNm;

    @Column(name = "ORIGINAL_FILE_NAME", length = 255)
    private String orignlFileNm;

    @Column(name = "FILE_EXTENSION", length = 20)
    private String fileExtsn;

    @Column(name = "FILE_CONTENT", columnDefinition = "TEXT")
    private String fileCn;

    @Column(name = "FILE_SIZE")
    private Long fileSize;
}
