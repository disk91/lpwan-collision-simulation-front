<template>
  <div>
    <div class="checkboxes">
      <label v-for="group in availableGroups" :key="group" class="checkbox-label">
        <input type="checkbox" v-model="selectedGroups" :value="group" />
        Groupe {{ group }}
      </label>
    </div>
  </div>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted, defineProps } from 'vue';
import * as echarts from 'echarts';
import { useSimulationAPI } from '~/composables/useSimulationAPI';

interface FrameData {
  time: number;
  duration: number;
  frequency: number;
  collision: boolean;
  group: number;
  lost: boolean;
  type: string;
}

const props = defineProps<{ simulationId: number }>();

const { simulationState, waitUntilSimulationFinished } = useSimulationAPI();

const chartRef = ref<HTMLElement | null>(null);
let chartInstance: echarts.ECharts | null = null;
const data = ref<FrameData[]>([]);
const windowSize = 20; // Fenêtre d'affichage en secondes

// Gestion du survol dans le graphique
const hoveredIndex = ref<number | null>(null);
const defaultOpacity = 0.6;

// Gestion de la sélection des groupes via checkbox
const selectedGroups = ref<number[]>([]);
const availableGroups = computed(() => {
  // Extrait les groupes disponibles à partir des données
  const groups = new Set<number>();
  data.value.forEach(frame => groups.add(frame.group));
  return Array.from(groups).sort((a, b) => a - b);
});

// Dès que la liste des groupes disponibles est mise à jour, on s'assure que
// tous les groupes soient sélectionnés par défaut (ou on complète ceux manquants)
watch(availableGroups, (newGroups) => {
  if (selectedGroups.value.length === 0) {
    selectedGroups.value = newGroups;
  } else {
    // Ajoute les nouveaux groupes s'ils n'étaient pas déjà sélectionnés
    newGroups.forEach(group => {
      if (!selectedGroups.value.includes(group)) {
        selectedGroups.value.push(group);
      }
    });
  }
});

// Met à jour le graphique lorsque la sélection change
watch(selectedGroups, () => {
  updateChart();
});

// Transformation des données reçues depuis le serveur
const transformSimulationData = (results: any): FrameData[] => {
  const combinedData: FrameData[] = [];

  // Pour LoRaWan
  if (results.loRaWanRun && results.loRaWanFrames) {
    combinedData.push(
      ...results.loRaWanFrames.map((frame: any) => ({
        time: frame.usStart / 1e6, // Conversion de microsecondes en secondes
        duration: (frame.usEnd - frame.usStart) / 1e6,
        frequency: frame.channel,
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'LoRaWan',
      }))
    );
  }

  // Pour Mioty
  if (results.miotyModelRun && results.miotyFrames) {
    combinedData.push(
      ...results.miotyFrames.map((frame: any) => ({
        time: frame.usStart / 1e6,
        duration: (frame.usEnd - frame.usStart) / 1e6,
        frequency: frame.channel,
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'Mioty',
      }))
    );
  }

  // Pour Sigfox
  if (results.sigfoxModelRun && results.sigfoxFrames) {
    combinedData.push(
      ...results.sigfoxFrames.map((frame: any) => ({
        time: frame.usStart / 1e6,
        duration: (frame.usEnd - frame.usStart) / 1e6,
        frequency: frame.channel,
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'Sigfox',
      }))
    );
  }

  return combinedData;
};

const updateChart = () => {
  if (!chartInstance) return;

  // Calcul des bornes sur l'axe X et Y
  let xMin = 0, xMax = windowSize, yMin = 0, yMax = 2000;
  if (data.value.length > 0) {
    const yValues = data.value.map(d => d.frequency);
    yMin = Math.min(...yValues);
    yMax = Math.max(...yValues);
  }
  const xPadding = (xMax - xMin) * 0.1;
  const yPadding = (yMax - yMin) * 0.1;

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
      min: xMin - xPadding,
      max: xMax + xPadding,
    },
    yAxis: {
      type: 'value',
      name: 'Fréquence (Hz)',
      min: yMin - yPadding,
      max: yMax + yPadding,
    },
    dataZoom: [
      {
        type: 'inside',
        xAxisIndex: 0,
        startValue: xMin - xPadding,
        endValue: Math.min(xMin + windowSize + xPadding, xMax + xPadding),
        zoomOnMouseWheel: true,
      },
      {
        type: 'inside',
        yAxisIndex: 0,
        zoomOnMouseWheel: 'shift',
      },
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

          const pt = api.coord([xValue, yValue]);
          const size = api.size([duration, 0]);
          const totalWidth = size[0];
          const borderWidth = 4;
          const leftWidth = Math.min(borderWidth, totalWidth);
          const rightWidth = Math.min(borderWidth, totalWidth - leftWidth);
          const middleWidth = totalWidth - leftWidth - rightWidth;

          const baseHeight = 10;
          const isHovered = hoveredIndex.value === params.dataIndex;
          const scaleFactor = isHovered ? 1.05 : 1;
          const newHeight = baseHeight * scaleFactor;
          const startY = pt[1] - newHeight / 2;
          const currentOpacity = isHovered ? 1 : defaultOpacity;

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
            children: [leftRect, middleRect, rightRect],
          };
        },
        encode: {
          x: 'time',
          y: 'frequency',
          tooltip: ['time', 'frequency', 'duration', 'collision', 'group', 'lost', 'type'],
        },
        // On filtre les données selon la sélection des groupes
        data: data.value
          .filter(d => selectedGroups.value.includes(d.group))
          .map((d) => [
            d.time,
            d.frequency,
            d.duration,
            d.collision,
            d.group,
            d.lost,
            d.type,
          ]),
      },
    ],
  });
};

const initChart = () => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value);
    updateChart();
  }
};

const resizeChart = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

const fetchAndTransform = async () => {
  try {
    // Attend que la simulation soit terminée
    const results = await waitUntilSimulationFinished(props.simulationId);
    data.value = transformSimulationData(results);
    updateChart();
  } catch (error) {
    console.error('Erreur lors de la récupération de la simulation:', error);
  }
};

watch(
  () => simulationState.simulations[props.simulationId],
  (newVal) => {
    if (newVal) {
      data.value = transformSimulationData(newVal);
      updateChart();
    }
  },
  { deep: true }
);

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

onMounted(() => {
  initChart();
  fetchAndTransform();
  window.addEventListener('resize', resizeChart);
  window.addEventListener('keydown', handleKeyDown);
  window.addEventListener('keyup', handleKeyUp);
});

onUnmounted(() => {
  if (chartInstance) chartInstance.dispose();
  window.removeEventListener('resize', resizeChart);
  window.removeEventListener('keydown', handleKeyDown);
  window.removeEventListener('keyup', handleKeyUp);
});
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 100%;
}

/* Style pour les cases à cocher */
.checkboxes {
  margin-top: 10px;
  text-align: center;
}

.checkbox-label {
  margin-right: 15px;
  font-size: 14px;
}
</style>
