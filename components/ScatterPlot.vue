<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import { useSimulationAPI } from '~/composables/useSimulationAPI';

const { getSimulationResults } = useSimulationAPI();

const chartRef = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
const data = ref([]);
const windowSize = 20; // Fenêtre fixe de 20 secondes

// Variable réactive pour stocker l'index de la trame survolée
const hoveredIndex = ref<number | null>(null);

const defaultOpacity = 0.6; // Opacité par défaut (60%)

const fetchSimulationResults = async (simulationId: number) => {
  try {
    const results = await getSimulationResults(simulationId);
    const combinedData = [];

    if (results.loRaWanRun) {
      combinedData.push(...results.loRaWanFrames.map((frame) => ({
        time: frame.usStart / 1e6, // Convertir microsecondes en secondes
        duration: (frame.usEnd - frame.usStart) / 1e6, // Convertir microsecondes en secondes
        frequency: frame.channel, // Utiliser le champ channel comme fréquence
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'LoRaWan'
      })));
    }

    if (results.MiotyModelRun) {
      combinedData.push(...results.miotyFrames.map((frame) => ({
        time: frame.usStart / 1e6, // Convertir microsecondes en secondes
        duration: (frame.usEnd - frame.usStart) / 1e6, // Convertir microsecondes en secondes
        frequency: frame.channel, // Utiliser le champ channel comme fréquence
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'Mioty'
      })));
    }

    if (results.sigfoxRun) {
      combinedData.push(...results.sigfoxFrames.map((frame) => ({
        time: frame.usStart / 1e6, // Convertir microsecondes en secondes
        duration: (frame.usEnd - frame.usStart) / 1e6, // Convertir microsecondes en secondes
        frequency: frame.channel, // Utiliser le champ channel comme fréquence
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'Sigfox'
      })));
    }

    data.value = combinedData;
    updateChart();
  } catch (error) {
    console.error('Erreur lors de la récupération des résultats de la simulation :', error);
  }
};

const updateDataZoom = (xZoomOnWheel: boolean) => {
  if (!chartInstance) return;
  chartInstance.setOption({
    dataZoom: [
      { type: 'inside', xAxisIndex: 0, zoomOnMouseWheel: xZoomOnWheel },
      { type: 'inside', yAxisIndex: 0, zoomOnMouseWheel: 'shift' },
    ],
  });
};

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Shift') {
    updateDataZoom(false);
  }
};

const handleKeyUp = (event: KeyboardEvent) => {
  if (event.key === 'Shift') {
    updateDataZoom(true);
  }
};

const updateChart = () => {
  if (!chartInstance) return;

  chartInstance.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        const frame = params.data;
        return `
          <b>Temps :</b> ${frame[0]} s<br/>
          <b>Fréquence :</b> ${frame[1]} Hz<br/>
          <b>Durée :</b> ${frame[2]} s<br/>
          <b>Collision :</b> ${frame[3] ? "Oui" : "Non"}<br/>
          <b>Groupe :</b> ${frame[4]}<br/>
          <b>Perte :</b> ${frame[5] ? "Oui" : "Non"}<br/>
          <b>Type :</b> ${frame[6]}
        `;
      },
    },
    xAxis: {
      type: 'value',
      name: 'Temps (s)',
      min: 0,
      max: windowSize,
    },
    yAxis: {
      type: 'value',
      name: 'Fréquence (Hz)',
      min: 0,
      max: 2000,
    },
    dataZoom: [
      { type: 'inside', xAxisIndex: 0, zoomOnMouseWheel: true },
      { type: 'inside', yAxisIndex: 0, zoomOnMouseWheel: 'shift' },
    ],
    series: [
      {
        type: 'custom',
        renderItem: (params, api) => {
          const xValue = api.value(0);
          const yValue = api.value(1);
          const duration = api.value(2);
          const collision = api.value(3);
          const mainColor = collision ? 'red' : 'blue';

          // Positionnement horizontal (inchangé)
          const pt = api.coord([xValue, yValue]);
          const size = api.size([duration, 0]);
          const totalWidth = size[0];
          const borderWidth = 4; // Largeur fixe pour les bords gauche et droit
          const leftWidth = Math.min(borderWidth, totalWidth);
          const rightWidth = Math.min(borderWidth, totalWidth - leftWidth);
          const middleWidth = totalWidth - leftWidth - rightWidth;

          // Modification de la hauteur au survol
          const baseHeight = 10;
          const isHovered = hoveredIndex.value === params.dataIndex;
          const scaleFactor = isHovered ? 1.05 : 1; // Augmentation de 5% si survolé
          const newHeight = baseHeight * scaleFactor;
          // Centrer verticalement la trame
          const startY = pt[1] - newHeight / 2;
          const currentOpacity = isHovered ? 1 : defaultOpacity;

          // Conserver l'empilement horizontal : les rectangles sont alignés sur la même ligne
          const leftRect = {
            type: 'rect',
            shape: {
              x: pt[0],
              y: startY,
              width: leftWidth,
              height: newHeight,
            },
            style: {
              fill: 'green',
              opacity: currentOpacity,
            },
          };

          const middleRect = {
            type: 'rect',
            shape: {
              x: pt[0] + leftWidth,
              y: startY,
              width: middleWidth,
              height: newHeight,
            },
            style: {
              fill: mainColor,
              opacity: currentOpacity,
            },
          };

          const rightRect = {
            type: 'rect',
            shape: {
              x: pt[0] + leftWidth + middleWidth,
              y: startY,
              width: rightWidth,
              height: newHeight,
            },
            style: {
              fill: 'yellow',
              opacity: currentOpacity,
            },
          };

          return {
            type: 'group',
            triggerEvent: true,
            silent: false,
            children: [leftRect, middleRect, rightRect],
          };
        },
        encode: {
          x: 'time',
          y: 'frequency',
          tooltip: ['time', 'frequency', 'duration', 'collision', 'group', 'lost', 'type'],
        },
        data: data.value.map((d) => [d.time, d.frequency, d.duration, d.collision, d.group, d.lost, d.type]),
      },
    ],
  });
};

const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    updateChart();

    // Attache des écouteurs d'événements pour gérer le survol
    chartInstance.on('mouseover', (params) => {
      if (params.componentType === 'series' && params.seriesType === 'custom') {
        hoveredIndex.value = params.dataIndex;
        updateChart();
      }
    });
    chartInstance.on('mouseout', (params) => {
      if (params.componentType === 'series' && params.seriesType === 'custom') {
        hoveredIndex.value = null;
        updateChart();
      }
    });
  }
};

const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

onMounted(() => {
  initChart();
  fetchSimulationResults(0); // Remplacez 0 par l'ID de votre simulation
  window.addEventListener('resize', resizeChart);
  window.addEventListener('keydown', handleKeyDown);
  window.addEventListener('keyup', handleKeyUp);
});

onUnmounted(() => {
  if (chartInstance) chartInstance.dispose();
  window.removeEventListener('resize', resizeChart);
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
