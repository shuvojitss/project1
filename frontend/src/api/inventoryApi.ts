import { apiClient } from './client';
import { StockLevel, StockLedgerEntry, ReorderRule, PageResponse } from '@/types';

export const inventoryApi = {
  getStockLevels: async (page = 0, size = 20, warehouseId?: string, productId?: string): Promise<PageResponse<StockLevel>> => {
    const params: Record<string, any> = { page, size };
    if (warehouseId) params.warehouseId = warehouseId;
    if (productId) params.productId = productId;
    const res = await apiClient.get<PageResponse<StockLevel>>('/stock-levels', { params });
    return res.data;
  },

  getLedger: async (page = 0, size = 20, productId?: string, warehouseId?: string): Promise<PageResponse<StockLedgerEntry>> => {
    const params: Record<string, any> = { page, size };
    if (productId) params.productId = productId;
    if (warehouseId) params.warehouseId = warehouseId;
    const res = await apiClient.get<PageResponse<StockLedgerEntry>>('/stock-ledger', { params });
    return res.data;
  },

  getReorderRules: async (): Promise<ReorderRule[]> => {
    const res = await apiClient.get<ReorderRule[]>('/reorder-rules');
    return res.data;
  },

  createAdjustment: async (data: {
    productId: string;
    warehouseId: string;
    deltaQuantity: number;
    reason: string;
  }): Promise<StockLevel> => {
    const res = await apiClient.post<StockLevel>('/stock-adjustments', data);
    return res.data;
  },
};
