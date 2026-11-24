import { apiClient } from './client';
import { DashboardSummary } from '@/types';

export const analyticsApi = {
  getDashboardSummary: async (warehouseId?: string): Promise<DashboardSummary> => {
    const res = await apiClient.get<DashboardSummary>('/analytics/dashboard', {
      params: { warehouseId },
    });
    return res.data;
  },

  getInventoryKpis: async (): Promise<any> => {
    const res = await apiClient.get('/analytics/inventory');
    return res.data;
  },

  getProcurementKpis: async (): Promise<any> => {
    const res = await apiClient.get('/analytics/procurement');
    return res.data;
  },

  getSupplierKpis: async (): Promise<any> => {
    const res = await apiClient.get('/analytics/suppliers');
    return res.data;
  },

  getLogisticsKpis: async (): Promise<any> => {
    const res = await apiClient.get('/analytics/logistics');
    return res.data;
  },
};
