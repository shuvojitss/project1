import React, { useState } from 'react';
import { Row, Col, Card, Tabs, Tab, Button, Form } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { TrendChart } from '@/components/TrendChart';
import { BarBreakdown } from '@/components/BarBreakdown';
import { formatCurrency } from '@/utils/formatters';

export const AnalyticsPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('inventory');
  const [dateRange, setDateRange] = useState('30d');

  const inventoryTrend = [
    { date: 'Week 1', accuracy: 97.8, turnover: 4.2 },
    { date: 'Week 2', accuracy: 98.1, turnover: 4.3 },
    { date: 'Week 3', accuracy: 98.4, turnover: 4.5 },
    { date: 'Week 4', accuracy: 98.6, turnover: 4.6 },
  ];

  const procurementTrend = [
    { date: 'Week 1', spend: 45000, savings: 3200 },
    { date: 'Week 2', spend: 52000, savings: 4100 },
    { date: 'Week 3', spend: 38000, savings: 2900 },
    { date: 'Week 4', spend: 49500, savings: 4500 },
  ];

  const logisticsData = [
    { name: 'FastFreight Logistics', onTime: 96.2, delayCount: 3 },
    { name: 'Apex Express Cargo', onTime: 94.8, delayCount: 4 },
    { name: 'TransContinental Air', onTime: 98.5, delayCount: 1 },
  ];

  const handleExportCsv = () => {
    const csvContent =
      'data:text/csv;charset=utf-8,Metric,Value,Target,Status\n' +
      'OTIF Delivery Rate,94.2%,95.0%,Healthy\n' +
      'Inventory Accuracy,98.5%,98.0%,Optimal\n' +
      'Order Fill Rate,96.1%,95.0%,Optimal\n' +
      'Supplier On-Time Rate,96.8%,95.0%,Optimal\n' +
      'DIOH (Days Inventory On Hand),38 days,40 days,Optimal\n';
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `scm360-kpis-${activeTab}-${dateRange}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div>
      <PageHeader
        title="Supply Chain Analytics & KPI Intelligence"
        subtitle="Mathematical KPI aggregation, inventory turnover, procurement spend analysis and carrier performance"
        actions={
          <div className="d-flex gap-2 align-items-center">
            <Form.Select
              size="sm"
              value={dateRange}
              onChange={(e) => setDateRange(e.target.value)}
              style={{ width: '130px' }}
            >
              <option value="7d">Last 7 Days</option>
              <option value="30d">Last 30 Days</option>
              <option value="90d">Last Quarter</option>
            </Form.Select>
            <Button variant="outline-success" size="sm" onClick={handleExportCsv}>
              📥 Export CSV
            </Button>
          </div>
        }
      />

      <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k || 'inventory')} className="mb-4">
        {/* Inventory Analytics Tab */}
        <Tab eventKey="inventory" title="Inventory & Turnover">
          <Row className="g-3 mb-4">
            <Col md={4}>
              <Card className="p-3 shadow-sm border-0">
                <span className="small text-muted">Inventory Accuracy</span>
                <h4 className="fw-bold text-success mb-0">98.5%</h4>
                <span className="small text-muted">Based on cycle count physical audits</span>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="p-3 shadow-sm border-0">
                <span className="small text-muted">Annualized Inventory Turnover</span>
                <h4 className="fw-bold text-primary mb-0">4.6x</h4>
                <span className="small text-muted">Cost of Goods Sold / Average Stock</span>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="p-3 shadow-sm border-0">
                <span className="small text-muted">DIOH (Days Inventory on Hand)</span>
                <h4 className="fw-bold text-dark mb-0">38.2 Days</h4>
                <span className="small text-muted">Target threshold ≤ 40 days</span>
              </Card>
            </Col>
          </Row>

          <Card className="shadow-sm border-0 p-3">
            <h6 className="fw-bold mb-3">Inventory Accuracy & Turnover Trend</h6>
            <TrendChart
              data={inventoryTrend}
              lines={[
                { key: 'accuracy', color: '#10b981', name: 'Accuracy %' },
                { key: 'turnover', color: '#6366f1', name: 'Turnover (x)' },
              ]}
              height={300}
            />
          </Card>
        </Tab>

        {/* Procurement Analytics Tab */}
        <Tab eventKey="procurement" title="Procurement & Spend">
          <Row className="g-3 mb-4">
            <Col md={4}>
              <Card className="p-3 shadow-sm border-0">
                <span className="small text-muted">Total Monthly Spend</span>
                <h4 className="fw-bold text-primary mb-0">{formatCurrency(184500)}</h4>
                <span className="small text-muted">Across 14 approved POs</span>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="p-3 shadow-sm border-0">
                <span className="small text-muted">Average PO Cycle Time</span>
                <h4 className="fw-bold text-success mb-0">2.4 Days</h4>
                <span className="small text-muted">Requisition to PO issue</span>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="p-3 shadow-sm border-0">
                <span className="small text-muted">Negotiated Savings</span>
                <h4 className="fw-bold text-info mb-0">{formatCurrency(14700)}</h4>
                <span className="small text-muted">Multi-sourcing cost delta</span>
              </Card>
            </Col>
          </Row>

          <Card className="shadow-sm border-0 p-3">
            <h6 className="fw-bold mb-3">Weekly Procurement Spend ($)</h6>
            <TrendChart
              data={procurementTrend}
              lines={[
                { key: 'spend', color: '#0ea5e9', name: 'Spend ($)' },
                { key: 'savings', color: '#22c55e', name: 'Savings ($)' },
              ]}
              height={300}
            />
          </Card>
        </Tab>

        {/* Logistics & Carrier Tab */}
        <Tab eventKey="logistics" title="Logistics & Carrier Performance">
          <Card className="shadow-sm border-0 p-3">
            <h6 className="fw-bold mb-3">Carrier On-Time Delivery Comparison (%)</h6>
            <BarBreakdown
              data={logisticsData}
              bars={[{ key: 'onTime', color: '#3b82f6', name: 'On-Time Rate %' }]}
              height={300}
            />
          </Card>
        </Tab>
      </Tabs>
    </div>
  );
};
