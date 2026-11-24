import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { RoleName } from '@/types';

interface NavItem {
  to: string;
  label: string;
  icon: string;
  allowedRoles: RoleName[];
}

const ALL_ROLES: RoleName[] = [
  'ADMIN',
  'PROCUREMENT_MANAGER',
  'PROCUREMENT_OFFICER',
  'WAREHOUSE_MANAGER',
  'WAREHOUSE_OPERATOR',
  'LOGISTICS_PLANNER',
  'SALES_COORDINATOR',
  'EXECUTIVE',
];

const NAV_ITEMS: NavItem[] = [
  {
    to: '/',
    label: 'Dashboard',
    icon: '📊',
    allowedRoles: ALL_ROLES,
  },
  {
    to: '/catalog',
    label: 'Product Catalog',
    icon: '🏷️',
    allowedRoles: ['ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER', 'SALES_COORDINATOR', 'EXECUTIVE'],
  },
  {
    to: '/suppliers',
    label: 'Suppliers',
    icon: '🤝',
    allowedRoles: ['ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER', 'EXECUTIVE'],
  },
  {
    to: '/procurement',
    label: 'Procurement & POs',
    icon: '📝',
    allowedRoles: ['ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER', 'EXECUTIVE'],
  },
  {
    to: '/inventory',
    label: 'Inventory Control',
    icon: '📦',
    allowedRoles: ['ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR', 'PROCUREMENT_MANAGER', 'EXECUTIVE'],
  },
  {
    to: '/warehouse',
    label: 'Warehouse Operations',
    icon: '🏭',
    allowedRoles: ['ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR'],
  },
  {
    to: '/orders',
    label: 'Sales Orders',
    icon: '🛒',
    allowedRoles: ['ADMIN', 'SALES_COORDINATOR', 'LOGISTICS_PLANNER', 'EXECUTIVE'],
  },
  {
    to: '/transportation',
    label: 'Transportation',
    icon: '🚚',
    allowedRoles: ['ADMIN', 'LOGISTICS_PLANNER', 'EXECUTIVE'],
  },
  {
    to: '/analytics',
    label: 'Analytics & KPIs',
    icon: '📈',
    allowedRoles: ['ADMIN', 'EXECUTIVE', 'PROCUREMENT_MANAGER', 'WAREHOUSE_MANAGER', 'LOGISTICS_PLANNER'],
  },
  {
    to: '/notifications',
    label: 'Notifications',
    icon: '🔔',
    allowedRoles: ALL_ROLES,
  },
  {
    to: '/admin',
    label: 'Administration',
    icon: '⚙️',
    allowedRoles: ['ADMIN'],
  },
];

export const Sidebar: React.FC = () => {
  const { user } = useAuth();

  // If supplier user, only show supplier portal links per Item 44
  if (user?.roles.includes('SUPPLIER_USER')) {
    return (
      <div className="sidebar p-3" style={{ width: '250px' }}>
        <div className="text-muted small text-uppercase fw-bold px-3 mb-2">Supplier Portal</div>
        <NavLink
          to="/supplier-portal"
          className={({ isActive }) => `d-flex align-items-center gap-2 ${isActive ? 'active' : ''}`}
        >
          <span>🌐</span>
          <span>My Portal</span>
        </NavLink>
      </div>
    );
  }

  const authorizedItems = NAV_ITEMS.filter((item) =>
    item.allowedRoles.some((r) => user?.roles.includes(r))
  );

  return (
    <div className="sidebar p-3" style={{ width: '250px' }}>
      <div className="text-muted small text-uppercase fw-bold px-3 mb-2">Navigation</div>
      <nav className="nav flex-column">
        {authorizedItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end={item.to === '/'}
            className={({ isActive }) => `d-flex align-items-center gap-2 ${isActive ? 'active' : ''}`}
          >
            <span>{item.icon}</span>
            <span>{item.label}</span>
          </NavLink>
        ))}
      </nav>
    </div>
  );
};
