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
    };
  },
  mounted() {
    this.initWaterfall();

  },
  methods: {
    initWaterfall() {
      const canvas = this.$refs.waterfallCanvas;
      const width = 800;
      const height = 400;
      const direction = 'down';
      const options = {
        lineRate: 30,
        colormap: this.generateColormap(),
      };

      const buffer = new Uint8Array(width * height);
      for (let i = 0; i < buffer.length; i++) {
        buffer[i] = Math.floor(Math.random() * 256);
      }

      this.waterfall = new Waterfall({ buffer }, width, height, direction, options);
      canvas.width = width;
      canvas.height = height;
      const ctx = canvas.getContext('2d');
      ctx.drawImage(this.waterfall.offScreenCvs, 0, 0);

      this.waterfall.start();
    },
    generateColormap() {
      const colormap = [];
      for (let i = 0; i < 256; i++) {
        colormap.push([i, 0, 255 - i, 255]);
      }
      return colormap;
    },
  },
};
</script>

<style scoped>
canvas {
  border: 1px solid black;
}
</style>