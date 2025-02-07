import { ref, computed, watch } from 'vue';
import { useWebSocket } from '@vueuse/core';

const serverAddress = ref('ws://websockets.chilkat.io/wsChilkatEcho.ashx');
const lastMessage = ref(null);

const { status, send, open, close, data } = useWebSocket(serverAddress.value, {
  autoReconnect: true,
  heartbeat: { message: 'ping', interval: 5000 },
});

const isConnected = computed(() => status.value === 'OPEN');

const addMessage = (message) => {
  console.log('New message:', message);
};

watch(data, (newMessage) => {
  if (newMessage) addMessage(newMessage);
});

const sendCommand = (action) => {
  if (isConnected.value) {
    const message = JSON.stringify({ action, parameters: [] });
    lastMessage.value = message;
    send(message);
  }
};

const setServerAddress = (address) => {
  serverAddress.value = address;
  close();
  open();
};

export function useWebSocketHandler() {
  return {
    serverAddress,
    status,
    lastMessage,
    isConnected,
    setServerAddress,
    sendCommand,
    open,
    close,
  };
}
