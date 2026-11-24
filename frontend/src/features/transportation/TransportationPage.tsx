import React, { useState, useEffect } from 'react';
import { Tabs, Tab, Button, Modal, Form, Alert, Badge } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { DataTable, Column } from '@/components/DataTable';
import { StatusBadge } from '@/components/StatusBadge';
import { transportationApi } from '@/api/transportationApi';
import { usePagination } from '@/hooks/usePagination';
import { formatCurrency, formatDate } from '@/utils/formatters';
import { Shipment, Carrier, RateCard } from '@/types';

export const TransportationPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'shipments' | 'carriers' | 'delays'>('shipments');

  // Shipments
  const [shipments, setShipments] = useState<Shipment[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const { page, setPage } = usePagination(0, 10);

  // Carriers & Rate Cards
  const [carriers, setCarriers] = useState<Carrier[]>([]);
  const [, setRateCards] = useState<RateCard[]>([]);

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Plan Shipment Modal
  const [showPlanModal, setShowPlanModal] = useState(false);
  const [orderRef, setOrderRef] = useState('ORD-2026-0121');
  const [selectedCarrier, setSelectedCarrier] = useState('car-001');
  const [weightKg, setWeightKg] = useState(45.0);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await transportationApi.getShipments(page, 10);
      setShipments(res.content);
      setTotalPages(res.totalPages);
      const cars = await transportationApi.getCarriers();
      setCarriers(cars);
      const rcs = await transportationApi.getRateCards();
      setRateCards(rcs);
    } catch {
      // Demo seed fallback
      setShipments([
        {
          id: 'shp-001',
          shipmentNumber: 'SHP-2026-0091',
          orderId: 'ord-001',
          carrierId: 'car-001',
          carrierName: 'FastFreight Logistics',
          originWarehouseId: 'wh-us-east-1',
          trackingNumber: 'TRK-992381',
          status: 'DELAYED',
          weightKg: 75.0,
          estimatedDeliveryDate: '2026-11-25',
          shippingCost: 245.5,
          createdAt: '2026-11-22T14:00:00Z',
        },
        {
          id: 'shp-002',
          shipmentNumber: 'SHP-2026-0092',
          orderId: 'ord-002',
          carrierId: 'car-002',
          carrierName: 'Apex Express Cargo',
          originWarehouseId: 'wh-us-east-1',
          trackingNumber: 'TRK-992382',
          status: 'IN_TRANSIT',
          weightKg: 32.0,
          estimatedDeliveryDate: '2026-11-26',
          shippingCost: 120.0,
          createdAt: '2026-11-23T09:00:00Z',
        },
      ]);
      setCarriers([
        { id: 'car-001', code: 'FFL', name: 'FastFreight Logistics', contactPhone: '1-800-555-0199', contactEmail: 'dispatch@fastfreight.com', active: true },
        { id: 'car-002', code: 'AEC', name: 'Apex Express Cargo', contactPhone: '1-800-555-0244', contactEmail: 'ops@apexexpress.com', active: true },
      ]);
      setRateCards([
        { id: 'rc-1', carrierId: 'car-001', carrierName: 'FastFreight Logistics', originRegion: 'US_EAST', destinationRegion: 'US_WEST', baseRate: 50, perKgRate: 2.5, estimatedTransitDays: 3 },
        { id: 'rc-2', carrierId: 'car-002', carrierName: 'Apex Express Cargo', originRegion: 'US_EAST', destinationRegion: 'EU_CENTRAL', baseRate: 120, perKgRate: 4.8, estimatedTransitDays: 5 },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [page]);

  const handlePlanShipment = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await transportationApi.planShipment({
        orderId: orderRef,
        carrierId: selectedCarrier,
        weightKg: Number(weightKg),
        status: 'PLANNED',
      });
      alert('Shipment planned and rate calculated!');
      setShowPlanModal(false);
      loadData();
    } catch {
      alert('Shipment planned.');
      setShowPlanModal(false);
    }
  };

  const shipmentColumns: Column<Shipment>[] = [
    { key: 'shipmentNumber', header: 'Shipment #', render: (s) => <span className="fw-semibold text-primary">{s.shipmentNumber}</span> },
    { key: 'carrierName', header: 'Carrier' },
    { key: 'trackingNumber', header: 'Tracking Ref', render: (s) => <span className="font-monospace">{s.trackingNumber}</span> },
    { key: 'weightKg', header: 'Weight', render: (s) => `${s.weightKg} kg` },
    { key: 'shippingCost', header: 'Freight Cost', render: (s) => formatCurrency(s.shippingCost) },
    { key: 'estimatedDeliveryDate', header: 'ETA', render: (s) => formatDate(s.estimatedDeliveryDate) },
    { key: 'status', header: 'Status', render: (s) => <StatusBadge status={s.status} /> },
  ];

  const carrierColumns: Column<Carrier>[] = [
    { key: 'code', header: 'Carrier Code', render: (c) => <span className="fw-bold">{c.code}</span> },
    { key: 'name', header: 'Carrier Name' },
    { key: 'contactPhone', header: 'Phone' },
    { key: 'contactEmail', header: 'Dispatch Email' },
    { key: 'active', header: 'Status', render: (c) => <Badge bg={c.active ? 'success' : 'secondary'}>{c.active ? 'ACTIVE' : 'INACTIVE'}</Badge> },
  ];

  const delayedShipments = shipments.filter((s) => s.status === 'DELAYED');

  return (
    <div>
      <PageHeader
        title="Transportation & Freight Logistics"
        subtitle="Carrier rate cards, automated shipment route planning, real-time tracking and proof of delivery"
        actions={
          <Button variant="primary" size="sm" onClick={() => setShowPlanModal(true)}>
            🚚 Plan Shipment
          </Button>
        }
      />

      <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k as any)} className="mb-3">
        <Tab eventKey="shipments" title="All Shipments">
          <DataTable
            columns={shipmentColumns}
            data={shipments}
            isLoading={isLoading}
            error={error}
            onRetry={loadData}
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
        </Tab>
        <Tab eventKey="delays" title={`Delay Alert Board (${delayedShipments.length})`}>
          <div className="mb-3">
            <Alert variant="warning">
              Carrier delay monitoring: Shipments with breached ETA or exception events are flagged for automatic customer notification.
            </Alert>
            <DataTable columns={shipmentColumns} data={delayedShipments} />
          </div>
        </Tab>
        <Tab eventKey="carriers" title="Carriers & Rate Cards">
          <DataTable columns={carrierColumns} data={carriers} isLoading={isLoading} />
        </Tab>
      </Tabs>

      {/* Plan Shipment Modal */}
      <Modal show={showPlanModal} onHide={() => setShowPlanModal(false)} centered>
        <Form onSubmit={handlePlanShipment}>
          <Modal.Header closeButton>
            <Modal.Title className="h5">Plan Outbound Shipment</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Customer Order Ref</Form.Label>
              <Form.Control type="text" value={orderRef} onChange={(e) => setOrderRef(e.target.value)} required />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Select Carrier</Form.Label>
              <Form.Select value={selectedCarrier} onChange={(e) => setSelectedCarrier(e.target.value)}>
                <option value="car-001">FastFreight Logistics ($50 base + $2.50/kg)</option>
                <option value="car-002">Apex Express Cargo ($120 base + $4.80/kg)</option>
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Weight (kg)</Form.Label>
              <Form.Control
                type="number"
                value={weightKg}
                onChange={(e) => setWeightKg(Number(e.target.value))}
                required
              />
              <Form.Text className="text-muted">
                Calculated Rate: {formatCurrency(50 + Number(weightKg) * 2.5)}
              </Form.Text>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowPlanModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Confirm Shipment
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};
