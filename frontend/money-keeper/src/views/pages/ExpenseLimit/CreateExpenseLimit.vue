<script setup>
import { ref, watch, getCurrentInstance, onMounted, inject } from "vue";
import { useRouter, useRoute } from "vue-router";
import DictionaryExpense from "@components/DictionaryExpense.vue";
import AccountModal from "@components/AccountModal.vue";
import { ExpenseLimitLoopTime } from "@/constants/ExpenseLimitLoopTime.js";
import { formatDate, parseDateString } from "@/utils/DateUtil.js";

const { proxy } = getCurrentInstance();
const router = useRouter();
const swal = inject("$swal");
const route = useRoute();

const timeOptions = ref(ExpenseLimitLoopTime)
const dictionaryBucketPayment = ref([]);
const account = ref([]);
const categories = ref([]);
const showPopupCategory = ref(false);

const errMsg = ref("");
const showAccountModal = ref(false);

const expenseLimit = ref({
    amount: 0,
    name: "",
    categoriesId: "",
    bucketPaymentIds: "",
    repeatTime: "Hàng tháng",
    startDate: (() => {
        const date = new Date();
        date.setHours(0, 0, 0, 0);
        return date;
    })(),
    endDate: "",
});

function isValid() {
    const numberRegex = /^(0|[1-9]\d*)$/; // Cho phép số 0 hoặc số dương không bắt đầu bằng 0

    if (!numberRegex.test(expenseLimit.value.amount)) {
        errMsg.value = "Số tiền không hợp lệ";
        return false;
    }

    if (expenseLimit.value.name === "") {
        errMsg.value = "Tên hạn mức không được để trống";
        return false;
    }

    if (expenseLimit.value.amount <= 0) {
        errMsg.value = "Số tiền phải lớn hơn 0";
        return false;
    }

    if (account.value.length === 0) {
        errMsg.value = "Tài khoản không được để trống";
        return false;
    }

    if (categories.value.length === 0) {
        errMsg.value = "Hạng mục không được để trống";
        return false;
    }

    if (expenseLimit.value.startDate === "" || expenseLimit.value.startDate === null) {
        errMsg.value = "Thời điểm chi tiêu không được để trống";
        return false;
    }

    errMsg.value = "";
    return true;
}

async function createExpenseLimit() {
    if (!isValid()) {
        return;
    }

    expenseLimit.value.bucketPaymentIds = account.value.map(val => val.id).join();
    expenseLimit.value.categoriesId = categories.value.map(val => val.id).join();
    expenseLimit.value.startDate = formatDate(expenseLimit.value.startDate);
    if (expenseLimit.value.endDate !== "" && expenseLimit.value.endDate !== null) {
        expenseLimit.value.endDate = formatDate(expenseLimit.value.endDate);
        if(parseDateString(expenseLimit.value.endDate).getTime() <= parseDateString(expenseLimit.value.startDate).getTime()){
            errMsg.value = `Ngày kết thúc phải lớn hơn ${expenseLimit.value.startDate.split(" ")[0]}`;
            return;
        }
    }
    if(expenseLimit.value.repeatTime === "Không lặp lại"){
        expenseLimit.value.endDate = formatDate(expenseLimit.value.endDate);
        if(parseDateString(expenseLimit.value.endDate).getTime() <= parseDateString(expenseLimit.value.startDate).getTime() || (expenseLimit.value.endDate === "" || expenseLimit.value.endDate === null)){
            errMsg.value = `Ngày kết thúc phải lớn hơn hoặc bằng ngày bắt đầu`;
            return;
        }
    }


    await proxy.$api.post("/expense-limit", expenseLimit.value).then(() => {
        swal.fire({
            title: "Thành công",
            text: "Bạn đã thêm khoản hạn mức chi thành công!",
            icon: "success",
        });
        router.push("/expense-limit");
    }).catch((error) =>{
        if(error.response.data.code === 9002){
            errMsg.value = "Ngày kết thúc phải lớn hơn " + error.response.data.message;
        }
        console.log(error)
    });
}

function handleConfirmCategory(){
    showPopupCategory.value = false
}

function handleConfirmAccount() {
    showAccountModal.value = false;
}
</script>

