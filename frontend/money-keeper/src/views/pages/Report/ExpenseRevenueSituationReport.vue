<script setup>
import { ref, onMounted, watch } from "vue";
import { formatCurrency } from "@/utils/Format";
import ExpenseRevenueChart from "@/views/components/ExpenseRevenueChart.vue";
import AccountModal from "@components/AccountModal.vue";

const TimeOptionReportPage = ["Hiện tại", "Tháng", "Quý", "Năm", "Tùy chọn"]
const timeOptions = ref(TimeOptionReportPage);
const timeOption = ref("Hiện tại");
const startDate = ref("");
const endDate = ref("");
const date = ref(new Date());
const showAccountModal = ref(false);
const account = ref([]);
const rangeYear = ref([new Date(), new Date()]);

onMounted(async () => {

});

async function getData() {

}

watch(timeOption, async () => {
    if (timeOption.value !== 'Tùy chọn') {
        startDate.value = '';
        endDate.value = '';
        date.value = new Date();
        await getData();
    }
});

watch(startDate, async () => {
    await getData();
    console.log(typeof startDate.value);
});

watch(endDate, async () => {
    await getData();
});

watch(date, async () => {
    await getData();
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
                    <el-date-picker class="mr-2" v-model="startDate" type="date" placeholder="Ngày bắt đầu"
                        size="large" />
                    <el-date-picker v-model="endDate" type="date" placeholder="Ngày kết thúc" size="large" />
                </v-col>
            </template>
            <template v-else-if="timeOption === 'Năm'">
                <el-date-picker v-model="rangeYear" type="yearrange" range-separator="đến" start-placeholder="Start Year"
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
            :year="date.getFullYear()" :startDate="startDate" :endDate="endDate" :startYear="rangeYear[0].getFullYear()"
            :endYear="rangeYear[1].getFullYear()" />
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
</style>