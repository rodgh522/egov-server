package com.example.egov.domain.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByProductId(String productId);

    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

    List<Product> findByProductCategory(String productCategory);

    List<Product> findByProductType(String productType);

    List<Product> findByIsActive(String isActive);

    List<Product> findByUseAt(String useAt);

    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:keyword% OR p.productCode LIKE %:keyword%")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE p.isActive = 'Y' AND p.useAt = 'Y'")
    List<Product> findActiveProducts();

    List<Product> findByTenantId(String tenantId);
}
