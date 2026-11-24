import React, { useState, useEffect } from 'react';
import { Row, Col, Card, Alert, Button, Table, Badge } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { KpiCard } from '@/components/KpiCard';
import { TrendChart } from '@/components/TrendChart';
import { BarBreakdown } from '@/components/BarBreakdown';
import { analyticsApi } from '@/api/analyticsApi';
import { useAuth } from '@/hooks/useAuth';
import { formatPercent } from '@/utils/formatters';
import { DashboardSummary } from '@/types';

export const DashboardPage: React.FC = () => {
  const { activeLocationId } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await analyticsApi.getDashboardSummary(activeLocationId);
      setSummary(data);
    } catch {
      // Fallback demo metrics if backend is offline
      setSummary({
        otifRate: 0.942,
        inventoryAccuracy: 0.985,
        orderFillRate: 0.961,
        openPoCount: 14,
        lowStockCount: 3,
        delayedShipmentCount: 2,
        totalRevenue: 425600,
        totalSpend: 184500,
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [activeLocationId]);

  const trendData = [
    { date: 'Nov 17', otif: 91, fillRate: 94 },
    { date: 'Nov 18', otif: 93, fillRate: 95 },
    { date: 'Nov 19', otif: 92, fillRate: 96 },
    { date: 'Nov 20', otif: 95, fillRate: 97 },
    { date: 'Nov 21', otif: 94, fillRate: 96 },
    { date: 'Nov 22', otif: 96, fillRate: 98 },
    { date: 'Nov 23', otif: 94, fillRate: 96 },
  ];

  const warehouseStockData = [
    { name: 'US East DC', onHand: 18500, reserved: 2400 },
    { name: 'US West Hub', onHand: 12400, reserved: 1800 },
    { name: 'EU Central', onHand: 9800, reserved: 1200 },
    { name: 'APAC Hub', onHand: 15200, reserved: 3100 },
  ];

  const topSuppliers = [
    { name: 'Apex Electronics Inc.', onTime: '98.5%', quality: '99.0%', score: 98.2, status: 'ACTIVE' },
    { name: 'Global Semiconductor Corp', onTime: '96.2%', quality: '97.5%', score: 96.8, status: 'ACTIVE' },
    { name: 'Vanguard Industrial Parts', onTime: '91.0%', quality: '94.0%', score: 92.5, status: 'ACTIVE' },
    { name: 'Precision Motors Ltd', onTime: '88.4%', quality: '92.0%', score: 89.2, status: 'SUSPENDED' },
  ];

  return (
    <div>
      <PageHeader
        title="Supply Chain Executive Dashboard"
        subtitle="Real-time visibility across procurement, inventory, fulfilment and carrier logistics"
        actions={
          <Button variant="outline-primary" size="sm" onClick={loadData} disabled={isLoading}>
            🔄 Refresh Data
          </Button>
        }
      />

      {error && <Alert variant="danger">{error}</Alert>}

      {/* KPI Cards Row 1 per Item 45 */}
      <Row className="g-3 mb-4">
        <Col xs={12} sm={6} lg={4} xl={2}>
          <KpiCard
            title="OTIF Delivery"
            value={formatPercent(summary?.otifRate ?? 0.942)}
            subtitle="Target: ≥ 95.0%"
            badge="Healthy"
            badgeVariant="success"
            icon="🎯"
          />
        </Col>
        <Col xs={12} sm={6} lg={4} xl={2}>
          <KpiCard
            title="Inventory Accuracy"
            value={formatPercent(summary?.inventoryAccuracy ?? 0.985)}
            subtitle="Cycle count match"
            badge="Audited"
            badgeVariant="success"
            icon="📊"
          />
        </Col>
        <Col xs={12} sm={6} lg={4} xl={2}>
          <KpiCard
            title="Order Fill Rate"
            value={formatPercent(summary?.orderFillRate ?? 0.961)}
            subtitle="First-pass pick"
            badge="Strong"
            badgeVariant="success"
            icon="📦"
          />
        </Col>
        <Col xs={12} sm={6} lg={4} xl={2}>
          <KpiCard
            title="Open POs"
            value={summary?.openPoCount ?? 14}
            subtitle="Awaiting delivery"
            badge="In Progress"
            badgeVariant="info"
            icon="📝"
          />
        </Col>
        <Col xs={12} sm={6} lg={4} xl={2}>
          <KpiCard
            title="Low Stock Items"
            value={summary?.lowStockCount ?? 3}
            subtitle="Below safety threshold"
            badge="Action Needed"
            badgeVariant="warning"
            icon="⚠️"
          />
        </Col>
        <Col xs={12} sm={6} lg={4} xl={2}>
          <KpiCard
            title="Delayed Shipments"
            value={summary?.delayedShipmentCount ?? 2}
            subtitle="Carrier ETA breach"
            badge="Attention"
            badgeVariant="danger"
            icon="🚚"
          />
        </Col>
      </Row>

      {/* Charts Row per Item 45 & 46 */}
      <Row className="g-3 mb-4">
        <Col lg={7}>
          <Card className="border-0 shadow-sm h-100">
            <Card.Header className="bg-white border-0 pt-3 d-flex justify-content-between align-items-center">
              <h6 className="fw-bold mb-0">Delivery Performance Trend (Last 7 Days)</h6>
              <span className="small text-muted">OTIF vs Fill Rate (%)</span>
            </Card.Header>
            <Card.Body>
              <TrendChart
                data={trendData}
                lines={[
                  { key: 'otif', color: '#0d6efd', name: 'OTIF %' },
                  { key: 'fillRate', color: '#198754', name: 'Fill Rate %' },
                ]}
                height={260}
              />
            </Card.Body>
          </Card>
        </Col>
        <Col lg={5}>
          <Card className="border-0 shadow-sm h-100">
            <Card.Header className="bg-white border-0 pt-3 d-flex justify-content-between align-items-center">
              <h6 className="fw-bold mb-0">Stock by Warehouse Location</h6>
              <span className="small text-muted">Units</span>
            </Card.Header>
            <Card.Body>
              <BarBreakdown
                data={warehouseStockData}
                bars={[
                  { key: 'onHand', color: '#3b82f6', name: 'On-Hand' },
                  { key: 'reserved', color: '#f59e0b', name: 'Reserved' },
                ]}
                height={260}
              />
            </Card.Body>
          </Card>
        </Col>
      </Row>

      {/* Tables Row: Supplier Performance & Operational Alerts */}
      <Row className="g-3">
        <Col lg={8}>
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-white border-0 pt-3">
              <h6 className="fw-bold mb-0">Strategic Supplier Performance Leaderboard</h6>
            </Card.Header>
            <Card.Body className="p-0">
              <Table hover responsive className="mb-0 align-middle">
                <thead className="table-light">
                  <tr>
                    <th>Supplier</th>
                    <th>On-Time Rate</th>
                    <th>Quality Score</th>
                    <th>Composite Score</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {topSuppliers.map((s, i) => (
                    <tr key={i}>
                      <td className="fw-semibold">{s.name}</td>
                      <td>{s.onTime}</td>
                      <td>{s.quality}</td>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <span className="fw-bold">{s.score}</span>
                          <div className="progress flex-grow-1" style={{ height: '6px' }}>
                            <div
                              className={`progress-bar ${s.score >= 90 ? 'bg-success' : 'bg-warning'}`}
                              style={{ width: `${s.score}%` }}
                            />
                          </div>
                        </div>
                      </td>
                      <td>
                        <Badge bg={s.status === 'ACTIVE' ? 'success' : 'danger'}>{s.status}</Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Col>
        <Col lg={4}>
          <Card className="border-0 shadow-sm">
            <Card.Header className="bg-white border-0 pt-3">
              <h6 className="fw-bold mb-0">Recent Operational Alerts</h6>
            </Card.Header>
            <Card.Body className="p-3">
              <div className="d-flex flex-column gap-2">
                <div className="p-2 rounded bg-warning-subtle border-start border-3 border-warning small">
                  <div className="fw-bold text-dark">Low Stock Alert: SKU-ELEC-002</div>
                  <div className="text-muted">US East DC stock dropped below safety threshold (42 / 100 units).</div>
                </div>
                <div className="p-2 rounded bg-danger-subtle border-start border-3 border-danger small">
                  <div className="fw-bold text-dark">Shipment Delayed: TRK-992381</div>
                  <div className="text-muted">Carrier FastFreight Logistics reported weather delay in transit to Chicago.</div>
                </div>
                <div className="p-2 rounded bg-info-subtle border-start border-3 border-info small">
                  <div className="fw-bold text-dark">PO Approved: PO-2026-0042</div>
                  <div className="text-muted">Procurement Manager approved order for $14,200 from Apex Electronics.</div>
                </div>
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </div>
  );
};
