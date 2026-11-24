import { apiClient } from './client';
import { CustomerOrder, PageResponse } from '@/types';

export const orderApi = {
  getOrders: async (page = 0, size = 20, status?: string): Promise<PageResponse<CustomerOrder>> => {
    const params: Record<string, any> = { page, size };
    if (status) params.status = status;
    const res = await apiClient.get<PageResponse<CustomerOrder>>('/orders', { params });
    return res.data;
  },

  getOrderById: async (id: string): Promise<CustomerOrder> => {
    const res = await apiClient.get<CustomerOrder>(`/orders/${id}`);
    return res.data;
  },

  createOrder: async (order: Partial<CustomerOrder>): Promise<CustomerOrder> => {
    const res = await apiClient.post<CustomerOrder>('/orders', order);
    return res.data;
  },

  cancelOrder: async (id: string, reason: string): Promise<CustomerOrder> => {
    const res = await apiClient.post<CustomerOrder>(`/orders/${id}/cancel`, { reason });
    return res.data;
  },

  getOrderTimeline: async (id: string): Promise<{ status: string; timestamp: string; notes?: string }[]> => {
    const res = await apiClient.get(`/orders/${id}/timeline`);
    return res.data;
  },
};
