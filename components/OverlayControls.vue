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
                <UButton icon="i-mdi-download" @click="" class="btn-export"/>
        </UButtonGroup>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const props = defineProps<{ simulationId: number }>()
const packetLoss = ref(0)
const isFullScreen = ref(false)

function toggleFullScreen() {
    // Trouve le bon conteneur en fonction de l'ID
    const fullScreenContainer = document.querySelector(`.simulation-container[data-id="${props.simulationId}"]`)
    if (!fullScreenContainer) return

    if (!isFullScreen.value) {
        if (fullScreenContainer.requestFullscreen) {
            fullScreenContainer.requestFullscreen()
        } else if ((fullScreenContainer as any).webkitRequestFullscreen) {
            (fullScreenContainer as any).webkitRequestFullscreen()
        }
    } else {
        if (document.exitFullscreen) {
            document.exitFullscreen()
        } else if ((document as any).webkitExitFullscreen) {
            (document as any).webkitExitFullscreen()
        }
    }
}

function onFullScreenChange() {
    const fullScreenContainer = document.querySelector(`.simulation-container[data-id="${props.simulationId}"]`)
    isFullScreen.value = !!document.fullscreenElement && document.fullscreenElement === fullScreenContainer
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