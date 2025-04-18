<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { formatCurrency } from "@/utils/Format";
import VRange from "@/components/VRange.vue";
import { useExpenseLimitStore } from "@/store/ExpenseLimitStore";
import { useReportStore } from "@/store/ReportStore";

const router = useRouter();
const expenseLimitStore = useExpenseLimitStore();
const reportStore = useReportStore();
const expenseLimits = ref([]);

onMounted(async () => {
  expenseLimits.value = await expenseLimitStore.getExpenseLimits();
  expenseLimits.value.forEach(async (expenseLimit) => {
    expenseLimit.currentStartDate = expenseLimitStore.getCurrentStartDate(expenseLimit);
    expenseLimit.currentEndDate = expenseLimitStore.getEndDate(expenseLimit.currentStartDate, expenseLimit.repeatTime, expenseLimit.endDate);
    expenseLimit.remainingDays = expenseLimitStore.remainingDays(expenseLimit);
    expenseLimit.totalExpense = (await reportStore.getTotalExpenseByExpenseLimit(expenseLimit.id, expenseLimit.currentStartDate + " 00:00:00", expenseLimit.currentEndDate + " 23:59:59")).totalExpense || 0;
    expenseLimit.remainingBudget = expenseLimitStore.remainingBudget(expenseLimit);
    expenseLimit.progress = expenseLimitStore.calculateProgress(expenseLimit);
    expenseLimit.isOverBudget = expenseLimit.remainingBudget < 0;
  });
});

function viewLimitDetail(id) {
  router.push(`/expense-limit/info/${id}`);
}
</script>

<template>
  <div class="expense-limit-page">
    <v-toolbar flat class="expense-limit-toolbar">
      <v-btn icon variant="text" class="back-button d-flex align-center text-primary" to="/home">
        <font-awesome-icon :icon="['fas', 'angle-left']" style="font-size: 32px" />
      </v-btn>
      <v-toolbar-title class="text-h5 font-weight-bold">Hạn mức chi</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon variant="text" elevation="2" class="add-button" to="/expense-limit/create">
        <font-awesome-icon :icon="['fas', 'plus']" style="font-size: 28px" />
      </v-btn>
    </v-toolbar>

    <div class="expense-limit-content">
      <v-list class="expense-limit-list">
        <v-list-item v-for="(limit, index) in expenseLimits" :key="index" @click="viewLimitDetail(limit.id)"
          class="expense-limit-item">
          <v-list-item-title class="w-100">
            <div class="d-flex align-start">
              <div class="flex-grow-1">
                <div class="d-flex justify-space-between">
                  <div>
                    <div class="text-h5 d-flex font-weight-medium mb-2">
                      <template v-if="limit.categories.length > 0">
                        <div class="stacked-images">
                          <template v-for="(item, index) in limit.categories" :key="item">
                            <img v-if="index < 2" :src="item.iconUrl" alt=""
                              :class="['stacked-image', `stacked-image-${index + 1}`]" />
                          </template>
                        </div>
                        <v-tooltip activator="parent" location="bottom">Hạng mục</v-tooltip>
                      </template>
                      {{ limit.name }}
                    </div>

                    <div class="text-h6 text-grey mb-3">
                      {{ expenseLimitStore.formatDateRange(limit) }}
                    </div>
                  </div>
                  
                  <div class="text-h5 font-weight-medium">
                    {{ formatCurrency(limit.amount) }}
                  </div>
                </div>
                <v-range :progress="limit?.progress || 0" :color="limit?.isOverBudget ? 'error' : 'warning'" class="my-4"
                  :height="8"></v-range>
                <div class="d-flex justify-space-between">
                  <div class="mb-2 text-h6 text-subtitle-1 text-range">Còn {{ limit.remainingDays }} ngày</div>
                  <div v-if="limit.remainingBudget < 0" class="text-subtitle-1">
                    <span class="text-grey-darken-1 mr-1">(Bội chi)</span>
                    <span class="text-red-accent-3">{{ formatCurrency(Math.abs(limit.remainingBudget)) }}</span>
                  </div>
                  <div v-else class="text-subtitle-1 text-success">
                    {{ formatCurrency(limit.remainingBudget) }}
                  </div>
                </div>

              </div>
            </div>
          </v-list-item-title>
        </v-list-item>
      </v-list>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.expense-limit-page {
  min-height: 100vh;
  background: white;
  display: flex;
  flex-direction: column;
}

.expense-limit-toolbar {
  background: white !important;
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  padding: 0 24px;
  height: 80px !important;
}

.expense-limit-content {
  flex: 1;
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 80px);
}

.expense-limit-list {
  background: transparent;
  padding: 0;
  overflow-y: auto;
  max-height: calc(100vh - 200px);
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 0, 0, 0.2) transparent;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background-color: rgba(0, 0, 0, 0.2);
    border-radius: 3px;
  }
}

.expense-limit-item {
  background: white;
  border-radius: 16px;
  margin-bottom: 24px;
  padding: 24px !important;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  }

  &:last-child {
    margin-bottom: 0;
  }
}

// Responsive
@media (min-width: 600px) {
  .expense-limit-content {
    padding: 32px;
  }

  .expense-limit-item {
    padding: 32px !important;
  }
}

@media (min-width: 960px) {
  .expense-limit-content {
    padding: 40px;
  }

  .expense-limit-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 32px;
    max-height: calc(100vh - 200px);
  }

  .expense-limit-item {
    margin-bottom: 0;
  }
}

@media (min-width: 1264px) {
  .expense-limit-list {
    grid-template-columns: repeat(2, 1fr);
  }
}

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
