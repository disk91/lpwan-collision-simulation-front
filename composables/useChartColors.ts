// ~/composables/useChartColors.ts
import { reactive } from 'vue';

export const chartColors = reactive({
  lostCollisionMain: "#FFA07A",
  lostCollisionBorder: "#8B0000",
  lostNoCollisionMain: "#A9A9A9",
  lostNoCollisionBorder: "#000000",
  notLostCollisionMain: "#EE82EE",
  notLostCollisionBorder: "#9400D3",
  notLostNoCollisionMain: "#90EE90",
  notLostNoCollisionBorder: "#006400"
});
