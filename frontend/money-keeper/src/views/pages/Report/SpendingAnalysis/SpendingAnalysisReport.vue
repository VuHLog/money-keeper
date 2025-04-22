<script setup>
import { ref, onMounted, watch, watchEffect } from "vue";
import { formatCurrency } from "@/utils/Format";
import { useReportStore } from "@/store/ReportStore.js";
import AccountModal from "@components/AccountModal.vue";
import ExpenseModal from "@/views/components/ExpenseModal.vue";
import DictionaryExpense from "@components/DictionaryExpense.vue";
import { colorRepos } from "@/common/ColorRepos.js";


const reportStore = useReportStore();

const TimeOptionReportPage = ["Ngày", "Tháng", "Năm"]
const timeOptions = ref(TimeOptionReportPage);
const showAccountModal = ref(false);
const showExpenseRevenueModal = ref(false);
const timeOption = ref("Tháng");
const account = ref([]);
const showPopupCategory = ref(false);
const yearRange = ref([new Date(), new Date()]);
const monthRange = ref([new Date(), new Date()]);
const dateRange = ref([new Date(), new Date()]);
const data = ref([]);
const categories = ref([]);
const chartOptions = ref({});
const chartSeries = ref([]);
const chartOptionsInitial = ref({});
const chartSeriesInitial = ref([]);
const itemSelected = ref({});

onMounted(async () => {
    initialChart();
    await getData();
    setupChart();
});

watchEffect(async () => {
    await getData();
    setupChart();
});

async function getData() {
    data.value = await reportStore.getTotalExpenseForSpendingAnalysis(
        timeOption.value,
        account.value.length > 0 ? account.value.join(",") : null,
        categories.value.length > 0 ? categories.value.join(",") : null,
        dateRange.value[0] || null,
        dateRange.value[1] || null,
        monthRange.value[0] || null,
        monthRange.value[1] || null,
        yearRange.value[0].getFullYear() || null,
        yearRange.value[1].getFullYear() || null,
    );
}

function handleConfirmCategory() {
    showPopupCategory.value = false
}

function handleConfirmAccount() {
    showAccountModal.value = false;
}


function setupChart() {
    chartOptions.value = {
        ...chartOptionsInitial.value,
    };
    chartSeries.value = [
        ...chartSeriesInitial.value,
    ];
    chartSeries.value = [
        {
            name: "Tổng chi",
            data: data.value.map(item => item.totalExpense),
        },
    ];

    if (timeOption.value === "Ngày") {
        chartOptions.value.xaxis.categories = data.value.map(item => item.date);
    } else if (timeOption.value === "Tháng") {
        chartOptions.value.xaxis.categories = data.value.map(item => item.yearMonth);
    } else if (timeOption.value === "Năm") {
        chartOptions.value.xaxis.categories = data.value.map(item => item.year);
    }
}

