<script setup>
import { UButton } from '#components';
import { useWebSocketHandler } from '@/composables/useWebSocket';

const { status, isConnected, serverAddress, sendCommand, open, close } = useWebSocketHandler();

</script>

<template>
  <div>
    <h1>Status: {{ status }}</h1>
    <p>Address: {{ serverAddress }}</p>
    <UButton @click="sendCommand('start_simu')" :disabled="!isConnected" color="primary">
      Start simulation
    </UButton>
    <UButton @click="sendCommand('stop_simu')" :disabled="!isConnected" color="red">
      Stop simulation
    </UButton>

    <!-- Se connecter -->
    <UButton @click="open()" :disabled="isConnected" color="primary">
      Connect
    </UButton>
    <!-- Se déconnecter -->
    <UButton @click="close()" :disabled="!isConnected" color="red">
      Disconnect
    </UButton>
    
  </div>
</template>
