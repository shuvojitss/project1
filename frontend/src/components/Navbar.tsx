import React, { useState } from 'react';
import { Navbar as BsNavbar, Container, Nav, NavDropdown, Form, Badge } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { useSse } from '@/hooks/useSse';
import { ROLE_LABELS } from '@/utils/constants';
import { RoleName } from '@/types';

const LOCATIONS = [
  { id: 'wh-us-east-1', name: 'US East DC (New Jersey)' },
  { id: 'wh-us-west-1', name: 'US West Hub (California)' },
  { id: 'wh-eu-central-1', name: 'EU Central Fulfilment (Frankfurt)' },
  { id: 'wh-ap-south-1', name: 'APAC Regional Hub (Singapore)' },
];

export const Navbar: React.FC = () => {
  const { user, logout, activeLocationId, setActiveLocationId, switchUserForDemo } = useAuth();
  const { notifications, unreadCount, setNotifications, setUnreadCount } = useSse(!!user);
  const [showNotifications, setShowNotifications] = useState(false);
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const handleNotificationClick = (id: string, linkUrl?: string) => {
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
    setUnreadCount((c) => Math.max(0, c - 1));
    setShowNotifications(false);
    if (linkUrl) {
      navigate(linkUrl);
    }
  };

  const primaryRole = user?.roles[0] || 'ADMIN';

  return (
    <BsNavbar bg="dark" variant="dark" expand="lg" sticky="top" className="shadow-sm py-2 px-3">
      <Container fluid>
        <BsNavbar.Brand as={Link} to="/" className="fw-bold d-flex align-items-center gap-2">
          <span>📦</span>
          <span>Infy_SupplyChain360</span>
        </BsNavbar.Brand>

        <BsNavbar.Toggle aria-controls="top-navbar-nav" />

        <BsNavbar.Collapse id="top-navbar-nav" className="justify-content-end">
          <Nav className="align-items-center gap-3">
            {/* Active Warehouse Location Selector per Item 42 & 53 */}
            {!user?.roles.includes('SUPPLIER_USER') && (
              <div className="d-flex align-items-center gap-2">
                <span className="text-light small">📍 Location:</span>
                <Form.Select
                  size="sm"
                  value={activeLocationId}
                  onChange={(e) => setActiveLocationId(e.target.value)}
                  style={{ width: '230px', backgroundColor: '#334155', color: '#fff', borderColor: '#475569' }}
                >
                  {LOCATIONS.map((loc) => (
                    <option key={loc.id} value={loc.id}>
                      {loc.name}
                    </option>
                  ))}
                </Form.Select>
              </div>
            )}

            {/* Notification Bell Dropdown per Item 42 & 46 */}
            <NavDropdown
              title={
                <span className="position-relative d-inline-block p-1">
                  🔔
                  {unreadCount > 0 && (
                    <Badge
                      pill
                      bg="danger"
                      className="position-absolute top-0 start-100 translate-middle small"
                      style={{ fontSize: '0.65rem' }}
                    >
                      {unreadCount}
                    </Badge>
                  )}
                </span>
              }
              id="notifications-dropdown"
              align="end"
              show={showNotifications}
              onToggle={(isOpen) => setShowNotifications(isOpen)}
            >
              <div style={{ width: '320px', maxHeight: '380px', overflowY: 'auto' }} className="p-2">
                <div className="d-flex justify-content-between align-items-center mb-2 px-2">
                  <span className="fw-bold small">Alerts & Notifications</span>
                  <Link
                    to="/notifications"
                    className="small text-decoration-none"
                    onClick={() => setShowNotifications(false)}
                  >
                    View All
                  </Link>
                </div>
                {notifications.length === 0 ? (
                  <p className="text-muted small text-center my-3">No notifications</p>
                ) : (
                  notifications.slice(0, 5).map((n) => (
                    <div
                      key={n.id}
                      className={`p-2 mb-1 rounded small cursor-pointer ${
                        n.read ? 'bg-light text-muted' : 'bg-primary-subtle border-start border-3 border-primary'
                      }`}
                      style={{ cursor: 'pointer' }}
                      onClick={() => handleNotificationClick(n.id, n.linkUrl)}
                    >
                      <div className="fw-semibold text-dark">{n.title}</div>
                      <div className="text-truncate">{n.message}</div>
                    </div>
                  ))
                )}
              </div>
            </NavDropdown>

            {/* User & Role Switcher Menu */}
            <NavDropdown
              title={
                <span className="d-inline-flex align-items-center gap-2 text-light">
                  <span className="bg-primary rounded-circle px-2 py-1 small fw-bold">
                    {user?.fullName.charAt(0) || 'U'}
                  </span>
                  <span className="small">{user?.fullName || 'User'}</span>
                </span>
              }
              id="user-profile-dropdown"
              align="end"
            >
              <div className="px-3 py-2 border-bottom">
                <div className="fw-bold small">{user?.fullName}</div>
                <div className="text-muted small">{user?.email}</div>
                <Badge bg="info" className="mt-1 small">
                  {ROLE_LABELS[primaryRole] || primaryRole}
                </Badge>
              </div>

              <div className="px-3 py-1 text-muted small fw-semibold">Switch Demo Role</div>
              {(Object.keys(ROLE_LABELS) as RoleName[]).slice(0, 6).map((r) => (
                <NavDropdown.Item key={r} onClick={() => switchUserForDemo(r)} className="small">
                  {ROLE_LABELS[r]}
                </NavDropdown.Item>
              ))}

              <NavDropdown.Divider />
              <NavDropdown.Item onClick={handleLogout} className="text-danger small">
                Sign Out
              </NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </BsNavbar.Collapse>
      </Container>
    </BsNavbar>
  );
};
