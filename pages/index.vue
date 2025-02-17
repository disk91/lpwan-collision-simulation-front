<template>
  <ClientOnly fallback-tag="span" fallback="Loading graphs...">
    <div :key="containerKey" class="simulations">
      <div class="simulation-container" v-for="id in simulationIds" :key="`sim-${id}`" :data-id="id">
        <ScatterPlot :simulation-id="id" />
        <OverlayControls :simulation-id="id" />
        <OverlayTitle :title="simulationState.simulationsTitle[id]" />
      </div>
    </div>
  </ClientOnly>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useSimulationAPI } from '~/composables/useSimulationAPI'
import ScatterPlot from '~/components/ScatterPlot.vue'
import OverlayTitle from '~/components/OverlayTitle.vue'
import OverlayControls from '~/components/OverlayControls.vue'
import { computed } from 'vue'

const { simulationState, getSimulationIds, pingServer } = useSimulationAPI()
const simulationIds = computed(() => simulationState.simulationIds)
const containerKey = ref(0)

watch(simulationIds, () => {
  containerKey.value++
}, { deep: true })

onMounted(async () => {
  await getSimulationIds()

  // Démarrer le ping toutes les 2 secondes
  const pingInterval = setInterval(async () => {
    try {
      await pingServer()
    } catch (error) {
      console.error("Erreur lors du ping du serveur :", error)
    }
  }, 2000)

  onUnmounted(() => {
    clearInterval(pingInterval)
  })
})
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
  flex: 1;
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