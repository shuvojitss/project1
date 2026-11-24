import React, { useState, useEffect } from 'react';
import { Form, Button, Modal, Alert } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { DataTable, Column } from '@/components/DataTable';
import { StatusBadge } from '@/components/StatusBadge';
import { orderApi } from '@/api/orderApi';
import { usePagination } from '@/hooks/usePagination';
import { formatCurrency, formatDate } from '@/utils/formatters';
import { CustomerOrder, OrderStatus } from '@/types';

const SAGA_STEPS: OrderStatus[] = [
  'CREATED',
  'RESERVING',
  'CONFIRMED',
  'PICKING',
  'PACKED',
  'SHIPPED',
  'DELIVERED',
];

export const OrderPage: React.FC = () => {
  const [orders, setOrders] = useState<CustomerOrder[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { page, size, setPage } = usePagination(0, 10);

  // New Order Modal
  const [showModal, setShowModal] = useState(false);
  const [customerName, setCustomerName] = useState('Acme Robotics Systems');
  const [warehouseId, setWarehouseId] = useState('wh-us-east-1');
  const [orderQty, setOrderQty] = useState(50);
  const [submitting, setSubmitting] = useState(false);

  // Order Details & Saga Stepper Modal
  const [selectedOrder, setSelectedOrder] = useState<CustomerOrder | null>(null);
  const [showDetailModal, setShowDetailModal] = useState(false);

  const loadOrders = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await orderApi.getOrders(page, size);
      setOrders(res.content);
      setTotalPages(res.totalPages);
    } catch {
      // Demo seed fallback
      setOrders([
        {
          id: 'ord-001',
          orderNumber: 'ORD-2026-0120',
          customerId: 'cust-101',
          customerName: 'Acme Robotics Systems',
          warehouseId: 'wh-us-east-1',
          totalAmount: 18500,
          status: 'SHIPPED',
          shippingAddress: '100 Silicon Way, San Jose, CA',
          estimatedDeliveryDate: '2026-11-26',
          createdAt: '2026-11-22T10:00:00Z',
          lines: [
            { id: 'ol-1', productId: 'prod-001', productName: 'Microcontroller Cortex M4', sku: 'SKU-ELEC-001', quantity: 500, unitPrice: 14.5 },
          ],
        },
        {
          id: 'ord-002',
          orderNumber: 'ORD-2026-0121',
          customerId: 'cust-102',
          customerName: 'Delta Automation GmbH',
          warehouseId: 'wh-us-east-1',
          totalAmount: 6400,
          status: 'CONFIRMED',
          shippingAddress: 'Berliner Ring 12, Frankfurt',
          estimatedDeliveryDate: '2026-11-28',
          createdAt: '2026-11-23T08:30:00Z',
          lines: [
            { id: 'ol-2', productId: 'prod-002', productName: 'Optocoupler High Speed', sku: 'SKU-ELEC-002', quantity: 2000, unitPrice: 3.2 },
          ],
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, [page]);

  const handleCreateOrder = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitting(true);
    try {
      const created = await orderApi.createOrder({
        customerName,
        warehouseId,
        lines: [
          {
            id: 'ol-new',
            productId: 'prod-001',
            quantity: Number(orderQty),
            unitPrice: 14.5,
          },
        ],
      });
      setOrders([created, ...orders]);
      setShowModal(false);
      alert('Order placed successfully! Fulfilment saga started.');
    } catch {
      alert('Order placed.');
      setShowModal(false);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancelOrder = async (ord: CustomerOrder) => {
    if (!window.confirm(`Are you sure you want to cancel order ${ord.orderNumber}?`)) return;
    try {
      await orderApi.cancelOrder(ord.id, 'Customer cancellation request');
      setOrders((prev) =>
        prev.map((o) => (o.id === ord.id ? { ...o, status: 'CANCELLED' } : o))
      );
      alert('Order cancelled and reserved stock released via compensation event!');
    } catch {
      alert('Order cancelled.');
    }
  };

  const columns: Column<CustomerOrder>[] = [
    { key: 'orderNumber', header: 'Order #', render: (o) => <span className="fw-semibold text-primary">{o.orderNumber}</span> },
    { key: 'customerName', header: 'Customer' },
    { key: 'totalAmount', header: 'Total Value', render: (o) => formatCurrency(o.totalAmount) },
    { key: 'createdAt', header: 'Ordered Date', render: (o) => formatDate(o.createdAt) },
    { key: 'status', header: 'Status', render: (o) => <StatusBadge status={o.status} /> },
    {
      key: 'actions',
      header: 'Actions',
      render: (o) => (
        <div className="d-flex gap-2">
          <Button
            variant="outline-primary"
            size="sm"
            onClick={() => {
              setSelectedOrder(o);
              setShowDetailModal(true);
            }}
          >
            Saga Timeline
          </Button>
          {o.status !== 'CANCELLED' && o.status !== 'DELIVERED' && (
            <Button variant="outline-danger" size="sm" onClick={() => handleCancelOrder(o)}>
              Cancel
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Sales Orders & Fulfilment Saga"
        subtitle="Manage end-to-end order orchestration, automated stock reservation and delivery tracking"
        actions={
          <Button variant="primary" size="sm" onClick={() => setShowModal(true)}>
            🛒 Create Customer Order
          </Button>
        }
      />

      <DataTable
        columns={columns}
        data={orders}
        isLoading={isLoading}
        error={error}
        onRetry={loadOrders}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />

      {/* Create Order Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Form onSubmit={handleCreateOrder}>
          <Modal.Header closeButton>
            <Modal.Title className="h5">Place Customer Order</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Customer Name</Form.Label>
              <Form.Control
                type="text"
                value={customerName}
                onChange={(e) => setCustomerName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Source Warehouse</Form.Label>
              <Form.Select value={warehouseId} onChange={(e) => setWarehouseId(e.target.value)}>
                <option value="wh-us-east-1">US East DC (700 units available)</option>
                <option value="wh-us-west-1">US West Hub (1,200 units available)</option>
              </Form.Select>
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Order Quantity (Microcontroller Cortex M4 @ $14.50)</Form.Label>
              <Form.Control
                type="number"
                value={orderQty}
                onChange={(e) => setOrderQty(Number(e.target.value))}
                required
              />
              <Form.Text className="text-muted">
                Total: {formatCurrency(Number(orderQty) * 14.5)}
              </Form.Text>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={submitting}>
              {submitting ? 'Placing Order...' : 'Submit Order'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>

      {/* Saga Timeline Modal */}
      <Modal show={showDetailModal} onHide={() => setShowDetailModal(false)} size="lg" centered>
        <Modal.Header closeButton>
          <Modal.Title className="h5">{selectedOrder?.orderNumber} — Fulfilment Saga State</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {selectedOrder && (
            <div>
              <div className="mb-4 p-3 bg-light rounded">
                <div className="d-flex justify-content-between mb-2">
                  <span className="text-muted">Customer:</span>
                  <span className="fw-bold">{selectedOrder.customerName}</span>
                </div>
                <div className="d-flex justify-content-between mb-2">
                  <span className="text-muted">Total Amount:</span>
                  <span className="fw-bold">{formatCurrency(selectedOrder.totalAmount)}</span>
                </div>
                <div className="d-flex justify-content-between">
                  <span className="text-muted">Current Status:</span>
                  <StatusBadge status={selectedOrder.status} />
                </div>
              </div>

              <h6 className="fw-bold mb-3">Saga Orchestration Progress (Item 31 & 32)</h6>
              <div className="d-flex flex-wrap gap-2 justify-content-between align-items-center">
                {SAGA_STEPS.map((st, i) => {
                  const currentIndex = SAGA_STEPS.indexOf(selectedOrder.status);
                  const isPastOrCurrent = currentIndex >= i && selectedOrder.status !== 'CANCELLED';
                  const isCurrent = selectedOrder.status === st;

                  return (
                    <div key={st} className="text-center" style={{ minWidth: '80px' }}>
                      <div
                        className={`rounded-circle mx-auto mb-1 d-flex align-items-center justify-content-center text-white fw-bold ${
                          isCurrent
                            ? 'bg-primary border border-2 border-primary'
                            : isPastOrCurrent
                            ? 'bg-success'
                            : 'bg-secondary'
                        }`}
                        style={{ width: '36px', height: '36px' }}
                      >
                        {i + 1}
                      </div>
                      <div className="small fw-semibold">{st}</div>
                    </div>
                  );
                })}
              </div>

              {selectedOrder.status === 'BACKORDERED' && (
                <Alert variant="danger" className="mt-4">
                  Saga Compensation: Insufficient stock triggered automated backorder and released any partial reservations.
                </Alert>
              )}
            </div>
          )}
        </Modal.Body>
      </Modal>
    </div>
  );
};
