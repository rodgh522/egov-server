package com.example.egov.service.sales;

import com.example.egov.config.multitenancy.TenantContext;
import com.example.egov.domain.sales.Product;
import com.example.egov.domain.sales.ProductRepository;
import com.example.egov.web.sales.dto.ProductCreateRequest;
import com.example.egov.web.sales.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private static final String SYSTEM_TENANT_ID = "SYSTEM";

    private final ProductRepository productRepository;

    private boolean isSystemTenant() {
        return SYSTEM_TENANT_ID.equals(TenantContext.getCurrentTenantId());
    }

    private void verifyProductAccess(Product product) {
        if (!isSystemTenant() && !product.getTenantId().equals(TenantContext.getCurrentTenantId())) {
            throw new AccessDeniedException("Access denied to product: " + product.getProductId());
        }
    }

    @Transactional
    public Product createProduct(ProductCreateRequest request) {
        String tenantId = TenantContext.getCurrentTenantId();

        if (productRepository.existsByProductCode(request.getProductCode())) {
            throw new IllegalArgumentException("Product code already exists: " + request.getProductCode());
        }

        String productId = generateProductId();
        log.info("Creating product: {} for tenant: {}", productId, tenantId);

        Product product = new Product();
        product.setProductId(productId);
        product.setProductCode(request.getProductCode());
        product.setProductName(request.getProductName());
        product.setProductCategory(request.getProductCategory());
        product.setProductType(request.getProductType() != null ? request.getProductType() : "PRODUCT");
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setCostPrice(request.getCostPrice());
        product.setCurrency(request.getCurrency() != null ? request.getCurrency() : "KRW");
        product.setTaxRate(request.getTaxRate() != null ? request.getTaxRate() : new BigDecimal("10.00"));
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setIsActive(request.getIsActive() != null ? request.getIsActive() : "Y");
        product.setUseAt(request.getUseAt() != null ? request.getUseAt() : "Y");
        product.setTenantId(tenantId);

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully: {}", savedProduct.getProductId());

        return savedProduct;
    }

    @Transactional
    public Product updateProduct(String productId, ProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        verifyProductAccess(product);
        log.info("Updating product: {}", productId);

        if (request.getProductCode() != null && !request.getProductCode().equals(product.getProductCode())) {
            if (productRepository.existsByProductCode(request.getProductCode())) {
                throw new IllegalArgumentException("Product code already exists: " + request.getProductCode());
            }
            product.setProductCode(request.getProductCode());
        }
        if (request.getProductName() != null) {
            product.setProductName(request.getProductName());
        }
        if (request.getProductCategory() != null) {
            product.setProductCategory(request.getProductCategory());
        }
        if (request.getProductType() != null) {
            product.setProductType(request.getProductType());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getUnitPrice() != null) {
            product.setUnitPrice(request.getUnitPrice());
        }
        if (request.getCostPrice() != null) {
            product.setCostPrice(request.getCostPrice());
        }
        if (request.getCurrency() != null) {
            product.setCurrency(request.getCurrency());
        }
        if (request.getTaxRate() != null) {
            product.setTaxRate(request.getTaxRate());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }
        if (request.getUseAt() != null) {
            product.setUseAt(request.getUseAt());
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct.getProductId());

        return updatedProduct;
    }

    public Optional<Product> getProduct(String productId) {
        Optional<Product> product = productRepository.findById(productId);
        product.ifPresent(this::verifyProductAccess);
        return product;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findActiveProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByProductCategory(category);
    }

    public List<Product> getProductsByType(String type) {
        return productRepository.findByProductType(type);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    @Transactional
    public void deleteProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        verifyProductAccess(product);

        log.info("Deleting product: {}", productId);
        productRepository.delete(product);
        log.info("Product deleted successfully: {}", productId);
    }

    public boolean existsByProductCode(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }

    private String generateProductId() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String suffix = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "PRD" + timestamp.substring(timestamp.length() - 10) + suffix;
    }
}
