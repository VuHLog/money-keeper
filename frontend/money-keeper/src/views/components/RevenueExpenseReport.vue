<script setup>
import { ref, getCurrentInstance, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useReportStore } from "@/store/ReportStore.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment";
import { formatCurrency } from "@/utils/format.js";

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
const myBucketPayments = ref([]);
const reportStore = useReportStore();
const isSelectAll = ref(true);
const countItemSelected = ref();
const dictionaryBucketPaymentStore = useDictionaryBucketPaymentStore();
const searchBucketPaymentName = ref("");
const revenueExpenseData = ref({
  totalExpense: 0,
  totalRevenue: 0,
});
const chartSeries = ref([]);
onMounted(async () => {
  await getData();
});

async function getData() {
  revenueExpenseData.value =
    await reportStore.getTotalExpenseRevenueByBucketPaymentIdAndTimeOption(
      null,
      selectedPeriod.value,
      null,
      null
    );
  chartSeries.value = [
    {
      name: "Thu Chi",
      data: [
        {
          x: "Thu",
          y: revenueExpenseData.value.totalRevenue,
        },
        {
          x: "Chi b",
          y: revenueExpenseData.value.totalExpense,
        },
      ],
    },
  ];

  myBucketPayments.value = await dictionaryBucketPaymentStore.getMyBucketPayments();
  myBucketPayments.value.forEach((value) => value.isSelected = true);

  countItemSelected.value = myBucketPayments.value.length;
}

const selectedPeriod = ref("Tháng này");
const timePeriods = ref([
  "Hôm nay",
  "Tuần này",
  "Tháng này",
  "Quý này",
  "Năm nay",
]);

watch(selectedPeriod, async () => {
  await getData();
});

const chartOptions = ref({
  chart: {
    type: "bar",
    toolbar: {
      show: true,
    },
  },
  plotOptions: {
    bar: {
      horizontal: false,
      columnWidth: "50",
      borderRadius: 10,
      borderRadiusApplication: "end",
      borderRadiusWhenStacked: "last",
      distributed: true,
    },
  },
  colors: ["#1867C0", "#FF1744"],
  grid: {
    show: true,
  },
  dataLabels: {
    enabled: false,
  },
  xaxis: {
    categories: ["Thu", "Chi"],
    labels: {
      show: true,
      style: {
        colors: ["#1867C0", "#FF1744"],
        fontSize: "12px",
      },
    },
    axisBorder: {
      show: false,
    },
    axisTicks: {
      show: false,
    },
  },
  legend: {
    show: false,
  },
  yaxis: {
    labels: {
      show: true,
    },
    title: {
      text: "₫ (Đồng)",
      rotate: 0, // 👉 chữ nằm ngang
      offsetX: 0,
      offsetY: -80,
      style: {
        fontSize: "14px",
        fontWeight: "bold",
      },
    },
  },
  tooltip: {
    enabled: true,
    y: {
      formatter: function (val) {
        return formatCurrency(val);
      },
    },
  },
});

function handleSelectAllCheckBox() {
  if (isSelectAll.value) {
    myBucketPayments.value.forEach((value) => value.isSelected = true);
    countItemSelected.value = myBucketPayments.value.length;
  } else {
    myBucketPayments.value.forEach((value) => value.isSelected = false);
  }
}

function handleSelectBucketPayment(bucketPayment) {
  let isSelect = bucketPayment.isSelected;
  if (isSelect) {
    countItemSelected.value++;
  } else {
    countItemSelected.value--;
  }
  if (countItemSelected.value === myBucketPayments.value.length) {
    isSelectAll.value = true;
  } else {
    isSelectAll.value = false;
  }
}

const donutChartOptions = ref({
  chart: {
    type: "donut",
  },
  colors: ["#ffc107", "#f44336"],
  labels: ["Không xác định (99,2%)", "Ăn uống (0,7%)"],
  legend: {
    position: "bottom",
  },
  plotOptions: {
    pie: {
      donut: {
        size: "70%",
      },
    },
  },
});

const donutSeries = ref([99.2, 0.7]);
</script>

