<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { formatCurrency } from '@/utils/Format';
import { formatDateStringToDate } from '@/utils/DateUtil';
import { useExpenseLimitStore } from '@/store/ExpenseLimitStore';
import { ExpenseLimitLoopTime } from "@/constants/ExpenseLimitLoopTime.js";
import { useExpenseRegularStore } from '@/store/ExpenseRegularStore';
import ExpenseLimitDetail from '@/views/pages/ExpenseLimit/ExpenseLimitDetail.vue';

const route = useRoute();
const expenseLimitId = route.params.expenseLimitId;
const expenseLimitStore = useExpenseLimitStore();
const expenseRegularStore = useExpenseRegularStore();
const expenseLimit = ref(null);
const totalExpense = ref(0);
const actualSpending = ref(44);
const recommendedSpending = ref(21);
const expectedSpending = ref(1331);
const totalExpenseList = ref(null);
const showDetailDialog = ref(false);

onMounted(async () => {
  await getExpenseLimit();
  let currentStartDate = getCurrentStartDate.value;
  let endDate = getEndDate(currentStartDate, expenseLimit.value?.repeatTime, expenseLimit.value?.endDate);
  await getTotalExpenseList(currentStartDate + " 00:00:00", endDate + " 23:59:59");
});

const getExpenseLimit = async () => {
  expenseLimit.value = await expenseLimitStore.getExpenseLimitByExpenseLimitId("5cb59449-ca40-4836-bf2c-338534fddd96");
};

const getTotalExpenseList = async (startDate, endDate) => {
  totalExpenseList.value = await expenseRegularStore.getTotalExpenseByExpenseLimit("5cb59449-ca40-4836-bf2c-338534fddd96", startDate, endDate);
};

// Tính ngày bắt đầu dựa trên thời điểm hiện tại
const getCurrentStartDate = computed(() => {
  if (!expenseLimit.value?.startDate) return null;
  
  const originalStartDate = new Date(expenseLimit.value.startDate);
  const today = new Date();
  let currentStartDate = new Date(originalStartDate);
  let nextEndDate;

  while (true) {
    switch (expenseLimit.value.repeatTime) {
      case "Hàng ngày":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setDate(nextEndDate.getDate() + 1);
        break;
      case "Hàng tuần":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setDate(nextEndDate.getDate() + 7);
        break;
      case "Hàng tháng":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setMonth(nextEndDate.getMonth() + 1);
        break;
      case "Hàng quý":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setMonth(nextEndDate.getMonth() + 3);
        break;
      case "Hàng năm":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setFullYear(nextEndDate.getFullYear() + 1);
        break;
      default:
        return expenseLimit.value.startDate;
    }

    // Nếu thời điểm hiện tại nằm trong khoảng này
    if (today >= currentStartDate && today < nextEndDate) {
      return currentStartDate.toISOString().split('T')[0];
    }

    // Nếu thời điểm hiện tại chưa đến khoảng này, trả về khoảng đầu tiên
    if (today < currentStartDate) {
      return currentStartDate.toISOString().split('T')[0];
    }

    // Di chuyển đến khoảng tiếp theo
    currentStartDate = new Date(nextEndDate);
  }
});

const getEndDate = (startDate, repeatTime, endDate) => {
  if (!startDate) return null;
  if (repeatTime === "Không lặp lại") return endDate;
  
  const originalStartDate = new Date(startDate);
  const today = new Date();
  let currentStartDate = new Date(originalStartDate);
  let nextEndDate;

  // Tìm khoảng thời gian phù hợp bao gồm thời điểm hiện tại
  while (true) {
    switch (repeatTime) {
      case "Hàng ngày":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setDate(nextEndDate.getDate() + 1);
        break;
      case "Hàng tuần":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setDate(nextEndDate.getDate() + 7);
        break;
      case "Hàng tháng":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setMonth(nextEndDate.getMonth() + 1);
        break;
      case "Hàng quý":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setMonth(nextEndDate.getMonth() + 3);
        break;
      case "Hàng năm":
        nextEndDate = new Date(currentStartDate);
        nextEndDate.setFullYear(nextEndDate.getFullYear() + 1);
        break;
    }

    // Nếu thời điểm hiện tại nằm trong khoảng này
    if (today >= currentStartDate && today < nextEndDate) {
      return nextEndDate.toISOString().split('T')[0];
    }

    // Nếu thời điểm hiện tại chưa đến khoảng này, trả về khoảng đầu tiên
    if (today < currentStartDate) {
      return nextEndDate.toISOString().split('T')[0];
    }

    // Di chuyển đến khoảng tiếp theo
    currentStartDate = new Date(nextEndDate);
  }
};

