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
      group: frame.group,
      lost: frame.lost,
    }));
    updateChart();
  } catch (error) {
    console.error("Erreur lors de la récupération des résultats de la simulation :", error);
  }
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

// Met à jour l'ensemble du graphique
const updateChart = () => {
  if (!chartInstance) return;

  chartInstance.setOption({
    tooltip: {
      trigger: "item",
      formatter: (params) => {
        const frame = params.data;
        return `
          <b>Temps :</b> ${frame[0]} s<br/>
          <b>Fréquence :</b> ${frame[1]} Hz<br/>
          <b>Durée :</b> ${frame[2]} s<br/>
          <b>Collision :</b> ${frame[3] ? "Oui" : "Non"}<br/>
          <b>Groupe :</b> ${frame[4]}<br/>
          <b>Perte :</b> ${frame[5] ? "Oui" : "Non"}
        `;
      },
    },
    xAxis: {
      type: "value",
      name: "Temps (s)",
      min: 0,
      max: windowSize,
    },
    yAxis: {
      type: "value",
      name: "Fréquence (Hz)",
      min: 0,
      max: 2000,
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
          const height = 10;

          return {
            type: "rect",
            shape: { x: point[0], y: point[1] - height / 2, width, height },
            style: { fill: api.value(3) ? "red" : "blue" },
          };
        },
        encode: {
          x: "time",
          y: "frequency",
          tooltip: ["time", "frequency", "duration", "collision", "group", "lost"],
        },
        data: data.value.map((d) => [d.time, d.frequency, d.duration, d.collision, d.group, d.lost]),
      },
    ],
  });
};

// Initialise le graphique
const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    updateChart();
  }
};

// Redimensionne le graphique
const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

onMounted(() => {
  initChart();
  fetchSimulationResults(0); // Remplacez 0 par l'ID de votre simulation
  window.addEventListener("resize", resizeChart);

  window.addEventListener("keydown", handleKeyDown);
  window.addEventListener("keyup", handleKeyUp);
  window.addEventListener("resize", resizeChart);
});

onUnmounted(() => {
  if (chartInstance) chartInstance.dispose();
  window.removeEventListener("resize", resizeChart);
});
</script>

<template>
  <div ref="chartRef" class="chart-container"></div>
</template>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
}
</style>
