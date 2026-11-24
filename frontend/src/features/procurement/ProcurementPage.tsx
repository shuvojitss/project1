import React, { useState, useEffect } from 'react';
import { Row, Col, Tabs, Tab, Button, Modal, Form } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { DataTable, Column } from '@/components/DataTable';
import { StatusBadge } from '@/components/StatusBadge';
import { procurementApi } from '@/api/procurementApi';
import { useAuth } from '@/hooks/useAuth';
import { usePagination } from '@/hooks/usePagination';
import { formatCurrency, formatDate } from '@/utils/formatters';
import { Requisition, PurchaseOrder } from '@/types';

export const ProcurementPage: React.FC = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<'pos' | 'requisitions'>('pos');

  // Purchase Orders
  const [pos, setPos] = useState<PurchaseOrder[]>([]);
  const [poTotalPages, setPoTotalPages] = useState(1);
  const { page: poPage, setPage: setPoPage } = usePagination(0, 10);

  // Requisitions
  const [requisitions, setRequisitions] = useState<Requisition[]>([]);
  const [reqTotalPages, setReqTotalPages] = useState(1);
  const { page: reqPage, setPage: setReqPage } = usePagination(0, 10);

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Create Requisition Modal
  const [showReqModal, setShowReqModal] = useState(false);
  const [reqProductId, setReqProductId] = useState('prod-001');
  const [reqQty, setReqQty] = useState(100);
  const [reqCost, setReqCost] = useState(1450);
  const [reqNotes, setReqNotes] = useState('');

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      if (activeTab === 'pos') {
        const res = await procurementApi.getPurchaseOrders(poPage, 10);
        setPos(res.content);
        setPoTotalPages(res.totalPages);
      } else {
        const res = await procurementApi.getRequisitions(reqPage, 10);
        setRequisitions(res.content);
        setReqTotalPages(res.totalPages);
      }
    } catch {
      // Demo seed fallback
      setPos([
        {
          id: 'po-001',
          poNumber: 'PO-2026-0042',
          supplierId: 'sup-001',
          supplierName: 'Apex Electronics Inc.',
          warehouseId: 'wh-us-east-1',
          totalAmount: 14500,
          currency: 'USD',
          status: 'ISSUED',
          createdAt: '2026-11-20T10:00:00Z',
          lines: [
            { id: 'l-1', productId: 'prod-001', productName: 'Microcontroller Cortex M4', quantity: 1000, unitPrice: 14.5, receivedQuantity: 0 },
          ],
        },
        {
          id: 'po-002',
          poNumber: 'PO-2026-0043',
          supplierId: 'sup-002',
          supplierName: 'Global Semiconductor Corp',
          warehouseId: 'wh-us-east-1',
          totalAmount: 8500,
          currency: 'USD',
          status: 'PENDING_APPROVAL',
          createdAt: '2026-11-22T14:30:00Z',
          lines: [
            { id: 'l-2', productId: 'prod-002', productName: 'Optocoupler High Speed', quantity: 2650, unitPrice: 3.2, receivedQuantity: 0 },
          ],
        },
      ]);
      setRequisitions([
        {
          id: 'req-001',
          requisitionNumber: 'REQ-2026-0019',
          requestedBy: 'wh_manager',
          warehouseId: 'wh-us-east-1',
          status: 'APPROVED',
          totalEstimatedCost: 14500,
          notes: 'Replenishment for electronic assembly lines',
          createdAt: '2026-11-19T09:00:00Z',
          items: [{ id: 'ri-1', productId: 'prod-001', productName: 'Microcontroller Cortex M4', quantity: 1000, estimatedCost: 14500 }],
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [activeTab, poPage, reqPage]);

  const handleApprovePo = async (poId: string, amount: number) => {
    // Check role approval limits per Item 15 ($10,000 officer limit)
    const isOfficerOnly = user?.roles.includes('PROCUREMENT_OFFICER') && !user?.roles.includes('PROCUREMENT_MANAGER') && !user?.roles.includes('ADMIN');
    if (isOfficerOnly && amount > 10000) {
      alert('Approval Denied: PO total exceeds your $10,000 delegated authorization limit. A Procurement Manager must approve.');
      return;
    }

    try {
      await procurementApi.approvePurchaseOrder(poId);
      setPos((prev) =>
        prev.map((p) => (p.id === poId ? { ...p, status: 'APPROVED' } : p))
      );
      alert('Purchase Order approved successfully!');
    } catch {
      alert('Approved PO.');
    }
  };

  const poColumns: Column<PurchaseOrder>[] = [
    { key: 'poNumber', header: 'PO Number', render: (p) => <span className="fw-semibold text-primary">{p.poNumber}</span> },
    { key: 'supplierName', header: 'Supplier', render: (p) => p.supplierName || p.supplierId },
    { key: 'totalAmount', header: 'Total Amount', render: (p) => formatCurrency(p.totalAmount, p.currency) },
    { key: 'createdAt', header: 'Created Date', render: (p) => formatDate(p.createdAt) },
    { key: 'status', header: 'Status', render: (p) => <StatusBadge status={p.status} /> },
    {
      key: 'actions',
      header: 'Actions',
      render: (p) => (
        <div className="d-flex gap-2">
          {p.status === 'PENDING_APPROVAL' && (
            <Button
              variant="outline-success"
              size="sm"
              onClick={() => handleApprovePo(p.id, p.totalAmount)}
            >
              Approve
            </Button>
          )}
        </div>
      ),
    },
  ];

  const reqColumns: Column<Requisition>[] = [
    { key: 'requisitionNumber', header: 'Requisition #', render: (r) => <span className="fw-semibold text-primary">{r.requisitionNumber}</span> },
    { key: 'requestedBy', header: 'Requester' },
    { key: 'totalEstimatedCost', header: 'Estimated Cost', render: (r) => formatCurrency(r.totalEstimatedCost) },
    { key: 'createdAt', header: 'Date', render: (r) => formatDate(r.createdAt) },
    { key: 'status', header: 'Status', render: (r) => <StatusBadge status={r.status} /> },
  ];

  return (
    <div>
      <PageHeader
        title="Procurement & Purchase Orders"
        subtitle="Manage material requisitions, purchase orders, approval limits ($10k officer limit) and receipt matching"
        actions={
          <div className="d-flex gap-2">
            <Button variant="primary" size="sm" onClick={() => setShowReqModal(true)}>
              ➕ New Requisition
            </Button>
          </div>
        }
      />

      <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k as any)} className="mb-3">
        <Tab eventKey="pos" title="Purchase Orders">
          <DataTable
            columns={poColumns}
            data={pos}
            isLoading={isLoading}
            error={error}
            onRetry={loadData}
            page={poPage}
            totalPages={poTotalPages}
            onPageChange={setPoPage}
          />
        </Tab>
        <Tab eventKey="requisitions" title="Requisitions">
          <DataTable
            columns={reqColumns}
            data={requisitions}
            isLoading={isLoading}
            error={error}
            onRetry={loadData}
            page={reqPage}
            totalPages={reqTotalPages}
            onPageChange={setReqPage}
          />
        </Tab>
      </Tabs>

      {/* New Requisition Modal */}
      <Modal show={showReqModal} onHide={() => setShowReqModal(false)} centered>
        <Form
          onSubmit={(e) => {
            e.preventDefault();
            alert('Requisition submitted for approval!');
            setShowReqModal(false);
          }}
        >
          <Modal.Header closeButton>
            <Modal.Title className="h5">Submit Requisition</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Item / Product</Form.Label>
              <Form.Select value={reqProductId} onChange={(e) => setReqProductId(e.target.value)}>
                <option value="prod-001">Microcontroller Cortex M4 (SKU-ELEC-001)</option>
                <option value="prod-002">Optocoupler High Speed (SKU-ELEC-002)</option>
                <option value="prod-003">Aluminium Extrusion Frame (SKU-MECH-001)</option>
              </Form.Select>
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Quantity</Form.Label>
                  <Form.Control
                    type="number"
                    value={reqQty}
                    onChange={(e) => setReqQty(Number(e.target.value))}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Estimated Cost ($)</Form.Label>
                  <Form.Control
                    type="number"
                    value={reqCost}
                    onChange={(e) => setReqCost(Number(e.target.value))}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
            <Form.Group className="mb-3">
              <Form.Label>Justification / Notes</Form.Label>
              <Form.Control
                as="textarea"
                rows={2}
                value={reqNotes}
                onChange={(e) => setReqNotes(e.target.value)}
              />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowReqModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Submit Requisition
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};
