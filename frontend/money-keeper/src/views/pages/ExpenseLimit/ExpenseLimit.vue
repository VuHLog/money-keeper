<template>
  <div class="expense-limit-page">
    <v-toolbar flat>
      <v-btn icon="fas fa-arrow-left" @click="$router.back()"></v-btn>
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
        <template v-slot:prepend>
          <v-avatar size="40">
            <v-img :src="limit.avatar"></v-img>
          </v-avatar>
        </template>

        <v-list-item-title class="text-subtitle-1 font-weight-medium">
          {{ limit.name }}
        </v-list-item-title>

        <v-list-item-subtitle class="text-caption text-grey">
          {{ limit.dateRange }}
        </v-list-item-subtitle>

        <template v-slot:append>
          <div class="text-right">
            <div class="text-subtitle-1">{{ formatCurrency(limit.amount) }}</div>
            <div class="d-flex align-center justify-end mt-1">
              <span class="text-caption mr-2">Còn {{ limit.remainingDays }} ngày</span>
              <span v-if="limit.isOverBudget" class="text-caption text-error">
                (Bội chi) {{ formatCurrency(limit.overAmount) }}
              </span>
              <span v-else class="text-caption">
                {{ formatCurrency(limit.remainingAmount) }}
              </span>
            </div>
          </div>
        </template>

        <template v-slot:additional>
          <v-progress-linear
            :model-value="limit.progress"
            :color="limit.isOverBudget ? 'error' : 'warning'"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </template>
      </v-list-item>
    </v-list>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { formatCurrency } from '@/utils/Format';

const router = useRouter();

const expenseLimits = ref([
  {
    id: 1,
    name: 'Longg',
    avatar: '/path/to/avatar1.jpg',
    dateRange: '01/04 - 30/04',
    amount: 10000,
    remainingDays: 13,
    remainingAmount: 1560,
    isOverBudget: false,
    progress: 70
  },
  {
    id: 2,
    name: 'Long',
    avatar: '/path/to/avatar2.jpg',
    dateRange: '05/03 - 04/06',
    amount: 1000,
    remainingDays: 48,
    overAmount: 7450,
    isOverBudget: true,
    progress: 100
  }
]);

function viewLimitDetail(id) {
  router.push(`/expense-limit/${id}`);
}

function addNewLimit() {
  router.push('/expense-limit/new');
}
</script>

<style lang="scss" scoped>
.expense-limit-page {
  min-height: 100vh;
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
