// Axios instance and interceptors per Item 43

import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';
import { ApiError } from '@/types';

const API_BASE_URL = (import.meta as any).env?.VITE_API_BASE_URL || '/api/v1';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: AxiosError | null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
    } else {
      prom.resolve();
    }
  });
  failedQueue = [];
};

// Request interceptor: attaches Bearer token & X-Correlation-Id
apiClient.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = sessionStorage.getItem('scm_access_token');
  if (token) {
    config.headers.set('Authorization', `Bearer ${token}`);
  }

  // Generate X-Correlation-Id if missing
  if (!config.headers.has('X-Correlation-Id')) {
    config.headers.set(
      'X-Correlation-Id',
      'web-' + Math.random().toString(36).substring(2, 11) + '-' + Date.now()
    );
  }

  return config;
});

// Response interceptor: handles 401 & queues concurrent requests during refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry && !originalRequest.url?.includes('/auth/')) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        })
          .then(() => apiClient(originalRequest))
          .catch((err) => Promise.reject(err));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      const refreshToken = sessionStorage.getItem('scm_refresh_token');
      if (!refreshToken) {
        sessionStorage.clear();
        window.location.href = '/login';
        return Promise.reject(error);
      }

      try {
        const { data } = await axios.post(`${API_BASE_URL}/auth/refresh`, {
          refreshToken,
        });

        sessionStorage.setItem('scm_access_token', data.accessToken);
        if (data.refreshToken) {
          sessionStorage.setItem('scm_refresh_token', data.refreshToken);
        }

        processQueue(null);
        return apiClient(originalRequest);
      } catch (refreshErr) {
        processQueue(refreshErr as AxiosError);
        sessionStorage.clear();
        window.location.href = '/login';
        return Promise.reject(refreshErr);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(formatApiError(error));
  }
);

export function formatApiError(error: AxiosError): ApiError {
  if (error.response?.data) {
    const data = error.response.data as any;
    return {
      status: error.response.status,
      title: data.title || 'Request Error',
      detail: data.detail || data.message || 'An unexpected error occurred.',
      violations: data.violations || [],
    };
  }
  return {
    status: error.status || 500,
    title: 'Network Error',
    detail: error.message || 'Unable to connect to the server.',
  };
}
