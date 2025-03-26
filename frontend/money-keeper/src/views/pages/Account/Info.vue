<script setup>
import { ref, onMounted, watch } from "vue";
import { TimeOption } from "@/constants/TimeOption.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment.js";
import { useRoute } from "vue-router";
import { formatCurrency } from "@/utils/format.js";

const dictionaryBucketPaymentStore = useDictionaryBucketPaymentStore();
const route = useRoute();
const bucketPaymentId = ref(route.params.accountId);
const timeOptions = ref(TimeOption);
const timeOption = ref();

const datePicker = ref();
const bucketPayment = ref();
const totalExpense = ref();
const totalRevenue = ref();

onMounted(async () => {
  bucketPayment.value = await dictionaryBucketPaymentStore.getBucketPaymentById(bucketPaymentId.value);
  await getData();
});

async function getData(){
  totalExpense.value = await dictionaryBucketPaymentStore.getTotalExpenseByBucketPaymentId(bucketPaymentId.value, timeOption.value);
  totalRevenue.value = await dictionaryBucketPaymentStore.getTotalRevenueByBucketPaymentId(bucketPaymentId.value, timeOption.value);
}

watch(timeOption, () => {
  getData();
});
</script>

<template>
  <div class="account-info text-grey-color">
    <div class="text-primary-color position-relative mb-10">
      <router-link
        to="/account"
        class="position-absolute top-0 left-0 text-primary-color text-decoration-none"
      >
        <font-awesome-icon
          :icon="['fas', 'angle-left']"
          style="font-size: 32px"
        />
      </router-link>
      <h1 class="text-center">Ví</h1>
    </div>
    <div>
      <v-row class="mb-2">
        <v-col cols="3">
          <v-select
            v-model="timeOption"
            label="Chọn thời gian"
            width="100%"
            :items="timeOptions"
            item-title="option"
            item-value="option"
            no-data-text="Không tìm thấy"
            class="text-grey-color d-inline-block"
          >
            <template v-slot:selection="{ item }">
              <div>
                <span class="text-grey-color">{{ item.raw.option }}</span>
              </div>
            </template>
          </v-select>
        </v-col>
        <v-col cols="4" v-if="timeOption?.id === 9">
          <el-date-picker
            v-model="datePicker"
            type="daterange"
            start-placeholder="Ngày bắt đầu"
            end-placeholder="Ngày kết thúc"
          />
        </v-col>
      </v-row>
      <v-row
        no-gutters
        class="pa-2 mb-2 rounded box-shadow-default"
      >
        <v-col cols="4">
          <div class="flex-center flex-column text-20 border-e-sm">
            <span class="text-grey-color">Tổng thu</span>
            <span class="text-blue-accent-3 font-weight-bold">{{ formatCurrency(totalRevenue) }}</span>
          </div>
        </v-col>
        <v-col cols="4">
          <div class="flex-center flex-column text-20 border-e-sm">
            <span class="text-grey-color">Tổng chi</span>
            <span class="text-red-accent-3 font-weight-bold">{{ formatCurrency(totalExpense) }}</span>
          </div>
        </v-col>
        <v-col cols="4">
          <div class="flex-center flex-column text-20">
            <span class="text-grey-color">Số dư hiện tại</span>
            <span class="text-grey-color font-weight-bold">{{ formatCurrency(bucketPayment?.balance) }}</span>
          </div>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <div
            class="pa-2 rounded box-shadow-default"
          >
            <div class="account-history-list">
              <div class="account-history-item">
                <div class="title d-flex justify-space-between">
                  <div class="flex-center ml-2 pb-2">
                    <span class="text-32 font-weight-bold">08</span>
                    <div class="ml-3">
                      <span class="font-weight-bold">Hôm nay</span>
                      <div class="text-grey-lighten-1">03/2025</div>
                    </div>
                  </div>
                  <div class="text-red-accent-3">6.000 ₫</div>
                </div>
                <v-timeline
                  class="ml-5 border-t-sm"
                  density="compact"
                  align="start"
                  side="end"
                >
                  <v-timeline-item
                    width="100%"
                    dot-color="green-accent-2"
                    size="small"
                  >
                    <template v-slot:icon>
                      <v-icon>
                        <img
                          src="https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png"
                          alt="icon"
                          style="width: 24px; height: 24px"
                        />
                      </v-icon>
                    </template>
                    <router-link to="" class="text-decoration-none">
                      <div class="d-flex align-center justify-space-between">
                        <span class="text-grey-color font-weight-bold"
                          >Dịch vụ sinh hoạt</span
                        >
                        <div
                          class="d-flex align-end justify-center flex-column"
                        >
                          <span class="text-red-accent-3">6.000 ₫</span>
                          <span class="text-grey-lighten-1">(-6.000 ₫)</span>
                        </div>
                      </div>
                    </router-link>
                  </v-timeline-item>
                  <v-timeline-item
                    width="100%"
                    dot-color="green-accent-2"
                    size="small"
                  >
                    <template v-slot:icon>
                      <v-icon>
                        <img
                          src="https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png"
                          alt="icon"
                          style="width: 24px; height: 24px"
                        />
                      </v-icon>
                    </template>
                    <router-link to="" class="text-decoration-none">
                      <div class="d-flex align-center justify-space-between">
                        <span class="text-grey-color font-weight-bold"
                          >Dịch vụ sinh hoạt</span
                        >
                        <div
                          class="d-flex align-end justify-center flex-column"
                        >
                          <span class="text-red-accent-3">6.000 ₫</span>
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
        <v-col cols="6">
          <div
            class="pa-2 rounded box-shadow-default"
          >
            <div class="account-history-list">
              <div class="account-history-item">
                <div class="title d-flex justify-space-between">
                  <div class="flex-center ml-2 pb-2">
                    <span class="text-32 font-weight-bold">08</span>
                    <div class="ml-3">
                      <span class="font-weight-bold">Hôm nay</span>
                      <div class="text-grey-lighten-1">03/2025</div>
                    </div>
                  </div>
                  <div class="text-red-accent-3">6.000 ₫</div>
                </div>
                <v-timeline
                  class="ml-5 border-t-sm"
                  density="compact"
                  align="start"
                  side="end"
                >
                  <v-timeline-item
                    width="100%"
                    dot-color="green-accent-2"
                    size="small"
                  >
                    <template v-slot:icon>
                      <v-icon>
                        <img
                          src="https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png"
                          alt="icon"
                          style="width: 24px; height: 24px"
                        />
                      </v-icon>
                    </template>
                    <router-link to="" class="text-decoration-none">
                      <div class="d-flex align-center justify-space-between">
                        <span class="text-grey-color font-weight-bold"
                          >Dịch vụ sinh hoạt</span
                        >
                        <div
                          class="d-flex align-end justify-center flex-column"
                        >
                          <span class="text-red-accent-3">6.000 ₫</span>
                          <span class="text-grey-lighten-1">(-6.000 ₫)</span>
                        </div>
                      </div>
                    </router-link>
                  </v-timeline-item>
                  <v-timeline-item
                    width="100%"
                    dot-color="green-accent-2"
                    size="small"
                  >
                    <template v-slot:icon>
                      <v-icon>
                        <img
                          src="https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png"
                          alt="icon"
                          style="width: 24px; height: 24px"
                        />
                      </v-icon>
                    </template>
                    <router-link to="" class="text-decoration-none">
                      <div class="d-flex align-center justify-space-between">
                        <span class="text-grey-color font-weight-bold"
                          >Dịch vụ sinh hoạt</span
                        >
                        <div
                          class="d-flex align-end justify-center flex-column"
                        >
                          <span class="text-red-accent-3">6.000 ₫</span>
                          <span class="text-grey-lighten-1">(-6.000 ₫)</span>
                        </div>
                      </div>
                    </router-link>
                  </v-timeline-item>
                  <v-timeline-item
                    width="100%"
                    dot-color="green-accent-2"
                    size="small"
                  >
                    <template v-slot:icon>
                      <v-icon>
                        <img
                          src="https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png"
                          alt="icon"
                          style="width: 24px; height: 24px"
                        />
                      </v-icon>
                    </template>
                    <router-link to="" class="text-decoration-none">
                      <div class="d-flex align-center justify-space-between">
                        <span class="text-grey-color font-weight-bold"
                          >Dịch vụ sinh hoạt</span
                        >
                        <div
                          class="d-flex align-end justify-center flex-column"
                        >
                          <span class="text-red-accent-3">6.000 ₫</span>
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
