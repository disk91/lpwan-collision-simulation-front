<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";
import { useSimulationAPI } from "~/composables/useSimulationAPI";

const { getSimulationResults } = useSimulationAPI();

const chartRef = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
const data = ref([]);
const windowSize = 20; // Fenêtre fixe de 20 secondes

// Fonction pour récupérer les résultats de la simulation
const fetchSimulationResults = async (simulationId: number) => {
  try {
    const results = await getSimulationResults(simulationId);
    data.value = results.loRaWanFrames.map((frame) => ({
      time: frame.usStart / 1e6, // Convertir microsecondes en secondes
      duration: (frame.usEnd - frame.usStart) / 1e6, // Convertir microsecondes en secondes
      frequency: frame.channel, // Utiliser le champ channel comme fréquence
      collision: frame.collision,
    }));
    updateChart();
  } catch (error) {
    console.error("Erreur lors de la récupération des résultats de la simulation :", error);
  }
};

// Met à jour l'ensemble du graphique
const updateChart = () => {
  if (!chartInstance) return;

  chartInstance.setOption({
    tooltip: { trigger: "item" },
    xAxis: {
      type: "value",
      name: "Temps (s)",
      min: 0,
      max: windowSize,
      animation: true,
    },
    yAxis: {
      type: "value",
      name: "Fréquence (Hz)",
      min: 0,
      max: 2000,
      animation: true,
    },
    dataZoom: [
      { type: "inside", xAxisIndex: 0, zoomOnMouseWheel: true },
      { type: "inside", yAxisIndex: 0, zoomOnMouseWheel: "shift" },
    ],
    series: [
      {
        type: "custom",
        renderItem: (params, api) => {
          const point = api.coord([api.value(0), api.value(1)]);
          const width = api.size([api.value(2), 0])[0];
          const height = 10; // Épaisseur fixe des bandes

          return {
            type: "rect",
            shape: { x: point[0], y: point[1] - height / 2, width, height },
            style: { fill: api.value(3) ? "red" : "blue" },
          };
        },
        encode: {
          x: "time",
          y: "frequency",
          tooltip: ["time", "frequency", "duration", "collision"],
        },
        data: data.value
          .filter((d) => d.time <= windowSize)
          .map((d) => [d.time, d.frequency, d.duration, d.collision]),
        animation: true,
      },
    ],
  });
};

// Met à jour uniquement la configuration du zoom (dataZoom) pour l'axe X
const updateDataZoom = (xZoomOnWheel: boolean) => {
  if (!chartInstance) return;

  chartInstance.setOption({
    dataZoom: [
      { type: "inside", xAxisIndex: 0, zoomOnMouseWheel: xZoomOnWheel },
      { type: "inside", yAxisIndex: 0, zoomOnMouseWheel: "shift" },
    ],
  });
};

// Désactive le zoom sur l'axe X lorsque SHIFT est enfoncée
const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === "Shift") {
    updateDataZoom(false);
  }
};

// Réactive le zoom sur l'axe X lorsque SHIFT est relâchée
const handleKeyUp = (event: KeyboardEvent) => {
  if (event.key === "Shift") {
    updateDataZoom(true);
  }
};

// Initialise le graphique
const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    updateChart();
  }
};

// Fonction pour redimensionner le graphique en cas de changement de taille
const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

onMounted(() => {
  initChart();
  fetchSimulationResults(0); // Remplacez 1 par l'ID de votre simulation

  window.addEventListener("keydown", handleKeyDown);
  window.addEventListener("keyup", handleKeyUp);
  window.addEventListener("resize", resizeChart);
});

onUnmounted(() => {
  if (chartInstance) chartInstance.dispose();
  window.removeEventListener("keydown", handleKeyDown);
  window.removeEventListener("keyup", handleKeyUp);
  window.removeEventListener("resize", resizeChart);
});
</script>

<template>
  <!-- Utilise une classe CSS pour définir le style du conteneur -->
  <div ref="chartRef" class="chart-container"></div>
</template>

<style scoped>
/* Le conteneur prend 100% de la largeur et de la hauteur de son parent */
.chart-container {
  width: 100%;
  height: 100%;
}
</style>
