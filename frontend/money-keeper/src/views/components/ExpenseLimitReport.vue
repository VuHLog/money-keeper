<script setup>
import { ref, onMounted } from "vue";
import VRange from "@/components/VRange.vue";
import { useExpenseLimitStore } from "@/store/ExpenseLimitStore";
import { useReportStore } from "@/store/ReportStore";
import { formatCurrency } from "@/utils/Format";

const expenseLimitStore = useExpenseLimitStore();
const reportStore = useReportStore();

const spendingLimit = ref({
  name: "Long",
  avatar: "path/to/avatar.jpg",
  limit: 100000,
  current: 10000,
  daysLeft: 21,
  remaining: 9999993
});

const expenseLimit = ref({});

onMounted(async () => {
  expenseLimit.value = (await expenseLimitStore.getExpenseLimits())[0];
  expenseLimit.value.currentStartDate = expenseLimitStore.getCurrentStartDate(expenseLimit.value);
  expenseLimit.value.currentEndDate = expenseLimitStore.getEndDate(expenseLimit.value.currentStartDate, expenseLimit.value.repeatTime, expenseLimit.value.endDate);
  expenseLimit.value.remainingDays = expenseLimitStore.remainingDays(expenseLimit.value);
  expenseLimit.value.totalExpense = (await reportStore.getTotalExpenseByExpenseLimit(expenseLimit.value.id, expenseLimit.value.currentStartDate + " 00:00:00", expenseLimit.value.currentEndDate + " 23:59:59")).totalExpense || 0;
  expenseLimit.value.remainingBudget = expenseLimitStore.remainingBudget(expenseLimit.value);
  expenseLimit.value.progress = expenseLimitStore.calculateProgress(expenseLimit.value);
  expenseLimit.value.isOverBudget = expenseLimit.value.remainingBudget < 0;
});
</script>

<template>
  <v-card class="pa-4">
    <h3 class="mb-4">Hạn mức chi</h3>
    <div class="d-flex align-start">
      <div class="flex-grow-1">
        <div class="d-flex justify-space-between">
          <div>
            <div class="text-h5 d-flex font-weight-medium mb-2">
              <template v-if="expenseLimit?.categories?.length > 0">
                <div class="stacked-images">
                  <template v-for="(item, index) in expenseLimit?.categories" :key="item">
                    <img v-if="index < 2" :src="item.iconUrl" alt=""
                      :class="['stacked-image', `stacked-image-${index + 1}`]" />
                  </template>
                </div>
                <v-tooltip activator="parent" location="bottom">Hạng mục</v-tooltip>
              </template>
              {{ expenseLimit?.name }}
            </div>

            <div class="text-h6 text-grey mb-3">
              {{ expenseLimitStore.formatDateRange(expenseLimit) }}
            </div>
          </div>

          <div class="text-h5 font-weight-medium">
            {{ formatCurrency(expenseLimit?.amount) }}
          </div>
        </div>
        <v-range :progress="expenseLimit?.progress || 0" :color="expenseLimit?.isOverBudget ? 'error' : 'warning'"
          class="my-4" :height="8"></v-range>
        <div class="d-flex justify-space-between">
          <div class="mb-2 text-h6 text-subtitle-1 text-range">Còn {{ expenseLimit?.remainingDays }} ngày</div>
          <div v-if="expenseLimit?.remainingBudget < 0" class="text-subtitle-1">
            <span class="text-grey-darken-1 mr-1">(Bội chi)</span>
            <span class="text-red-accent-3">{{ formatCurrency(Math.abs(expenseLimit.remainingBudget)) }}</span>
          </div>
          <div v-else class="text-subtitle-1 text-success">
            {{ formatCurrency(expenseLimit.remainingBudget) }}
          </div>
        </div>
      </div>
    </div>
    <router-link to="/expense-limit" class="d-flex justify-end text-decoration-none">
      <span class="text-primary text-h6 font-weight-medium py-2">Xem thêm
        <font-awesome-icon :icon="['fas', 'angle-right']" />
      </span>
    </router-link>
  </v-card>
</template>

<style lang="scss" scoped>
.stacked-images {
  position: relative;
  width: 40px;
  height: 40px;
  margin-right: 8px;

  .stacked-image {
    position: absolute;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: 2px solid white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s ease;

    &:hover {
      z-index: 3;
      transform: scale(1.1);
    }
  }

  .stacked-image-1 {
    left: 0;
    z-index: 3;
  }

  .stacked-image-2 {
    left: 8px;
    z-index: 2;
  }

  .stacked-image-3 {
    left: 16px;
    z-index: 1;
  }
}
</style>