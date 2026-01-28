package com.example.egov.web.sales;

import com.example.egov.domain.sales.PipelineStage;
import com.example.egov.service.sales.PipelineStageService;
import com.example.egov.web.sales.dto.PipelineStageCreateRequest;
import com.example.egov.web.sales.dto.PipelineStageResponse;
import com.example.egov.web.sales.dto.PipelineStageUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/pipeline-stages")
@RequiredArgsConstructor
public class PipelineStageController {

    private final PipelineStageService pipelineStageService;

    @PostMapping
    public ResponseEntity<PipelineStageResponse> createStage(@Valid @RequestBody PipelineStageCreateRequest request) {
        log.info("Creating pipeline stage: {}", request.getStageName());
        PipelineStage stage = pipelineStageService.createStage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(PipelineStageResponse.from(stage));
    }

    @PutMapping("/{stageId}")
    public ResponseEntity<PipelineStageResponse> updateStage(
            @PathVariable String stageId,
            @Valid @RequestBody PipelineStageUpdateRequest request) {
        log.info("Updating pipeline stage: {}", stageId);
        PipelineStage stage = pipelineStageService.updateStage(stageId, request);
        return ResponseEntity.ok(PipelineStageResponse.from(stage));
    }

    @GetMapping("/{stageId}")
    public ResponseEntity<PipelineStageResponse> getStage(@PathVariable String stageId) {
        log.debug("Fetching pipeline stage: {}", stageId);
        return pipelineStageService.getStage(stageId)
                .map(stage -> ResponseEntity.ok(PipelineStageResponse.from(stage)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<PipelineStageResponse>> getAllStages() {
        log.debug("Fetching all pipeline stages");
        List<PipelineStage> stages = pipelineStageService.getAllStages();
        return ResponseEntity.ok(PipelineStageResponse.fromList(stages));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PipelineStageResponse>> getActiveStagesOrdered() {
        log.debug("Fetching active pipeline stages ordered");
        List<PipelineStage> stages = pipelineStageService.getActiveStagesOrdered();
        return ResponseEntity.ok(PipelineStageResponse.fromList(stages));
    }

    @GetMapping("/won")
    public ResponseEntity<PipelineStageResponse> getWonStage() {
        log.debug("Fetching won pipeline stage");
        return pipelineStageService.getWonStage()
                .map(stage -> ResponseEntity.ok(PipelineStageResponse.from(stage)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lost")
    public ResponseEntity<PipelineStageResponse> getLostStage() {
        log.debug("Fetching lost pipeline stage");
        return pipelineStageService.getLostStage()
                .map(stage -> ResponseEntity.ok(PipelineStageResponse.from(stage)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{stageId}")
    public ResponseEntity<Void> deleteStage(@PathVariable String stageId) {
        log.info("Deleting pipeline stage: {}", stageId);
        pipelineStageService.deleteStage(stageId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/code/{stageCode}")
    public ResponseEntity<Boolean> existsByStageCode(@PathVariable String stageCode) {
        log.debug("Checking if stage code exists: {}", stageCode);
        return ResponseEntity.ok(pipelineStageService.existsByStageCode(stageCode));
    }
}
