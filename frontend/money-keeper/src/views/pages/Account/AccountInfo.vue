<script setup>
import { ref, onMounted, watch } from "vue";
import { TimeOption } from "@/constants/TimeOption.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment.js";
import { useRoute } from "vue-router";
import { formatCurrency } from "@/utils/format.js";
import { getWeekday, formatDate } from "@/utils/DateUtil.js";
import { TransferType, TransferTypeIcon } from "@/constants/TransferType.js";

const dictionaryBucketPaymentStore = useDictionaryBucketPaymentStore();
const route = useRoute();
const bucketPaymentId = ref(route.params.accountId);
const timeOptions = ref(TimeOption);
const timeOption = ref("Toàn bộ thời gian");
const startDate = ref("");
const endDate = ref("");

const bucketPayment = ref();
const transactionHistory = ref([]);
const totalExpense = ref();
const totalRevenue = ref();

onMounted(async () => {
  bucketPayment.value = await dictionaryBucketPaymentStore.getBucketPaymentById(
    bucketPaymentId.value
  );
  await getData();
});

async function getData() {
  totalExpense.value =
    await dictionaryBucketPaymentStore.getTotalExpenseByBucketPaymentId(
      bucketPaymentId.value,
      timeOption.value,
      startDate.value,
      endDate.value
    );
  totalRevenue.value =
    await dictionaryBucketPaymentStore.getTotalRevenueByBucketPaymentId(
      bucketPaymentId.value,
      timeOption.value,
      startDate.value,
      endDate.value 
    );
  transactionHistory.value =
    await dictionaryBucketPaymentStore.getTransactionHistoryByBucketPaymentId(
      bucketPaymentId.value,
      timeOption.value,
      startDate.value,
      endDate.value
    );
  convertTransactionHistory();
}

watch(timeOption, () => {
  if (timeOption.value !== 'Tùy chọn') {
    startDate.value = '';
    endDate.value = '';
    getData();
  }
});

watch(startDate, () => {
  getData();
});

watch(endDate, () => {
  getData();
});

function convertTransactionHistory() {
  transactionHistory.value = transactionHistory.value.reduce((acc, item) => {
    // Lấy ngày (YYYY-MM-DD) từ chuỗi date
    const dateKey = item.date.split(" ")[0];

    // Kiểm tra xem ngày này đã tồn tại trong kết quả chưa
    const existingGroup = acc.find((group) => group.date === dateKey);

    if (existingGroup) {
      existingGroup.history.push(item);
    } else {
      acc.push({ date: dateKey, history: [item] });
    }

    return acc;
  }, []);
}

function sumHistoryByDate(date, type) {
  const itemTransaction = transactionHistory.value.find((item) => item.date === date);
  if (itemTransaction) {
    return itemTransaction.history.reduce((acc, item) => {
      if (item.type === type) {
        return acc + item.amount;
      }
      return acc;
    }, 0);
  }
  return 0;
}
</script>

