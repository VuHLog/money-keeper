<script setup>
import { ref, getCurrentInstance, onMounted, computed, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useReportStore } from "@/store/ReportStore.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment";
import { formatCurrency } from "@/utils/format.js";
import ExpenseRevenueModal from "@pages/Report/ExpenseRevenueSituation/ExpenseRevenueModal.vue";

const props = defineProps({
    account: {
        type: Array,
        required: true,
    },
    timeOption: {
        type: String,
        default: "",
    },
    year: {
        type: Number,
        default: 0,
    },
    startDate: {
        type: Object,
        default: {},
    },
    endDate: {
        type: Object,
        default: {},
    },
    startYear: {
        type: Number,
        default: 0,
    },
    endYear: {
        type: Number,
        default: 0,
    },
});

const showExpenseRevenueModal = ref(false);
const reportStore = useReportStore();
const bucketPaymentIds = computed(() => props.account.length > 0 ? props.account.join(",") : null);
const timeOption = computed(() => props.timeOption);
const year = computed(() => props.year);
const startDate = computed(() => props.startDate);
const endDate = computed(() => props.endDate);
const startYear = computed(() => props.startYear);
const endYear = computed(() => props.endYear);
const data = ref([]);
const chartOptions = ref({});
const chartSeries = ref([]);
const itemIdSelected = ref("");

onMounted(async () => {
    await getData();
    setupChart();
});

async function getData() {
    data.value = await reportStore.getTotalExpenseRevenueForExpenseRevenueSituation(
        bucketPaymentIds.value,
        timeOption.value,
        year.value,
        startYear.value,
        endYear.value,
        startDate.value,
        endDate.value
    );
}

watchEffect(async () => {
    data.value = await reportStore.getTotalExpenseRevenueForExpenseRevenueSituation(
        bucketPaymentIds.value,
        timeOption.value,
        year.value,
        startYear.value,
        endYear.value,
        startDate.value,
        endDate.value
    );
    setupChart();
});

function setupChart() {
    if (data.value.length > 0) {
        chartSeries.value = [
            {
                name: "Tổng chi",
                data: timeOption.value === "Tùy chọn" ? data.value.totalExpense : data.value.map(item => item.totalExpense),
            },
            {
                name: "Tổng thu",
                data: timeOption.value === "Tùy chọn" ? data.value.totalRevenue : data.value.map(item => item.totalRevenue),
            },
        ];

        chartOptions.value = {
            chart: {
                type: "bar",
                stacked: true,
                toolbar: {
                    show: true,
                },
                zoom: {
                    enabled: true
                }
            },
            colors: ["#FF1744", "#1867C0"],
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
                categories: timeOption.value === "Tùy chọn" ? [] : data.value.map(item => timeOption.value + " " + item.unit),
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
            plotOptions: {
                bar: {
                    horizontal: false,
                    borderRadius: 10,
                    borderRadiusApplication: 'end',
                    borderRadiusWhenStacked: 'last',
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
}
</script>

<template>
    <v-card class="pa-4 text-grey-color" style="overflow: visible">
        <div class="d-flex align-items-center justify-space-between gap-4 mb-6">
            <div class="w-100">
                <apexchart type="bar" :options="chartOptions" :series="chartSeries" height="200"></apexchart>
            </div>
        </div>
        <div>
            <div v-for="(item, index) in data" :key="index" class="mb-4">
                <v-card class="period-card cursor-pointer" elevation="2" rounded="lg" v-if="timeOption !== 'Tùy chọn'"
                    @click="showExpenseRevenueModal = true; itemIdSelected = item.id">
                    <v-card-title class="text-h6 font-weight-medium">
                        {{ timeOption }} {{ item.unit }}
                    </v-card-title>
                    <v-card-text>
                        <div class="d-flex flex-column gap-2">
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1">Thu nhập</span>
                                <span class="text-primary text-subtitle-1 font-weight-medium">
                                    {{ formatCurrency(item.totalRevenue) }}
                                </span>
                            </div>
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1">Chi tiêu</span>
                                <span class="text-red-accent-3 text-subtitle-1 font-weight-medium">
                                    {{ formatCurrency(item.totalExpense) }}
                                </span>
                            </div>
                            <v-divider class="my-2"></v-divider>
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1 font-weight-bold"></span>
                                <span class="text-grey-darken-4 text-subtitle-1 font-weight-bold">
                                    {{ formatCurrency(item.totalRevenue - item.totalExpense) }}
                                </span>
                            </div>
                        </div>
                    </v-card-text>
                    <v-dialog v-if="itemIdSelected === item.id" v-model="showExpenseRevenueModal" width="auto">
                        <expense-revenue-modal :title="timeOption + ' ' + item.unit" :bucketPaymentIds="bucketPaymentIds" :timeOption="timeOption"
                            :month="item.unit" :quarter="item.unit"
                            :year="timeOption == 'Năm' ? item.unit : year"
                            :startDate="startDate" :endDate="endDate"
                            :totalExpense="item.totalExpense" :totalRevenue="item.totalRevenue"
                        ></expense-revenue-modal>
                    </v-dialog>
                </v-card>
            </div>
            <div class="mb-4" v-if="timeOption === 'Tùy chọn'">
                <v-card class="cursor-pointer period-card" v-if="data.totalExpense || data.totalRevenue" elevation="2" rounded="lg" @click="showExpenseRevenueModal = true;">
                    <v-card-text>
                        <div class="d-flex flex-column gap-2">
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1">Thu nhập</span>
                                <span class="text-primary text-subtitle-1 font-weight-medium">
                                    {{ formatCurrency(data.totalRevenue) }}
                                </span>
                            </div>
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1">Chi tiêu</span>
                                <span class="text-red-accent-3 text-subtitle-1 font-weight-medium">
                                    {{ formatCurrency(data.totalExpense) }}
                                </span>
                            </div>
                            <v-divider class="my-2"></v-divider>
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1 font-weight-bold"></span>
                                <span class="text-grey-darken-4 text-subtitle-1 font-weight-bold">
                                    {{ formatCurrency(data.totalRevenue - data.totalExpense) }}
                                </span>
                            </div>
                        </div>
                    </v-card-text>
                </v-card>
                <v-dialog v-model="showExpenseRevenueModal" width="auto">
                    <expense-revenue-modal :title="startDate + ' -> ' + endDate" :bucketPaymentIds="bucketPaymentIds" :timeOption="timeOption"
                        :startDate="startDate" :endDate="endDate"
                        :totalExpense="data.totalExpense" :totalRevenue="data.totalRevenue"
                    ></expense-revenue-modal>
                </v-dialog>
            </div>
        </div>
    </v-card>
</template>

<style lang="scss" scoped>
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
