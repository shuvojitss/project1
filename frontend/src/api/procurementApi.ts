import { apiClient } from './client';
import { Requisition, PurchaseOrder, PageResponse } from '@/types';

export const procurementApi = {
  getRequisitions: async (page = 0, size = 20): Promise<PageResponse<Requisition>> => {
    const res = await apiClient.get<PageResponse<Requisition>>('/requisitions', { params: { page, size } });
    return res.data;
  },

  createRequisition: async (data: Partial<Requisition>): Promise<Requisition> => {
    const res = await apiClient.post<Requisition>('/requisitions', data);
    return res.data;
  },

  approveRequisition: async (id: string): Promise<Requisition> => {
    const res = await apiClient.post<Requisition>(`/requisitions/${id}/approve`);
    return res.data;
  },

  getPurchaseOrders: async (page = 0, size = 20): Promise<PageResponse<PurchaseOrder>> => {
    const res = await apiClient.get<PageResponse<PurchaseOrder>>('/purchase-orders', { params: { page, size } });
    return res.data;
  },

  getPurchaseOrderById: async (id: string): Promise<PurchaseOrder> => {
    const res = await apiClient.get<PurchaseOrder>(`/purchase-orders/${id}`);
    return res.data;
  },

  createPurchaseOrder: async (data: Partial<PurchaseOrder>): Promise<PurchaseOrder> => {
    const res = await apiClient.post<PurchaseOrder>('/purchase-orders', data);
    return res.data;
  },

  approvePurchaseOrder: async (id: string): Promise<PurchaseOrder> => {
    const res = await apiClient.post<PurchaseOrder>(`/purchase-orders/${id}/approve`);
    return res.data;
  },
};