<template>
  <v-card class="pa-4 text-grey-color" style="overflow: visible">
    <div class="d-flex justify-space-between align-center mb-4">
      <h3>Tình hình thu chi</h3>
      <div class="d-flex align-center">
        <v-menu location="bottom end" offset="5">
          <template v-slot:activator="{ props }">
            <v-btn variant="text" size="small" v-bind="props" class="text-caption text-grey">
              {{ selectedPeriod }}
              <font-awesome-icon :icon="['fas', 'chevron-down']" class="ml-1" />
            </v-btn>
          </template>
          <v-list>
            <v-list-item v-for="period in timePeriods" :key="period" :value="period" @click="selectedPeriod = period">
              <v-list-item-title>{{ period }}</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu location="bottom end">
          <template v-slot:activator="{ props }">
            <v-btn variant="text" size="small" v-bind="props" class="px-1">
              <font-awesome-icon :icon="['fas', 'ellipsis-vertical']" />
            </v-btn>
          </template>
          <v-list>
            <v-list-item @click="
              router.push({
                path: '/transaction-history',
                query: { redirect: route.fullPath },
              })
              ">
              <v-list-item-title>Xem lịch sử ghi chép</v-list-item-title>
            </v-list-item>
            <v-list-item @click="() => { }">
              <v-list-item-title>
                <v-menu location="end" :submenu="true" :close-on-content-click="false">
                  <template v-slot:activator="{ props }">
                    <div v-bind="props">
                      <span>Tất cả tài khoản</span>
                      <font-awesome-icon :icon="['fas', 'chevron-down']" class="ml-1" />
                    </div>
                  </template>
                  <v-list width="300">
                    <v-list-item>
                      <v-text-field density="compact" v-model="searchBucketPaymentName" label="Tìm kiếm theo tên tài khoản" variant="outlined" hide-details single-line>
                        <template v-slot:prepend-inner>
                          <v-icon icon="fa:fas fa-magnifying-glass" />
                        </template>
                      </v-text-field>
                    </v-list-item>
                    <v-list-item>
                      <v-checkbox v-model="isSelectAll" color="green-darken-1" label="Chọn tất cả" hide-details
                        @change="handleSelectAllCheckBox()"></v-checkbox>
                    </v-list-item>
                    <v-list-item v-for="bucketPayment in myBucketPayments" :key="bucketPayment">
                      <v-list-item-title class="d-flex align-center">
                        <v-checkbox v-model="bucketPayment.isSelected" color="green-darken-1"
                          @change="handleSelectBucketPayment(bucketPayment)" hide-details></v-checkbox>
                        <div>
                          <v-avatar start>
                            <img class="icon-size" :src="bucketPayment.accountType?.icon" alt="icon" />
                          </v-avatar>
                          <span class="text-grey-color">{{ bucketPayment.accountName }}</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </div>

    <div class="d-flex align-items-center justify-space-between gap-4 mb-6">
      <div style="width: 40%">
        <apexchart type="bar" :options="chartOptions" :series="chartSeries" height="200"></apexchart>
      </div>

      <div class="d-flex flex-column gap-4" style="width: 50%">
        <div class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <div class="rounded-circle mr-2 bg-primary" style="width: 10px; height: 10px"></div>
            <span>Thu</span>
          </div>
          <div class="text-h6 font-weight-bold text-primary">
            {{ formatCurrency(revenueExpenseData.totalRevenue) }}
          </div>
        </div>
        <div class="d-flex align-center justify-space-between pb-2 border-b-sm">
          <div class="d-flex align-center">
            <div class="rounded-circle mr-2 bg-red-accent-3" style="width: 10px; height: 10px"></div>
            <span>Chi</span>
          </div>
          <div class="text-h6 font-weight-bold text-red-accent-3">
            {{ formatCurrency(revenueExpenseData.totalExpense) }}
          </div>
        </div>
        <div class="d-flex align-center justify-end">
          <div class="text-h6 font-weight-bold">
            {{
              formatCurrency(
                revenueExpenseData.totalRevenue -
                revenueExpenseData.totalExpense
              )
            }}
          </div>
        </div>
      </div>
    </div>

    <apexchart type="donut" :options="donutChartOptions" :series="donutSeries" height="200"></apexchart>
  </v-card>
</template>

<style lang="scss" scoped>
</style>
