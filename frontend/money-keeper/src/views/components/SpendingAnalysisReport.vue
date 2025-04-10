<script setup>
import { ref } from "vue";
import VueApexCharts from "vue3-apexcharts";

const chartOptions = ref({
  chart: {
    type: 'bar',
    height: 200,
    toolbar: {
      show: false
    }
  },
  plotOptions: {
    bar: {
      columnWidth: '50%'
    }
  },
  colors: ['#2196f3'],
  xaxis: {
    categories: ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '12'],
  },
  yaxis: {
    max: 14
  }
});

const chartSeries = ref([
  {
    name: 'Chi tiêu',
    data: [0, 6, 13, 5, 0, 0, 0, 0, 0, 0, 0]
  }
]);
</script>

<template>
  <v-card class="pa-4">
    <div class="d-flex justify-space-between align-center mb-4">
      <h3>Phân tích chi tiêu</h3>
      <div class="text-caption">11/2025 - 12/2025</div>
    </div>
    <apexchart
      type="bar"
      :options="chartOptions"
      :series="chartSeries"
      height="200"
    ></apexchart>
  </v-card>
</template> 