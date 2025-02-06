<template>
  <header class="w-full bg-gray-900 text-white shadow-md">
    <UContainer class="flex items-center justify-between py-4">
      <!-- Logo -->
      <NuxtLink to="/" class="text-xl font-bold">LPWAN Collision Simulator</NuxtLink>

      <!-- Bouton Mode Sombre -->
      <UButton icon="i-heroicons-moon" variant="ghost" @click="toggleDarkMode" />
    </UContainer>
  </header>
</template>

<script setup>
import { ref } from 'vue'
const isOpen = ref(false)

const toggleDarkMode = () => {
  const html = document.documentElement
  html.classList.toggle('dark')
}
</script>
