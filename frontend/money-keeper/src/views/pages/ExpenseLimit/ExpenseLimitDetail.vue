<script setup>
import { ref, computed } from 'vue';
import { formatCurrency } from '@/utils/Format';
import { formatDateStringToDate } from '@/utils/DateUtil';

const props = defineProps({
  expenses: {
    type: Array,
    default: () => []
  }
});

const formatDate = (date) => {
  if (date === 'Hôm nay') return date;
  return formatDateStringToDate(date, false);
};

const expensesByCategory = computed(() => {
  const categories = [
    {
      name: 'Điện',
      icon: ['fas', 'bolt'],
      color: 'warning',
      total: 8000,
      expenses: [
        {
          id: 1,
          name: 'Tổng chi',
          date: '19/04/2025',
          amount: 8000,
          wallet: 'Ví'
        }
      ]
    },
    {
      name: 'Ăn sáng',
      icon: ['fas', 'utensils'],
      color: 'orange',
      total: 450,
      expenses: [
        {
          id: 2,
          name: 'Tổng chi',
          date: 'Hôm nay',
          amount: 20,
          wallet: 'Ví'
        },
        {
          id: 3,
          date: '15/04/2025',
          amount: 20,
          wallet: 'Ví'
        },
        {
          id: 4,
          date: '12/04/2025',
          amount: 10,
          wallet: 'Ví'
        },
        {
          id: 5,
          date: '09/04/2025',
          amount: 400,
          wallet: 'Ví'
        }
      ]
    },
    {
      name: 'Đi chợ/siêu thị',
      icon: ['fas', 'shopping-cart'],
      color: 'grey',
      total: 0,
      expenses: [
        {
          id: 6,
          name: 'Tổng chi',
          date: '27/04/2025',
          amount: 0,
          wallet: 'Ví'
        }
      ]
    }
  ];

  return categories;
});

const totalAmount = computed(() => {
  return expensesByCategory.value.reduce((total, category) => total + category.total, 0);
});
</script>

<template>
  <div class="expense-detail">
    <!-- Header cố định -->
    <div class="expense-detail-header">
      <div class="d-flex justify-space-between align-center pa-4">
        <div class="text-h5 font-weight-bold">Chi tiết khoản chi</div>
        <div class="text-h5 primary--text font-weight-bold">{{ formatCurrency(totalAmount) }}</div>
      </div>
      <v-divider></v-divider>
    </div>

    <!-- Content có thể scroll -->
    <div class="expense-detail-content">
      <v-list class="expense-list">
        <v-list-subheader class="list-header">
          <div class="d-flex justify-space-between w-100">
            <div class="text-subtitle-2 text-grey-darken-1">Danh mục</div>
            <div class="d-flex justify-space-between" style="width: 60%">
              <div class="text-subtitle-2 text-grey-darken-1">Ngày</div>
              <div class="text-subtitle-2 text-grey-darken-1">Số tiền</div>
              <div class="text-subtitle-2 text-grey-darken-1">Ví</div>
            </div>
          </div>
        </v-list-subheader>

        <div class="expense-categories">
          <template v-for="(category, index) in expensesByCategory" :key="index">
            <div class="category-section" :class="{ 'mt-4': index > 0 }">
              <v-list-item class="category-header">
                <div class="d-flex align-center w-100">
                  <v-avatar size="40" class="mr-3" :color="category.color + ' lighten-4'">
                    <font-awesome-icon :icon="category.icon" :class="category.color + '--text text-darken-2'" />
                  </v-avatar>
                  <div class="flex-grow-1">
                    <div class="d-flex justify-space-between align-center">
                      <div class="text-subtitle-1 font-weight-medium">{{ category.name }}</div>
                      <div class="text-subtitle-1" :class="category.total > 0 ? 'error--text' : 'success--text'">
                        {{ formatCurrency(category.total) }}
                      </div>
                    </div>
                  </div>
                </div>
              </v-list-item>

              <v-list-item v-for="expense in category.expenses" :key="expense.id" 
                class="expense-item pl-16" 
                :class="{ 'today': expense.date === 'Hôm nay' }">
                <div class="d-flex justify-space-between w-100">
                  <div class="text-subtitle-2 text-grey-darken-1">{{ expense.name || 'Chi tiêu' }}</div>
                  <div class="d-flex justify-space-between" style="width: 60%">
                    <div class="text-subtitle-2" :class="{'primary--text': expense.date === 'Hôm nay'}">
                      {{ formatDate(expense.date) }}
                    </div>
                    <div class="text-subtitle-2">{{ formatCurrency(expense.amount) }}</div>
                    <div class="text-subtitle-2">
                      <v-chip size="small" color="primary" variant="flat">{{ expense.wallet }}</v-chip>
                    </div>
                  </div>
                </div>
              </v-list-item>
            </div>
          </template>
        </div>
      </v-list>
    </div>

    <!-- Footer cố định -->
    <div class="expense-detail-footer">
      <v-divider></v-divider>
      <v-list-item class="total-section pa-4">
        <div class="d-flex justify-space-between w-100">
          <div class="text-h6">Tổng chi</div>
          <div class="text-h6 error--text">{{ formatCurrency(totalAmount) }}</div>
        </div>
      </v-list-item>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.expense-detail {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 80vh; // Chiều cao tối đa của modal
}

.expense-detail-header {
  background: linear-gradient(to right, rgba(255, 255, 255, 0.95), rgba(255, 255, 255, 0.95));
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 1;
}

.expense-detail-content {
  flex: 1;
  overflow-y: auto;
  
  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
    
    &:hover {
      background: #555;
    }
  }
}

.expense-detail-footer {
  background: white;
  position: sticky;
  bottom: 0;
  z-index: 1;
}

.expense-list {
  padding: 0;
}

.list-header {
  background: #f5f5f5;
  height: 48px;
  font-weight: 500;
  position: sticky;
  top: 0;
  z-index: 1;
}

.category-section {
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(0, 0, 0, 0.01);
  }
}

.category-header {
  border-radius: 8px;
  margin: 0 8px;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(0, 0, 0, 0.02);
  }
}

.expense-item {
  padding-top: 4px;
  padding-bottom: 4px;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(0, 0, 0, 0.02);
  }

  &.today {
    background: rgba(var(--v-theme-primary), 0.05);
  }
}

.total-section {
  background: #fafafa;
}
</style>