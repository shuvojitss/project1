import React from 'react';
import { Badge } from 'react-bootstrap';
import { STATUS_BADGE_VARIANTS } from '@/utils/constants';

interface StatusBadgeProps {
  status: string | null | undefined;
}

export const StatusBadge: React.FC<StatusBadgeProps> = ({ status }) => {
  if (!status) return null;
  const variant = STATUS_BADGE_VARIANTS[status.toUpperCase()] || 'secondary';
  return (
    <Badge bg={variant} className="status-badge text-uppercase">
      {status.replace(/_/g, ' ')}
    </Badge>
  );
};