// Tính số ngày còn lại
const remainingDays = computed(() => {
  if (!expenseLimit.value?.startDate) return 0;
  const today = new Date();
  const end = new Date(getEndDate(
    expenseLimit.value.startDate, 
    expenseLimit.value.repeatTime,
    expenseLimit.value.endDate
  ));
  const diffTime = end - today;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
});

// Thêm computed property để xử lý hiển thị ngày tháng
const formatDateRange = computed(() => {
  if (!expenseLimit.value?.startDate) return '';
  
  const startDate = new Date(getCurrentStartDate.value);
  const endDate = new Date(getEndDate(
    getCurrentStartDate.value,
    expenseLimit.value?.repeatTime,
    expenseLimit.value?.endDate
  ));

  // Nếu cùng năm, chỉ hiển thị ngày/tháng
  const showYear = startDate.getFullYear() !== endDate.getFullYear();
  return `${formatDateStringToDate(startDate.toISOString(), showYear)} - ${formatDateStringToDate(endDate.toISOString(), showYear)}`;
});

// Tính thực tế chi tiêu trung bình mỗi ngày
const actualDailySpending = computed(() => {
  if (!totalExpense.value || !getCurrentStartDate.value) return 0;
  
  const startDate = new Date(getCurrentStartDate.value);
  const today = new Date();
  const diffTime = today - startDate;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  
  // Nếu là ngày đầu tiên, trả về tổng chi tiêu
  if (diffDays <= 0) return totalExpense.value;
  
  return Math.round(totalExpense.value / diffDays);
});

// Tính bội chi (số tiền còn lại trong hạn mức)
const remainingBudget = computed(() => {
  if (!expenseLimit.value?.amount || !totalExpense.value) return 0;
  return expenseLimit.value.amount - totalExpense.value;
});

//#region Chart
const chartOptions = computed(() => ({
  chart: {
    type: 'area',
    toolbar: {
      show: false
    },
    sparkline: {
      enabled: false
    },
    zoom: {
      enabled: true,
      type: 'x',
      autoScaleYaxis: true
    },
    animations: {
      enabled: true
    },
    padding: {
      left: 0,
      right: 0,
      top: 0,
      bottom: 0
    }
  },
  title: {
    text: 'Biểu đồ chi tiêu',
    align: 'center',
    style: {
      fontSize: '24px',
      fontWeight: 'bold'
    }
  },
  stroke: {
    curve: 'smooth',
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
    min: expenseLimit.value?.startDate ? new Date(getCurrentStartDate.value).getTime() : undefined,
    max: expenseLimit.value?.startDate ? new Date(getEndDate(
      getCurrentStartDate.value,
      expenseLimit.value?.repeatTime,
      expenseLimit.value?.endDate
    )).getTime() : undefined,
    labels: {
      format: 'dd/MM',
      datetimeUTC: false,
      style: {
        fontSize: '12px'
      },
      formatter: function(value) {
        const date = new Date(value);
        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        return `${day}/${month}`;
      }
    },
    tickAmount: 'dataPoints',
    tooltip: {
      formatter: function(value) {
        const date = new Date(value);
        const day = date.getDate().toString().padStart(2, '0');
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const year = date.getFullYear();
        return `${day}/${month}/${year}`;
      }
    }
  },
  yaxis: {
    labels: {
      formatter: (value) => {
        return formatCurrency(value);
      },
      style: {
        fontSize: '12px'
      }
    }
  },
  tooltip: {
    x: {
      formatter: function(value) {
        const date = new Date(value);
        return formatDateStringToDate(date.toISOString(), true); // Hiển thị cả năm trong tooltip
      }
    },
    y: {
      formatter: (value) => formatCurrency(value)
    }
  },
  grid: {
    show: true,
    padding: {
      left: 15,
      right: 15,
      top: 0,
      bottom: 0
    }
  },
  markers: {
    size: 4,
    strokeWidth: 0
  }
}));

