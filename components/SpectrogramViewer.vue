<template>
  <div>
    <canvas ref="waterfallCanvas"></canvas>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue';
import { useWebSocketHandler } from '@/composables/useWebSocket';
import { Waterfall } from '~/public/libs/Spectrogram-2v01.js';

const { status } = useWebSocketHandler();

const waterfallCanvas = ref(null);
const waterfall = ref(null);
const width = 400;
const height = 400;
const updateInterval = ref(null);
const dataResolution = 8;
let dataObj = null;

const initWaterfall = () => {
  if (!waterfallCanvas.value) return;

  const direction = 'down';
  const options = {
    lineRate: 100,
    colormap: generateColormap(),
  };

  dataObj = getDynamicDataBuffer();
  waterfall.value = new Waterfall(dataObj, width, height, direction, options);
  waterfallCanvas.value.width = width;
  waterfallCanvas.value.height = height;

  waterfall.value.start();
};

const generateColormap = () => {
  return Array.from({ length: 256 }, (_, i) => [i, 0, 255 - i, 255]);
};

const getDynamicDataBuffer = () => {
  const bufferAry = [];
  bufferAry[0] = new Uint8Array(width);
  bufferAry[1] = new Uint8Array(width);
  const resolution = dataResolution;
  const stretchFactor = Math.ceil(width / resolution);

  const genDynamicData = () => {
    const smallBuffer = new Uint8Array(resolution);

    for (let i = 0; i < resolution; i++) {
      smallBuffer[i] = Math.floor(Math.random() * 256);
    }

    for (let i = 0; i < resolution; i++) {
      const value = smallBuffer[i];
      for (let j = 0; j < stretchFactor; j++) {
        const index = i * stretchFactor + j;
        if (index < width) {
          bufferAry[1][index] = value;
        }
      }
    }

    let tmpBuf = bufferAry[0];
    bufferAry[0] = bufferAry[1];
    bufferAry[1] = tmpBuf;

    setTimeout(genDynamicData, 100);
  };

  genDynamicData();
  return { buffer: bufferAry[0] };
};

const startUpdating = () => {
  console.log("Démarrage du spectrogramme")
  if (updateInterval.value) return;
  updateInterval.value = setInterval(() => {
    waterfall.value.newLine();
    redrawCanvas();
  }, 10);
};

const stopUpdating = () => {
  if (updateInterval.value) {
    clearInterval(updateInterval.value);
    updateInterval.value = null;
  }
};

const redrawCanvas = () => {
  if (!waterfallCanvas.value) return;
  const ctx = waterfallCanvas.value.getContext('2d');
  ctx.drawImage(waterfall.value.offScreenCvs, 0, 0);
};

watch(status, (newStatus) => {
  if (newStatus === 'OPEN') {
    console.log("Démarrage du spectrogramme")
    startUpdating();
  } else {
    console.log("Arrêt du spectrogramme")
    stopUpdating();
  }
});

onMounted(() => {
  initWaterfall();
});

onBeforeUnmount(() => {
  stopUpdating();
});
</script>

<style scoped>
canvas {
  border: 1px solid black;
}
</style>
