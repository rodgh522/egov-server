package com.example.egov.domain.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MENUS")
@Getter @Setter
@NoArgsConstructor
public class Menu {
    @Id
    @Column(name = "MENU_NO")
    private Long menuNo;

    @Column(name = "MENU_NAME", length = 60)
    private String menuNm;

    @Column(name = "PROGRAM_FILE_NAME", length = 60)
    private String progrmFileNm;

    @Column(name = "MENU_ORDER")
    private Integer menuOrdr;

    @Column(name = "MENU_DESCRIPTION", length = 250)
    private String menuDc;

    @Column(name = "UPPER_MENU_NO")
    private Long upperMenuNo;

    @Column(name = "RELATE_IMAGE_PATH", length = 100)
    private String relateImagePath;

    @Column(name = "RELATED_IMAGE_NAME", length = 60)
    private String relateImageNm;
    
    @Column(name = "TENANT_ID", length = 20)
    private String tenantId;
}
