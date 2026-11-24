import React, { useState } from 'react';
import { Row, Col, Card, Tabs, Tab, Button, Modal, Form, Table, Badge } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { StatusBadge } from '@/components/StatusBadge';
import { formatCurrency, formatDate } from '@/utils/formatters';

export const SupplierPortalPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState('orders');

  const [assignedPos, setAssignedPos] = useState([
    {
      id: 'po-001',
      poNumber: 'PO-2026-0042',
      amount: 14500,
      item: 'Microcontroller Cortex M4',
      qty: 1000,
      deliveryLocation: 'US East DC (New Jersey)',
      status: 'ISSUED',
      date: '2026-11-20',
    },
    {
      id: 'po-002',
      poNumber: 'PO-2026-0045',
      amount: 7250,
      item: 'Microcontroller Cortex M4',
      qty: 500,
      deliveryLocation: 'US East DC (New Jersey)',
      status: 'ACKNOWLEDGED',
      date: '2026-11-22',
    },
  ]);

  const [asns, setAsns] = useState([
    {
      asnNumber: 'ASN-2026-001',
      poNumber: 'PO-2026-0042',
      carrier: 'FastFreight Logistics',
      tracking: 'FFL-882910',
      shippedDate: '2026-11-22',
      eta: '2026-11-25',
      status: 'IN_TRANSIT',
    },
  ]);

  // Create ASN Modal
  const [showAsnModal, setShowAsnModal] = useState(false);
  const [asnPoNumber, setAsnPoNumber] = useState('PO-2026-0042');
  const [carrierName, setCarrierName] = useState('FastFreight Logistics');
  const [trackingNumber, setTrackingNumber] = useState('FFL-882911');
  const [eta, setEta] = useState('2026-11-26');

  const handleAcknowledge = (poId: string) => {
    setAssignedPos((prev) =>
      prev.map((p) => (p.id === poId ? { ...p, status: 'ACKNOWLEDGED' } : p))
    );
    alert('Purchase order acknowledged! Buyer notified via events.');
  };

  const handleCreateAsn = (e: React.FormEvent) => {
    e.preventDefault();
    const newAsn = {
      asnNumber: `ASN-2026-${String(asns.length + 1).padStart(3, '0')}`,
      poNumber: asnPoNumber,
      carrier: carrierName,
      tracking: trackingNumber,
      shippedDate: new Date().toISOString().substring(0, 10),
      eta,
      status: 'DISPATCHED',
    };
    setAsns([newAsn, ...asns]);
    setShowAsnModal(false);
    alert('Advanced Shipping Notice (ASN) transmitted to warehouse receiving dock!');
  };

  return (
    <div>
      <PageHeader
        title="Supplier Collaboration Portal"
        subtitle="Direct collaboration: Acknowledge purchase orders, dispatch Advance Shipping Notices (ASN) and monitor performance"
        actions={
          <Button variant="primary" size="sm" onClick={() => setShowAsnModal(true)}>
            📦 Create ASN
          </Button>
        }
      />

      <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k || 'orders')} className="mb-4">
        {/* Purchase Orders Tab */}
        <Tab eventKey="orders" title="Assigned Purchase Orders">
          <Card className="shadow-sm border-0">
            <Card.Body className="p-0">
              <Table hover responsive className="mb-0 align-middle">
                <thead className="table-light">
                  <tr>
                    <th>PO Number</th>
                    <th>Date Issued</th>
                    <th>Item & Quantity</th>
                    <th>Destination DC</th>
                    <th>Total Value</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {assignedPos.map((po) => (
                    <tr key={po.id}>
                      <td className="fw-bold text-primary">{po.poNumber}</td>
                      <td>{formatDate(po.date)}</td>
                      <td>{po.qty} × {po.item}</td>
                      <td>{po.deliveryLocation}</td>
                      <td className="fw-semibold">{formatCurrency(po.amount)}</td>
                      <td><StatusBadge status={po.status} /></td>
                      <td>
                        {po.status === 'ISSUED' && (
                          <Button
                            variant="success"
                            size="sm"
                            onClick={() => handleAcknowledge(po.id)}
                          >
                            ✓ Acknowledge PO
                          </Button>
                        )}
                        {po.status === 'ACKNOWLEDGED' && (
                          <span className="small text-muted">Ready for dispatch</span>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Tab>

        {/* Advance Shipping Notices (ASN) */}
        <Tab eventKey="asns" title="Advance Shipping Notices (ASN)">
          <Card className="shadow-sm border-0">
            <Card.Body className="p-0">
              <Table hover responsive className="mb-0 align-middle">
                <thead className="table-light">
                  <tr>
                    <th>ASN Number</th>
                    <th>PO Ref</th>
                    <th>Carrier</th>
                    <th>Tracking #</th>
                    <th>Shipped Date</th>
                    <th>Estimated Arrival</th>
                    <th>Status</th>
                  </tr>
                </thead>
                <tbody>
                  {asns.map((a, i) => (
                    <tr key={i}>
                      <td className="fw-bold text-primary">{a.asnNumber}</td>
                      <td>{a.poNumber}</td>
                      <td>{a.carrier}</td>
                      <td className="font-monospace">{a.tracking}</td>
                      <td>{formatDate(a.shippedDate)}</td>
                      <td>{formatDate(a.eta)}</td>
                      <td><StatusBadge status={a.status} /></td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Tab>

        {/* Scorecard Profile */}
        <Tab eventKey="scorecard" title="My Performance Scorecard">
          <Row className="g-3">
            <Col md={3}>
              <Card className="p-3 shadow-sm border-0 text-center">
                <span className="small text-muted">Composite Score</span>
                <h3 className="fw-bold text-success mb-0">98.2 / 100</h3>
                <Badge bg="success" className="mt-2">Preferred Tier 1</Badge>
              </Card>
            </Col>
            <Col md={3}>
              <Card className="p-3 shadow-sm border-0 text-center">
                <span className="small text-muted">On-Time Delivery Rate</span>
                <h3 className="fw-bold text-primary mb-0">98.5%</h3>
                <span className="small text-muted">Target ≥ 95%</span>
              </Card>
            </Col>
            <Col md={3}>
              <Card className="p-3 shadow-sm border-0 text-center">
                <span className="small text-muted">Quality Acceptance Rate</span>
                <h3 className="fw-bold text-success mb-0">99.0%</h3>
                <span className="small text-muted">Target ≥ 98%</span>
              </Card>
            </Col>
            <Col md={3}>
              <Card className="p-3 shadow-sm border-0 text-center">
                <span className="small text-muted">Fulfilled POs</span>
                <h3 className="fw-bold text-dark mb-0">48 Orders</h3>
                <span className="small text-muted">Zero defects recorded</span>
              </Card>
            </Col>
          </Row>
        </Tab>
      </Tabs>

      {/* Create ASN Modal */}
      <Modal show={showAsnModal} onHide={() => setShowAsnModal(false)} centered>
        <Form onSubmit={handleCreateAsn}>
          <Modal.Header closeButton>
            <Modal.Title className="h5">Dispatch Advance Shipping Notice (ASN)</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Purchase Order Reference</Form.Label>
              <Form.Select value={asnPoNumber} onChange={(e) => setAsnPoNumber(e.target.value)}>
                <option value="PO-2026-0042">PO-2026-0042 (Microcontroller Cortex M4 × 1000)</option>
                <option value="PO-2026-0045">PO-2026-0045 (Microcontroller Cortex M4 × 500)</option>
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Assigned Freight Carrier</Form.Label>
              <Form.Control
                type="text"
                value={carrierName}
                onChange={(e) => setCarrierName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Tracking Consignment #</Form.Label>
              <Form.Control
                type="text"
                value={trackingNumber}
                onChange={(e) => setTrackingNumber(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Expected Date of Arrival</Form.Label>
              <Form.Control
                type="date"
                value={eta}
                onChange={(e) => setEta(e.target.value)}
                required
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowAsnModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Dispatch ASN
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};
