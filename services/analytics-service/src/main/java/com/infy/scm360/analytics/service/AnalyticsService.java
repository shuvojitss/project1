package com.infy.scm360.analytics.service;

import com.infy.scm360.analytics.domain.DimSupplier;
import com.infy.scm360.analytics.domain.DimWarehouse;
import com.infy.scm360.analytics.dto.*;
import com.infy.scm360.analytics.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final FactOrderRepository orderRepo;
    private final FactPoRepository poRepo;
    private final FactShipmentRepository shipmentRepo;
    private final FactStockSnapshotRepository snapshotRepo;
    private final FactCycleCountRepository cycleCountRepo;
    private final DimSupplierRepository supplierRepo;
    private final DimWarehouseRepository warehouseRepo;

    public AnalyticsService(
            FactOrderRepository orderRepo,
            FactPoRepository poRepo,
            FactShipmentRepository shipmentRepo,
            FactStockSnapshotRepository snapshotRepo,
            FactCycleCountRepository cycleCountRepo,
            DimSupplierRepository supplierRepo,
            DimWarehouseRepository warehouseRepo
    ) {
        this.orderRepo = orderRepo;
        this.poRepo = poRepo;
        this.shipmentRepo = shipmentRepo;
        this.snapshotRepo = snapshotRepo;
        this.cycleCountRepo = cycleCountRepo;
        this.supplierRepo = supplierRepo;
        this.warehouseRepo = warehouseRepo;
    }

    public KpiOverviewDto getKpiOverview() {
        return new KpiOverviewDto(
                calculateInventoryAccuracy(),
                calculateStockTurnover(),
                calculateDaysOfInventoryOnHand(),
                calculateOrderFillRate(),
                calculateOtifDeliveryRate(),
                calculateSupplierOnTimeRate(),
                calculateAvgPoCycleTime(),
                calculateAvgOrderCycleTime(),
                calculateFreightCostPerKg(),
                calculateWarehouseUtilization()
        );
    }

    public InventoryAnalyticsDto getInventoryAnalytics() {
        Double avgInv = snapshotRepo.getAverageInventoryValue();
        BigDecimal totalInvVal = avgInv != null ? BigDecimal.valueOf(avgInv).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<ChartDataPoint> invByWarehouse = new ArrayList<>();
        List<DimWarehouse> warehouses = warehouseRepo.findAll();
        for (DimWarehouse w : warehouses) {
            invByWarehouse.add(new ChartDataPoint(w.getName(), BigDecimal.valueOf(w.getTotalBins() * 450.0).setScale(2, RoundingMode.HALF_UP)));
        }

        List<ChartDataPoint> accuracyTrend = List.of(
                new ChartDataPoint("Aug", BigDecimal.valueOf(95.2)),
                new ChartDataPoint("Sep", BigDecimal.valueOf(96.1)),
                new ChartDataPoint("Oct", BigDecimal.valueOf(96.8)),
                new ChartDataPoint("Nov", calculateInventoryAccuracy())
        );

        return new InventoryAnalyticsDto(
                calculateInventoryAccuracy(),
                calculateStockTurnover(),
                calculateDaysOfInventoryOnHand(),
                totalInvVal,
                calculateWarehouseUtilization(),
                invByWarehouse,
                accuracyTrend
        );
    }

    public ProcurementAnalyticsDto getProcurementAnalytics() {
        List<Object[]> spendCat = poRepo.getSpendByCategory();
        List<ChartDataPoint> catPoints = new ArrayList<>();
        BigDecimal totalSpend = BigDecimal.ZERO;

        for (Object[] row : spendCat) {
            String cat = (String) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            catPoints.add(new ChartDataPoint(cat != null ? cat : "General", amt));
            totalSpend = totalSpend.add(amt);
        }

        List<Object[]> spendSup = poRepo.getSpendBySupplier();
        List<ChartDataPoint> supPoints = new ArrayList<>();
        for (Object[] row : spendSup) {
            String sup = (String) row[0];
            BigDecimal amt = (BigDecimal) row[1];
            supPoints.add(new ChartDataPoint(sup, amt));
        }

        return new ProcurementAnalyticsDto(
                totalSpend,
                calculateAvgPoCycleTime(),
                calculateSupplierOnTimeRate(),
                catPoints,
                supPoints
        );
    }

    public SupplierAnalyticsDto getSupplierAnalytics() {
        List<DimSupplier> suppliers = supplierRepo.findAll();
        List<ChartDataPoint> scorecards = new ArrayList<>();
        BigDecimal totalRating = BigDecimal.ZERO;

        for (DimSupplier s : suppliers) {
            scorecards.add(new ChartDataPoint(s.getName(), s.getRating(), s.getTier()));
            totalRating = totalRating.add(s.getRating());
        }

        BigDecimal avgRating = !suppliers.isEmpty()
                ? totalRating.divide(BigDecimal.valueOf(suppliers.size()), 2, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(5.0);

        List<ChartDataPoint> onTimeBySup = List.of(
                new ChartDataPoint("Apex Global", BigDecimal.valueOf(92.5)),
                new ChartDataPoint("Zenith Tooling", BigDecimal.valueOf(88.0)),
                new ChartDataPoint("Omicron Sensors", BigDecimal.valueOf(95.0))
        );

        return new SupplierAnalyticsDto(
                avgRating,
                calculateSupplierOnTimeRate(),
                scorecards,
                onTimeBySup
        );
    }

    public FulfilmentAnalyticsDto getFulfilmentAnalytics() {
        long totalOrders = orderRepo.count();
        long delivered = orderRepo.countByStatus("DELIVERED");
        long shipped = orderRepo.countByStatus("SHIPPED");
        long confirmed = orderRepo.countByStatus("CONFIRMED");
        long backordered = orderRepo.countByStatus("BACKORDERED");

        List<ChartDataPoint> statusPoints = List.of(
                new ChartDataPoint("DELIVERED", BigDecimal.valueOf(delivered)),
                new ChartDataPoint("SHIPPED", BigDecimal.valueOf(shipped)),
                new ChartDataPoint("CONFIRMED", BigDecimal.valueOf(confirmed)),
                new ChartDataPoint("BACKORDERED", BigDecimal.valueOf(backordered))
        );

        List<ChartDataPoint> cycleByWh = List.of(
                new ChartDataPoint("Central Hub (Chicago)", BigDecimal.valueOf(22.5)),
                new ChartDataPoint("West Coast (San Jose)", BigDecimal.valueOf(28.0)),
                new ChartDataPoint("Southern Crossdock (Dallas)", BigDecimal.valueOf(24.0))
        );

        return new FulfilmentAnalyticsDto(
                calculateOrderFillRate(),
                calculateAvgOrderCycleTime(),
                delivered,
                backordered,
                statusPoints,
                cycleByWh
        );
    }

    public LogisticsAnalyticsDto getLogisticsAnalytics() {
        Double totalSpend = shipmentRepo.getTotalFreightSpend();
        Double totalWeight = shipmentRepo.getTotalWeightShippedKg();

        BigDecimal spend = totalSpend != null ? BigDecimal.valueOf(totalSpend).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        BigDecimal weight = totalWeight != null ? BigDecimal.valueOf(totalWeight).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<Object[]> transitByCarrier = shipmentRepo.getAverageTransitDaysByCarrier();
        List<ChartDataPoint> transitPoints = new ArrayList<>();
        for (Object[] row : transitByCarrier) {
            String carrier = (String) row[0];
            Double days = (Double) row[1];
            transitPoints.add(new ChartDataPoint(carrier, BigDecimal.valueOf(days).setScale(1, RoundingMode.HALF_UP)));
        }

        List<ChartDataPoint> carrierShipmentCounts = List.of(
                new ChartDataPoint("FedEx Freight", BigDecimal.valueOf(45)),
                new ChartDataPoint("UPS Ground", BigDecimal.valueOf(38)),
                new ChartDataPoint("DHL Global", BigDecimal.valueOf(18))
        );

        return new LogisticsAnalyticsDto(
                spend,
                weight,
                calculateFreightCostPerKg(),
                calculateOtifDeliveryRate(),
                transitPoints,
                carrierShipmentCounts
        );
    }

    // Mathematical formula implementations:
    public BigDecimal calculateInventoryAccuracy() {
        List<Object[]> totals = cycleCountRepo.getCycleCountAccuracyTotals();
        if (totals != null && !totals.isEmpty() && totals.get(0)[0] != null && totals.get(0)[1] != null) {
            long accurate = ((Number) totals.get(0)[0]).longValue();
            long total = ((Number) totals.get(0)[1]).longValue();
            if (total > 0) {
                return BigDecimal.valueOf((double) accurate / total * 100.0).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(98.50);
    }

    public BigDecimal calculateStockTurnover() {
        Double avgInv = snapshotRepo.getAverageInventoryValue();
        Double totalCogs = snapshotRepo.getTotalCogs();
        if (avgInv != null && totalCogs != null && avgInv > 0) {
            return BigDecimal.valueOf(totalCogs / avgInv).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(4.25);
    }

    public BigDecimal calculateDaysOfInventoryOnHand() {
        Double avgInv = snapshotRepo.getAverageInventoryValue();
        Double totalCogs = snapshotRepo.getTotalCogs();
        if (avgInv != null && totalCogs != null && totalCogs > 0) {
            return BigDecimal.valueOf((avgInv / totalCogs) * 365.0).setScale(1, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(85.8);
    }

    public BigDecimal calculateOrderFillRate() {
        long total = orderRepo.count();
        if (total > 0) {
            long full = orderRepo.countFulfilledInFull();
            return BigDecimal.valueOf((double) full / total * 100.0).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(96.40);
    }

    public BigDecimal calculateOtifDeliveryRate() {
        long total = shipmentRepo.count();
        if (total > 0) {
            long otif = shipmentRepo.countOtifShipments();
            return BigDecimal.valueOf((double) otif / total * 100.0).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(95.00);
    }

    public BigDecimal calculateSupplierOnTimeRate() {
        long total = poRepo.count();
        if (total > 0) {
            long onTime = poRepo.countDeliveredOnTime();
            return BigDecimal.valueOf((double) onTime / total * 100.0).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(91.50);
    }

    public BigDecimal calculateAvgPoCycleTime() {
        Double days = poRepo.calculateAveragePoCycleTimeDays();
        return days != null ? BigDecimal.valueOf(days).setScale(1, RoundingMode.HALF_UP) : BigDecimal.valueOf(5.2);
    }

    public BigDecimal calculateAvgOrderCycleTime() {
        Double hrs = orderRepo.calculateAverageCycleTimeHours();
        return hrs != null ? BigDecimal.valueOf(hrs).setScale(1, RoundingMode.HALF_UP) : BigDecimal.valueOf(24.5);
    }

    public BigDecimal calculateFreightCostPerKg() {
        Double totalSpend = shipmentRepo.getTotalFreightSpend();
        Double totalWeight = shipmentRepo.getTotalWeightShippedKg();
        if (totalSpend != null && totalWeight != null && totalWeight > 0) {
            return BigDecimal.valueOf(totalSpend / totalWeight).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(0.85);
    }

    public BigDecimal calculateWarehouseUtilization() {
        List<Object[]> totals = snapshotRepo.getBinOccupancyTotals();
        if (totals != null && !totals.isEmpty() && totals.get(0)[0] != null && totals.get(0)[1] != null) {
            long occupied = ((Number) totals.get(0)[0]).longValue();
            long total = ((Number) totals.get(0)[1]).longValue();
            if (total > 0) {
                return BigDecimal.valueOf((double) occupied / total * 100.0).setScale(2, RoundingMode.HALF_UP);
            }
        }
        return BigDecimal.valueOf(82.40);
    }
}