<template>
  <div class="account-info">
    <!-- Header Section -->
    <v-container fluid class="header-section pa-0">
      <v-row align="center" class="ma-0">
        <v-col cols="auto" class="pa-0">
          <v-btn
            icon
            variant="text"
            class="back-button text-primary"
            to="/account"
          >
            <font-awesome-icon :icon="['fas', 'angle-left']" style="font-size: 32px" />
          </v-btn>
        </v-col>
        <v-col>
          <h1 class="text-h4 text-center text-primary mb-0">Chi tiết ví</h1>
        </v-col>
      </v-row>
    </v-container>

    <!-- Filter Section -->
    <v-container fluid class="filter-section">
      <v-row align="center" class="ma-0">
        <v-col cols="3">
          <v-select
            v-model="timeOption"
            label="Chọn thời gian"
            :items="timeOptions"
            item-title="option"
            item-value="option"
            variant="outlined"
            density="comfortable"
            hide-details
            class="custom-select"
          >
          </v-select>
        </v-col>
        <template v-if="timeOption === 'Tùy chọn'">
          <v-col cols="6">
            <el-date-picker class="mr-2" v-model="startDate" type="date" placeholder="Ngày bắt đầu" size="large" />
            <el-date-picker v-model="endDate" type="date" placeholder="Ngày kết thúc" size="large" />
          </v-col>
        </template>
      </v-row>
    </v-container>

    <!-- Summary Cards -->
    <v-container fluid class="summary-section">
      <v-row no-gutters>
        <v-col :cols="timeOption === 'Toàn bộ thời gian' ? 4 : 6">
          <v-card class="summary-card revenue-card" elevation="2">
            <v-card-item>
              <v-card-title class="text-subtitle-1 text-medium-emphasis">
                Tổng thu
              </v-card-title>
              <v-card-text class="text-h5 text-primary">
                {{ formatCurrency(totalRevenue) }}
              </v-card-text>
            </v-card-item>
          </v-card>
        </v-col>
        <v-col :cols="timeOption === 'Toàn bộ thời gian' ? 4 : 6">
          <v-card class="summary-card expense-card" elevation="2">
            <v-card-item>
              <v-card-title class="text-subtitle-1 text-medium-emphasis">
                Tổng chi
              </v-card-title>
              <v-card-text class="text-h5 text-red-accent-3">
                {{ formatCurrency(totalExpense) }}
              </v-card-text>
            </v-card-item>
          </v-card>
        </v-col>
        <v-col cols="4" v-if="timeOption === 'Toàn bộ thời gian'">
          <v-card class="summary-card balance-card" elevation="2">
            <v-card-item>
              <v-card-title class="text-subtitle-1 text-medium-emphasis">
                Số dư hiện tại
              </v-card-title>
              <v-card-text class="text-h5 text-grey-color">
                {{ formatCurrency(bucketPayment?.balance) }}
              </v-card-text>
            </v-card-item>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- Transaction History -->
    <v-container fluid class="transaction-section">
      <v-row>
        <template v-for="item in transactionHistory" :key="item.date">
          <v-col cols="6">
            <v-card class="history-card" elevation="2">
              <v-card-item>
                <div class="d-flex justify-space-between align-center mb-4">
                  <div class="d-flex align-center">
                    <span class="text-h3 font-weight-bold text-primary mr-4 date-border">
                      {{ item.date.split("-")[2] }}
                    </span>
                    <div>
                      <div class="text-subtitle-1 font-weight-medium">
                        {{ getWeekday(item.date) }}
                      </div>
                      <div class="text-caption text-medium-emphasis">
                        {{ item.date.split("-")[1] + "/" + item.date.split("-")[0] }}
                      </div>
                    </div>
                  </div>
                  <div class="text-right">
                    <div class="text-red-accent-3">{{ formatCurrency(sumHistoryByDate(item.date, 'expense')) }}</div>
                    <div class="text-primary">{{ formatCurrency(sumHistoryByDate(item.date, 'revenue')) }}</div>
                  </div>
                </div>

                <v-timeline class="ml-5 border-t-sm" density="compact" align="start" side="end">
                  <v-timeline-item v-for="history in item.history" :key="history.id" width="100%" size="small">
                    <template v-slot:icon>
                      <v-icon>
                        <img :src="history.iconUrl" v-if="history.transferType === TransferType.NORMAL"
                          alt="icon" style="width: 40px; height: 40px" />
                        <img :src="TransferTypeIcon" v-if="history.transferType === TransferType.TRANSFER"
                          alt="icon" style="width: 40px; height: 40px" />
                      </v-icon>
                    </template>
                    <router-link :to="{path:history.type === 'expense' ?
                    (history.transferType === TransferType.NORMAL ? '/expense/' + history.id: '/transfer/info/'+ history.id)
                    : (history.transferType === TransferType.NORMAL ? '/revenue/' + history.id : '/transfer/info/'+ history.id),
                    query: { redirect: route.fullPath }}"
                    class="text-decoration-none">
                      <div class="d-flex justify-space-between">
                        <div class="d-flex flex-column">
                          <div>
                            <span class="text-grey-color font-weight-bold mr-2" v-if="history.transferType === TransferType.NORMAL">
                              {{history.categoryName}}
                            </span>
                            <span class="text-grey-color font-weight-bold mr-2" v-if="history.transferType === TransferType.TRANSFER">
                              Chuyển khoản từ tài khoản {{ history.accountName }}
                            </span>
                            <span class="text-grey-color">{{ history.date.split(' ')[1] }}</span>
                          </div>
                          <span class="text-grey-darken-1 font-italic">
                            {{history.interpretation}}
                          </span>
                        </div>
                        <div class="d-flex align-end justify-center flex-column text-right" style="min-width: 120px;">
                          <span :class="history.type === 'expense' ? 'text-red-accent-3' : 'text-blue-accent-3'">{{
                            history.amount < 0 ? "(-" + formatCurrency(history.amount) + ")" :
                              formatCurrency(history.amount) }}
                          </span>
                          <span class="text-grey-lighten-1">{{ formatCurrency(history.currentBalance) }}</span>
                        </div>
                      </div>
                    </router-link>
                  </v-timeline-item>
                </v-timeline>
              </v-card-item>
            </v-card>
          </v-col>
        </template>
      </v-row>
    </v-container>
  </div>
</template>

<style lang="scss" scoped>
.account-info {
  background-color: rgb(var(--v-theme-background));
  min-height: 100vh;

  .header-section {
    background-color: rgb(var(--v-theme-surface));
    border-bottom: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
    padding: 16px 0;

    .back-button {
      transition: transform 0.3s ease;
      &:hover {
        transform: translateX(-4px);
      }
    }
  }

  .filter-section {
    padding: 24px 0;

    .custom-select {
      :deep(.v-field) {
        border-radius: 12px;
      }
    }

    .custom-input {
      :deep(.v-field) {
        border-radius: 12px;
      }
    }
  }

  .summary-section {
    padding: 0 0 24px;

    .summary-card {
      height: 100%;
      border-radius: 16px;
      transition: transform 0.3s ease;

      &:hover {
        transform: translateY(-4px);
      }

      .v-card-item {
        padding: 24px;
      }
    }
  }

  .transaction-section {
    padding: 0 0 24px;

    .history-card {
      border-radius: 16px;
      margin-bottom: 24px;

      .v-card-item {
        padding: 24px;
      }

      .date-border {
        position: relative;
        padding-right: 16px;
        
        &::after {
          content: '';
          position: absolute;
          right: 0;
          top: 50%;
          transform: translateY(-50%);
          height: 70%;
          width: 1px;
          background-color: rgba(var(--v-theme-primary), 0.2);
        }
      }

      .custom-timeline {
        :deep(.v-timeline-item__dot) {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background-color: rgb(var(--v-theme-primary));
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        :deep(.v-timeline-item__inner-dot) {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background-color: rgb(var(--v-theme-primary));
        }

        :deep(.v-timeline-item__body) {
          margin-left: 48px;
        }
      }

      .timeline-icon {
        border: 2px solid rgb(var(--v-theme-primary));
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .transaction-item {
        background-color: transparent;
        border-radius: 12px;
        padding: 8px;
        transition: background-color 0.3s ease;

        &:hover {
          background-color: rgba(var(--v-theme-primary), 0.05);
        }

        .text-right {
          min-width: 120px;
        }
      }
    }
  }
}
</style>
