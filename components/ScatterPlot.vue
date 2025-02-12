<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import * as echarts from "echarts";

const chartRef = ref(null);
let chartInstance = null;
const data = ref([]);
const windowSize = 20; // Fenêtre fixe de 20 secondes
let currentTime = 0;

// Ajoute toutes les bandes d'un coup
const addBands = () => {
  for (let i = 0; i < windowSize * 100; i++) {
    const frequency = Math.random() * 1000; // Fréquence aléatoire entre 0 et 1000 Hz
    const duration = Math.random() * (0.05 - 0.01) + 0.01; // Durée entre 0.01s et 0.05s
    const collision = Math.random() < 0.1; // 10% de chance de collision

    data.value.push({
      time: currentTime, // Début de la bande (temps)
      duration,
      frequency,
      collision,
    });

    currentTime += 0.01; // Avance de 0.01s
  }
  updateChart();
};

// Met à jour l'ensemble du graphique
const updateChart = () => {
  if (!chartInstance) return;

  chartInstance.setOption({
    title: { text: "Utilisation des bandes de fréquences dans le temps" },
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
      max: 1000,
      animation: true,
    },
    // Configuration du zoom :
    // - Zoom horizontal (axe X) activé par défaut, mais sera désactivé lorsque SHIFT est enfoncée
    // - Zoom vertical (axe Y) uniquement activé lorsque SHIFT est enfoncée
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
const updateDataZoom = (xZoomOnWheel) => {
  if (!chartInstance) return;

  chartInstance.setOption({
    dataZoom: [
      { type: "inside", xAxisIndex: 0, zoomOnMouseWheel: xZoomOnWheel },
      { type: "inside", yAxisIndex: 0, zoomOnMouseWheel: "shift" },
    ],
  });
};

// Désactive le zoom sur l'axe X lorsque SHIFT est enfoncée
const handleKeyDown = (event) => {
  if (event.key === "Shift") {
    updateDataZoom(false);
  }
};

// Réactive le zoom sur l'axe X lorsque SHIFT est relâchée
const handleKeyUp = (event) => {
  if (event.key === "Shift") {
    updateDataZoom(true);
  }
};

// Initialise le graphique
const initChart = () => {
  chartInstance = echarts.init(chartRef.value);
  updateChart();
};

onMounted(() => {
  initChart();
  addBands(); // Ajoute toutes les bandes d'un coup
  window.addEventListener("keydown", handleKeyDown);
  window.addEventListener("keyup", handleKeyUp);
});

onUnmounted(() => {
  if (chartInstance) chartInstance.dispose();
  window.removeEventListener("keydown", handleKeyDown);
  window.removeEventListener("keyup", handleKeyUp);
});
</script>

<template>
  <div ref="chartRef" style="width: 800px; height: 500px"></div>
</template>
