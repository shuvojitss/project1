import { apiClient } from './client';
import { Product, Category, PageResponse } from '@/types';

export const catalogApi = {
  getProducts: async (page = 0, size = 20, search?: string, categoryId?: string): Promise<PageResponse<Product>> => {
    const params: Record<string, any> = { page, size };
    if (search) params.search = search;
    if (categoryId) params.categoryId = categoryId;
    const res = await apiClient.get<PageResponse<Product>>('/products', { params });
    return res.data;
  },

  getProductById: async (id: string): Promise<Product> => {
    const res = await apiClient.get<Product>(`/products/${id}`);
    return res.data;
  },

  createProduct: async (product: Partial<Product>): Promise<Product> => {
    const res = await apiClient.post<Product>('/products', product);
    return res.data;
  },

  updateProduct: async (id: string, product: Partial<Product>): Promise<Product> => {
    const res = await apiClient.put<Product>(`/products/${id}`, product);
    return res.data;
  },

  getCategories: async (): Promise<Category[]> => {
    const res = await apiClient.get<Category[]>('/categories');
    return res.data;
  },
};
