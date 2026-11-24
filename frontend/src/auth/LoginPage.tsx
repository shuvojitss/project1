import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Card, Form, Button, Alert, Row, Col, Badge } from 'react-bootstrap';
import { useAuth } from '@/hooks/useAuth';
import { RoleName } from '@/types';
import { ROLE_LABELS } from '@/utils/constants';

export const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { login, switchUserForDemo } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsSubmitting(true);
    try {
      await login(username, password);
      navigate('/');
    } catch (err: any) {
      setError(err?.detail || 'Invalid username or password. You can also click a quick demo role below.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleQuickRole = (role: RoleName) => {
    switchUserForDemo(role);
    if (role === 'SUPPLIER_USER') {
      navigate('/supplier-portal');
    } else {
      navigate('/');
    }
  };

  return (
    <div className="bg-light min-vh-100 d-flex align-items-center py-5">
      <Container>
        <Row className="justify-content-center">
          <Col md={8} lg={6}>
            <div className="text-center mb-4">
              <h1 className="fw-bold text-primary mb-1">📦 Infy_SupplyChain360</h1>
              <p className="text-muted">Enterprise Supply Chain & Logistics Operations Platform</p>
            </div>

            <Card className="shadow-sm border-0 mb-4">
              <Card.Body className="p-4">
                <h4 className="card-title mb-3">Sign In</h4>
                {error && <Alert variant="danger">{error}</Alert>}

                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-3" controlId="formUsername">
                    <Form.Label>Username</Form.Label>
                    <Form.Control
                      type="text"
                      placeholder="e.g. admin or proc_manager"
                      value={username}
                      onChange={(e) => setUsername(e.target.value)}
                      required
                      autoFocus
                    />
                  </Form.Group>

                  <Form.Group className="mb-3" controlId="formPassword">
                    <Form.Label>Password</Form.Label>
                    <Form.Control
                      type="password"
                      placeholder="Enter password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                    />
                  </Form.Group>

                  <Button
                    variant="primary"
                    type="submit"
                    className="w-100 py-2 mt-2"
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? 'Authenticating...' : 'Sign In'}
                  </Button>
                </Form>
              </Card.Body>
            </Card>

            <Card className="shadow-sm border-0">
              <Card.Body className="p-4">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <h6 className="mb-0 fw-bold text-secondary">Quick Switch / Demo Roles</h6>
                  <Badge bg="info">Item 10 Role Matrix</Badge>
                </div>
                <p className="small text-muted mb-3">
                  Click any role to authenticate instantly and evaluate role-based menus and operational permissions:
                </p>

                <div className="d-flex flex-wrap gap-2">
                  {(Object.keys(ROLE_LABELS) as RoleName[]).map((r) => (
                    <Button
                      key={r}
                      variant="outline-secondary"
                      size="sm"
                      onClick={() => handleQuickRole(r)}
                    >
                      {ROLE_LABELS[r]}
                    </Button>
                  ))}
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
};
