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
      width: 800,
      height: 400,
      updateInterval: null,
      dataObj: null,
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
      // Pas besoin de dessiner ici initialement, la mise à jour le fera
      // const ctx = canvas.getContext('2d');
      // ctx.drawImage(this.waterfall.offScreenCvs, 0, 0);

      this.waterfall.start();
    },
    generateColormap() {
      return Array.from({ length: 256 }, (_, i) => [i, 0, 255 - i, 255]);
    },
    getDynamicDataBuffer() {
      const bufferAry = [];
      bufferAry[0] = new Uint8Array(this.width);
      bufferAry[1] = new Uint8Array(this.width);

      const genDynamicData = () => {
        for (let i = 0; i < this.width; i++) {
          bufferAry[1][i] = Math.floor(Math.random() * 256);
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
        this.redrawCanvas(); // Ajout de la ligne pour redessiner
      }, 10);
    },
    redrawCanvas() { // Nouvelle méthode pour redessiner le canvas
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