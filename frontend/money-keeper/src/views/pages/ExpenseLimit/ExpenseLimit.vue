<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { formatCurrency } from "@/utils/Format";
import VRange from "@/components/VRange.vue";

const router = useRouter();

const expenseLimits = ref([
  {
    id: 1,
    name: "Longg",
    avatar: "/path/to/avatar1.jpg",
    dateRange: "01/04 - 30/04",
    amount: 10000,
    remainingDays: 13,
    remainingAmount: 1560,
    isOverBudget: false,
    progress: 70,
  },
  {
    id: 2,
    name: "Long",
    avatar: "/path/to/avatar2.jpg",
    dateRange: "05/03 - 04/06",
    amount: 1000,
    remainingDays: 48,
    overAmount: 7450,
    isOverBudget: true,
    progress: 100,
  },
]);

function viewLimitDetail(id) {
  router.push(`/expense-limit/info/${id}`);
}

function addNewLimit() {
  router.push("/expense-limit/create");
}
</script>

<template>
  <div class="expense-limit-page">
    <v-toolbar flat>
      <v-btn
        icon
        variant="text"
        class="back-button d-flex align-center text-primary"
        to="/home"
      >
        <font-awesome-icon
          :icon="['fas', 'angle-left']"
          style="font-size: 32px"
        />
      </v-btn>
      <v-toolbar-title>Hạn mức chi</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon="fas fa-plus" @click="addNewLimit"></v-btn>
    </v-toolbar>

    <v-list>
      <v-list-item
        v-for="(limit, index) in expenseLimits"
        :key="index"
        @click="viewLimitDetail(limit.id)"
        class="pa-4"
      >
        <v-list-item-title class="w-100">
          <div class="d-flex align-start">
            <!-- Avatar -->
            <v-avatar size="40" class="mr-4">
              <v-img :src="limit.avatar"></v-img>
            </v-avatar>

            <!-- Nội dung chính -->
            <div class="flex-grow-1">
              <!-- Tên giới hạn -->
              <div class="text-subtitle-1 font-weight-medium">
                {{ limit.name }}
              </div>

              <!-- Khoảng thời gian -->
              <div class="text-caption text-grey mb-1">
                {{ limit.dateRange }}
              </div>

              <!-- Số tiền và thông tin còn lại -->
              <div class="d-flex justify-space-between align-center">
                <!-- Số tiền đã chi -->
                <div class="text-subtitle-1">
                  {{ formatCurrency(limit.amount) }}
                </div>

                <!-- Ngày còn lại và trạng thái -->
                <div class="text-caption text-right">
                  <div>Còn {{ limit.remainingDays }} ngày</div>
                  <div>
                    <span v-if="limit.isOverBudget" class="text-error">
                      (Bội chi) {{ formatCurrency(limit.overAmount) }}
                    </span>
                    <span v-else>
                      {{ formatCurrency(limit.remainingAmount) }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- Progress bar -->
              <v-range
                :progress="limit.progress"
                :color="limit.isOverBudget ? 'error' : 'warning'"
                class="mt-2"
              ></v-range>
            </div>
          </div>
        </v-list-item-title>
      </v-list-item>
    </v-list>
  </div>
</template>

<style lang="scss" scoped>
.expense-limit-page {
  min-height: 80vh;
  background: white;
}

.v-list-item {
  cursor: pointer;

  &:hover {
    background-color: rgba(0, 0, 0, 0.02);
  }
}

@media (min-width: 960px) {
  .expense-limit-page {
    max-width: 800px;
    margin: 0 auto;
    box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  }
}
</style>
