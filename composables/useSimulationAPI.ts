import { useRuntimeConfig } from '#imports'

export const useSimulationAPI = () => {
  const config = useRuntimeConfig()
  const baseURL = config.public.baseURL

  interface FrameModel {
    channel: number;   // Représentation de la fréquence utilisée par la trame
    group: number;     // Groupe auquel appartient la trame

    usStart: number;   // Début de la trame en microsecondes
    usEnd: number;     // Fin de la trame en microsecondes

    collision: boolean;  // Indique une collision locale (un fragment)
    lost: boolean;       // Indique une collision globale (perte de la trame)

    next?: FrameModel | null;  // Chaînage vers la trame suivante
    //head?: FrameModel | null;  // Référence vers la première trame de la chaîne
    first: boolean;            // Indique si c'est la première trame de la chaîne
  }

  interface SimulationModel {
    simulationRunning: boolean;  // Indique si la simulation est en cours
    simulationMessagePerSecond: number;  // Nombre de messages simulés par seconde

    MiotyModelRun: boolean;  // Indique si le modèle Mioty est actif
    MiotyFrames: FrameModel[];  // Liste des trames Mioty

    SigfoxModelRun: boolean;  // Indique si le modèle Sigfox est actif
    SigfoxFrames: FrameModel[];  // Liste des trames Sigfox

    LoRaWanRun: boolean;  // Indique si le modèle LoRaWAN est actif
    LoRaWanFrames: FrameModel[];  // Liste des trames LoRaWAN
  }

  const createSimulation = async () => {
    try {
      const response = await fetch(`${baseURL}/api/new`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(`Erreur création simulation: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`)
      }

      return await response.json() // Retourne { id: identifiant }
    } catch (error) {
      console.error("Erreur dans createSimulation :", error)
      throw error
    }
  }

  const startSimulation = async (simulationId: number) => {
    try {
      if (typeof simulationId !== 'number' && typeof simulationId !== 'string') {
        throw new Error('simulationId doit être un nombre ou une chaîne');
      }
  
      const response = await fetch(`${baseURL}/api/run/${simulationId}`);
  
      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(`Erreur lancement: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`);
      }
  
      return await response.json();
    } catch (error) {
      console.error("Erreur dans startSimulation :", error);
      throw error;
    }
  }

  const getSimulationResults = async (simulationId: number) => {
    try {
      const response = await fetch(`${baseURL}/api/values/${simulationId}`)

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(`Erreur récupération résultats: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`)
      }

      return await response.json() // Retourne un objet SimulationModel
    } catch (error) {
      console.error("Erreur dans getSimulationResults :", error)
      throw error
    }
  }

  return { createSimulation, startSimulation, getSimulationResults }
}