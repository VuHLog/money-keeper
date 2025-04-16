<script setup>
import { ref, getCurrentInstance, onMounted, watch, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useReportStore } from "@/store/ReportStore.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment";
import { formatCurrency, formatNumberToPercent } from "@/utils/format.js";
import { colorRepos } from "@/common/ColorRepos.js";
import { TransferType, TransferTypeIcon } from "@/constants/TransferType.js";
import VRange from "@/components/VRange.vue";

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
const myBucketPayments = ref([]);
const reportStore = useReportStore();
const isSelectAll = ref(true);
const isManuallyToggledSelectAll = ref(false);
const itemSelectedMap =  ref(new Map());
const itemIdSelectedList = ref([]);
const dictionaryBucketPaymentStore = useDictionaryBucketPaymentStore();
const pageNumber = ref(1);
const pageSize = ref(1);
const totalElements= ref(0);
const isOpenDictionaryBucketPayment = ref(false);
const isChangeBucketPaymentsSelected = ref(false);
const field = ref("accountName")
const searchBucketPaymentName = ref("");
const revenueExpenseData = ref({
  totalExpense: 0,
  totalRevenue: 0,
});
const totalExpenseByCategory = ref([]);
const totalRevenueByCategory = ref([]);
const groupedRevenueCategories = computed(() => {
  const groups = []
  for (let i = 0; i < totalRevenueByCategory.value.length; i += lengthWindow.value) {
    groups.push(totalRevenueByCategory.value.slice(i, i + lengthWindow.value))
  }
  return groups
})
const groupedExpenseCategories = computed(() => {
  const groups = []
  for (let i = 0; i < totalExpenseByCategory.value.length; i += lengthWindow.value) {
    groups.push(totalExpenseByCategory.value.slice(i, i + lengthWindow.value))
  }
  return groups
})
const chartSeries = ref([]);
const startDate = ref(null);
const endDate = ref(null);
const windowExpense = ref(0);
const windowRevenue = ref(0);
const lengthWindow = ref(4);
onMounted(async () => {
  await getData();
  myBucketPayments.value.forEach((value) => {
    itemSelectedMap.value.set(value.id, true);
    itemIdSelectedList.value.push(value.id);
  });
});