const chartSeries = computed(() => {
  if (!expenseLimit.value?.startDate || !totalExpenseList.value) return [];
  
  // convert data from totalExpenseList to chart format
  const chartData = totalExpenseList.value.map(item => [
    new Date(item.expenseDate).getTime(),
    item.totalExpense
  ]);

  // add startDate if not exist
  const startDate = new Date(expenseLimit.value.startDate).getTime();
  if (!chartData.some(point => point[0] === startDate)) {
    chartData.push([startDate, 0]);
  }

  // add endDate if not exist
  const endDate = new Date(getEndDate(
    expenseLimit.value.startDate, 
    expenseLimit.value.repeatTime,
    expenseLimit.value.endDate
  ));
  
  // sort data by time
  chartData.sort((a, b) => a[0] - b[0]);

  // calculate cumulative sum
  let cumulativeSum = 0;
  const cumulativeData = chartData.map(point => {
    cumulativeSum += point[1];
    return [point[0], cumulativeSum];
  });
  totalExpense.value = cumulativeSum;

  // add endDate if not exist
  const lastDataPoint = cumulativeData[cumulativeData.length - 1];
  if (!lastDataPoint || new Date(lastDataPoint[0]).getTime() < endDate.getTime()) {
    cumulativeData.push([endDate.getTime(), lastDataPoint ? lastDataPoint[1] : 0]);
  }

  return [{
    name: 'Tổng chi tiêu',
    data: cumulativeData
  }];
});
//#endregion

function viewExpenseDetails() {
  showDetailDialog.value = true;
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
        <h2 class="text-h4">Hàng tháng</h2>
        <div class="text-right">
          <div class="text-subtitle-1 mb-2">Hạn mức chi tiêu</div>
          <div class="text-h4">{{ formatCurrency(expenseLimit?.amount) }}</div>
        </div>
      </div>

      <v-card class="mb-6 expense-card">
        <v-card-text>
          <div class="d-flex align-center mb-4" :class="expenseLimit?.startDate ? 'justify-space-between' : 'justify-start'">
            <div class="text-subtitle-1">
              {{ formatDateRange }}
            </div>
            <div class="text-right">
              <div class="text-subtitle-1">Còn lại: {{ remainingDays }} ngày</div>
              <div v-if="remainingBudget < 0" class="text-subtitle-1">
                <span class="text-grey-darken-1 mr-1">(Bội chi)</span> 
                <span class="text-red-accent-3">{{ formatCurrency(Math.abs(remainingBudget)) }}</span>
              </div>
              <div v-else class="text-subtitle-1">
                {{ formatCurrency(remainingBudget) }}
              </div>
            </div>
          </div>

          <div class="chart-container">
            <apexchart
              type="area"
              height="200"
              width="100%"
              :options="chartOptions"
              :series="chartSeries"
            ></apexchart>
          </div>

          <div class="d-flex justify-space-between mt-6">
            <div class="text-center stat-box">
              <div class="text-subtitle-1 mb-1 d-flex align-center">
                <span class="mr-1">Thực tế chi tiêu</span>
                <div class="cursor-pointer">
                  <font-awesome-icon :icon="['fas', 'circle-question']" />
                  <v-tooltip activator="parent" location="bottom">
                    <p>Số tiền đã chi</p>
                    <p style="border-top: 1px solid #ffffff;">Khoảng thời gian chi tiêu</p>
                  </v-tooltip>
                </div>
              </div>
              <div class="text-h6 primary--text">{{ formatCurrency(totalExpense) }}/ngày</div>
            </div>
            <div class="text-center stat-box">
              <div class="text-subtitle-1 mb-1 d-flex align-center">
                <span class="mr-1">Nên chi</span>
                <div class="cursor-pointer">
                  <font-awesome-icon :icon="['fas', 'circle-question']" />
                  <v-tooltip activator="parent" location="bottom">
                    <p>Số tiền còn lại</p>
                    <p style="border-top: 1px solid #ffffff;">Số ngày còn lại</p>
                  </v-tooltip>
                </div>
              </div>
              <div class="text-h6">{{ formatCurrency(recommendedSpending) }}/ngày</div>
            </div>
            <div class="text-center stat-box">
              <div class="text-subtitle-1 mb-1 d-flex align-center">
                <span class="mr-1">Dự kiến chi tiêu</span>
                <div class="cursor-pointer">
                  <font-awesome-icon :icon="['fas', 'circle-question']" />
                  <v-tooltip activator="parent" location="bottom">
                    <p>Thực tế chi tiêu * Số ngày còn lại + Số tiền đã chi</p>
                  </v-tooltip>
                </div>
              </div>
              <div class="text-h6 error--text">{{ formatCurrency(expectedSpending) }}</div>
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

  <v-dialog v-model="showDetailDialog" width="800">
    <ExpenseLimitDetail />
  </v-dialog>
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
  margin: 0;
  position: relative;
  z-index: 2;
  width: 100%;
}

@media (max-width: 1400px) {
  .decoration-left, .decoration-right {
    display: none;
  }
}
</style>