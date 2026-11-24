import { apiClient } from './client';
import { AuthResponse, User } from '@/types';

export const authApi = {
  login: async (username: string, password: string): Promise<AuthResponse> => {
    const res = await apiClient.post<AuthResponse>('/auth/login', { username, password });
    return res.data;
  },

  refresh: async (refreshToken: string): Promise<AuthResponse> => {
    const res = await apiClient.post<AuthResponse>('/auth/refresh', { refreshToken });
    return res.data;
  },

  logout: async (): Promise<void> => {
    const refreshToken = sessionStorage.getItem('scm_refresh_token');
    await apiClient.post('/auth/logout', { refreshToken }).catch(() => {});
  },

  getMe: async (): Promise<User> => {
    const res = await apiClient.get<User>('/users/me');
    return res.data;
  },
};
