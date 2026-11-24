import { apiClient } from './client';
import { Warehouse, InboundReceipt, PutawayTask, TransferOrder, CycleCount, PageResponse } from '@/types';

export const warehouseApi = {
  getWarehouses: async (): Promise<Warehouse[]> => {
    const res = await apiClient.get<Warehouse[]>('/warehouses');
    return res.data;
  },

  getInboundReceipts: async (page = 0, size = 20): Promise<PageResponse<InboundReceipt>> => {
    const res = await apiClient.get<PageResponse<InboundReceipt>>('/inbound-receipts', { params: { page, size } });
    return res.data;
  },

  receiveGoods: async (data: Partial<InboundReceipt>): Promise<InboundReceipt> => {
    const res = await apiClient.post<InboundReceipt>('/inbound-receipts', data);
    return res.data;
  },

  getPutaways: async (warehouseId?: string): Promise<PutawayTask[]> => {
    const res = await apiClient.get<PutawayTask[]>('/putaway-tasks', { params: { warehouseId } });
    return res.data;
  },

  completePutaway: async (taskId: string): Promise<void> => {
    await apiClient.post(`/putaway-tasks/${taskId}/complete`);
  },

  getTransfers: async (page = 0, size = 20): Promise<PageResponse<TransferOrder>> => {
    const res = await apiClient.get<PageResponse<TransferOrder>>('/transfers', { params: { page, size } });
    return res.data;
  },

  createTransfer: async (data: Partial<TransferOrder>): Promise<TransferOrder> => {
    const res = await apiClient.post<TransferOrder>('/transfers', data);
    return res.data;
  },

  getCycleCounts: async (warehouseId?: string): Promise<CycleCount[]> => {
    const res = await apiClient.get<CycleCount[]>('/cycle-counts', { params: { warehouseId } });
    return res.data;
  },
};
