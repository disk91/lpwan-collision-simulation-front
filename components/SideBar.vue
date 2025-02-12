<template>
  <aside class="sidebar">
    <div v-if="loading" class="loading">
      <p>Running simulation...</p>
    </div>
    
    <form v-else @submit.prevent="onRunSimulation">
      <div class="form-group">
        <label for="model">Model</label>
        <USelect v-model="parameters.model" :options="models" placeholder="Select a model" />
      </div>

      <div class="form-group">
        <label for="color">Graph color</label>
        <input type="color" id="color" v-model="parameters.color" />
      </div>

      <UButton type="submit" class="run-btn">Run !</UButton>
    </form>
  </aside>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useSimulationAPI } from '~/composables/useSimulationAPI'

const { startSimulation } = useSimulationAPI()

const models = ref([
  { label: 'Mioty', value: 'mioty' },
  { label: 'Sigfox', value: 'sigfox' },
  { label: 'LoRaWAN', value: 'lorawan' }
])

const parameters = ref({
  model: 'mioty',
  color: '#ff0000'
})

const loading = ref(false)

function onRunSimulation() {
  loading.value = true
  startSimulation(parameters.value)
    .then(() => {
      loading.value = false
    })
    .catch((error) => {
      console.error('Erreur de simulation:', error)
      loading.value = false
    })
}
</script>

<style scoped>
.sidebar {
  width: 250px;
  padding: 1rem;
  border-right: 1px solid #ddd;
  height: 100%;
  overflow-y: auto;
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

.run-btn {
  display: block;
  width: 100%;
  padding: 0.5rem;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}
</style>
