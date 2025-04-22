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
        default: "Tình hình chi tiêu",
    },
    bucketPaymentIds: {
        type: String,
        required: true,
    },
    categoriesId: {
        type: String,
        required: true,
    },
    timeOption: {
        type: String,
        required: true,
    },
    date: {
        type: Object,
        default: null,
    },
    yearMonth:{
        type: String,
        default: null,
    },
    year: {
        type: Number,
        default: null,
    },
    totalExpense: {
        type: Number,
        required: true,
    },
});
const reportStore = useReportStore();
const totalExpenseByCategory = ref([]);
const groupedExpenseCategories = computed(() => {
    const groups = []
    for (let i = 0; i < totalExpenseByCategory.value.length; i += lengthWindow.value) {
        groups.push(totalExpenseByCategory.value.slice(i, i + lengthWindow.value))
    }
    return groups
})
const windowExpense = ref(0);
const lengthWindow = ref(4);
const data = ref([]);
onMounted(async () => {
    await nextTick();
    await getData();
});

async function getData() {
    let month, year;
    if(props.timeOption === "Tháng"){
        let temp = props.yearMonth.split("/");
        month = temp[0];
        year = temp[1];
    }else{
        year = props.year;
    }

    data.value = await reportStore.getTotalExpenseExactByTime(
        props.timeOption,
        props.bucketPaymentIds,
        props.categoriesId,
        props.timeOption === "Ngày" ? props.date : null,
        props.timeOption === "Tháng" ? month : null,
        year || null,
    );
    totalExpenseByCategory.value = data.value.filter(item => item.type === "expense" && item.totalExpense > 0);
    if (totalExpenseByCategory.value.length > 0) {
        updateExpensePieSeries();
        updateExpensePieOptions();
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
</script>

<template>
    <v-card class="pa-4 text-grey-color" style="overflow: visible">
        <h2 class="text-center">{{ props.title }}</h2>

        <v-row>
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
