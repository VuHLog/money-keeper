<script setup>
import RevenueExpenseReport from '../components/RevenueExpenseReport.vue';
import ExpenseLimitReport from '../components/ExpenseLimitReport.vue';
import SavingsReport from '../components/SavingsReport.vue';
import SpendingAnalysisReport from '../components/SpendingAnalysisReport.vue';
</script>

<template>
  <v-container>
    <v-row>
      <!-- Thu Chi Chart -->
      <v-col cols="12" md="6">
        <revenue-expense-report />
      </v-col>

      <!-- expense Limit -->
      <v-col cols="12" md="6">
        <expense-limit-report />
        
        <!-- Savings -->
        <savings-report class="mt-4" />

        <!-- Debt Tracking -->
        <v-card class="pa-4 mt-4">
          <h3>Theo dõi vay nợ</h3>
          <p class="text-grey mt-2">Hiện tại, bạn không có khoản Cho vay và Còn nợ nào</p>
        </v-card>
      </v-col>

      <!-- Spending Analysis -->
      <v-col cols="12">
        <spending-analysis-report />
      </v-col>
    </v-row>
  </v-container>
</template>

<style lang="scss" scoped>
.v-card {
  border-radius: 12px;
}

h3 {
  font-size: 1.2rem;
  font-weight: 500;
}
</style>
