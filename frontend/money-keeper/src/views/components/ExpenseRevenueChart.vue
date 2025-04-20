<script setup>
import { ref, getCurrentInstance, onMounted, computed, watchEffect } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useReportStore } from "@/store/ReportStore.js";
import { useDictionaryBucketPaymentStore } from "@/store/DictionaryBucketPayment";
import { formatCurrency } from "@/utils/format.js";

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

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
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

watchEffect(async() => {
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
                data: data.value.map(item => item.totalExpense),
            },
            {
                name: "Tổng thu",
                data: data.value.map(item => item.totalRevenue),
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
                categories: data.value.map(item => timeOption.value+ " " + item.unit),
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
                <div class="d-flex justify-space-between">
                    <span>{{ timeOption }} {{ item.unit }}</span>
                    <div>
                        <span class="text-primary">
                            {{ formatCurrency(item.totalRevenue) }}
                        </span>
                    </div>
                </div>
                <div class="d-flex justify-space-between">
                    <span></span>
                    <div>
                        <span class="text-red-accent-3">
                            {{ formatCurrency(item.totalExpense) }}
                        </span>
                    </div>
                </div>
                <div class="d-flex justify-space-between">
                    <span></span>
                    <div>
                        <span class="text-grey-darken-4 font-weight-bold">
                            {{ formatCurrency(item.totalRevenue - item.totalExpense) }}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </v-card>
</template>

<style lang="scss" scoped></style>
