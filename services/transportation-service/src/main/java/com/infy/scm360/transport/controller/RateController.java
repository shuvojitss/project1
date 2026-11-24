package com.infy.scm360.transport.controller;

import com.infy.scm360.transport.dto.RateCalculationRequest;
import com.infy.scm360.transport.dto.RateQuoteDto;
import com.infy.scm360.transport.service.RateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rates")
@Tag(name = "Transportation Rate Calculator", description = "Dynamic multi-carrier rate calculation and tariff selection")
public class RateController {

    private final RateService rateService;

    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/calculate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Calculate and rank freight shipping rates across eligible carriers")
    public ResponseEntity<List<RateQuoteDto>> calculateRates(@Valid @RequestBody RateCalculationRequest request) {
        return ResponseEntity.ok(rateService.calculateBestRates(request));
    }
}
