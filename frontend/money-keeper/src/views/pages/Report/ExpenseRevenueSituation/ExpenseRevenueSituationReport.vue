<script setup>
import { ref, onMounted, watch } from "vue";
import { formatCurrency } from "@/utils/Format";
import ExpenseRevenueChart from "@/views/components/ExpenseRevenueChart.vue";
import { useReportStore } from "@/store/ReportStore.js";
import AccountModal from "@components/AccountModal.vue";
import ExpenseRevenueModal from "@pages/Report/ExpenseRevenueSituation/ExpenseRevenueModal.vue";

const reportStore = useReportStore();

const TimeOptionReportPage = ["Hiện tại", "Tháng", "Quý", "Năm", "Tùy chọn"]
const timeOptions = ref(TimeOptionReportPage);
const timeOption = ref("Hiện tại");
const startDate = ref("");
const endDate = ref("");
const date = ref(new Date());
const showAccountModal = ref(false);
const showExpenseRevenueModal = ref(false);
const account = ref([]);
const yearRange = ref([new Date(), new Date()]);
const dateRange = ref([new Date(), new Date()]);
const data = ref(null);
const presentOptionSelected = ref();

onMounted(async () => {
    await getDataPresent();
});

async function getDataPresent() {
    data.value = await reportStore.getTotalExpenseRevenueForExpenseRevenueSituation(
        account.value.length > 0 ? account.value.join(",") : null,
        timeOption.value,
        null,
        null,
        null,
        null,
        null
    );
}

watch(timeOption, async () => {
    if (timeOption.value === 'Hiện tại') {
        getDataPresent();
    }
});

function handleConfirmAccount() {
    showAccountModal.value = false;
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
            <template v-if="timeOption === 'Tùy chọn'">
                <v-col cols="5">
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="đến" start-placeholder="Ngày bắt đầu"
                    end-placeholder="Ngày kết thúc" />
                </v-col>
            </template>
            <template v-else-if="timeOption === 'Năm'">
                <el-date-picker v-model="yearRange" type="yearrange" range-separator="đến" start-placeholder="Start Year"
                    end-placeholder="End Year" />
            </template>
            <template v-else>
                <v-col cols="5">
                    <el-date-picker class="mr-2" v-model="date" type="year" placeholder="Năm" size="large"
                        :readonly="timeOption === 'Hiện tại'" :clearable="false" />
                </v-col>
            </template>
        </v-row>
        <expense-revenue-chart v-if="timeOption !== 'Hiện tại'" :account="account" :timeOption="timeOption"
            :year="date.getFullYear()" :startDate="dateRange[0]" :endDate="dateRange[1]" :startYear="yearRange[0].getFullYear()"
            :endYear="yearRange[1].getFullYear()" />
        <v-row v-if="timeOption === 'Hiện tại'" class="pa-3">
            <v-col cols="12" sm="6" md="4" v-for="(period, index) in [
                { title: 'Hôm nay', revenue: data?.todayRevenue, expense: data?.todayExpense },
                { title: 'Tuần này', revenue: data?.weekRevenue, expense: data?.weekExpense },
                { title: 'Tháng này', revenue: data?.monthRevenue, expense: data?.monthExpense },
                { title: 'Quý này', revenue: data?.quarterRevenue, expense: data?.quarterExpense },
                { title: 'Năm nay', revenue: data?.yearRevenue, expense: data?.yearExpense }
            ]" :key="index">
                <v-card class="period-card" elevation="2" rounded="lg" @click="showExpenseRevenueModal = true; presentOptionSelected = period.title">
                    <v-card-title class="text-h6 font-weight-medium">{{ period.title }}</v-card-title>
                    <v-card-text>
                        <div class="d-flex flex-column gap-2">
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1">Thu nhập</span>
                                <span class="text-primary text-subtitle-1 font-weight-medium">
                                    {{ formatCurrency(period.revenue) }}
                                </span>
                            </div>
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1">Chi tiêu</span>
                                <span class="text-red-accent-3 text-subtitle-1 font-weight-medium">
                                    {{ formatCurrency(period.expense) }}
                                </span>
                            </div>
                            <v-divider class="my-2"></v-divider>
                            <div class="d-flex justify-space-between align-center">
                                <span class="text-subtitle-1 font-weight-bold"></span>
                                <span class="text-grey-darken-4 text-subtitle-1 font-weight-bold">
                                    {{ formatCurrency(period.revenue - period.expense) }}
                                </span>
                            </div>
                        </div>
                    </v-card-text>
                </v-card>
                <v-dialog v-if="presentOptionSelected === period.title" v-model="showExpenseRevenueModal" width="auto">
                    <expense-revenue-modal :bucketPaymentIds="account.length > 0 ? account.join(',') : null" :timeOption="timeOption"
                                        :presentOption="period.title"
                                        :totalExpense="period.expense" :totalRevenue="period.revenue"
                    ></expense-revenue-modal>
                </v-dialog>
            </v-col>
        </v-row>
    </v-container>
    <v-dialog v-model="showAccountModal" width="auto">
        <account-modal v-model="account" @confirm="handleConfirmAccount"></account-modal>
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