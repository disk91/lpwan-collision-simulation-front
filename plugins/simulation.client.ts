import { useSimulationAPI } from "~/composables/useSimulationAPI"

export default defineNuxtPlugin(async (nuxtApp) => {
  const simulationAPI = useSimulationAPI();

  try {
    const ids = await simulationAPI.getSimulationIds();

    if (!Array.isArray(ids)) {
      console.error("Les IDs récupérés ne sont pas un tableau :", ids);
      return;
    }

    await Promise.all(ids.map(id => simulationAPI.getSimulationValues(id)));
  } catch (error) {
    console.error("Erreur lors de l'initialisation des simulations :", error);
  }
});
