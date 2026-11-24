import React from 'react';
import { Card } from 'react-bootstrap';

interface KpiCardProps {
  title: string;
  value: string | number;
  subtitle?: string;
  badge?: string;
  badgeVariant?: 'success' | 'warning' | 'danger' | 'info' | 'primary';
  icon?: string;
}

export const KpiCard: React.FC<KpiCardProps> = ({
  title,
  value,
  subtitle,
  badge,
  badgeVariant = 'success',
  icon,
}) => {
  return (
    <Card className="h-100 shadow-sm border-0">
      <Card.Body>
        <div className="d-flex justify-content-between align-items-start mb-2">
          <span className="text-muted small text-uppercase fw-semibold">{title}</span>
          {icon && <span className="fs-5 text-secondary">{icon}</span>}
        </div>
        <div className="d-flex align-items-baseline gap-2 mb-1">
          <h3 className="fw-bold mb-0 text-dark">{value}</h3>
          {badge && <span className={`badge bg-${badgeVariant}`}>{badge}</span>}
        </div>
        {subtitle && <div className="text-muted small">{subtitle}</div>}
      </Card.Body>
    </Card>
  );
};
