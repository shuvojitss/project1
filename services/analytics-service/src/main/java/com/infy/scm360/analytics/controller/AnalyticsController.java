package com.infy.scm360.analytics.controller;

import com.infy.scm360.analytics.dto.*;
import com.infy.scm360.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Supply Chain 360 Analytics", description = "Executive dashboards, read-model KPI aggregation, and domain performance metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/kpis")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get high-level 360 executive supply chain KPI metrics")
    public ResponseEntity<KpiOverviewDto> getKpiOverview() {
        return ResponseEntity.ok(analyticsService.getKpiOverview());
    }

    @GetMapping("/inventory")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get inventory health analytics, stock turnover, and DIOH")
    public ResponseEntity<InventoryAnalyticsDto> getInventoryAnalytics() {
        return ResponseEntity.ok(analyticsService.getInventoryAnalytics());
    }

    @GetMapping("/procurement")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get procurement spend analytics, category breakdown, and cycle time")
    public ResponseEntity<ProcurementAnalyticsDto> getProcurementAnalytics() {
        return ResponseEntity.ok(analyticsService.getProcurementAnalytics());
    }

    @GetMapping("/suppliers")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get supplier performance, OTIF, and rating scorecards")
    public ResponseEntity<SupplierAnalyticsDto> getSupplierAnalytics() {
        return ResponseEntity.ok(analyticsService.getSupplierAnalytics());
    }

    @GetMapping("/fulfilment")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get customer order fulfilment, fill rate, and order cycle times")
    public ResponseEntity<FulfilmentAnalyticsDto> getFulfilmentAnalytics() {
        return ResponseEntity.ok(analyticsService.getFulfilmentAnalytics());
    }

    @GetMapping("/logistics")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get logistics freight spend, freight cost per kg, and carrier transit metrics")
    public ResponseEntity<LogisticsAnalyticsDto> getLogisticsAnalytics() {
        return ResponseEntity.ok(analyticsService.getLogisticsAnalytics());
    }
}
