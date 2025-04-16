<script setup>
import { ref, onMounted, computed } from 'vue';

const userName = ref('Long');
const limitAmount = ref(1000);
const startDate = ref('01/04');
const endDate = ref('30/04');
const actualSpending = ref(44);
const recommendedSpending = ref(21);
const expectedSpending = ref(1331);

// Tính số ngày còn lại
const remainingDays = computed(() => {
  const today = new Date();
  const end = new Date(2024, 3, 30); // Tháng bắt đầu từ 0
  const diffTime = Math.abs(end - today);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
});

const chartOptions = {
  chart: {
    type: 'area',
    toolbar: {
      show: false
    },
    sparkline: {
      enabled: false
    }
  },
  stroke: {
    curve: 'straight',
    width: 2
  },
  fill: {
    type: 'gradient',
    gradient: {
      shadeIntensity: 1,
      opacityFrom: 0.7,
      opacityTo: 0.3,
      stops: [0, 90, 100]
    }
  },
  colors: ['#1E88E5'],
  xaxis: {
    type: 'datetime',
    labels: {
      format: 'dd/MM',
      style: {
        fontSize: '12px'
      }
    }
  },
  yaxis: {
    labels: {
      formatter: (value) => {
        return `${value}N`;
      },
      style: {
        fontSize: '12px'
      }
    }
  },
  tooltip: {
    x: {
      format: 'dd/MM/yyyy'
    }
  },
  grid: {
    padding: {
      left: 0,
      right: 0
    }
  }
};

const chartSeries = [{
  name: 'Chi tiêu',
  data: [
    [new Date('2024-04-01').getTime(), 300],
    [new Date('2024-04-10').getTime(), 1000],
    [new Date('2024-04-20').getTime(), 1000],
    [new Date('2024-04-30').getTime(), 1331]
  ]
}];

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN').format(value);
}

function viewExpenseDetails() {
  // Handle view details click
}
</script>

<template>
  <div class="expense-limit-container">
    <div class="decoration-left">
      <div class="decoration-circle primary"></div>
      <div class="decoration-circle secondary"></div>
      <div class="decoration-line"></div>
      <div class="decoration-icon">
        <font-awesome-icon :icon="['fas', 'chart-line']" size="2x" class="text-primary" />
      </div>
      <div class="decoration-icon">
        <font-awesome-icon :icon="['fas', 'wallet']" size="2x" class="text-secondary" />
      </div>
    </div>

    <div class="expense-limit-info pa-4">
      <div class="d-flex justify-space-between align-center mb-8">
        <h2 class="text-h4">Xin chào, {{ userName }}</h2>
        <div class="text-right">
          <div class="text-subtitle-1 mb-2">Hạn mức chi tiêu</div>
          <div class="text-h4">{{ formatCurrency(limitAmount) }} đ</div>
        </div>
      </div>

      <v-card class="mb-6 expense-card">
        <v-card-text>
          <div class="d-flex justify-space-between align-center mb-4">
            <div class="text-subtitle-1">{{ startDate }} - {{ endDate }}</div>
            <div class="text-subtitle-1">Còn lại: {{ remainingDays }} ngày</div>
          </div>

          <div class="chart-container">
            <apexchart
              type="area"
              height="200"
              :options="chartOptions"
              :series="chartSeries"
            ></apexchart>
          </div>

          <div class="d-flex justify-space-between mt-6">
            <div class="text-center stat-box">
              <div class="text-subtitle-1 mb-1">Thực tế chi tiêu</div>
              <div class="text-h6 primary--text">{{ actualSpending }} đ/ngày</div>
            </div>
            <div class="text-center stat-box">
              <div class="text-subtitle-1 mb-1">Nên chi</div>
              <div class="text-h6">{{ recommendedSpending }} đ/ngày</div>
            </div>
            <div class="text-center stat-box">
              <div class="text-subtitle-1 mb-1">Dự kiến chi</div>
              <div class="text-h6 error--text">{{ expectedSpending }} đ</div>
            </div>
          </div>
        </v-card-text>
      </v-card>

      <v-btn
        block
        color="primary"
        class="mt-4"
        @click="viewExpenseDetails"
        elevation="2"
      >
        Xem chi tiết khoản chi
        <font-awesome-icon :icon="['fas', 'arrow-right']" class="ml-2" />
      </v-btn>
    </div>

    <div class="decoration-right">
      <div class="decoration-circle error"></div>
      <div class="decoration-circle warning"></div>
      <div class="decoration-line"></div>
      <div class="decoration-icon">
        <font-awesome-icon :icon="['fas', 'piggy-bank']" size="2x" class="text-error" />
      </div>
      <div class="decoration-icon">
        <font-awesome-icon :icon="['fas', 'coins']" size="2x" class="text-warning" />
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.expense-limit-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding: 20px;
  min-height: 100vh;
  position: relative;
  background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.4) 100%);
}

.expense-limit-info {
  max-width: 800px;
  width: 100%;
  z-index: 2;
}

.expense-card {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.9) !important;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1) !important;
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-5px);
  }
}

.stat-box {
  padding: 16px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255, 255, 255, 0.8);
    transform: translateY(-3px);
  }
}

.decoration-left, .decoration-right {
  position: fixed;
  top: 50%;
  transform: translateY(-50%);
  width: 200px;
  height: 400px;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  align-items: center;
  z-index: 1;
}

.decoration-left {
  left: 40px;
}

.decoration-right {
  right: 40px;
}

.decoration-circle {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  opacity: 0.2;
  position: absolute;

  &.primary {
    background: var(--v-primary-base);
    top: 20%;
  }

  &.secondary {
    background: var(--v-secondary-base);
    bottom: 20%;
  }

  &.error {
    background: var(--v-error-base);
    top: 20%;
  }

  &.warning {
    background: var(--v-warning-base);
    bottom: 20%;
  }
}

.decoration-line {
  height: 70%;
  width: 2px;
  background: linear-gradient(to bottom, rgba(0,0,0,0.1), rgba(0,0,0,0.05));
}

.decoration-icon {
  background: rgba(255, 255, 255, 0.9);
  padding: 16px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    transform: scale(1.1) rotate(5deg);
  }
}

.chart-container {
  height: 200px;
  margin: 0 -16px;
  position: relative;
  z-index: 2;
}

@media (max-width: 1400px) {
  .decoration-left, .decoration-right {
    display: none;
  }
}
</style>