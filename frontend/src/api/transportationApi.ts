import { apiClient } from './client';
import { Shipment, Carrier, RateCard, TrackingCheckpoint, PageResponse } from '@/types';

export const transportationApi = {
  getShipments: async (page = 0, size = 20, status?: string): Promise<PageResponse<Shipment>> => {
    const params: Record<string, any> = { page, size };
    if (status) params.status = status;
    const res = await apiClient.get<PageResponse<Shipment>>('/shipments', { params });
    return res.data;
  },

  getShipmentById: async (id: string): Promise<Shipment> => {
    const res = await apiClient.get<Shipment>(`/shipments/${id}`);
    return res.data;
  },

  planShipment: async (data: Partial<Shipment>): Promise<Shipment> => {
    const res = await apiClient.post<Shipment>('/shipments', data);
    return res.data;
  },

  getCarriers: async (): Promise<Carrier[]> => {
    const res = await apiClient.get<Carrier[]>('/carriers');
    return res.data;
  },

  getRateCards: async (): Promise<RateCard[]> => {
    const res = await apiClient.get<RateCard[]>('/rate-cards');
    return res.data;
  },

  getTrackingEvents: async (shipmentId: string): Promise<TrackingCheckpoint[]> => {
    const res = await apiClient.get<TrackingCheckpoint[]>(`/shipments/${shipmentId}/tracking`);
    return res.data;
  },

  confirmPod: async (shipmentId: string, pod: { receivedBy: string; signatureSvg?: string }): Promise<Shipment> => {
    const res = await apiClient.post<Shipment>(`/shipments/${shipmentId}/pod`, pod);
    return res.data;
  },
};
