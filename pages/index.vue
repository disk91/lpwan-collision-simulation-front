<template>
  <div class="simulations">
    <div class="simulation-container" v-for="id in simulationIds" :key="`sim-${id}`" :data-id="id">
      <ScatterPlot :simulation-id="id" />
      <OverlayControls :simulation-id="id" />
      <OverlayTitle title="Graphic title" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useSimulationAPI } from '~/composables/useSimulationAPI'
import ScatterPlot from '~/components/ScatterPlot.vue'
import OverlayTitle from '~/components/OverlayTitle.vue'
import OverlayControls from '~/components/OverlayControls.vue'
import { ref, computed } from 'vue'

const { simulationState, getSimulationIds, pingServer } = useSimulationAPI()
const renderKey = ref(0) // Ajoute une clé de rendu

async function refreshSimulations() {
  await getSimulationIds()
  renderKey.value++ // Incrémente pour forcer le re-rendu
}

const simulationIds = computed(() => simulationState.simulationIds)

onMounted(async () => {
  await getSimulationIds()

    // Démarrer le ping toutes les secondes
    const pingInterval = setInterval(async () => {
    try {
      await pingServer()
    } catch (error) {
      console.error("Erreur lors du ping du serveur :", error)
    }
  }, 2000)

  // Nettoyer l'intervalle lorsque le composant est démonté
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