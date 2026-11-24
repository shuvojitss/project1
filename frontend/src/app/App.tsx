import React, { Suspense, lazy } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from '@/auth/AuthContext';
import { ProtectedRoute } from '@/auth/ProtectedRoute';
import { RoleRoute } from '@/auth/RoleRoute';
import { Layout } from '@/components/Layout';
import { LoginPage } from '@/auth/LoginPage';
import { Spinner } from 'react-bootstrap';

// Lazy loaded feature screens per Item 44
const DashboardPage = lazy(() =>
  import('@/features/dashboard/DashboardPage').then((m) => ({ default: m.DashboardPage }))
);
const CatalogPage = lazy(() =>
  import('@/features/catalog/CatalogPage').then((m) => ({ default: m.CatalogPage }))
);
const SupplierListPage = lazy(() =>
  import('@/features/suppliers/SupplierListPage').then((m) => ({ default: m.SupplierListPage }))
);
const ProcurementPage = lazy(() =>
  import('@/features/procurement/ProcurementPage').then((m) => ({ default: m.ProcurementPage }))
);
const InventoryPage = lazy(() =>
  import('@/features/inventory/InventoryPage').then((m) => ({ default: m.InventoryPage }))
);
const WarehousePage = lazy(() =>
  import('@/features/warehouse/WarehousePage').then((m) => ({ default: m.WarehousePage }))
);
const OrderPage = lazy(() =>
  import('@/features/orders/OrderPage').then((m) => ({ default: m.OrderPage }))
);
const TransportationPage = lazy(() =>
  import('@/features/transportation/TransportationPage').then((m) => ({ default: m.TransportationPage }))
);
const AnalyticsPage = lazy(() =>
  import('@/features/analytics/AnalyticsPage').then((m) => ({ default: m.AnalyticsPage }))
);
const NotificationCenterPage = lazy(() =>
  import('@/features/notifications/NotificationCenterPage').then((m) => ({ default: m.NotificationCenterPage }))
);
const UserManagementPage = lazy(() =>
  import('@/features/users/UserManagementPage').then((m) => ({ default: m.UserManagementPage }))
);
const SupplierPortalPage = lazy(() =>
  import('@/features/supplier-portal/SupplierPortalPage').then((m) => ({ default: m.SupplierPortalPage }))
);

const PageLoader = () => (
  <div className="d-flex justify-content-center align-items-center py-5">
    <Spinner animation="border" variant="primary" role="status">
      <span className="visually-hidden">Loading page...</span>
    </Spinner>
  </div>
);

export const App: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Suspense fallback={<PageLoader />}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route index element={<DashboardPage />} />
              <Route
                path="catalog"
                element={
                  <RoleRoute
                    allowedRoles={[
                      'ADMIN',
                      'PROCUREMENT_MANAGER',
                      'PROCUREMENT_OFFICER',
                      'SALES_COORDINATOR',
                      'EXECUTIVE',
                    ]}
                  >
                    <CatalogPage />
                  </RoleRoute>
                }
              />
              <Route
                path="suppliers"
                element={
                  <RoleRoute allowedRoles={['ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER', 'EXECUTIVE']}>
                    <SupplierListPage />
                  </RoleRoute>
                }
              />
              <Route
                path="procurement"
                element={
                  <RoleRoute allowedRoles={['ADMIN', 'PROCUREMENT_MANAGER', 'PROCUREMENT_OFFICER', 'EXECUTIVE']}>
                    <ProcurementPage />
                  </RoleRoute>
                }
              />
              <Route
                path="inventory"
                element={
                  <RoleRoute
                    allowedRoles={[
                      'ADMIN',
                      'WAREHOUSE_MANAGER',
                      'WAREHOUSE_OPERATOR',
                      'PROCUREMENT_MANAGER',
                      'EXECUTIVE',
                    ]}
                  >
                    <InventoryPage />
                  </RoleRoute>
                }
              />
              <Route
                path="warehouse"
                element={
                  <RoleRoute allowedRoles={['ADMIN', 'WAREHOUSE_MANAGER', 'WAREHOUSE_OPERATOR']}>
                    <WarehousePage />
                  </RoleRoute>
                }
              />
              <Route
                path="orders"
                element={
                  <RoleRoute allowedRoles={['ADMIN', 'SALES_COORDINATOR', 'LOGISTICS_PLANNER', 'EXECUTIVE']}>
                    <OrderPage />
                  </RoleRoute>
                }
              />
              <Route
                path="transportation"
                element={
                  <RoleRoute allowedRoles={['ADMIN', 'LOGISTICS_PLANNER', 'EXECUTIVE']}>
                    <TransportationPage />
                  </RoleRoute>
                }
              />
              <Route
                path="analytics"
                element={
                  <RoleRoute
                    allowedRoles={[
                      'ADMIN',
                      'EXECUTIVE',
                      'PROCUREMENT_MANAGER',
                      'WAREHOUSE_MANAGER',
                      'LOGISTICS_PLANNER',
                    ]}
                  >
                    <AnalyticsPage />
                  </RoleRoute>
                }
              />
              <Route path="notifications" element={<NotificationCenterPage />} />
              <Route
                path="admin"
                element={
                  <RoleRoute allowedRoles={['ADMIN']}>
                    <UserManagementPage />
                  </RoleRoute>
                }
              />
              <Route
                path="supplier-portal"
                element={
                  <RoleRoute allowedRoles={['ADMIN', 'SUPPLIER_USER']}>
                    <SupplierPortalPage />
                  </RoleRoute>
                }
              />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </Suspense>
      </AuthProvider>
    </BrowserRouter>
  );
};
