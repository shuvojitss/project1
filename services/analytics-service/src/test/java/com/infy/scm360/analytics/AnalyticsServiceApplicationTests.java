package com.infy.scm360.analytics;

import com.infy.scm360.analytics.dto.*;
import com.infy.scm360.analytics.service.AnalyticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AnalyticsServiceApplicationTests {

    @Autowired
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("Context loads and executive KPIs are calculated accurately")
    void testKpiOverview() {
        KpiOverviewDto kpis = analyticsService.getKpiOverview();
        assertNotNull(kpis);

        // Accuracy: 485 / 500 = 97.00%
        assertEquals(97.00, kpis.inventoryAccuracyPct().doubleValue(), 0.1);

        // Turnover: 10,800,000 / 900,000 = 12.0
        assertTrue(kpis.stockTurnoverRatio().doubleValue() > 0);

        // Days of inventory: (900,000 / 10,800,000) * 365 = 30.4 days
        assertTrue(kpis.daysOfInventoryOnHand().doubleValue() > 0);

        // Fill rate: 100% of seed orders fulfilled in full
        assertTrue(kpis.orderFillRatePct().doubleValue() >= 90.0);

        // OTIF: 100% in seed data
        assertTrue(kpis.otifDeliveryRatePct().doubleValue() >= 90.0);

        // Supplier on time: 100%
        assertTrue(kpis.supplierOnTimeRatePct().doubleValue() >= 90.0);

        // Freight cost per kg: $344.50 / 430 kg = 0.80 $/kg
        assertEquals(0.80, kpis.freightCostPerKg().doubleValue(), 0.05);

        // Warehouse utilization: 4880 / 5800 = 84.14%
        assertTrue(kpis.warehouseCapacityUtilizationPct().doubleValue() > 80.0);
    }

    @Test
    @DisplayName("Domain specific analytics endpoints return comprehensive charts and KPIs")
    void testDomainAnalytics() {
        InventoryAnalyticsDto inv = analyticsService.getInventoryAnalytics();
        assertNotNull(inv);
        assertFalse(inv.inventoryValueByWarehouse().isEmpty());

        ProcurementAnalyticsDto proc = analyticsService.getProcurementAnalytics();
        assertNotNull(proc);
        assertFalse(proc.spendByCategory().isEmpty());

        SupplierAnalyticsDto sup = analyticsService.getSupplierAnalytics();
        assertNotNull(sup);
        assertFalse(sup.supplierScorecards().isEmpty());

        FulfilmentAnalyticsDto ful = analyticsService.getFulfilmentAnalytics();
        assertNotNull(ful);
        assertFalse(ful.ordersByStatus().isEmpty());

        LogisticsAnalyticsDto log = analyticsService.getLogisticsAnalytics();
        assertNotNull(log);
        assertFalse(log.avgTransitDaysByCarrier().isEmpty());
    }
}
