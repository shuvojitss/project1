package com.infy.scm360.transport.controller;

import com.infy.scm360.common.dto.PageResponse;
import com.infy.scm360.transport.dto.CarrierDto;
import com.infy.scm360.transport.service.CarrierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carriers")
@Tag(name = "Transportation Carriers", description = "Carrier master management, ratings, and service tiers")
public class CarrierController {

    private final CarrierService carrierService;

    public CarrierController(CarrierService carrierService) {
        this.carrierService = carrierService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LOGISTICS_PLANNER', 'ADMIN')")
    @Operation(summary = "Create carrier partner")
    public ResponseEntity<CarrierDto> createCarrier(@Valid @RequestBody CarrierDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carrierService.createCarrier(dto));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List active carrier partners")
    public ResponseEntity<PageResponse<CarrierDto>> listCarriers(Pageable pageable) {
        Page<CarrierDto> page = carrierService.listCarriers(pageable);
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all active carriers without pagination")
    public ResponseEntity<List<CarrierDto>> listAllCarriers() {
        return ResponseEntity.ok(carrierService.listAllActiveCarriers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get carrier by id")
    public ResponseEntity<CarrierDto> getCarrier(@PathVariable String id) {
        return ResponseEntity.ok(carrierService.getCarrier(id));
    }
}
