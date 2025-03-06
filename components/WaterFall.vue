<template>
  <div class="checkboxes-container">
    <div class="checkboxes">
      <UCheckbox v-for="group in availableGroups" :key="group" v-model="selectedGroups" :value="group"
        :label="`Group ${group}`" class="checkbox-label" />
    </div>
  </div>
  <div ref="chartRef" class="chart-container"></div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import * as echarts from 'echarts';
import { useSimulationAPI } from '~/composables/useSimulationAPI';
import { chartColors } from '~/composables/useChartColors';

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
const windowSize = 20; // Display window size in seconds

// Hover management in the chart
const hoveredIndex = ref<number | null>(null);
const defaultOpacity = 0.6;

// Group selection management via checkboxes
const selectedGroups = ref<number[]>([]);
const availableGroups = computed(() => {
  // Extract available groups from the data
  const groups = new Set<number>();
  data.value.forEach(frame => groups.add(frame.group));
  return Array.from(groups).sort((a, b) => a - b);
});

// Ensure all groups are selected by default when the available groups list is updated
watch(availableGroups, (newGroups) => {
  if (selectedGroups.value.length === 0) {
    selectedGroups.value = newGroups;
  } else {
    // Add new groups if they were not already selected
    newGroups.forEach(group => {
      if (!selectedGroups.value.includes(group)) {
        selectedGroups.value.push(group);
      }
    });
  }
});

// Update the chart when the selection changes
watch(selectedGroups, () => {
  updateChart();
});

// Transform data received from the server
const transformSimulationData = (results: any): FrameData[] => {
  const combinedData: FrameData[] = [];

  // For LoRaWan
  if (results.LoRaWanFrames) {
    combinedData.push(
      ...results.LoRaWanFrames.map((frame: any) => ({
        time: frame.usStart / 1e6, // Convert microseconds to seconds
        duration: (frame.usEnd - frame.usStart) / 1e6,
        frequency: frame.channel,
        collision: frame.collision,
        group: frame.group,
        lost: frame.lost,
        type: 'LoRaWan',
      }))
    );
  }

  // For Mioty
  if (results.MiotyFrames) {
    combinedData.push(
      ...results.MiotyFrames.map((frame: any) => ({
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

  // For Sigfox
  if (results.SigfoxFrames) {
    combinedData.push(
      ...results.SigfoxFrames.map((frame: any) => ({
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

  // Calculate bounds on the X and Y axes
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
          <b>Time:</b> ${frame[0]} s<br/>
          <b>Frequency:</b> ${frame[1]} Hz<br/>
          <b>Duration:</b> ${frame[2]} s<br/>
          <b>Collision:</b> ${frame[3] ? "Yes" : "No"}<br/>
          <b>Group:</b> ${frame[4]}<br/>
          <b>Lost:</b> ${frame[5] ? "Yes" : "No"}<br/>
          <b>Type:</b> ${frame[6]}
        `;
      },
    },
    xAxis: {
      type: 'value',
      name: 'Time (s)',
      min: xMin - xPadding,
      max: xMax + xPadding,
    },
    yAxis: {
      type: 'value',
      name: 'Frequency (Hz)',
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
          const lost = api.value(5);

          let computedMainColor = '';
          let computedBorderColor = '';

          if (lost) {
            if (collision) {
              computedMainColor = chartColors.lostCollisionMain;
              computedBorderColor = chartColors.lostCollisionBorder;
            } else {
              computedMainColor = chartColors.lostNoCollisionMain;
              computedBorderColor = chartColors.lostNoCollisionBorder;
            }
          } else {
            if (collision) {
              computedMainColor = chartColors.notLostCollisionMain;
              computedBorderColor = chartColors.notLostCollisionBorder;
            } else {
              computedMainColor = chartColors.notLostNoCollisionMain;
              computedBorderColor = chartColors.notLostNoCollisionBorder;
            }
          }


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
              fill: computedBorderColor,
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
              fill: computedMainColor,
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
              fill: computedBorderColor,
              opacity: currentOpacity,
            },
          };

          return {
            type: 'group',
            children: [
              leftRect,
              middleRect,
              rightRect,
            ]
          };
        },
        encode: {
          x: 'time',
          y: 'frequency',
          tooltip: ['time', 'frequency', 'duration', 'collision', 'group', 'lost', 'type'],
        },
        // Filter data based on group selection
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
    // Wait for the simulation to finish
    const results = await waitUntilSimulationFinished(props.simulationId);
    data.value = transformSimulationData(results);
    updateChart();
  } catch (error) {
    console.error('Error fetching simulation data:', error);
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

// Watch graph colors and update the chart
watch(chartColors, () => {
  updateChart();
});

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

.checkboxes-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

.checkboxes {
  display: flex;
  flex-wrap: nowrap;
  gap: 15px;
  align-items: center;
  overflow-x: auto;
  white-space: nowrap;
}
</style>
