// Shared TypeScript interfaces and enums mirroring API DTOs

export type RoleName =
  | 'ADMIN'
  | 'PROCUREMENT_MANAGER'
  | 'PROCUREMENT_OFFICER'
  | 'WAREHOUSE_MANAGER'
  | 'WAREHOUSE_OPERATOR'
  | 'LOGISTICS_PLANNER'
  | 'SALES_COORDINATOR'
  | 'SUPPLIER_USER'
  | 'EXECUTIVE';

export interface User {
  id: string;
  username: string;
  email: string;
  fullName: string;
  roles: RoleName[];
  locationIds: string[];
  supplierId?: string | null;
  enabled: boolean;
  locked: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  user: User;
}

export interface ApiError {
  type?: string;
  title?: string;
  status: number;
  detail: string;
  instance?: string;
  timestamp?: string;
  violations?: { field: string; message: string }[];
}

export interface PageResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// Catalog
export interface Product {
  id: string;
  sku: string;
  name: string;
  description?: string;
  categoryId: string;
  categoryName?: string;
  uom: string;
  weightKg: number;
  lengthCm?: number;
  widthCm?: number;
  heightCm?: number;
  unitCost: number;
  status: 'ACTIVE' | 'DISCONTINUED' | 'DRAFT';
  createdAt?: string;
}

export interface Category {
  id: string;
  code: string;
  name: string;
  parentId?: string | null;
}

// Supplier
export type SupplierStatus = 'ACTIVE' | 'PENDING_APPROVAL' | 'SUSPENDED' | 'INACTIVE';

export interface Supplier {
  id: string;
  code: string;
  name: string;
  contactEmail: string;
  contactPhone?: string;
  country: string;
  city: string;
  paymentTerms: string;
  currency: string;
  status: SupplierStatus;
  overallScore?: number;
}

export interface SupplierItem {
  id: string;
  supplierId: string;
  supplierName?: string;
  productId: string;
  productName?: string;
  supplierSku?: string;
  unitPrice: number;
  currency: string;
  leadTimeDays: number;
  minimumOrderQuantity: number;
  priority: number;
}

export interface SupplierScorecard {
  id: string;
  supplierId: string;
  periodYear: number;
  periodMonth: number;
  onTimeDeliveryRate: number;
  qualityScore: number;
  complianceScore: number;
  overallScore: number;
  totalOrders: number;
}

export interface Asn {
  id: string;
  asnNumber: string;
  poNumber: string;
  supplierId: string;
  shippedDate: string;
  expectedDeliveryDate: string;
  carrierName: string;
  trackingNumber: string;
  status: 'DISPATCHED' | 'IN_TRANSIT' | 'DELIVERED';
}

// Procurement
export type RequisitionStatus = 'DRAFT' | 'SUBMITTED' | 'APPROVED' | 'REJECTED' | 'ORDERED';
export type PoStatus = 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'REJECTED' | 'ISSUED' | 'ACKNOWLEDGED' | 'PARTIALLY_RECEIVED' | 'RECEIVED' | 'CANCELLED';

export interface Requisition {
  id: string;
  requisitionNumber: string;
  requestedBy: string;
  warehouseId: string;
  status: RequisitionStatus;
  totalEstimatedCost: number;
  notes?: string;
  createdAt: string;
  items: {
    id: string;
    productId: string;
    productName?: string;
    quantity: number;
    estimatedCost: number;
  }[];
}

export interface PurchaseOrder {
  id: string;
  poNumber: string;
  requisitionId?: string;
  supplierId: string;
  supplierName?: string;
  warehouseId: string;
  totalAmount: number;
  currency: string;
  status: PoStatus;
  approvedBy?: string;
  issuedAt?: string;
  createdAt: string;
  lines: {
    id: string;
    productId: string;
    productName?: string;
    sku?: string;
    quantity: number;
    unitPrice: number;
    receivedQuantity: number;
  }[];
}

// Inventory
export interface StockLevel {
  id: string;
  productId: string;
  productSku?: string;
  productName?: string;
  warehouseId: string;
  warehouseName?: string;
  onHandQuantity: number;
  reservedQuantity: number;
  allocatedQuantity: number;
  availableQuantity: number;
  reorderPoint: number;
  reorderQuantity: number;
  safetyStock: number;
  updatedAt: string;
}

