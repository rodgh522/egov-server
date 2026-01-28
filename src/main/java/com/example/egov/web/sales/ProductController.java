package com.example.egov.web.sales;

import com.example.egov.domain.sales.Product;
import com.example.egov.service.sales.ProductService;
import com.example.egov.web.sales.dto.ProductCreateRequest;
import com.example.egov.web.sales.dto.ProductResponse;
import com.example.egov.web.sales.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        log.info("Creating product: {}", request.getProductName());
        Product product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String productId,
            @Valid @RequestBody ProductUpdateRequest request) {
        log.info("Updating product: {}", productId);
        Product product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String productId) {
        log.debug("Fetching product: {}", productId);
        return productService.getProduct(productId)
                .map(product -> ResponseEntity.ok(ProductResponse.from(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(ProductResponse.fromList(products));
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        log.debug("Fetching active products");
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(ProductResponse.fromList(products));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        log.debug("Fetching products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ProductResponse.fromList(products));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ProductResponse>> getProductsByType(@PathVariable String type) {
        log.debug("Fetching products by type: {}", type);
        List<Product> products = productService.getProductsByType(type);
        return ResponseEntity.ok(ProductResponse.fromList(products));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
        log.debug("Searching products with keyword: {}", keyword);
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ProductResponse.fromList(products));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        log.info("Deleting product: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{productCode}")
    public ResponseEntity<Boolean> existsByProductCode(@PathVariable String productCode) {
        log.debug("Checking if product code exists: {}", productCode);
        return ResponseEntity.ok(productService.existsByProductCode(productCode));
    }
}
