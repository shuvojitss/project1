// System constants and status mappings

import { RoleName } from '@/types';

export const ROLE_LABELS: Record<RoleName, string> = {
  ADMIN: 'System Administrator',
  PROCUREMENT_MANAGER: 'Procurement Manager',
  PROCUREMENT_OFFICER: 'Procurement Officer',
  WAREHOUSE_MANAGER: 'Warehouse Manager',
  WAREHOUSE_OPERATOR: 'Warehouse Operator',
  LOGISTICS_PLANNER: 'Logistics Planner',
  SALES_COORDINATOR: 'Sales Coordinator',
  SUPPLIER_USER: 'Supplier Portal User',
  EXECUTIVE: 'Executive / Management',
};

export const STATUS_BADGE_VARIANTS: Record<string, string> = {
  // Common
  ACTIVE: 'success',
  INACTIVE: 'secondary',
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger',
  COMPLETED: 'success',
  CANCELLED: 'secondary',

  // Order
  CREATED: 'info',
  RESERVING: 'warning',
  CONFIRMED: 'primary',
  PICKING: 'info',
  PACKED: 'primary',
  SHIPPED: 'info',
  DELIVERED: 'success',
  BACKORDERED: 'danger',

  // Procurement
  DRAFT: 'secondary',
  SUBMITTED: 'info',
  PENDING_APPROVAL: 'warning',
  ISSUED: 'primary',
  ACKNOWLEDGED: 'info',
  PARTIALLY_RECEIVED: 'warning',
  RECEIVED: 'success',

  // Transport
  PLANNED: 'secondary',
  DISPATCHED: 'primary',
  IN_TRANSIT: 'info',
  OUT_FOR_DELIVERY: 'warning',
  DELAYED: 'danger',
  EXCEPTION: 'danger',

  // Severity
  INFO: 'info',
  WARNING: 'warning',
  ERROR: 'danger',
};
