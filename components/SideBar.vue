<template>
  <aside class="sidebar">
    <!-- Bouton pour ajouter une nouvelle simulation -->
    <div class="header">
      <UButton @click="addSimulation" class="new-simulation-btn">
        New Simulation
      </UButton>
    </div>

    <!-- Liste des simulations -->
    <div v-for="(simulation, index) in simulations" :key="simulation.id" class="simulation-item">
      <!-- En-tête cliquable pour ouvrir/fermer le panneau -->
      <div class="simulation-header" @click="toggleSimulation(simulation.id)">
        <h3>Simulation {{ index + 1 }}</h3>
        <!-- Icône de flèche -->
        <UIcon :name="simulation.isOpen ? 'chevron-up' : 'chevron-down'" />
      </div>

      <!-- Corps du panneau -->
      <div v-if="simulation.isOpen" class="simulation-body">
        <!-- Affichage d'un message de chargement pendant la simulation -->
        <div v-if="simulation.loading" class="loading">
          <p>Running simulation...</p>
        </div>

        <!-- Formulaire de paramètres de la simulation -->
        <form v-else @submit.prevent="runSimulation(simulation.id)">
          <div class="form-group">
            <label>Model</label>
            <USelect 
              v-model="simulation.parameters.model" 
              :options="models" 
              placeholder="Select a model" 
            />
          </div>

          <div class="form-group">
            <label>Graph color</label>
            <input type="color" v-model="simulation.parameters.color" />
          </div>

          <!-- Boutons d'action -->
          <div class="buttons">
            <UButton type="submit" class="run-btn">
              {{ simulation.ran ? 'Rerun Simulation' : 'Run Simulation' }}
            </UButton>
            <UButton type="button" @click="removeSimulation(simulation.id)" class="delete-btn">
              Delete Simulation
            </UButton>
          </div>
        </form>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useSimulationAPI } from '~/composables/useSimulationAPI'
// Pour générer des identifiants uniques
import { nanoid } from 'nanoid'

const { startSimulation } = useSimulationAPI()

// Options disponibles pour le sélecteur de modèle
const models = [
  { label: 'Mioty', value: 'mioty' },
  { label: 'Sigfox', value: 'sigfox' },
  { label: 'LoRaWAN', value: 'lorawan' }
]

// Interface pour représenter une simulation
interface Simulation {
  id: string
  isOpen: boolean
  loading: boolean
  ran: boolean
  parameters: {
    model: string
    color: string
  }
}

// Tableau réactif de simulations
const simulations = ref<Simulation[]>([])

// Ajoute une nouvelle simulation avec des valeurs par défaut
function addSimulation() {
  simulations.value.push({
    id: nanoid(),
    isOpen: true,
    loading: false,
    ran: false,
    parameters: {
      model: 'mioty',
      color: '#ff0000'
    }
  })
}

// Supprime une simulation en filtrant par son id
function removeSimulation(id: string) {
  simulations.value = simulations.value.filter(sim => sim.id !== id)
}

// Ouvre ou ferme le panneau d'une simulation
function toggleSimulation(id: string) {
  const sim = simulations.value.find(sim => sim.id === id)
  if (sim) {
    sim.isOpen = !sim.isOpen
  }
}

// Lance ou relance la simulation pour la simulation dont l'id est fourni
function runSimulation(id: string) {
  const sim = simulations.value.find(sim => sim.id === id)
  if (!sim) return

  sim.loading = true
  startSimulation(0) // à remplacer par sim.parameters
    .then(() => {
      sim.loading = false
      sim.ran = true
    })
    .catch((error) => {
      console.error('Simulation error:', error)
      sim.loading = false
    })
}
</script>

<style scoped>
.sidebar {
  width: 300px;
  padding: 1rem;
  border-right: 1px solid #ddd;
  height: 100%;
  overflow-y: auto;
}

.header {
  margin-bottom: 1rem;
}

.new-simulation-btn {
  width: 100%;
}

.simulation-item {
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 1rem;
  overflow: hidden;
}

.simulation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background-color: #f5f5f5;
  cursor: pointer;
}

.simulation-body {
  padding: 0.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

input[type="color"] {
  width: 100%;
  padding: 0.3rem;
}

.buttons {
  display: flex;
  gap: 0.5rem;
}

.run-btn {
  flex: 1;
}

.delete-btn {
  background-color: #e74c3c;
  flex: 1;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100px;
}
</style>