async function getData() {
  revenueExpenseData.value =
    await reportStore.getTotalExpenseRevenueByBucketPaymentIdAndTimeOption(
      itemIdSelectedList.value,
      selectedPeriod.value,
      startDate.value,
      endDate.value
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

  let res = await dictionaryBucketPaymentStore.getMyBucketPaymentsPagination(field.value, pageNumber.value, pageSize.value, 'ASC', searchBucketPaymentName.value);
  myBucketPayments.value = res.content;
  totalElements.value = res.totalElements;

  totalExpenseByCategory.value = await reportStore.getTotalExpenseByBucketPaymentIdAndTimeOptionAndCategory(itemIdSelectedList.value,selectedPeriod.value,startDate.value,endDate.value, isSelectAll.value);
  if(totalExpenseByCategory.value.length > 0){
    updateExpensePieSeries();
    updateExpensePieOptions();
  }
  totalRevenueByCategory.value = await reportStore.getTotalRevenueByBucketPaymentIdAndTimeOptionAndCategory(itemIdSelectedList.value,selectedPeriod.value,startDate.value,endDate.value, isSelectAll.value);
  if(totalRevenueByCategory.value.length > 0){
    updateRevenuePieSeries();
    updateRevenuePieOptions();
  }
}

watch(
  searchBucketPaymentName,
  () =>{
    totalElements.value = 0;
    pageSize.value = 1;
    pageNumber.value = 1;
    getData();
  }
)

function resetBucketPaymentPagination(){
  pageSize.value = 1;
  pageNumber.value = 1;
}

async function clickLoadMoreBucketPayment() {
  if (myBucketPayments.value.length >= totalElements.value) return;
  pageSize.value += 1;
  let res = await dictionaryBucketPaymentStore.getMyBucketPaymentsPagination(field.value, pageNumber.value, pageSize.value, 'ASC', searchBucketPaymentName.value);
  myBucketPayments.value = res.content;
  if(isSelectAll.value){
    myBucketPayments.value.forEach((value) => {
      if(!itemSelectedMap.value.has(value.id)){
        itemSelectedMap.value.set(value.id, true);
        itemIdSelectedList.value.push(value.id);
      }
    });
  }
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

watch(isSelectAll,
  () =>{
      if (!isManuallyToggledSelectAll.value) return; // handle when click select all check box
      itemIdSelectedList.value = [];
      isChangeBucketPaymentsSelected.value = true;
      if (isSelectAll.value) {
        myBucketPayments.value.forEach((value) => {
          itemSelectedMap.value.set(value.id, true);
          itemIdSelectedList.value.push(value.id);
        });
      } else {
        itemSelectedMap.value.clear();
        itemIdSelectedList.value = [];
      }
      isManuallyToggledSelectAll.value = false; // reset
    }
)

function handleSelectBucketPayment(id) {
  const isSelected = itemSelectedMap.value.get(id);
  isChangeBucketPaymentsSelected.value = true;
  if (!isSelected) {
    itemSelectedMap.value.delete(id);
    itemIdSelectedList.value = itemIdSelectedList.value.filter(value => value !== id);
  }else{
    itemIdSelectedList.value.push(id);
  }
  if (itemIdSelectedList.value.length === totalElements.value) {
    isSelectAll.value = true;
  } else {
    isSelectAll.value = false;
  }
}

async function handleDictionaryBucketPaymentMenuToggle(){
  if (!isOpenDictionaryBucketPayment.value && isChangeBucketPaymentsSelected.value) {
      isChangeBucketPaymentsSelected.value = false;
      if(itemIdSelectedList.value.length === 0){
          isSelectAll.value = true;
      }
      resetBucketPaymentPagination();
      searchBucketPaymentName.value = "";
      await getData();
  }
}


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
      rotate: 0,
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

const expensePieChartOptions = ref({
  chart: {
    type: "pie",
    width: 380,
  },
  colors: [],
  dataLabels:{
    enabled: false,
  },
  labels: [],
  legend: {
    show: false,
  },
});

const expensePieSeries = ref([]);
const expensePieChart = ref(null)
function updateExpensePieSeries(){
  expensePieChart.value.updateSeries(totalExpenseByCategory.value.map(val => val.total));
}

const expenseOptions = ref({});
function updateExpensePieOptions() {
  const options = {
    imgSrc: [],
    colors: [],
    labels: [],
  };
  const availableColors = [...colorRepos];
  totalExpenseByCategory.value.forEach(val => {
    options.imgSrc.push(val.iconUrl);
    const randomIndex = Math.floor(Math.random() * availableColors.length);
    const selectedColor = availableColors[randomIndex];
    options.colors.push(selectedColor);
    availableColors.splice(randomIndex, 1);
    options.labels.push(val.name);
  });
  expenseOptions.value = options;
  expensePieChart.value.updateOptions(
    {
      colors: options.colors,
      fill: {
        image: {
          src: options.imgSrc
        },
      },
      labels: options.labels,
      stroke: {
        width: totalExpenseByCategory.value.length === 1 ? 0 : 2,
        colors: ['#ffffff']
      },
      tooltip: {
        custom: function({series, seriesIndex, dataPointIndex, w}) {
          return `<div class="d-flex align-center py-1 px-2">
                    <v-avatar class="mr-1 d-flex align-center" start>
                      <img src="${options.imgSrc[seriesIndex]}" alt="icon" style="width: 30px" />
                    </v-avatar>
                    <span class="text-grey-color text-14 text-white">${options.labels[seriesIndex]}: ${formatCurrency(series[seriesIndex])}</span>
                  </div>`
        }
      }
    }
  );
}

const revenuePieChartOptions = ref({
  chart: {
    type: "pie",
  },
  colors: [],
  dataLabels:{
    enabled: false,
  },
  labels: [],
  legend: {
    show: false,
  },
});

const revenuePieSeries = ref([]);
const revenuePieChart = ref(null)
function updateRevenuePieSeries(){
  revenuePieChart.value.updateSeries(totalRevenueByCategory.value.map(val => val.total));
}
const revenueOptions = ref({});
function updateRevenuePieOptions() {
  const options = {
    imgSrc: [],
    colors: [],
    labels: [],
  };

  const availableColors = [...colorRepos];
  totalRevenueByCategory.value.forEach(val => {
    options.imgSrc.push(val.iconUrl);
    
    const randomIndex = Math.floor(Math.random() * availableColors.length);
    const selectedColor = availableColors[randomIndex];
    options.colors.push(selectedColor);
    availableColors.splice(randomIndex, 1);
    let label = val.name;
    if(label === "Chuyển khoản"){
      label = "Nhận từ chuyển khoản";
    }
    options.labels.push(label);
  });
  
  revenueOptions.value = options;
  revenuePieChart.value.updateOptions(
    {
      colors: options.colors,
      labels: options.labels,
      stroke: {
        width: totalRevenueByCategory.value.length === 1 ? 0 : 2,
        colors: ['#ffffff']
      },
      tooltip: {
        custom: function({series, seriesIndex, dataPointIndex, w}) {
          return `<div class="d-flex align-center py-1 px-2">
                    <v-avatar class="mr-1 d-flex align-center" start>
                      <img src="${options.imgSrc[seriesIndex]}" alt="icon" style="width: 30px" />
                    </v-avatar>
                    <span class="text-grey-color text-14 text-white">${options.labels[seriesIndex]}: ${formatCurrency(series[seriesIndex])}</span>
                  </div>`
        }
      }
    }
  );
}
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
                <v-menu v-model="isOpenDictionaryBucketPayment"
                  @update:modelValue="handleDictionaryBucketPaymentMenuToggle" location="end" :submenu="true"
                  :close-on-content-click="false">
                  <template v-slot:activator="{ props }">
                    <div v-bind="props">
                      <span class="user-select-none">Tất cả tài khoản</span>
                      <font-awesome-icon :icon="['fas', 'chevron-down']" class="ml-1" />
                    </div>
                  </template>
                  <v-list width="300">
                    <v-list-item>
                      <v-text-field density="compact" v-model.trim="searchBucketPaymentName"
                        label="Tìm kiếm theo tên tài khoản" variant="outlined" hide-details single-line clearable>
                        <template v-slot:prepend-inner>
                          <v-icon icon="fa:fas fa-magnifying-glass" />
                        </template>
                      </v-text-field>
                    </v-list-item>
                    <template v-if="myBucketPayments.length > 0">
                      <v-list-item v-show="searchBucketPaymentName === '' || searchBucketPaymentName === null">
                        <div class="d-flex justify-space-between align-center">
                          <v-checkbox v-model="isSelectAll" color="green-darken-1" label="Chọn tất cả" hide-details
                            @click="() => isManuallyToggledSelectAll = true"></v-checkbox>
                          <span class="text-14 text-grey-darken-1">Đã chọn {{ isSelectAll?totalElements:itemIdSelectedList.length }}</span>
                        </div>
                      </v-list-item>
                      <div style="max-height: 240; overflow-y: auto">
                        <v-list-item height="48" v-for="bucketPayment in myBucketPayments" :key="bucketPayment">
                          <v-list-item-title class="d-flex align-center" style="height: 48px;">
                            <v-checkbox :model-value="itemSelectedMap.get(bucketPayment.id) || false"
                              @update:model-value="val => itemSelectedMap.set(bucketPayment.id, val)"
                              color="green-darken-1" @change="handleSelectBucketPayment(bucketPayment.id)"
                              hide-details></v-checkbox>
                            <div>
                              <v-avatar start>
                                <img class="icon-size" :src="bucketPayment.accountType?.icon" alt="icon" />
                              </v-avatar>
                              <span class="text-grey-color">{{ bucketPayment.accountName }}</span>
                            </div>
                          </v-list-item-title>
                        </v-list-item>
                      </div>
                      <v-list-item v-if="myBucketPayments.length < totalElements">
                        <div class="flex-center">
                          <span
                            class="text-12 cursor-pointer font-weight-bold text-decoration-underline text-grey-darken-1 pa-2"
                            @click="clickLoadMoreBucketPayment()">Tải thêm</span>
                        </div>
                      </v-list-item>
                    </template>
                    <v-list-item v-else>
                      <span class="text-12 flex-center font-weight-bold text-grey-darken-1">Không tồn tại tên tài khoản
                        chứa {{ searchBucketPaymentName }}</span>
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

    <v-row>
      <v-col cols="12" class="mb-3" v-if="revenueExpenseData.totalRevenue > 0">
        <div class="d-flex">
          <div>
            <p class="text-primary text-center">Thu</p>
            <apexchart class="border-e-sm align-self-center" type="pie" width="200" :options="revenuePieChartOptions"
              :series="revenuePieSeries" ref="revenuePieChart"></apexchart>
          </div>
          <div class="align-self-start w-100">
            <v-window v-model="windowRevenue" show-arrows="hover">
              <v-window-item v-for="(group,index1) in groupedRevenueCategories" :key="group">
                <v-card class="d-flex justify-center align-center" height="220px">
                  <div style="width: 80%;">
                    <template v-for="(item,index2) in group" :key="item">
                      <div class="mb-2">
                        <div class="d-flex justify-space-between align-center">
                          <div>
                            <v-avatar start>
                              <img :src="item.iconUrl" alt="icon" style="width: 30px" />
                            </v-avatar>
                            <span class="text-grey-color text-14" v-if="item.name === 'Chuyển khoản'">Nhận từ chuyển
                              khoản</span>
                            <span class="text-grey-color text-14" v-else>{{ item.name }}</span>
                          </div>
                          <div>
                            <span class="text-14 mr-1">{{ formatCurrency(item.total) }}</span>
                            <span class="text-14 text-grey-darken-1">{{ formatNumberToPercent(item.total,
                              revenueExpenseData.totalRevenue) }}</span>
                          </div>
                        </div>
                        <v-range :progress="(Math.round((item.total * 10000 / revenueExpenseData.totalRevenue))/100)"
                          :color="revenueOptions.colors[index1*lengthWindow + index2]"></v-range>
                      </div>
                    </template>
                  </div>
                </v-card>
              </v-window-item>
            </v-window>
          </div>
        </div>
      </v-col>
      <v-col cols="12" v-if="revenueExpenseData.totalExpense > 0">
        <div class="d-flex align-center">
          <div>
            <p class="text-red-accent-3 text-center">Chi</p>
            <apexchart class="border-e-sm align-self-center" type="pie" width="200" :options="expensePieChartOptions"
              :series="expensePieSeries" ref="expensePieChart"></apexchart>
          </div>
          <div class="align-self-start w-100">
            <v-window v-model="windowExpense" show-arrows="hover">
              <template v-slot:prev="{ props }">
                <div class="cursor-pointer hover-bg-grey-darken elevation-3 rounded-circle flex-center pa-3" @click="props.onClick" style="width: 16px; height: 16px;">
                  <font-awesome-icon :icon="['fas', 'chevron-left']" />
                </div>
              </template>
              <template v-slot:next="{ props }">
                <div class="cursor-pointer hover-bg-grey-darken elevation-3 rounded-circle flex-center pa-3" @click="props.onClick" style="width: 16px; height: 16px;">
                  <font-awesome-icon :icon="['fas', 'chevron-right']" />
                </div>
              </template>
              <v-window-item v-for="(group,index1) in groupedExpenseCategories" :key="group">
                <v-card class="d-flex justify-center align-center" height="220px">
                  <div class="w-75">
                    <template v-for="(item,index2) in group" :key="item">
                      <div class="mb-2">
                        <div class="d-flex justify-space-between align-center">
                          <div>
                            <v-avatar start>
                              <img :src="item.iconUrl" alt="icon" style="width: 30px" />
                            </v-avatar>
                            <span class="text-grey-color text-14">{{ item.name }}</span>
                          </div>
                          <div>
                            <span class="text-14 mr-1">{{ formatCurrency(item.total) }}</span>
                            <span class="text-14 text-grey-darken-1">{{ formatNumberToPercent(item.total,
                              revenueExpenseData.totalExpense) }}</span>
                          </div>
                        </div>
                        <v-range :progress="(Math.round((item.total * 10000 / revenueExpenseData.totalExpense))/100)"
                          :color="expenseOptions.colors[index1*lengthWindow + index2]"></v-range>
                      </div>
                    </template>
                  </div>
                </v-card>
              </v-window-item>
            </v-window>
          </div>
        </div>
      </v-col>
    </v-row>
  </v-card>
</template>

<style lang="scss" scoped>
</style>
