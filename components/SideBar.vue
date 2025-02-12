<template>
    <aside class="sidebar">
      <!-- Si la simulation est en cours de calcul, on affiche un indicateur de chargement -->
      <div v-if="loading" class="loading">
        <p>Chargement...</p>
      </div>
      <!-- Sinon, on affiche le formulaire permettant de configurer la simulation -->
      <form v-else @submit.prevent="onRunSimulation">
        <div class="form-group">
          <label for="model">Modèle</label>
          <select id="model" v-model="parameters.model">
            <option value="mioty">Mioty</option>
            <option value="sigfox">Sigfox</option>
            <option value="lorawan">LoRaWAN</option>
          </select>
        </div>
        <div class="form-group">
          <label for="color">Couleur du graphique</label>
          <input type="color" id="color" v-model="parameters.color" />
        </div>
        <!-- Ajoutez ici d'autres champs de paramètres si nécessaire -->
  
        <!-- Bouton pour lancer la simulation -->
        <button type="submit" class="run-btn">Run</button>
      </form>
    </aside>
  </template>
  
  <script setup lang="ts">
  import { ref } from 'vue'
  import { useSimulationAPI } from '~/composables/useSimulationAPI'

  const { startSimulation } = useSimulationAPI()
  
  // Exemple de paramètres modifiables par l'utilisateur
  const parameters = ref({
    model: 'mioty',
    color: '#ff0000'
    // Vous pouvez ajouter d'autres paramètres ici
  })
  
  // Indicateur local de chargement (vous pouvez également vous baser sur simulationState)
  const loading = ref(false)
  
  function onRunSimulation() {
    loading.value = true
    startSimulation(parameters.value)
      .then(() => {
        // La simulation a démarré ; la mise à jour de l'état (via useSimulationState) se fera par interrogation périodique
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
    background: #f7f7f7;
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
  
  input[type="color"],
  select {
    width: 100%;
    padding: 0.3rem;
  }
  
  .run-btn {
    display: block;
    width: 100%;
    padding: 0.5rem;
    background: #28a745;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .run-btn:hover {
    background: #218838;
  }
  
  .loading {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
  }
  </style>
  