export interface StockLedgerEntry {
  id: string;
  productId: string;
  warehouseId: string;
  transactionType: 'INBOUND_RECEIPT' | 'PICK' | 'ADJUSTMENT' | 'TRANSFER_IN' | 'TRANSFER_OUT' | 'RETURN';
  quantityDelta: number;
  balanceAfter: number;
  referenceType: string;
  referenceId: string;
  createdAt: string;
}

export interface ReorderRule {
  id: string;
  productId: string;
  warehouseId: string;
  minQuantity: number;
  reorderPoint: number;
  reorderQuantity: number;
  safetyStock: number;
  autoRequisition: boolean;
  preferredSupplierId?: string;
}

// Warehouse
export interface Warehouse {
  id: string;
  code: string;
  name: string;
  addressLine?: string;
  city: string;
  state?: string;
  country: string;
  postalCode?: string;
  active: boolean;
}

export interface InboundReceipt {
  id: string;
  receiptNumber: string;
  poNumber: string;
  supplierId: string;
  warehouseId: string;
  receivedDate: string;
  status: 'PENDING' | 'PUTAWAY_PENDING' | 'COMPLETED';
  items: {
    productId: string;
    quantityReceived: number;
    quantityRejected: number;
    discrepancyReason?: string;
  }[];
}

export interface PutawayTask {
  id: string;
  receiptId: string;
  productId: string;
  warehouseId: string;
  targetBinId: string;
  targetBinCode?: string;
  quantity: number;
  status: 'PENDING' | 'COMPLETED';
}

export interface TransferOrder {
  id: string;
  transferNumber: string;
  sourceWarehouseId: string;
  destinationWarehouseId: string;
  productId: string;
  quantity: number;
  status: 'REQUESTED' | 'IN_TRANSIT' | 'RECEIVED' | 'CANCELLED';
  createdAt: string;
}

export interface CycleCount {
  id: string;
  countNumber: string;
  warehouseId: string;
  status: 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED' | 'APPROVED';
  items: {
    productId: string;
    systemQuantity: number;
    countedQuantity: number;
    variance: number;
  }[];
}

// Orders
export type OrderStatus =
  | 'CREATED'
  | 'RESERVING'
  | 'CONFIRMED'
  | 'PICKING'
  | 'PACKED'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'BACKORDERED'
  | 'CANCELLED';

export interface CustomerOrder {
  id: string;
  orderNumber: string;
  customerId: string;
  customerName: string;
  warehouseId: string;
  totalAmount: number;
  status: OrderStatus;
  shippingAddress: string;
  estimatedDeliveryDate?: string;
  createdAt: string;
  lines: {
    id: string;
    productId: string;
    productName?: string;
    sku?: string;
    quantity: number;
    unitPrice: number;
  }[];
}

// Transportation
export type ShipmentStatus = 'PLANNED' | 'DISPATCHED' | 'IN_TRANSIT' | 'OUT_FOR_DELIVERY' | 'DELIVERED' | 'DELAYED' | 'EXCEPTION';

export interface Carrier {
  id: string;
  code: string;
  name: string;
  contactPhone: string;
  contactEmail: string;
  active: boolean;
}

export interface RateCard {
  id: string;
  carrierId: string;
  carrierName?: string;
  originRegion: string;
  destinationRegion: string;
  baseRate: number;
  perKgRate: number;
  estimatedTransitDays: number;
}

export interface Shipment {
  id: string;
  shipmentNumber: string;
  orderId: string;
  carrierId: string;
  carrierName?: string;
  originWarehouseId: string;
  trackingNumber: string;
  status: ShipmentStatus;
  weightKg: number;
  estimatedDeliveryDate: string;
  actualDeliveryDate?: string;
  shippingCost: number;
  createdAt: string;
}

export interface TrackingCheckpoint {
  id: string;
  shipmentId: string;
  location: string;
  status: string;
  notes?: string;
  timestamp: string;
}

// Analytics
export interface DashboardSummary {
  otifRate: number;
  inventoryAccuracy: number;
  orderFillRate: number;
  openPoCount: number;
  lowStockCount: number;
  delayedShipmentCount: number;
  totalRevenue: number;
  totalSpend: number;
}

export interface TrendDataPoint {
  date: string;
  value: number;
  target?: number;
}

// Notification
export interface ScmNotification {
  id: string;
  recipientUserId?: string;
  targetRole?: RoleName;
  type: string;
  title: string;
  message: string;
  read: boolean;
  severity: 'INFO' | 'WARNING' | 'ERROR';
  linkUrl?: string;
  createdAt: string;
}
