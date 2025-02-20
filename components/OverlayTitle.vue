<!-- components/OverlayTitle.vue -->
<template>
  <div class="overlay-title">
    <span>{{ title }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{ title: string }>()
</script>

<style scoped>
.overlay-title {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 5px 10px;
  border-radius: 4px;
  font-weight: bold;
  font-size: 1rem;
  z-index: 100;
  opacity: 0; /* Overlay is hidden by default */
  pointer-events: none;
  transition: opacity 0.3s ease;
}

/* Show overlay only when the mouse hovers over the container */
.simulation-container:hover .overlay-title {
  opacity: 1;
  pointer-events: auto;
}
</style>