<template>
    <div class="text-grey-color">
        <v-container fluid class="pa-0">
            <v-row align="center" class="ma-0">
                <v-col cols="auto" class="pa-0">
                    <v-btn icon variant="text" class="back-button text-primary" to="/expense-limit">
                        <font-awesome-icon :icon="['fas', 'angle-left']" style="font-size: 32px" />
                    </v-btn>
                </v-col>
                <v-col>
                    <h1 class="text-h4 text-center text-primary mb-0">Thêm hạn mức chi</h1>
                </v-col>
            </v-row>
        </v-container>
        <div class="my-10">
            <v-row class="mb-2 align-center" justify="space-between">
                <v-col cols="3">
                    <div class="flex-center flex-column text-20">
                        <div class="flex-center w-100">
                            <v-text-field v-model="expenseLimit.amount" label="Số tiền" type="number" hide-details="auto"
                                class="text-primary font-weight-bold text-end" bg-color="bg-white"
                                hide-spin-buttons>
                                <template v-slot:prepend>
                                    <v-avatar class="flex-center">
                                        <font-awesome-icon :icon="['fas', 'money-bill']" />
                                    </v-avatar>
                                </template>
                            </v-text-field>
                            <span class="font-weight-bold text-20">₫</span>
                        </div>
                    </div>
                </v-col>

                <v-col cols="4">
                    <v-btn class="cursor-pointer" elevation="4" rounded="xl" size="x-large" @click="showAccountModal = true">
                        <template v-if="account.length > 0">
                            <div class="stacked-images">
                                <template v-for="(item,index) in account" :key="item">
                                    <img v-if="index < 2" :src="item.iconUrl" alt="" 
                                        :class="['stacked-image', `stacked-image-${index + 1}`]" />
                                </template>
                            </div>
                            <template v-for="(item,index) in account" :key="item">
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
                            <font-awesome-icon :icon="['fas', 'circle-question']" class="text-20 mr-2" />
                            <span style="text-transform: none">Chọn tài khoản</span>
                        </template>
                    </v-btn>
                </v-col>
                
                <v-col cols="4">
                    <v-btn class="cursor-pointer" elevation="4" rounded="xl" size="x-large" @click="showPopupCategory = true">
                        <template v-if="categories.length > 0">
                            <div class="stacked-images">
                                <template v-for="(item,index) in categories" :key="item">
                                    <img v-if="index < 2" :src="item.iconUrl" alt="" 
                                        :class="['stacked-image', `stacked-image-${index + 1}`]" />
                                </template>
                            </div>
                            <template v-for="(item,index) in categories" :key="item">
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
                            <font-awesome-icon :icon="['fas', 'circle-question']" class="text-20 mr-2" />
                            <span style="text-transform: none">Chọn hạng mục</span>
                        </template>
                    </v-btn>
                </v-col>
            </v-row>
            <v-row class="mb-2 align-center" justify="space-between">
                <v-col cols="3">
                    <div class="flex-center flex-column text-20">
                        <div class="flex-center w-100">
                            <v-text-field v-model="expenseLimit.name" label="Tên hạn mức" hide-details="auto" class="text-grey-color text-end"
                                bg-color="bg-white" hide-spin-buttons>
                                <template v-slot:prepend>
                                    <v-avatar class="flex-center">
                                        <font-awesome-icon :icon="['fas', 'money-check-dollar']" />
                                    </v-avatar>
                                </template>
                            </v-text-field>
                        </div>
                    </div>
                </v-col>
                <v-col cols="4" class="d-flex align-center">
                    <div class="d-flex flex-column align-center justify-center">
                        <span class="text-14 mb-2 text-start w-100">Ngày bắt đầu</span>
                        <el-date-picker v-model="expenseLimit.startDate" type="date" placeholder="Ngày bắt đầu" size="large" :clearable="false"/>
                    </div>
                </v-col>
                <v-col cols="4" class="d-flex align-center">
                    <div class="d-flex flex-column align-center justify-center">
                        <span class="text-14 mb-2 text-start w-100">Ngày kết thúc</span>
                        <el-date-picker v-model="expenseLimit.endDate" type="date" placeholder="Ngày kết thúc" size="large" />
                    </div>
                </v-col>
            </v-row>
            <v-row justify="space-between">
                <v-col cols="3">
                <v-select
                    v-model="expenseLimit.repeatTime"
                    label="Lặp lại"
                    :items="timeOptions"
                    item-title="option"
                    item-value="option"
                    variant="outlined"
                    density="comfortable"
                    hide-details
                    class="custom-select"
                >
                </v-select>
                </v-col>
            </v-row>
            <v-col cols="12">
                <p class="text-red-accent-3 text-center">{{ errMsg }}</p>
            </v-col>
            <v-dialog v-model="showPopupCategory" width="auto">
                <dictionary-expense v-model="categories" :isMultiple="true" @confirm="handleConfirmCategory"></dictionary-expense>
            </v-dialog>
            <v-dialog v-model="showAccountModal" width="auto">
                <account-modal v-model="account" @confirm="handleConfirmAccount"></account-modal>
            </v-dialog>
        </div>
        <div class="text-center">
            <button class="bg-primary-color text-white py-2 px-10 rounded d-inline-flex justify-center"
                @click.stop="createExpenseLimit">
                <div class="mr-2">
                    <font-awesome-icon :icon="['fas', 'floppy-disk']" />
                </div>
                Ghi
            </button>
        </div>
    </div>
</template>

<style lang="scss" scoped>
.custom-select {
      :deep(.v-field) {
        border-radius: 12px;
      }
}
.stacked-images {
    position: relative;
    width: 40px;
    height: 40px;
    margin-right: 8px;

    .stacked-image {
        position: absolute;
        width: 32px;
        height: 32px;
        border-radius: 50%;
        border: 2px solid white;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        transition: transform 0.2s ease;

        &:hover {
            z-index: 3;
            transform: scale(1.1);
        }
    }

    .stacked-image-1 {
        left: 0;
        z-index: 3;
    }

    .stacked-image-2 {
        left: 8px;
        z-index: 2;
    }

    .stacked-image-3 {
        left: 16px;
        z-index: 1;
    }
}
</style>
