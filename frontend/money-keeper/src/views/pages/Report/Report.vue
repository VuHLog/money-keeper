<script setup>
import { ref, onMounted, watch } from "vue";
import { useRoute } from "vue-router";
import { ReportList } from "@/constants/ReportList";
import CurrentFinance from "@pages/Report/CurrentFinance.vue";
import ExpenseRevenueSituationReport from "@pages/Report/ExpenseRevenueSituationReport.vue";

const route = useRoute();
const report = ref("Tài chính hiện tại");
const reportList = ref(ReportList);

onMounted(async () => {

});
</script>

<template>
  <div class="account-info">
    <!-- Header Section -->
    <v-container fluid class="header-section pa-0">
      <v-row align="center" class="ma-0">
        <v-col>
          <h1 class="text-h4 text-center text-primary mb-0">Báo cáo</h1>
        </v-col>
      </v-row>
    </v-container>

    <!-- Filter Section -->
    <v-container fluid class="filter-section">
      <v-row align="center" class="ma-0">
        <v-col cols="3">
          <v-select
            v-model="report"
            label="Báo cáo"
            :items="reportList"
            variant="outlined"
            density="comfortable"
            hide-details
            class="custom-select"
          >
          </v-select>
        </v-col>
      </v-row>
    </v-container>

    <!-- Transaction History -->
    <v-container fluid class="transaction-section">
        <CurrentFinance v-if="report === 'Tài chính hiện tại'" />
        <ExpenseRevenueSituationReport v-else-if="report === 'Tình hình thu chi'" />
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

  }
}
</style>
