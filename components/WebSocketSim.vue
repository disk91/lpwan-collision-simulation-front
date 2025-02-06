<script setup>
import { ref, computed } from 'vue';
import { useWebSocket } from '@vueuse/core';

const serverAddress = 'ws://172.27.228.195:8080/api';

var { status, send, open, close, data } = useWebSocket(serverAddress, {
  autoReconnect: true,
  heartbeat: { message: 'ping', interval: 5000 },
});

const isConnected = computed(() => status.value === 'OPEN');
const isConnecting = computed(() => status.value === 'CONNECTING');
const isClosed = computed(() => status.value === 'CLOSED');

const messages = ref([]);

const addMessage = (message) => {
  messages.value.push(message);
};

watch(data, (newMessage) => {
  if (newMessage) addMessage(newMessage);
});

watch(status, (newStatus) => {
  if (newStatus === 'OPEN') {
    addMessage('Connexion établie');
  } else if (newStatus === 'CLOSED') {
    addMessage('Connexion fermée');
  }
  else {
    addMessage(status.value);
  }
});

const sendCommand = (action) => {
  if (isConnected.value) {
    const message = JSON.stringify({ action, parameters: [] });
    send(message);
    addMessage(`Envoyé: ${message}`);
  }
};
</script>

<template>
  <div class="container">
    <h1>Simulation WebSocket</h1>

    <p>Statut : <strong :class="{ connected: isConnected }">{{ status }}</strong></p>

    <div class="buttons">
      <UButton @click="sendCommand('start_simu')" :disabled="!isConnected" icon="i-heroicons-play" color="primary"
        size="xl">
        Start Simulation
      </UButton>

      <UButton @click="sendCommand('stop_simu')" :disabled="!isConnected" icon="i-heroicons-stop" color="red" size="xl">
        Stop Simulation
      </UButton>
    </div>

    <div class="controls">
      <UButton @click="open()" :disabled="status === 'OPEN'" icon="i-heroicons-arrow-path" color="blue">Reconnecter</UButton>
      <UButton @click="close()" :disabled="status === 'CLOSED'" icon="i-heroicons-x-circle" color="red">Déconnecter</UButton>
    </div>

    <div class="address">
      <p>Adresse du serveur : <strong>{{ serverAddress }}</strong></p>
    </div>

    <div class="messages">
      <h2>Messages reçus :</h2>
      <ul>
        <li v-for="(msg, index) in messages" :key="index">{{ msg }}</li>
      </ul>
    </div>
  </div>
</template>
