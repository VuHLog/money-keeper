<script setup>
import { ref } from "vue";

const selectedPeriod = ref('Tháng này');
const timePeriods = ref([
  'Hôm nay',
  'Tuần này',
  'Tháng này',
  'Quý này',
  'Năm nay'
]);

const revenueExpenseData = ref({
  revenue: 11400,
  expense: 1007,
  balance: 10393
});

const chartOptions = ref({
  chart: {
    type: 'bar',
    toolbar: {
      show: false
    },
    sparkline: {
      enabled: true
    }
  },
  colors: ['#1867C0', '#FF1744'],
  plotOptions: {
    bar: {
      horizontal: false,
      columnWidth: '40',
      borderRadius: 4,
      borderRadiusApplication: 'end',
      borderRadiusWhenStacked: 'last'
    }
  },
  grid: {
    show: false
  },
  dataLabels: {
    enabled: false
  },
  xaxis: {
    labels: {
      show: false
    },
    axisBorder: {
      show: false
    },
    axisTicks: {
      show: false
    }
  },
  yaxis: {
    labels: {
      show: false
    }
  },
  tooltip: {
    enabled: false
  }
});

const chartSeries = ref([{
  name: 'Thu chi',
  data: [revenueExpenseData.value.revenue, revenueExpenseData.value.expense]
}]);

const donutChartOptions = ref({
  chart: {
    type: 'donut',
  },
  colors: ['#ffc107', '#f44336'],
  labels: ['Không xác định (99,2%)', 'Ăn uống (0,7%)'],
  legend: {
    position: 'bottom'
  },
  plotOptions: {
    pie: {
      donut: {
        size: '70%'
      }
    }
  }
});

const donutSeries = ref([99.2, 0.7]);
</script>

<template>
  <v-card class="pa-4">
    <div class="d-flex justify-space-between align-center mb-4">
      <h3>Tình hình thu chi</h3>
      <div class="d-flex align-center">
        <v-menu location="bottom end" offset="5">
          <template v-slot:activator="{ props }">
            <v-btn
              variant="text"
              size="small"
              v-bind="props"
              class="text-caption text-grey"
            >
              {{ selectedPeriod }}
              <font-awesome-icon :icon="['fas', 'chevron-down']" class="ml-1" />
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="period in timePeriods"
              :key="period"
              :value="period"
              @click="selectedPeriod = period"
            >
              <v-list-item-title>{{ period }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu location="bottom end">
          <template v-slot:activator="{ props }">
            <v-btn
              variant="text"
              size="small"
              v-bind="props"
              class="px-1"
            >
              <font-awesome-icon :icon="['fas', 'ellipsis-vertical']" />
            </v-btn>
          </template>
          <v-list>
            <v-list-item
              v-for="(item, index) in ['Xem lịch sử ghi chép', 'Lọc theo thời gian']"
              :key="index"
              @click="() => {}"
            >
              <v-list-item-title>{{ item }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </div>
    
    <div class="d-flex align-items-center gap-4 mb-6">
      <div style="width: 40%;">
        <apexchart
          type="bar"
          height="120"
          :options="chartOptions"
          :series="chartSeries"
        ></apexchart>
      </div>

      <div class="d-flex flex-column gap-4" style="width: 60%;">
        <div class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <div class="rounded-circle mr-2 bg-primary" style="width: 10px; height: 10px"></div>
            <span>Thu</span>
          </div>
          <div class="text-h6 font-weight-bold text-primary">{{ revenueExpenseData.revenue }}đ</div>
        </div>
        <div class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <div class="rounded-circle mr-2 bg-red-accent-3" style="width: 10px; height: 10px"></div>
            <span>Chi</span>
          </div>
          <div class="text-h6 font-weight-bold text-red-accent-3">{{ revenueExpenseData.expense }}đ</div>
        </div>
        <div class="d-flex align-center justify-space-between">
          <div class="text-grey">Số dư</div>
          <div class="text-h6 font-weight-bold">{{ revenueExpenseData.balance }}đ</div>
        </div>
      </div>
    </div>

    <apexchart
      type="donut"
      :options="donutChartOptions"
      :series="donutSeries"
      height="200"
    ></apexchart>
  </v-card>
</template> 