package com.example.egov.config;

import com.example.egov.config.multitenancy.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration for the application.
 *
 * This configuration:
 * - Registers the TenantInterceptor for multi-tenancy support
 * - Can be extended with additional web-layer configurations
 *
 * The TenantInterceptor will run on all requests except excluded paths.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    /**
     * Registers interceptors with the application.
     *
     * Currently registers:
     * - TenantInterceptor: Extracts and sets tenant ID from authenticated user
     *
     * Excluded paths (interceptor will not run):
     * - /actuator/** - Spring Boot Actuator endpoints
     * - /swagger-ui/** - Swagger UI resources
     * - /v3/api-docs/** - OpenAPI documentation
     * - /error - Error handling endpoint
     * - /favicon.ico - Favicon requests
     *
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/error",
                        "/favicon.ico"
                );
    }
}
