<script setup>
import { UButton } from '#components';

var isConnected = false;
</script>

<template>
  <div>
    <h1>Status: {{ status }}</h1>
    <p>Address: {{ serverAddress }}</p>
    <UButton @click="" :disabled="!isConnected" color="primary">
      Start simulation
    </UButton>
    <UButton @click="" :disabled="!isConnected" color="red">
      Stop simulation
    </UButton>

    <!-- Se connecter -->
    <UButton @click="" :disabled="isConnected" color="primary">
      Connect
    </UButton>
    <!-- Se déconnecter -->
    <UButton @click=""" :disabled="!isConnected" color="red">
      Disconnect
    </UButton>
    
  </div>
</template>
