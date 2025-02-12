<template>
  <div class="simulation-container">
    <!-- Affiche le graphique de simulation -->
    <ScatterPlot />
    <!-- Vous pouvez intégrer ici d'éventuels contrôles superposés (OverlayControls) si besoin -->
  </div>
</template>

<script setup lang="ts">
import ScatterPlot from '~/components/ScatterPlot.vue'
</script>

<style scoped>
.simulation-container {
  position: relative;
  height: 100%;
  width: 100%;
}
</style>
