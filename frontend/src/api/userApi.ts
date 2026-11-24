import { apiClient } from './client';
import { User, PageResponse } from '@/types';

export const userApi = {
  getUsers: async (page = 0, size = 20): Promise<PageResponse<User>> => {
    const res = await apiClient.get<PageResponse<User>>('/users', { params: { page, size } });
    return res.data;
  },

  createUser: async (user: Partial<User> & { password?: string }): Promise<User> => {
    const res = await apiClient.post<User>('/users', user);
    return res.data;
  },

  updateRoles: async (userId: string, roles: string[]): Promise<User> => {
    const res = await apiClient.put<User>(`/users/${userId}/roles`, { roles });
    return res.data;
  },

  updateLocations: async (userId: string, locationIds: string[]): Promise<User> => {
    const res = await apiClient.put<User>(`/users/${userId}/locations`, { locationIds });
    return res.data;
  },

  getAuditLogs: async (page = 0, size = 50): Promise<any> => {
    const res = await apiClient.get('/audit-logs', { params: { page, size } });
    return res.data;
  },
};