function initialChart() {
    chartSeriesInitial.value = [
    ];

    chartOptionsInitial.value = {
        chart: {
            type: "bar",
            toolbar: {
                show: true,
            },
            zoom: {
                enabled: true
            }
        },
        colors: ["#FF1744"],
        plotOptions: {
            bar: {
                columnWidth: '50%',
                borderRadius: 10,
                borderRadiusApplication: 'end',
                borderRadiusWhenStacked: 'last',
            },
        },
        stroke: {
            width: 0
        },
        responsive: [{
            breakpoint: 480,
            options: {
                legend: {
                    position: 'bottom',
                    offsetX: -10,
                    offsetY: 0
                }
            }
        }],
        xaxis: {
            labels: {
                rotate: -45
            },
            tickPlacement: 'on',
        },
        yaxis: {
            labels: {
                show: true,
                formatter: function (val) {
                    return formatCurrency(val);
                },
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
        dataLabels: {
            enabled: false,
        },
        legend: {
            position: 'right',
            offsetY: 40
        },
        fill: {
            opacity: 1,
        },
        tooltip: {
            y: {
                formatter: function (val) {
                    return formatCurrency(val);
                },
            },
        },
    };
}

</script>

<template>
    <v-container fluid class="filter-section">
        <v-row align="center" class="ma-0">
            <v-col cols="3">
                <v-select v-model="timeOption" label="Thời điểm" :items="timeOptions" variant="outlined"
                    density="comfortable" hide-details class="custom-select">
                </v-select>
            </v-col>
            <v-col cols="3">
                <v-btn class="cursor-pointer" elevation="4" rounded="xl" size="x-large"
                    @click="showAccountModal = true">
                    <template v-if="account.length > 0">
                        <div class="stacked-images">
                            <template v-for="(item, index) in account" :key="item">
                                <img v-if="index < 2" :src="item.iconUrl" alt=""
                                    :class="['stacked-image', `stacked-image-${index + 1}`]" />
                            </template>
                        </div>
                        <template v-for="(item, index) in account" :key="item">
                            <span v-if="index === 0" class="text-14" style="text-transform: none">{{
                                item.accountName
                            }}</span>
                            <span v-if="index === 1" class="text-14" style="text-transform: none">{{
                                ", " + item.accountName
                            }}</span>
                        </template>
                        <span v-if="account.length > 2" class="text-14" style="text-transform: none">{{
                            " + " + (account.length - 2) + " tài khoản khác"
                        }}</span>
                        <v-tooltip activator="parent" location="bottom">Tài khoản</v-tooltip>
                    </template>
                    <template v-else>
                        <span style="text-transform: none">Tất cả tài khoản</span>
                    </template>
                </v-btn>
            </v-col>
            <v-col cols="4">
                <v-btn class="cursor-pointer" elevation="4" rounded="xl" size="x-large"
                    @click="showPopupCategory = true">
                    <template v-if="categories.length > 0">
                        <div class="stacked-images">
                            <template v-for="(item, index) in categories" :key="item">
                                <img v-if="index < 2" :src="item.iconUrl" alt=""
                                    :class="['stacked-image', `stacked-image-${index + 1}`]" />
                            </template>
                        </div>
                        <template v-for="(item, index) in categories" :key="item">
                            <span v-if="index === 0" class="text-14" style="text-transform: none">{{
                                item.name
                                }}</span>
                            <span v-if="index === 1" class="text-14" style="text-transform: none">{{
                                ", " + item.name
                                }}</span>
                        </template>
                        <span v-if="categories.length > 2" class="text-14" style="text-transform: none">{{
                            " + " + (categories.length - 2) + " hạng mục khác"
                            }}</span>
                        <v-tooltip activator="parent" location="bottom">Hạng mục</v-tooltip>
                    </template>
                    <template v-else>
                        <span style="text-transform: none">Tất cả hạng mục</span>
                    </template>
                </v-btn>
            </v-col>
            <v-col cols="5">
                <template v-if="timeOption === 'Ngày'">
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="đến"
                        start-placeholder="Ngày bắt đầu" end-placeholder="Ngày kết thúc" />
                </template>
                <template v-else-if="timeOption === 'Tháng'">
                    <el-date-picker v-model="monthRange" type="monthrange" range-separator="đến"
                        start-placeholder="Tháng bắt đầu" end-placeholder="Tháng kết thúc" />
                </template>
                <template v-else-if="timeOption === 'Năm'">
                    <el-date-picker v-model="yearRange" type="yearrange" range-separator="đến"
                        start-placeholder="Năm bắt đầu" end-placeholder="Năm kết thúc" />
                </template>
            </v-col>
        </v-row>
        <v-card class="pa-4 text-grey-color" style="overflow: visible">
            <div class="d-flex justify-end">
                <div class="d-flex justify-space-between gap-2 mb-4" style="width: 30%;">
                    <div class="text-grey-darken-1">
                        <p>Tổng chi tiêu</p>
                        <p>Trung bình hàng ngày</p>
                    </div>
                    <div>
                        <p class="text-24 font-weight-bold text-red-accent-3">
                            {{formatCurrency(data.reduce((acc, item) => acc + item.totalExpense, 0))}}
                        </p>
                        <p class="text-24 font-weight-bold text-grey-darken-4">
                            {{formatCurrency(data.reduce((acc, item) => acc + item.totalExpense, 0) / data.length)}}
                        </p>
                    </div>
                </div>
            </div>
            <div class="d-flex align-items-center justify-space-between gap-4 mb-6">
                <div class="w-100">
                    <apexchart type="bar" :options="chartOptions" :series="chartSeries" height="200"></apexchart>
                </div>
            </div>
        </v-card>
        <v-expansion-panels class="mt-4" accordion>
            <v-expansion-panel>
                <v-expansion-panel-title class="font-weight-bold">Xem chi tiết</v-expansion-panel-title>
                <v-expansion-panel-text>
                    <v-row>
                        <template v-for="item in data" :key="item.id">
                            <v-col cols="3" class="period-card cursor-pointer rounded" @click="showExpenseRevenueModal = true; itemSelected = item">
                                <div class="d-flex justify-space-between align-center">
                                    <div>
                                        <template v-if="timeOption === 'Ngày'">
                                            <span class="pa-3 bg-green-accent-3 mr-4 text-white">{{
                                                item.date.split("-")[0] }}</span>
                                            <span>{{ item.date }}</span>
                                        </template>
                                        <template v-else-if="timeOption === 'Tháng'">
                                            <span class="pa-3 bg-green-accent-3 mr-4 text-white">{{
                                                item.yearMonth.split("/")[0] }}</span>
                                            <span>{{ item.yearMonth }}</span>
                                        </template>
                                        <template v-else-if="timeOption === 'Năm'">
                                            <span class="pa-3 bg-green-accent-3 mr-4 text-white">{{
                                                item.year.toString()[2] + item.year.toString()[3] }}</span>
                                            <span>{{ item.year }}</span>
                                        </template>
                                    </div>
                                    <p class="text-24 font-weight-bold text-red-accent-3">
                                        {{formatCurrency(item.totalExpense)}}
                                    </p>
                                </div>
                            </v-col>
                        </template>
                    </v-row>
                </v-expansion-panel-text>
            </v-expansion-panel>
        </v-expansion-panels>
    </v-container>
    <v-dialog v-model="showAccountModal" width="auto">
        <account-modal v-model="account" @confirm="handleConfirmAccount"></account-modal>
    </v-dialog>
    <v-dialog v-model="showPopupCategory" width="auto">
        <dictionary-expense v-model="categories" :isMultiple="true"
            @confirm="handleConfirmCategory"></dictionary-expense>
    </v-dialog>
    <v-dialog v-model="showExpenseRevenueModal" width="auto">
        <expense-modal :bucketPaymentIds="account.length > 0 ? account.join(',') : null"
            :categoriesId="categories.length > 0 ? categories.join(',') : null"
            :timeOption="timeOption" :date="itemSelected.date || null"
            :yearMonth="itemSelected.yearMonth || null" :year="itemSelected.year || null"
            :totalExpense="itemSelected.totalExpense"></expense-modal>
    </v-dialog>
</template>

<style lang="scss" scoped>
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

.period-card {
    height: 100%;
    transition: transform 0.2s ease-in-out;

    &:hover {
        transform: translateY(-4px);
        background-color: #EEEEEE;
    }

    .v-card-title {
        padding-bottom: 8px;
        border-bottom: 1px solid rgba(0, 0, 0, 0.12);
    }

    .v-card-text {
        padding: 16px;
    }
}

.gap-2 {
    gap: 8px;
}
</style>