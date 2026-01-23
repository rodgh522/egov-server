package com.example.egov.web.admin.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating an existing Menu.
 *
 * All fields are optional to support partial updates.
 */
@Getter
@Setter
public class MenuUpdateRequest {

    @Size(max = 100, message = "Menu name must not exceed 100 characters")
    private String menuName;

    @Size(max = 20, message = "Menu type must not exceed 20 characters")
    @Pattern(regexp = "^(MENU|FOLDER|LINK)$", message = "Menu type must be MENU, FOLDER, or LINK")
    private String menuType;

    @Size(max = 255, message = "Menu path must not exceed 255 characters")
    private String menuPath;

    @Size(max = 255, message = "API endpoint must not exceed 255 characters")
    private String apiEndpoint;

    @Size(max = 50, message = "Icon name must not exceed 50 characters")
    private String iconName;

    private Long upperMenuNo;

    private Integer menuOrder;

    @Size(max = 255, message = "Menu description must not exceed 255 characters")
    private String menuDescription;

    @Pattern(regexp = "^[YN]$", message = "isVisible must be Y or N")
    private String isVisible;

    @Pattern(regexp = "^[YN]$", message = "isActive must be Y or N")
    private String isActive;
}
