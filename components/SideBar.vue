<template>
  <aside class="sidebar">
    <!-- Button to add a new simulation -->
    <div class="header">
      <UButton @click="addSimulation" class="new-simulation-btn">
        New Simulation
      </UButton>
    </div>

    <!-- List of simulations -->
    <div v-for="sim in localSimulations" :key="sim.id" class="simulation-item">
      <!-- Clickable header to open/close the panel -->
      <div class="simulation-header" @click="toggleSimulation(sim.id)">
        <h3>{{ simulationAPI.simulationState.simulationsTitle[sim.id] }}</h3>
        <UIcon :name="sim.isOpen ? 'mdi-chevron-up' : 'mdi-chevron-down'" />
      </div>

      <!-- Panel body -->
      <div v-if="sim.isOpen" class="simulation-body">
        <!-- Loading message during simulation -->
        <div v-if="sim.loading" class="loading">
          <p>Running simulation...</p>
        </div>

        <!-- Parameters form -->
        <form v-else @submit.prevent="runSimulation_p(sim.id)">
          <div class="form-group">
            <label>Model</label>
            <USelect 
              v-model="sim.parameters.model" 
              :options="models" 
              placeholder="Select a model" 
            />
          </div>

          <div class="form-group">
            <label>Graph color</label>
            <input type="color" v-model="sim.parameters.color" />
          </div>

          <div class="form-group">
            <label>Messages / s</label>
            <input type="range" v-model="sim.parameters.simulationMessagePerSecond" min="1" max="200" />
            <span>{{ sim.parameters.simulationMessagePerSecond }}</span>
          </div>

          <div class="buttons">
            <UButton type="submit" class="run-btn">
              {{ sim.ran ? 'Rerun Simulation' : 'Run Simulation' }}
            </UButton>
            <UButton type="button" @click="removeSimulation(sim.id)" class="delete-btn">
              Delete Simulation
            </UButton>
          </div>
        </form>
      </div>
    </div>
  </aside>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import simulationAPI from '~/composables/useSimulationAPI'

interface LocalSimulation {
  id: number
  isOpen: boolean
  loading: boolean
  ran: boolean
  parameters: { model: string | null; color: string; simulationMessagePerSecond: number }
}

const localSimulations = ref<LocalSimulation[]>([])

const updateLocalSimulations = () => {
  localSimulations.value = simulationAPI.simulationState.simulationIds.map(id => {
    const existing = localSimulations.value.find(sim => sim.id === id)
    return existing || {
      id,
      isOpen: false,
      loading: false,
      ran: false,
      parameters: { model: "Mioty", color: '#000000', simulationMessagePerSecond: 10 }
    }
  })
}

// Initialization and synchronization with the store
updateLocalSimulations()
watch(() => simulationAPI.simulationState.simulationIds, updateLocalSimulations)
watch(() => simulationAPI.simulationState.simulationsTitle, updateLocalSimulations)

function addSimulation() {
  simulationAPI.createSimulation()
    .then(() => {
      updateLocalSimulations()
    })
    .catch(console.error)
}

function removeSimulation(id: number) {
  simulationAPI.deleteSimulation(id)
    .then(() => {
      localSimulations.value = localSimulations.value.filter(sim => sim.id !== id)
    })
    .catch(console.error)
}

function toggleSimulation(id: number) {
  const sim = localSimulations.value.find(sim => sim.id === id)
  if (sim) {
    sim.isOpen = !sim.isOpen
  }
}

function runSimulation_p(id: number) {
  const sim = localSimulations.value.find(sim => sim.id === id)
  if (!sim) return

  sim.loading = true

  // Build the parameters object based on the selected model
  const parameters = {
    simulationMessagePerSecond: sim.parameters.simulationMessagePerSecond,
    MiotyModelRun: sim.parameters.model === 'Mioty',
    SigfoxModelRun: sim.parameters.model === 'Sigfox',
    LoRaWanModelRun: sim.parameters.model === 'LoRaWan'
  }

  // Call the set_parameters endpoint to update the parameters
  simulationAPI.setSimulationParameters(id, parameters)
    .then(() => {
      // Once the parameters are updated, run the simulation
      return simulationAPI.runSimulation(id)
    })
    .then(() => {
      sim.loading = false
      sim.ran = true
    })
    .catch(error => {
      console.error('Simulation error:', error)
      sim.loading = false
    })
}

const models = ['Mioty', 'Sigfox', 'LoRaWan']
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
input[type="range"] {
  width: 100%;
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