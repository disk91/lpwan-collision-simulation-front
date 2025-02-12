<template>
  <div class="simulation-container">
    <ScatterPlot />
    <OverlayControls />
  </div>
</template>

<script setup lang="ts">
import ScatterPlot from '~/components/ScatterPlot.vue'
import OverlayControls from '~/components/OverlayControls.vue'
</script>

<style scoped>
.simulation-container {
  position: relative; /* Pour le positionnement des overlay controls */
  height: 100%;
  width: 100%;
}
</style>

<style>
/* Forcer un fond clair en plein écran (pour la compatibilité Webkit également) */
:fullscreen,
::backdrop {
    background-color: rgba(255, 255, 255, 0);
}
:fullscreen::backdrop,
:-webkit-full-screen::backdrop {
    background: #fff;
}
</style>