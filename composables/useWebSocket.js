import { useWebSocket } from '@vueuse/core';

export function useWebSocketAPI(url) {
  const pingInterval = 5000;

  const { status, data: messages, send, open, close } = useWebSocket(url, {
    autoReconnect: true,
    heartbeat: { message: 'ping', interval: pingInterval },
  });

  return { status, messages, send, open, close };
}
