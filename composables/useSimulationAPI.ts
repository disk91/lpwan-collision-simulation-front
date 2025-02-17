// ~/composables/useSimulationAPI.ts
import { reactive } from 'vue'

// On récupère la baseURL dans un contexte valide (lors de l'exécution d'une fonction)
function getBaseURL() {
  return useRuntimeConfig().public.baseURL;
}

// Définition des interfaces pour typage
export interface FrameModel {
  channel: number             // Fréquence utilisée par la trame
  group: number               // Groupe auquel appartient la trame
  usStart: number             // Début de la trame en microsecondes
  usEnd: number               // Fin de la trame en microsecondes
  collision: boolean          // Indique une collision locale (un fragment)
  lost: boolean               // Indique une collision globale (perte de la trame)
  next?: FrameModel | null    // Chaînage vers la trame suivante
  first: boolean              // Indique si c'est la première trame de la chaîne
}

export interface SimulationModel {
  simulationRunning: boolean
  simulationMessagePerSecond: number
  MiotyModelRun: boolean
  MiotyFrames: FrameModel[]
  SigfoxModelRun: boolean
  SigfoxFrames: FrameModel[]
  LoRaWanRun: boolean
  LoRaWanFrames: FrameModel[]
}

// Objet réactif centralisé (sans isLoading)
const simulationState = reactive({
  simulationIds: [] as number[],
  simulations: {} as Record<number, SimulationModel>,
  error: null as string | null,
  connectionStatus: 'disconnected'
})

async function createSimulation() {
  simulationState.error = null
  const baseURL = getBaseURL();
  try {
    const parameters = {
      simulationMessagePerSecond: 2,
      MiotyModelRun: true,
      SigfoxModelRun: false,
      LoRaWanRun: false,
    }

    const response = await fetch(`${baseURL}/api/new`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(parameters)
    })

    console.log(JSON.stringify(parameters))

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(
        `Erreur création simulation: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      )
    }

    const data = await response.json() // { id: identifiant }
    simulationState.simulationIds.push(data.id)
    return data
  } catch (error: any) {
    console.error("Erreur dans createSimulation :", error)
    simulationState.error = error.message || "Erreur inconnue"
    throw error
  }
}

async function runSimulation(simulationId: number) {
  simulationState.error = null
  const baseURL = getBaseURL();
  try {
    if (typeof simulationId !== 'number' && typeof simulationId !== 'string') {
      throw new Error('simulationId doit être un nombre ou une chaîne')
    }

    // Lancement de la simulation
    const response = await fetch(`${baseURL}/api/run/${simulationId}`, {
      method: 'POST'
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(
        `Erreur lancement: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      )
    }

    const result = await response.json()

    // Démarrer un polling toutes les 1 secondes pour mettre à jour la simulation
    const interval = setInterval(async () => {
      try {
        const simulationData = await getSimulationValues(simulationId);
        // Si simulationRunning est à false, arrêter le polling
        if (!simulationData.simulationRunning) {
          clearInterval(interval);
        }
      } catch (error) {
        clearInterval(interval);
        console.error("Erreur lors du polling de la simulation :", error);
      }
    }, 1000);

    return result;
  } catch (error: any) {
    console.error("Erreur dans runSimulation :", error)
    simulationState.error = error.message || "Erreur inconnue"
    throw error
  }
}

async function getSimulationValues(simulationId: number) {
  simulationState.error = null
  const baseURL = getBaseURL();
  try {
    const response = await fetch(`${baseURL}/api/get_values/${simulationId}`)
    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(
        `Erreur récupération résultats: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      )
    }

    const data = (await response.json()) as SimulationModel
    // Mise à jour de la simulation dans le store pour avoir le suivi en temps réel
    simulationState.simulations[simulationId] = data
    //console.log(data)
    return data
  } catch (error: any) {
    console.error("Erreur dans getSimulationValues :", error)
    simulationState.error = error.message || "Erreur inconnue"
    throw error
  }
}

async function getSimulationIds() {
  simulationState.error = null;
  const baseURL = getBaseURL();
  try {
    const response = await fetch(`${baseURL}/api/get_ids`);

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
        `Erreur récupération identifiants: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      );
    }

    const data = await response.json();

    if (!data?.id || !Array.isArray(data.id)) {
      throw new Error(`Réponse inattendue de l'API : ${JSON.stringify(data)}`);
    }

    simulationState.simulationIds = data.id; // Stocke uniquement le tableau d'IDs
    return data.id;
  } catch (error: any) {
    console.error("Erreur dans getSimulationIds :", error);
    simulationState.error = error.message || "Erreur inconnue";
    throw error;
  }
}

async function setSimulationParameters(simulationId: number, parameters: any) {
  simulationState.error = null
  const baseURL = getBaseURL();
  try {
    if (typeof simulationId !== 'number' && typeof simulationId !== 'string') {
      throw new Error('simulationId doit être un nombre ou une chaîne')
    }

    const response = await fetch(`${baseURL}/api/set_parameters/${simulationId}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(parameters)
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(
        `Erreur paramétrage: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      )
    }

    return await response.json()
  } catch (error: any) {
    console.error("Erreur dans setSimulationParameters :", error)
    simulationState.error = error.message || "Erreur inconnue"
    throw error
  }
}

async function deleteSimulation(simulationId: number) {
  simulationState.error = null
  const baseURL = getBaseURL();
  try {
    if (typeof simulationId !== 'number' && typeof simulationId !== 'string') {
      throw new Error('simulationId doit être un nombre ou une chaîne')
    }

    const response = await fetch(`${baseURL}/api/delete/${simulationId}`, {
      method: 'POST'
    })

    if (!response.ok) {
      const errorData = await response.json()
      throw new Error(
        `Erreur suppression: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      )
    }

    const result = await response.json()
    simulationState.simulationIds = simulationState.simulationIds.filter(id => id !== simulationId)
    delete simulationState.simulations[simulationId]
    return result
  } catch (error: any) {
    console.error("Erreur dans deleteSimulation :", error)
    simulationState.error = error.message || "Erreur inconnue"
    throw error
  }
}

async function pingServer() {
  simulationState.error = null
  const baseURL = getBaseURL();
  try {
    const response = await fetch(`${baseURL}/api/ping`)

    if (!response.ok) {
      const errorData = await response.json()
      simulationState.connectionStatus = 'disconnected'
      throw new Error(
        `Erreur ping: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`
      )
    }

    simulationState.connectionStatus = 'connected'
    return await response.json()
  } catch (error: any) {
    console.error("Erreur dans pingServer :", error)
    simulationState.error = error.message || "Erreur inconnue"
    simulationState.connectionStatus = 'disconnected'
    throw error
  }
}

export default {
  simulationState,
  createSimulation,
  runSimulation,
  getSimulationValues,
  getSimulationIds,
  setSimulationParameters,
  deleteSimulation,
  pingServer
}


export function useSimulationAPI() {
  return {
    simulationState,
    createSimulation,
    runSimulation,
    getSimulationValues,
    getSimulationIds,
    setSimulationParameters,
    deleteSimulation,
    pingServer,
  }
}
