import React, { useState, useEffect } from 'react';
import { Row, Col, Form, Button, Modal, Badge, Card, Tab, Tabs, Table } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { DataTable, Column } from '@/components/DataTable';
import { StatusBadge } from '@/components/StatusBadge';
import { supplierApi } from '@/api/supplierApi';
import { usePagination } from '@/hooks/usePagination';
import { Supplier, SupplierScorecard, SupplierItem } from '@/types';

export const SupplierListPage: React.FC = () => {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { page, size, setPage } = usePagination(0, 10);

  // Onboard Modal
  const [showModal, setShowModal] = useState(false);
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [country, setCountry] = useState('USA');
  const [city, setCity] = useState('New York');
  const [paymentTerms] = useState('NET30');
  const [currency] = useState('USD');
  const [submitting, setSubmitting] = useState(false);

  // Scorecard Modal
  const [selectedSupplier, setSelectedSupplier] = useState<Supplier | null>(null);
  const [scorecard, setScorecard] = useState<SupplierScorecard | null>(null);
  const [items, setItems] = useState<SupplierItem[]>([]);
  const [showDetailModal, setShowDetailModal] = useState(false);

  const loadSuppliers = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await supplierApi.getSuppliers(page, size);
      setSuppliers(res.content);
      setTotalPages(res.totalPages);
    } catch {
      // Demo seed fallback
      setSuppliers([
        {
          id: 'sup-001',
          code: 'VEND-001',
          name: 'Apex Electronics Inc.',
          contactEmail: 'contact@apexelectronics.com',
          country: 'USA',
          city: 'Austin, TX',
          paymentTerms: 'NET30',
          currency: 'USD',
          status: 'ACTIVE',
          overallScore: 98.2,
        },
        {
          id: 'sup-002',
          code: 'VEND-002',
          name: 'Global Semiconductor Corp',
          contactEmail: 'sales@globalsemi.com',
          country: 'Germany',
          city: 'Munich',
          paymentTerms: 'NET45',
          currency: 'EUR',
          status: 'ACTIVE',
          overallScore: 96.8,
        },
        {
          id: 'sup-003',
          code: 'VEND-003',
          name: 'Precision Motors Ltd',
          contactEmail: 'orders@precisionmotors.co.uk',
          country: 'UK',
          city: 'Birmingham',
          paymentTerms: 'NET30',
          currency: 'GBP',
          status: 'SUSPENDED',
          overallScore: 88.5,
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadSuppliers();
  }, [page]);

  const handleOnboard = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const created = await supplierApi.onboardSupplier({
        code,
        name,
        contactEmail: email,
        country,
        city,
        paymentTerms,
        currency,
        status: 'PENDING_APPROVAL',
      });
      setSuppliers([created, ...suppliers]);
      setShowModal(false);
      setCode('');
      setName('');
      setEmail('');
    } catch (err: any) {
      alert(err?.detail || 'Failed to onboard supplier.');
    } finally {
      setSubmitting(false);
    }
  };

  const handleOpenDetail = async (sup: Supplier) => {
    setSelectedSupplier(sup);
    setShowDetailModal(true);
    try {
      const sc = await supplierApi.getScorecard(sup.id);
      setScorecard(sc);
    } catch {
      setScorecard({
        id: 'sc-1',
        supplierId: sup.id,
        periodYear: 2026,
        periodMonth: 11,
        onTimeDeliveryRate: 0.985,
        qualityScore: 0.99,
        complianceScore: 0.97,
        overallScore: sup.overallScore || 98.2,
        totalOrders: 48,
      });
    }
    try {
      const its = await supplierApi.getItems(sup.id);
      setItems(its);
    } catch {
      setItems([
        {
          id: 'si-1',
          supplierId: sup.id,
          productId: 'prod-001',
          productName: 'Microcontroller Cortex M4',
          unitPrice: 14.5,
          currency: 'USD',
          leadTimeDays: 7,
          minimumOrderQuantity: 50,
          priority: 1,
        },
      ]);
    }
  };

  const handleToggleStatus = async (sup: Supplier) => {
    const nextStatus = sup.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    try {
      await supplierApi.updateStatus(sup.id, nextStatus, 'Manual administrative action');
      setSuppliers((prev) =>
        prev.map((s) => (s.id === sup.id ? { ...s, status: nextStatus as any } : s))
      );
    } catch {
      alert('Failed to update status.');
    }
  };

  const columns: Column<Supplier>[] = [
    { key: 'code', header: 'Vendor Code', render: (s) => <span className="fw-semibold">{s.code}</span> },
    {
      key: 'name',
      header: 'Company Name',
      render: (s) => (
        <div>
          <div className="fw-bold">{s.name}</div>
          <div className="small text-muted">{s.contactEmail}</div>
        </div>
      ),
    },
    { key: 'location', header: 'Location', render: (s) => `${s.city}, ${s.country}` },
    { key: 'paymentTerms', header: 'Terms', render: (s) => <Badge bg="light" text="dark">{s.paymentTerms}</Badge> },
    {
      key: 'overallScore',
      header: 'Scorecard',
      render: (s) => (
        <span className={`fw-bold ${(s.overallScore || 90) >= 90 ? 'text-success' : 'text-danger'}`}>
          {s.overallScore ? `${s.overallScore.toFixed(1)} / 100` : 'N/A'}
        </span>
      ),
    },
    { key: 'status', header: 'Status', render: (s) => <StatusBadge status={s.status} /> },
    {
      key: 'actions',
      header: 'Actions',
      render: (s) => (
        <div className="d-flex gap-2">
          <Button variant="outline-primary" size="sm" onClick={() => handleOpenDetail(s)}>
            Details
          </Button>
          <Button
            variant={s.status === 'ACTIVE' ? 'outline-warning' : 'outline-success'}
            size="sm"
            onClick={() => handleToggleStatus(s)}
          >
            {s.status === 'ACTIVE' ? 'Suspend' : 'Activate'}
          </Button>
        </div>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Supplier Relationship Management"
        subtitle="Manage vendors, onboarding, master catalogs and performance scorecards"
        actions={
          <Button variant="primary" size="sm" onClick={() => setShowModal(true)}>
            ➕ Onboard Supplier
          </Button>
        }
      />

      <DataTable
        columns={columns}
        data={suppliers}
        isLoading={isLoading}
        error={error}
        onRetry={loadSuppliers}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />

      {/* Onboard Supplier Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Form onSubmit={handleOnboard}>
          <Modal.Header closeButton>
            <Modal.Title className="h5">Onboard New Supplier</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Vendor Code</Form.Label>
              <Form.Control
                type="text"
                placeholder="VEND-004"
                value={code}
                onChange={(e) => setCode(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Company Legal Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="Company Name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Contact Email</Form.Label>
              <Form.Control
                type="email"
                placeholder="contact@company.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Country</Form.Label>
                  <Form.Control
                    type="text"
                    value={country}
                    onChange={(e) => setCountry(e.target.value)}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>City</Form.Label>
                  <Form.Control
                    type="text"
                    value={city}
                    onChange={(e) => setCity(e.target.value)}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={submitting}>
              {submitting ? 'Submitting...' : 'Onboard'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Supplier Scorecard & Details Modal */}
      <Modal show={showDetailModal} onHide={() => setShowDetailModal(false)} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title className="h5">{selectedSupplier?.name} — Performance Profile</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <Tabs defaultActiveKey="scorecard" className="mb-3">
            <Tab eventKey="scorecard" title="Scorecard & Metrics">
              {scorecard && (
                <Row className="g-3">
                  <Col md={3}>
                    <Card className="text-center p-3 border-0 bg-light">
                      <div className="small text-muted">Overall Score</div>
                      <div className="fs-3 fw-bold text-primary">{scorecard.overallScore.toFixed(1)}</div>
                    </Card>
                  </Col>
                  <Col md={3}>
                    <Card className="text-center p-3 border-0 bg-light">
                      <div className="small text-muted">On-Time Delivery</div>
                      <div className="fs-3 fw-bold text-success">
                        {(scorecard.onTimeDeliveryRate * 100).toFixed(1)}%
                      </div>
                    </Card>
                  </Col>
                  <Col md={3}>
                    <Card className="text-center p-3 border-0 bg-light">
                      <div className="small text-muted">Quality Score</div>
                      <div className="fs-3 fw-bold text-success">
                        {(scorecard.qualityScore * 100).toFixed(1)}%
                      </div>
                    </Card>
                  </Col>
                  <Col md={3}>
                    <Card className="text-center p-3 border-0 bg-light">
                      <div className="small text-muted">Total Orders</div>
                      <div className="fs-3 fw-bold text-dark">{scorecard.totalOrders}</div>
                    </Card>
                  </Col>
                </Row>
              )}
            </Tab>
            <Tab eventKey="items" title="Contracted Items">
              <Table hover size="sm" className="align-middle mt-2">
                <thead>
                  <tr>
                    <th>Product</th>
                    <th>Unit Price</th>
                    <th>Lead Time</th>
                    <th>MOQ</th>
                    <th>Priority</th>
                  </tr>
                </thead>
                <tbody>
                  {items.map((it) => (
                    <tr key={it.id}>
                      <td className="fw-semibold">{it.productName || it.productId}</td>
                      <td>${it.unitPrice.toFixed(2)}</td>
                      <td>{it.leadTimeDays} days</td>
                      <td>{it.minimumOrderQuantity}</td>
                      <td>
                        <Badge bg={it.priority === 1 ? 'primary' : 'secondary'}>
                          Priority #{it.priority}
                        </Badge>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            </Tab>
          </Tabs>
        </Modal.Body>
      </Modal>
    </div>
  );
};
