// EventSource wrapper hook with automatic reconnect and fallback to polling every 30 seconds per Item 46

import { useState, useEffect, useRef, useCallback } from 'react';
import { ScmNotification } from '@/types';
import { notificationApi } from '@/api/notificationApi';

export function useSse(enabled: boolean = true) {
  const [notifications, setNotifications] = useState<ScmNotification[]>([]);
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimeoutRef = useRef<any>(null);
  const pollingIntervalRef = useRef<any>(null);

  const fetchFallback = useCallback(async () => {
    try {
      const data = await notificationApi.getNotifications(0, 20);
      setNotifications(data.content);
      setUnreadCount(data.content.filter((n) => !n.read).length);
    } catch {
      // ignore in background
    }
  }, []);

  const connectSse = useCallback(() => {
    if (!enabled) return;

    const token = sessionStorage.getItem('scm_access_token');
    const sseUrl = `/api/v1/notifications/stream${token ? `?token=${encodeURIComponent(token)}` : ''}`;

    try {
      const es = new EventSource(sseUrl);
      eventSourceRef.current = es;

      es.onmessage = (event) => {
        try {
          const item: ScmNotification = JSON.parse(event.data);
          setNotifications((prev) => [item, ...prev.slice(0, 19)]);
          setUnreadCount((c) => c + 1);
        } catch (err) {
          console.error('Failed to parse SSE event payload', err);
        }
      };

      es.onerror = () => {
        es.close();
        eventSourceRef.current = null;
        // Schedule reconnect after 5s
        reconnectTimeoutRef.current = setTimeout(connectSse, 5000);
      };
    } catch {
      // Fallback to polling
    }
  }, [enabled]);

  useEffect(() => {
    if (enabled) {
      fetchFallback();
      connectSse();
      // Setup 30s background sync polling
      pollingIntervalRef.current = setInterval(fetchFallback, 30000);
    }

    return () => {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
      }
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current);
      }
    };
  }, [enabled, connectSse, fetchFallback]);

  return { notifications, unreadCount, setNotifications, setUnreadCount, refresh: fetchFallback };
}
