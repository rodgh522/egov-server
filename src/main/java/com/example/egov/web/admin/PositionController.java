package com.example.egov.web.admin;

import com.example.egov.domain.admin.Position;
import com.example.egov.service.admin.PositionService;
import com.example.egov.web.admin.dto.PositionCreateRequest;
import com.example.egov.web.admin.dto.PositionResponse;
import com.example.egov.web.admin.dto.PositionUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Position management.
 *
 * Access control:
 * - SYSTEM tenant can access all positions across tenants
 * - Other tenants can only access their own positions
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping
    public ResponseEntity<PositionResponse> createPosition(@Valid @RequestBody PositionCreateRequest request) {
        log.info("Creating position: {}", request.getPositionId());

        Position position = positionService.createPosition(request);
        PositionResponse response = PositionResponse.from(position);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{positionId}")
    public ResponseEntity<PositionResponse> updatePosition(
            @PathVariable String positionId,
            @Valid @RequestBody PositionUpdateRequest request) {
        log.info("Updating position: {}", positionId);

        Position position = positionService.updatePosition(positionId, request);
        PositionResponse response = PositionResponse.from(position);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<PositionResponse> getPosition(@PathVariable String positionId) {
        log.debug("Fetching position: {}", positionId);

        return positionService.getPosition(positionId)
                .map(position -> ResponseEntity.ok(PositionResponse.from(position)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PositionResponse>> getAllPositions() {
        log.debug("Fetching all positions");

        List<Position> positions = positionService.getAllPositions();
        List<PositionResponse> responses = PositionResponse.fromList(positions);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PositionResponse>> getActivePositions() {
        log.debug("Fetching active positions");

        List<Position> positions = positionService.getActivePositions();
        List<PositionResponse> responses = PositionResponse.fromList(positions);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get positions by tenant ID (SYSTEM tenant only).
     */
    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<PositionResponse>> getPositionsByTenant(@PathVariable String tenantId) {
        log.debug("Fetching positions for tenant: {}", tenantId);

        List<Position> positions = positionService.getPositionsByTenant(tenantId);
        List<PositionResponse> responses = PositionResponse.fromList(positions);

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{positionId}")
    public ResponseEntity<Void> deletePosition(@PathVariable String positionId) {
        log.info("Deleting position: {}", positionId);

        positionService.deletePosition(positionId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{positionId}")
    public ResponseEntity<Boolean> existsByPositionId(@PathVariable String positionId) {
        log.debug("Checking if position exists: {}", positionId);

        boolean exists = positionService.existsByPositionId(positionId);

        return ResponseEntity.ok(exists);
    }

    @GetMapping("/exists/code/{positionCode}")
    public ResponseEntity<Boolean> existsByPositionCode(@PathVariable String positionCode) {
        log.debug("Checking if position code exists: {}", positionCode);

        boolean exists = positionService.existsByPositionCode(positionCode);

        return ResponseEntity.ok(exists);
    }
}
