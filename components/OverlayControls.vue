<template>
    <div class="overlay-controls">
        <!-- Percentage of lost packets -->
        <div class="packet-loss">
            Collision Rate : {{ collisionRate }}% <!-- | Packet Loss: {{ packetLoss }}% -->
        </div>
        <!-- Button for fullscreen mode -->
        <UButtonGroup>
            <!-- <UButton icon="i-mdi-info" @click="" class="btn-export" /> Focus sidebar item -->
            <UButton icon="i-mdi-delete" @click="_deleteSimulation" class="btn-delete" color="red" />
            <UButton :icon="isFullScreen ? 'i-mdi-fullscreen-exit' : 'i-mdi-fullscreen'" @click="toggleFullScreen"
                class="btn-fullscreen" />
            <!-- <UButton icon="i-mdi-download" @click="" class="btn-export" /> -->
        </UButtonGroup>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useSimulationAPI } from '~/composables/useSimulationAPI'

const props = defineProps<{ simulationId: number }>()
const { simulationState, deleteSimulation } = useSimulationAPI()

// const packetLoss = computed(() => {
//     const simulation = simulationState.simulations[props.simulationId];
//     if (simulation && simulation.totalFrames > 0) {
//         // Calculate the percentage of by counting the number of lost packets
//         const lostPackets = simulation.MiotyFrames.filter(frame => frame.lost).length +
//                     simulation.SigfoxFrames.filter(frame => frame.lost).length +
//                     simulation.LoRaWanFrames.filter(frame => frame.lost).length;
//         return ((lostPackets * 100.0) / simulation.totalFrames).toFixed(2);
//     }
//     return '0.00';
// });

const collisionRate = computed(() => {
    const simulation = simulationState.simulations[props.simulationId];
    if (simulation && simulation.totalFrames > 0) {
        return (simulation.totalCollisions * 100.0 / simulation.totalFrames).toFixed(2);
    }
    return '0.00';
});

const isFullScreen = ref(false)

function toggleFullScreen() {
    // Find the correct container based on the ID
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

function _deleteSimulation() {
    deleteSimulation(props.simulationId)
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
    /* By default, overlay controls are invisible */
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

/* Rules to make the overlay appear when hovering over the container */
.simulation-container:hover .overlay-controls {
    opacity: 1;
    pointer-events: auto;
}
</style>