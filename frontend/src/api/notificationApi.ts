import { apiClient } from './client';
import { ScmNotification, PageResponse } from '@/types';

export const notificationApi = {
  getNotifications: async (page = 0, size = 20, unreadOnly = false): Promise<PageResponse<ScmNotification>> => {
    const res = await apiClient.get<PageResponse<ScmNotification>>('/notifications', {
      params: { page, size, unreadOnly },
    });
    return res.data;
  },

  markAsRead: async (id: string): Promise<void> => {
    await apiClient.put(`/notifications/${id}/read`);
  },

  markAllAsRead: async (): Promise<void> => {
    await apiClient.put('/notifications/read-all');
  },
};
