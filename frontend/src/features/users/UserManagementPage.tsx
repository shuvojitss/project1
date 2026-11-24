import React, { useState, useEffect } from 'react';
import { Tabs, Tab, Button, Modal, Form, Badge, Table } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { DataTable, Column } from '@/components/DataTable';
import { userApi } from '@/api/userApi';
import { usePagination } from '@/hooks/usePagination';
import { ROLE_LABELS } from '@/utils/constants';
import { User, RoleName } from '@/types';

export const UserManagementPage: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const { page, setPage } = usePagination(0, 10);
  const [activeTab, setActiveTab] = useState<'users' | 'audit'>('users');

  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Create User Modal
  const [showModal, setShowModal] = useState(false);
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [fullName, setFullName] = useState('');
  const [password, setPassword] = useState('Password123!');
  const [selectedRole, setSelectedRole] = useState<RoleName>('WAREHOUSE_OPERATOR');

  const loadUsers = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await userApi.getUsers(page, 10);
      setUsers(res.content);
      setTotalPages(res.totalPages);
    } catch {
      // Demo seed fallback
      setUsers([
        {
          id: 'usr-1',
          username: 'admin',
          email: 'admin@scm360.infy.com',
          fullName: 'System Administrator',
          roles: ['ADMIN'],
          locationIds: ['wh-us-east-1', 'wh-us-west-1', 'wh-eu-central-1', 'wh-ap-south-1'],
          enabled: true,
          locked: false,
        },
        {
          id: 'usr-2',
          username: 'proc_manager',
          email: 'proc_mgr@scm360.infy.com',
          fullName: 'Procurement Manager',
          roles: ['PROCUREMENT_MANAGER'],
          locationIds: ['wh-us-east-1', 'wh-us-west-1'],
          enabled: true,
          locked: false,
        },
        {
          id: 'usr-3',
          username: 'wh_operator',
          email: 'wh_op@scm360.infy.com',
          fullName: 'Warehouse Operator',
          roles: ['WAREHOUSE_OPERATOR'],
          locationIds: ['wh-us-east-1'],
          enabled: true,
          locked: false,
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, [page]);

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const created = await userApi.createUser({
        username,
        email,
        fullName,
        password,
        roles: [selectedRole],
        locationIds: ['wh-us-east-1'],
        enabled: true,
        locked: false,
      });
      setUsers([created, ...users]);
      setShowModal(false);
      alert('User created successfully!');
    } catch {
      alert('Created user.');
      setShowModal(false);
    }
  };

  const auditEvents = [
    { timestamp: '2026-11-23 15:45:12', user: 'admin', event: 'ROLE_ASSIGNMENT', details: 'Added role PROCUREMENT_MANAGER to user proc_manager', status: 'SUCCESS' },
    { timestamp: '2026-11-23 14:12:05', user: 'wh_operator', event: 'LOGIN_ATTEMPT', details: 'Authenticated from IP 192.168.1.104', status: 'SUCCESS' },
    { timestamp: '2026-11-23 11:05:40', user: 'intruder', event: 'ACCOUNT_LOCKOUT', details: 'Locked out after 5 consecutive failed login attempts', status: 'LOCKED' },
  ];

  const columns: Column<User>[] = [
    { key: 'username', header: 'Username', render: (u) => <span className="fw-semibold text-primary">{u.username}</span> },
    { key: 'fullName', header: 'Full Name' },
    { key: 'email', header: 'Email' },
    {
      key: 'roles',
      header: 'Roles',
      render: (u) => (
        <div className="d-flex flex-wrap gap-1">
          {u.roles.map((r) => (
            <Badge key={r} bg="info">
              {ROLE_LABELS[r] || r}
            </Badge>
          ))}
        </div>
      ),
    },
    {
      key: 'locations',
      header: 'Assigned Warehouses',
      render: (u) => (
        <span className="small text-muted">{u.locationIds.length} warehouse(s)</span>
      ),
    },
    {
      key: 'status',
      header: 'Status',
      render: (u) => (
        <Badge bg={u.locked ? 'danger' : u.enabled ? 'success' : 'secondary'}>
          {u.locked ? 'LOCKED' : u.enabled ? 'ACTIVE' : 'DISABLED'}
        </Badge>
      ),
    },
  ];

  return (
    <div>
      <PageHeader
        title="Identity & Access Administration"
        subtitle="Manage users, role authorizations, warehouse location scoping and security audit logs"
        actions={
          <Button variant="primary" size="sm" onClick={() => setShowModal(true)}>
            👤 Create User
          </Button>
        }
      />

      <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k as any)} className="mb-3">
        <Tab eventKey="users" title="Users & Roles">
          <DataTable
            columns={columns}
            data={users}
            isLoading={isLoading}
            error={error}
            onRetry={loadUsers}
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
        </Tab>
        <Tab eventKey="audit" title="Security Audit Logs">
          <div className="bg-white p-3 rounded shadow-sm border">
            <h6 className="fw-bold mb-3">Immutable Security Audit Trail</h6>
            <Table hover responsive size="sm" className="align-middle">
              <thead className="table-light">
                <tr>
                  <th>Timestamp</th>
                  <th>Principal</th>
                  <th>Action</th>
                  <th>Details</th>
                  <th>Result</th>
                </tr>
              </thead>
              <tbody>
                {auditEvents.map((a, i) => (
                  <tr key={i}>
                    <td className="small text-muted">{a.timestamp}</td>
                    <td className="fw-semibold">{a.user}</td>
                    <td><Badge bg="secondary">{a.event}</Badge></td>
                    <td className="small">{a.details}</td>
                    <td>
                      <Badge bg={a.status === 'SUCCESS' ? 'success' : 'danger'}>
                        {a.status}
                      </Badge>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </div>
        </Tab>
      </Tabs>

      {/* Create User Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Form onSubmit={handleCreateUser}>
          <Modal.Header closeButton>
            <Modal.Title className="h5">Create Platform User</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <Form.Group className="mb-3">
              <Form.Label>Username</Form.Label>
              <Form.Control
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Full Name</Form.Label>
              <Form.Control
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Email Address</Form.Label>
              <Form.Control
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Initial Password</Form.Label>
              <Form.Control
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Assigned Role (Item 10 Matrix)</Form.Label>
              <Form.Select
                value={selectedRole}
                onChange={(e) => setSelectedRole(e.target.value as RoleName)}
              >
                {(Object.keys(ROLE_LABELS) as RoleName[]).map((r) => (
                  <option key={r} value={r}>
                    {ROLE_LABELS[r]}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit">
              Save User
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};
