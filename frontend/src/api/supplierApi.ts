import { apiClient } from './client';
import { Supplier, SupplierItem, SupplierScorecard, Asn, PageResponse } from '@/types';

export const supplierApi = {
  getSuppliers: async (page = 0, size = 20): Promise<PageResponse<Supplier>> => {
    const res = await apiClient.get<PageResponse<Supplier>>('/suppliers', { params: { page, size } });
    return res.data;
  },

  getSupplierById: async (id: string): Promise<Supplier> => {
    const res = await apiClient.get<Supplier>(`/suppliers/${id}`);
    return res.data;
  },

  onboardSupplier: async (data: Partial<Supplier>): Promise<Supplier> => {
    const res = await apiClient.post<Supplier>('/suppliers', data);
    return res.data;
  },

  updateStatus: async (id: string, status: string, reason?: string): Promise<Supplier> => {
    const res = await apiClient.put<Supplier>(`/suppliers/${id}/status`, { status, reason });
    return res.data;
  },

  getItems: async (supplierId: string): Promise<SupplierItem[]> => {
    const res = await apiClient.get<SupplierItem[]>(`/suppliers/${supplierId}/items`);
    return res.data;
  },

  getScorecard: async (supplierId: string): Promise<SupplierScorecard> => {
    const res = await apiClient.get<SupplierScorecard>(`/suppliers/${supplierId}/scorecard`);
    return res.data;
  },

  getAsns: async (supplierId: string, page = 0, size = 20): Promise<PageResponse<Asn>> => {
    const res = await apiClient.get<PageResponse<Asn>>(`/suppliers/${supplierId}/asns`, { params: { page, size } });
    return res.data;
  },

  createAsn: async (supplierId: string, asn: Partial<Asn>): Promise<Asn> => {
    const res = await apiClient.post<Asn>(`/suppliers/${supplierId}/asns`, asn);
    return res.data;
  },
};
