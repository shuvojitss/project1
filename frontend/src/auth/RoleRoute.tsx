import React, { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { RoleName } from '@/types';
import { Alert, Container } from 'react-bootstrap';

interface RoleRouteProps {
  children: ReactNode;
  allowedRoles: RoleName[];
}

export const RoleRoute: React.FC<RoleRouteProps> = ({ children, allowedRoles }) => {
  const { user, hasAnyRole } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (!hasAnyRole(allowedRoles)) {
    // If supplier user, redirect to supplier portal
    if (user.roles.includes('SUPPLIER_USER')) {
      return <Navigate to="/supplier-portal" replace />;
    }

    return (
      <Container className="py-5">
        <Alert variant="danger">
          <Alert.Heading>Access Denied</Alert.Heading>
          <p>
            You do not have the required permissions to view this resource. Required roles:{' '}
            <strong>{allowedRoles.join(', ')}</strong>.
          </p>
        </Alert>
      </Container>
    );
  }

  return <>{children}</>;
};
