import React, { ReactNode } from 'react';
import { Breadcrumb } from 'react-bootstrap';

interface PageHeaderProps {
  title: string;
  subtitle?: string;
  breadcrumbs?: { label: string; href?: string }[];
  actions?: ReactNode;
}

export const PageHeader: React.FC<PageHeaderProps> = ({ title, subtitle, breadcrumbs, actions }) => {
  return (
    <div className="d-flex flex-wrap justify-content-between align-items-center mb-4 pb-2 border-bottom">
      <div>
        {breadcrumbs && (
          <Breadcrumb className="small mb-1">
            {breadcrumbs.map((b, idx) => (
              <Breadcrumb.Item key={idx} href={b.href} active={idx === breadcrumbs.length - 1}>
                {b.label}
              </Breadcrumb.Item>
            ))}
          </Breadcrumb>
        )}
        <h2 className="h4 fw-bold mb-0 text-dark">{title}</h2>
        {subtitle && <p className="text-muted small mb-0 mt-1">{subtitle}</p>}
      </div>
      {actions && <div className="mt-2 mt-md-0 d-flex gap-2">{actions}</div>}
    </div>
  );
};
