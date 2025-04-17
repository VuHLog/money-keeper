<script setup>
import { ref, computed, onMounted } from 'vue';
import { useExpenseLimitStore } from '@/store/ExpenseLimitStore';
import { formatCurrency } from '@/utils/Format';
import { formatDateStringToDate } from '@/utils/DateUtil';

const props = defineProps({
  expenseLimit: {
    type: Object,
    required: true
  },
  startDate: {
    type: String,
    required: true
  },
  endDate: {
    type: String,
    required: true
  }
});

const expenseLimitStore = useExpenseLimitStore();
const expenseLimit = ref(props.expenseLimit);
const expenseLimitDetail = ref([]);

onMounted(async () => {
  expenseLimitDetail.value = await expenseLimitStore.getExpenseLimitDetailByExpenseLimitId(expenseLimit.value.id, props.startDate, props.endDate);
  expenseLimitDetail.value = convertData(expenseLimitDetail.value);
});

const convertData = (data) => {
  const categories = Object.values(data.reduce((acc, item) => {
    if (!acc[item.name]) {
      acc[item.name] = {
        name: item.name,
        totalAmount: 0,
        iconUrl: item.iconUrl,
        children: []
      };
    }
    acc[item.name].totalAmount += item.amount;
    acc[item.name].children.push({
      amount: item.amount,
      expenseDate: item.expenseDate,
      bucketPaymentName: item.bucketPaymentName
    });
    return acc;
  }, {}));

  return categories;
};

const totalAmount = computed(() => {
  return expenseLimitDetail.value.reduce((total, category) => total + category.totalAmount, 0);
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
          <template v-for="(detail, index) in expenseLimitDetail" :key="index">
            <div class="category-section" :class="{ 'mt-4': index > 0 }">
              <v-list-item class="category-header">
                <div class="d-flex align-center w-100">
                  <v-avatar class="mr-1 d-flex align-center" start>
                    <img :src="detail.iconUrl" alt="icon" style="width: 30px" />
                  </v-avatar>
                  <div class="flex-grow-1">
                    <div class="d-flex justify-space-between align-center">
                      <div class="text-subtitle-1 font-weight-medium">{{ detail.name }}</div>
                      <div class="text-subtitle-1 font-weight-bold" :class="detail.total > 0 ? 'error--text' : 'success--text'">
                        {{ formatCurrency(detail.totalAmount) }}
                      </div>
                    </div>
                  </div>
                </div>
              </v-list-item>

              <v-list-item v-for="expense in detail.children" :key="expense.id" 
                class="expense-item pl-16">
                <div class="d-flex justify-space-between w-100">
                  <div class="d-flex justify-space-between w-100">
                    <v-row>
                      <v-col cols="4">
                        <div class="text-subtitle-2">
                          {{ expense.expenseDate.split(".")[0] }}
                        </div>
                      </v-col>
                      <v-col cols="5">
                        <div class="text-subtitle-2 text-red-accent-3 text-center">{{ formatCurrency(expense.amount) }}</div>
                      </v-col>
                      <v-col cols="3">
                        <div class="text-subtitle-2 text-end">
                          <v-chip size="small" color="primary" variant="flat">{{ expense.bucketPaymentName }}</v-chip>
                        </div>
                      </v-col>
                    </v-row>
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