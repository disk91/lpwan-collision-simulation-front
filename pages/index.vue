<template>
  <div class="simulations">
    <div class="simulation-container" v-for="i in 2" :key="i">
      <ScatterPlot />
      <OverlayControls />
      <OverlayTitle title="Graphic title"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import ScatterPlot from '~/components/ScatterPlot.vue'
import OverlayTitle from '~/components/OverlayTitle.vue'
import OverlayControls from '~/components/OverlayControls.vue'
</script>

<style scoped>
.simulations {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

/* Chaque simulation-container se verra attribuer une part égale de l'espace vertical */
.simulation-container {
  position: relative;
  height: 100%;
  width: 100%;
}
</style>

<style>
/* Pour forcer un fond clair en plein écran (compatibilité Webkit) */
:fullscreen,
::backdrop {
  background-color: rgba(255, 255, 255, 0);
}
:fullscreen::backdrop,
:-webkit-full-screen::backdrop {
  background: #fff;
}
</style>
