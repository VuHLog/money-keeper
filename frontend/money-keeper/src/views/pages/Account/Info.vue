<script setup>
import { ref, onMounted, watch } from "vue";
import { TimeOption } from "@/constants/TimeOption.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment.js";
import { useRoute } from "vue-router";
import { formatCurrency } from "@/utils/format.js";
import { getWeekday, formatDate } from "@/utils/DateUtil.js";

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
  <div class="account-info text-grey-color">
    <div class="text-primary-color position-relative mb-10">
      <router-link to="/account" class="position-absolute top-0 left-0 text-primary-color text-decoration-none">
        <font-awesome-icon :icon="['fas', 'angle-left']" style="font-size: 32px" />
      </router-link>
      <h1 class="text-center">Ví</h1>
    </div>
    <div>
      <v-row class="mb-2">
        <v-col cols="3">
          <v-select v-model="timeOption" label="Chọn thời gian" width="100%" :items="timeOptions" item-title="option"
            item-value="option" no-data-text="Không tìm thấy" class="text-grey-color d-inline-block">
            <template v-slot:selection="{ item }">
              <div>
                <span class="text-grey-color">{{ item.raw.option }}</span>
              </div>
            </template>
          </v-select>
        </v-col>
        <template v-if="timeOption === 'Tùy chọn'">
          <v-col cols="6">
            <el-date-picker class="mr-2" v-model="startDate" type="date" placeholder="Ngày bắt đầu" size="large" />
            <el-date-picker v-model="endDate" type="date" placeholder="Ngày kết thúc" size="large" />
          </v-col>
        </template>
      </v-row>
      <v-row no-gutters class="pa-2 mb-2 rounded box-shadow-default">
        <v-col :cols="timeOption === 'Toàn bộ thời gian' ? 4 : 6">
          <div class="flex-center flex-column text-20 border-e-sm">
            <span class="text-grey-color">Tổng thu</span>
            <span class="text-blue-accent-3 font-weight-bold">{{
              formatCurrency(totalRevenue)
            }}</span>
          </div>
        </v-col>
        <v-col :cols="timeOption === 'Toàn bộ thời gian' ? 4 : 6">
          <div class="flex-center flex-column text-20 border-e-sm">
            <span class="text-grey-color">Tổng chi</span>
            <span class="text-red-accent-3 font-weight-bold">{{
              formatCurrency(totalExpense)
            }}</span>
          </div>
        </v-col>
        <v-col cols="4" v-if="timeOption === 'Toàn bộ thời gian'">
          <div class="flex-center flex-column text-20">
            <span class="text-grey-color">Số dư hiện tại</span>
            <span class="text-grey-color font-weight-bold">{{
              formatCurrency(bucketPayment?.balance)
            }}</span>
          </div>
        </v-col>
      </v-row>
      <v-row>
        <template v-for="item in transactionHistory" :key="item.date">
          <v-col cols="6">
            <div class="pa-2 rounded box-shadow-default">
              <div class="account-history-list">
                <div class="account-history-item">
                  <div class="title d-flex justify-space-between">
                    <div class="flex-center ml-2 pb-2">
                      <span class="text-32 font-weight-bold">{{
                        item.date.split("-")[2]
                      }}</span>
                      <div class="ml-3">
                        <span class="font-weight-bold">{{
                          getWeekday(item.date)
                        }}</span>
                        <div class="text-grey-lighten-1">
                          {{
                            item.date.split("-")[1] +
                            "/" +
                            item.date.split("-")[0]
                          }}
                        </div>
                      </div>
                    </div>
                    <div class="d-flex flex-column">
                      <span class="text-red-accent-3">{{ formatCurrency(sumHistoryByDate(item.date, 'expense')) }}</span>
                      <span class="text-blue-accent-3">{{ formatCurrency(sumHistoryByDate(item.date, 'revenue')) }}</span>
                    </div>
                  </div>
                  <v-timeline class="ml-5 border-t-sm" density="compact" align="start" side="end">
                    <v-timeline-item v-for="history in item.history" :key="history.id" width="100%"
                      dot-color="green-accent-2" size="small">
                      <template v-slot:icon>
                        <v-icon>
                          <img src="https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png"
                            alt="icon" style="width: 24px; height: 24px" />
                        </v-icon>
                      </template>
                      <router-link to="" class="text-decoration-none">
                        <div class="d-flex align-center justify-space-between">
                          <span class="text-grey-color font-weight-bold">{{
                            history.categoryName
                          }}</span>
                          <div class="d-flex align-end justify-center flex-column">
                            <span class="text-red-accent-3" :class="history.type === 'expense' ? 'text-red-accent-3' : 'text-blue-accent-3'">{{
                              history.amount < 0 ? "(-" + formatCurrency(history.amount) + ")" :
                                formatCurrency(history.amount) }}</span>
                                <span class="text-grey-lighten-1">(-6.000 ₫)</span>
                          </div>
                        </div>
                      </router-link>
                    </v-timeline-item>
                  </v-timeline>
                </div>
              </div>
            </div>
          </v-col>
        </template>
      </v-row>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.account-info {
  .account-history-list {
    .account-history-item {
      .title {
        border-left: 12px solid #00e676;
      }
    }
  }
}
</style>
