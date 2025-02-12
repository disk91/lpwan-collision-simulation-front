<template>
  <nav class="topbar">
    <!-- Partie gauche : logo et nom du projet -->
    <div class="topbar-left">
      <img src="/assets/images/logo.png" alt="Logo" class="logo" />
      <span class="project-name">LPWAN Collision Simulation</span>
    </div>

    <!-- Partie centrale : boutons de contrôle et indicateur d'état -->
    <div class="topbar-center">
      <button @click="startSimulation" class="control-btn">Démarrer</button>
      <button @click="" class="control-btn">Arrêter</button> <!-- stopSimulation() -->
      <button @click="" class="control-btn">Pause</button> <!-- pauseSimulation -->
      <span class="simulation-status">Running ?</span> <!-- {{ simulationStatus }} -->
    </div>

    <!-- Partie droite : bouton mode sombre et (optionnel) bouton hamburger pour mobile -->
    <div class="topbar-right">
      <button @click="toggleDarkMode" class="darkmode-btn">
        <span v-if="isDarkMode">🌙</span>
        <span v-else>☀️</span>
      </button>
      <!-- Bouton hamburger (visible uniquement sur mobile via CSS) -->
      <button class="hamburger-btn" @click="toggleSidebar">
        <svg width="24" height="24" viewBox="0 0 24 24">
          <path d="M3 6h18M3 12h18M3 18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
        </svg>
      </button>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useSimulationAPI } from '~/composables/useSimulationAPI'
//import { useSimulationState } from '~/composables/useSimulationState'

// Récupération des fonctions d'API pour le contrôle de la simulation
const { startSimulation, stopSimulation, pauseSimulation } = useSimulationAPI()
// Récupération de l'état de la simulation
//const simulationState = useSimulationState()
//const simulationStatus = simulationState.simulationStatus

// Gestion du mode sombre
const isDarkMode = ref(false)
function toggleDarkMode() {
  isDarkMode.value = !isDarkMode.value
  document.documentElement.classList.toggle('dark', isDarkMode.value)
}

// Pour le menu hamburger, vous pouvez soit émettre un événement vers le layout, soit gérer une variable globale
function toggleSidebar() {
  console.log('Toggle sidebar (mobile)')
}
</script>

<style scoped>
.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  padding: 0.5rem 1rem;
  border-bottom: 1px solid #ddd;
}

.topbar-left {
  display: flex;
  align-items: center;
}

.logo {
  height: 40px;
  margin-right: 0.5rem;
}

.project-name {
  font-size: 1.2rem;
  font-weight: bold;
}

.topbar-center {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.control-btn {
  padding: 0.3rem 0.8rem;
  border: none;
  background: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.control-btn:hover {
  background: #0056b3;
}

.simulation-status {
  margin-left: 1rem;
  font-weight: bold;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.darkmode-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.5rem;
}

.hamburger-btn {
  background: none;
  border: none;
  cursor: pointer;
  display: none; /* Affiché uniquement sur mobile */
}

@media (max-width: 768px) {
  .hamburger-btn {
    display: block;
  }
}
</style>
