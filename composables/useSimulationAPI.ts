import { useRuntimeConfig } from '#imports'

export const useSimulationAPI = () => {
  const config = useRuntimeConfig()
  const baseURL = config.public.apiBaseURL

  const startSimulation = async (params: any) => {
    try {
      const response = await fetch(`${baseURL}/simulations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(params)
      })
      if (!response.ok) {
        const errorData = await response.json() // Essayer de récupérer les détails de l'erreur du serveur
        throw new Error(`Erreur lors du démarrage: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`)
      }
      return await response.json()
    } catch (error) {
      console.error("Erreur dans startSimulation :", error)
      throw error
    }
  }

  const stopSimulation = async (simulationId: string) => {
    try {
      const response = await fetch(`${baseURL}/simulations/${simulationId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ action: 'stop' })
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(`Erreur lors de l'arrêt: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`)
      }

      return await response.json() // Retourne l'objet de simulation modifié

    } catch (error) {
      console.error("Erreur dans stopSimulation :", error)
      throw error
    }
  }

  const getSimulationStatus = async (simulationId: string) => {
    try {
      const response = await fetch(`${baseURL}/simulations/${simulationId}/status`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(`Erreur statut: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`)
      }
      return await response.json()
    } catch (error) {
      console.error("Erreur dans getSimulationStatus :", error)
      throw error
    }
  }

  const getSimulationResults = async (simulationId: string) => {
    try {
      const response = await fetch(`${baseURL}/simulations/${simulationId}/results`)
      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(`Erreur résultats: ${response.status} ${response.statusText} - ${errorData?.message || 'Détails non disponibles'}`)
      }
      return await response.json()
    } catch (error) {
      console.error("Erreur dans getSimulationResults :", error)
      throw error
    }
  }

  return { startSimulation, stopSimulation, getSimulationStatus, getSimulationResults }
}