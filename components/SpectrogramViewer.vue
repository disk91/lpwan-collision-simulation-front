<template>
  <div>
    <canvas ref="waterfallCanvas"></canvas>
  </div>
</template>

<script>
import { Waterfall } from '~/public/libs/Spectrogram-2v01.js';

export default {
  name: 'SpectrogramViewer',
  data() {
    return {
      waterfall: null,
      width: 400,
      height: 400,
      updateInterval: null,
      dataObj: null,
      dataResolution: 8, // Définir la résolution des données à 8
    };
  },
  mounted() {
    this.initWaterfall();
    this.startUpdating();
  },
  beforeUnmount() {
    clearInterval(this.updateInterval);
  },
  methods: {
    initWaterfall() {
      const canvas = this.$refs.waterfallCanvas;
      const direction = 'down';
      const options = {
        lineRate: 100,
        colormap: this.generateColormap(),
      };

      this.dataObj = this.getDynamicDataBuffer();
      this.waterfall = new Waterfall(this.dataObj, this.width, this.height, direction, options);
      canvas.width = this.width;
      canvas.height = this.height;

      this.waterfall.start();
    },
    generateColormap() {
      return Array.from({ length: 256 }, (_, i) => [i, 0, 255 - i, 255]);
    },
    getDynamicDataBuffer() {
      const bufferAry = [];
      bufferAry[0] = new Uint8Array(this.width);
      bufferAry[1] = new Uint8Array(this.width);
      const resolution = this.dataResolution; // Récupérer la résolution définie
      const stretchFactor = Math.ceil(this.width / resolution); // Facteur d'étirement

      const genDynamicData = () => {
        const smallBuffer = new Uint8Array(resolution); // Buffer de taille réduite

        // Remplir le petit buffer de données aléatoires
        for (let i = 0; i < resolution; i++) {
          smallBuffer[i] = Math.floor(Math.random() * 256);
        }

        // Étirer le petit buffer pour remplir bufferAry[1]
        for (let i = 0; i < resolution; i++) {
          const value = smallBuffer[i];
          for (let j = 0; j < stretchFactor; j++) {
            const index = i * stretchFactor + j;
            if (index < this.width) { // Vérifier pour ne pas dépasser la largeur
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
    },
    startUpdating() {
      this.updateInterval = setInterval(() => {
        this.waterfall.newLine();
        this.redrawCanvas();
      }, 10);
    },
    redrawCanvas() {
      const canvas = this.$refs.waterfallCanvas;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(this.waterfall.offScreenCvs, 0, 0);
    },
  },
};
</script>

<style scoped>
canvas {
  border: 1px solid black;
}
</style>