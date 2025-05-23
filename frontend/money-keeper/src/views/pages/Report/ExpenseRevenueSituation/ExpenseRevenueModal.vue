<script setup>
import { ref, getCurrentInstance, onMounted, watch, computed } from "vue";
import { useReportStore } from "@/store/ReportStore.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment";
import { formatCurrency, formatNumberToPercent } from "@/utils/format.js";
import { colorRepos } from "@/common/ColorRepos.js";
import VRange from "@/components/VRange.vue";
import { nextTick } from 'vue';

const props = defineProps({
    title: {
        type: String,
        default: "Tình hình thu chi",
    },
    bucketPaymentIds: {
        type: String,
        required: true,
    },
    timeOption: {
        type: String,
        required: true,
    },
    presentOption: {
        type: String,
        default: null
    },
    month: {
        type: Number,
        default: null,
    },
    quarter: {
        type: Number,
        default: null,
    },
    year: {
        type: Number,
        default: null,
    },
    startDate: {
        type: Object,
        default: null,
    },
    endDate: {
        type: Object,
        default: null,
    },
    totalExpense: {
        type: Number,
        required: true,
    },
    totalRevenue: {
        type: Number,
        required: true,
    },
});
const reportStore = useReportStore();
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
const startDate = ref(null);
const endDate = ref(null);
const windowExpense = ref(0);
const windowRevenue = ref(0);
const lengthWindow = ref(4);
const data = ref([]);
onMounted(async () => {
    await nextTick();
    await getData();
});

async function getData() {
    data.value = await reportStore.getTotalExpenseRevenueForCategory(props.bucketPaymentIds, props.timeOption, props.presentOption, props.month, props.quarter, props.year, props.startDate, props.endDate);
    totalExpenseByCategory.value = data.value.filter(item => item.type === "expense" && item.totalExpense > 0);
    totalRevenueByCategory.value = data.value.filter(item => item.type === "revenue" && item.totalRevenue > 0);
    if (totalExpenseByCategory.value.length > 0) {
        updateExpensePieSeries();
        updateExpensePieOptions();
    }
    if (totalRevenueByCategory.value.length > 0) {
        updateRevenuePieSeries();
        updateRevenuePieOptions();
    }
}

const expensePieChartOptions = ref({
    chart: {
        type: "pie",
        width: 380,
    },
    colors: [],
    dataLabels: {
        enabled: false,
    },
    labels: [],
    legend: {
        show: false,
    },
});

const expensePieSeries = ref([]);
const expensePieChart = ref(null)
function updateExpensePieSeries() {
    expensePieChart.value.updateSeries(totalExpenseByCategory.value.map(val => val.totalExpense));
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
                custom: function ({ series, seriesIndex, dataPointIndex, w }) {
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
    dataLabels: {
        enabled: false,
    },
    labels: [],
    legend: {
        show: false,
    },
});

const revenuePieSeries = ref([]);
const revenuePieChart = ref(null)
function updateRevenuePieSeries() {
    revenuePieChart.value.updateSeries(totalRevenueByCategory.value.map(val => val.totalRevenue));
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
        if (label === "Chuyển khoản") {
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
                custom: function ({ series, seriesIndex, dataPointIndex, w }) {
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
        <h2 class="text-center">{{ props.title }}</h2>

        <v-row>
            <v-col cols="12" class="mb-3" v-if="props.totalRevenue > 0">
                <div class="d-flex">
                    <div>
                        <p class="text-primary text-center">Thu</p>
                        <apexchart class="border-e-sm align-self-center" type="pie" width="200"
                            :options="revenuePieChartOptions" :series="revenuePieSeries" ref="revenuePieChart">
                        </apexchart>
                    </div>
                    <div class="align-self-start w-100">
                        <v-window v-model="windowRevenue" show-arrows="hover">
                            <v-window-item v-for="(group, index1) in groupedRevenueCategories" :key="group">
                                <v-card class="d-flex justify-center align-center" height="220px">
                                    <div style="width: 80%;">
                                        <template v-for="(item, index2) in group" :key="item">
                                            <div class="mb-2">
                                                <div class="d-flex justify-space-between align-center">
                                                    <div>
                                                        <v-avatar start>
                                                            <img :src="item.iconUrl" alt="icon" style="width: 30px" />
                                                        </v-avatar>
                                                        <span class="text-grey-color text-14"
                                                            v-if="item.name === 'Chuyển khoản'">Nhận từ chuyển
                                                            khoản</span>
                                                        <span class="text-grey-color text-14" v-else>{{ item.name
                                                            }}</span>
                                                    </div>
                                                    <div>
                                                        <span class="text-14 mr-1">{{ formatCurrency(item.totalRevenue)
                                                            }}</span>
                                                        <span class="text-14 text-grey-darken-1">{{
                                                            formatNumberToPercent(item.totalRevenue,
                                                            props.totalRevenue) }}</span>
                                                    </div>
                                                </div>
                                                <v-range
                                                    :progress="(Math.round((item.totalRevenue * 10000 / props.totalRevenue)) / 100)"
                                                    :color="revenueOptions.colors[index1 * lengthWindow + index2]"></v-range>
                                            </div>
                                        </template>
                                    </div>
                                </v-card>
                            </v-window-item>
                        </v-window>
                    </div>
                </div>
            </v-col>
            <v-col cols="12" v-if="props.totalExpense > 0">
                <div class="d-flex align-center">
                    <div>
                        <p class="text-red-accent-3 text-center">Chi</p>
                        <apexchart class="border-e-sm align-self-center" type="pie" width="200"
                            :options="expensePieChartOptions" :series="expensePieSeries" ref="expensePieChart">
                        </apexchart>
                    </div>
                    <div class="align-self-start w-100">
                        <v-window v-model="windowExpense" show-arrows="hover">
                            <template v-slot:prev="{ props }">
                                <div class="cursor-pointer hover-bg-grey-darken elevation-3 rounded-circle flex-center pa-3"
                                    @click="props.onClick" style="width: 16px; height: 16px;">
                                    <font-awesome-icon :icon="['fas', 'chevron-left']" />
                                </div>
                            </template>
                            <template v-slot:next="{ props }">
                                <div class="cursor-pointer hover-bg-grey-darken elevation-3 rounded-circle flex-center pa-3"
                                    @click="props.onClick" style="width: 16px; height: 16px;">
                                    <font-awesome-icon :icon="['fas', 'chevron-right']" />
                                </div>
                            </template>
                            <v-window-item v-for="(group, index1) in groupedExpenseCategories" :key="group">
                                <v-card class="d-flex justify-center align-center" height="220px">
                                    <div class="w-75">
                                        <template v-for="(item, index2) in group" :key="item">
                                            <div class="mb-2">
                                                <div class="d-flex justify-space-between align-center">
                                                    <div>
                                                        <v-avatar start>
                                                            <img :src="item.iconUrl" alt="icon" style="width: 30px" />
                                                        </v-avatar>
                                                        <span class="text-grey-color text-14">{{ item.name }}</span>
                                                    </div>
                                                    <div>
                                                        <span class="text-14 mr-1">{{ formatCurrency(item.totalExpense)
                                                            }}</span>
                                                        <span class="text-14 text-grey-darken-1">{{
                                                            formatNumberToPercent(item.totalExpense,
                                                            props.totalExpense) }}</span>
                                                    </div>
                                                </div>
                                                <v-range
                                                    :progress="(Math.round((item.totalExpense * 10000 / props.totalExpense)) / 100)"
                                                    :color="expenseOptions.colors[index1 * lengthWindow + index2]"></v-range>
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

<style lang="scss" scoped></style>
