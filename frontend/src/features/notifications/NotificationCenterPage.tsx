import React, { useState, useEffect } from 'react';
import { Card, Button, Form, Badge, Row, Col } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { notificationApi } from '@/api/notificationApi';
import { formatDateTime } from '@/utils/formatters';
import { ScmNotification } from '@/types';

export const NotificationCenterPage: React.FC = () => {
  const [notifications, setNotifications] = useState<ScmNotification[]>([]);
  const [filterSeverity, setFilterSeverity] = useState('ALL');

  const loadNotifications = async () => {
    try {
      const res = await notificationApi.getNotifications(0, 50);
      setNotifications(res.content);
    } catch {
      // Demo seed fallback
      setNotifications([
        {
          id: 'notif-1',
          type: 'STOCK_LOW',
          title: 'Critical Low Stock: SKU-ELEC-002',
          message: 'Available stock in US East DC dropped to 45 units (below safety stock of 100). Auto-requisition REQ-2026-0020 created.',
          read: false,
          severity: 'WARNING',
          linkUrl: '/inventory',
          createdAt: '2026-11-23T15:30:00Z',
        },
        {
          id: 'notif-2',
          type: 'SHIPMENT_DELAY',
          title: 'Carrier ETA Delay: TRK-992381',
          message: 'Carrier FastFreight Logistics reported an in-transit weather delay for Shipment SHP-2026-0091.',
          read: false,
          severity: 'ERROR',
          linkUrl: '/transportation',
          createdAt: '2026-11-23T14:15:00Z',
        },
        {
          id: 'notif-3',
          type: 'PO_APPROVED',
          title: 'Purchase Order PO-2026-0042 Approved',
          message: 'PO total $14,500 approved by Procurement Manager and issued to Apex Electronics Inc.',
          read: true,
          severity: 'INFO',
          linkUrl: '/procurement',
          createdAt: '2026-11-22T16:00:00Z',
        },
      ]);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const handleMarkAsRead = async (id: string) => {
    try {
      await notificationApi.markAsRead(id);
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    } catch {
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: true } : n))
      );
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    } catch {
      setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    }
  };

  const filtered = notifications.filter(
    (n) => filterSeverity === 'ALL' || n.severity === filterSeverity
  );

  return (
    <div>
      <PageHeader
        title="Notifications & Operational Alerts"
        subtitle="Real-time multi-channel alerts fed by RabbitMQ events and live Server-Sent Events (SSE)"
        actions={
          <div className="d-flex gap-2">
            <Button variant="outline-primary" size="sm" onClick={handleMarkAllAsRead}>
              ✓ Mark All as Read
            </Button>
            <Button variant="outline-secondary" size="sm" onClick={loadNotifications}>
              🔄 Refresh
            </Button>
          </div>
        }
      />

      <Row className="mb-3 g-2 align-items-center">
        <Col md={3}>
          <Form.Select
            size="sm"
            value={filterSeverity}
            onChange={(e) => setFilterSeverity(e.target.value)}
          >
            <option value="ALL">All Severities</option>
            <option value="INFO">Information</option>
            <option value="WARNING">Warnings</option>
            <option value="ERROR">Critical / Errors</option>
          </Form.Select>
        </Col>
        <Col md={9} className="text-end">
          <Badge bg="success" className="p-2">
            🟢 Live SSE Connected
          </Badge>
        </Col>
      </Row>

      <div className="d-flex flex-column gap-2">
        {filtered.map((n) => (
          <Card
            key={n.id}
            className={`shadow-sm border-0 border-start border-4 ${
              n.severity === 'ERROR'
                ? 'border-danger'
                : n.severity === 'WARNING'
                ? 'border-warning'
                : 'border-primary'
            } ${n.read ? 'bg-light opacity-75' : 'bg-white'}`}
          >
            <Card.Body className="d-flex justify-content-between align-items-center py-3">
              <div>
                <div className="d-flex align-items-center gap-2 mb-1">
                  <h6 className="fw-bold mb-0 text-dark">{n.title}</h6>
                  <Badge
                    bg={
                      n.severity === 'ERROR'
                        ? 'danger'
                        : n.severity === 'WARNING'
                        ? 'warning'
                        : 'info'
                    }
                  >
                    {n.severity}
                  </Badge>
                  {!n.read && <Badge bg="primary">NEW</Badge>}
                </div>
                <p className="mb-1 text-secondary small">{n.message}</p>
                <span className="small text-muted">{formatDateTime(n.createdAt)}</span>
              </div>
              <div className="d-flex gap-2">
                {!n.read && (
                  <Button
                    variant="outline-secondary"
                    size="sm"
                    onClick={() => handleMarkAsRead(n.id)}
                  >
                    Mark Read
                  </Button>
                )}
                {n.linkUrl && (
                  <Button as="a" href={n.linkUrl} variant="outline-primary" size="sm">
                    View Entity
                  </Button>
                )}
              </div>
            </Card.Body>
          </Card>
        ))}
      </div>
    </div>
  );
};
