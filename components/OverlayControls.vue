<!-- components/OverlayControls.vue -->
<template>
    <div class="overlay-controls">
        <!-- Pourcentage de paquets perdus -->
        <div class="packet-loss">
            {{ packetLoss }}%
        </div>
        <!-- Bouton pour le mode plein écran -->
        <UButtonGroup>
            <UButton icon="i-mdi-info" @click="" class="btn-export"/> <!-- Focus sidebar item -->
            <UButton    
                :icon="isFullScreen ? 'i-mdi-fullscreen-exit' : 'i-mdi-fullscreen'" 
                @click="toggleFullScreen"
                class="btn-fullscreen"
                />
                <UButton icon="i-mdi-download" @click="exportChart" class="btn-export"/>
        </UButtonGroup>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// Exemple de donnée pour le pourcentage de paquets perdus
const packetLoss = ref(0)
const isFullScreen = ref(false)

// Fonction qui demande le mode plein écran sur le conteneur parent
function toggleFullScreen() {
    // On sélectionne le conteneur qui englobe à la fois le graphique et les overlays
    const fullScreenContainer = document.querySelector('.simulation-container')
    if (!fullScreenContainer) return

    if (!isFullScreen.value) {
        // Demande de passage en mode plein écran
        if (fullScreenContainer.requestFullscreen) {
            fullScreenContainer.requestFullscreen()
        } else if ((fullScreenContainer as any).webkitRequestFullscreen) {
            (fullScreenContainer as any).webkitRequestFullscreen()
        }
        // La mise à jour de isFullScreen se fera via l'événement fullscreenchange
    } else {
        // Sortie du mode plein écran
        if (document.exitFullscreen) {
            document.exitFullscreen()
        } else if ((document as any).webkitExitFullscreen) {
            (document as any).webkitExitFullscreen()
        }
    }
}

function exportChart() {
    console.log('Exporter le graphique')
}

// Met à jour isFullScreen en fonction de l'état de fullscreen
function onFullScreenChange() {
    isFullScreen.value = !!document.fullscreenElement
}

onMounted(() => {
    document.addEventListener('fullscreenchange', onFullScreenChange)
    document.addEventListener('webkitfullscreenchange', onFullScreenChange)
})

onUnmounted(() => {
    document.removeEventListener('fullscreenchange', onFullScreenChange)
    document.removeEventListener('webkitfullscreenchange', onFullScreenChange)
})
</script>

<style scoped>
.overlay-controls {
    position: absolute;
    top: 10px;
    right: 10px;
    display: flex;
    align-items: center;
    gap: 10px;
    /* Par défaut, les overlay controls sont invisibles */
    opacity: 0;
    pointer-events: none;
    transition: opacity 0.3s ease;
    z-index: 100;
}

.packet-loss {
    padding: 5px 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
    font-weight: bold;
}

/* Règles pour que l'overlay apparaisse lors du survol du conteneur */
.simulation-container:hover .overlay-controls {
    opacity: 1;
    pointer-events: auto;
}
</style>