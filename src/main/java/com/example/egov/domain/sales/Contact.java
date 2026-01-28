package com.example.egov.domain.sales;

import com.example.egov.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "CONTACTS")
@Filter(name = "tenantFilter", condition = "TENANT_ID = :tenantId")
@Getter
@Setter
@NoArgsConstructor
public class Contact extends BaseEntity {

    @Id
    @Column(name = "CONTACT_ID", length = 20)
    private String contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @Column(name = "CONTACT_NAME", length = 100, nullable = false)
    private String contactName;

    @Column(name = "CONTACT_TITLE", length = 100)
    private String contactTitle;

    @Column(name = "DEPARTMENT", length = 100)
    private String department;

    @Column(name = "PHONE", length = 30)
    private String phone;

    @Column(name = "MOBILE", length = 30)
    private String mobile;

    @Column(name = "EMAIL", length = 100)
    private String email;

    @Column(name = "IS_PRIMARY", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String isPrimary = "N";

    @Column(name = "USE_AT", length = 1)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String useAt = "Y";

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;
}
