<script setup>
import { ref, watch, getCurrentInstance, onMounted, inject } from "vue";
import { useRouter, useRoute } from "vue-router";
import DictionaryExpense from "@components/DictionaryExpense.vue";
import TripEvent from "@components/TripEvent.vue";
import Beneficiary from "@components/Beneficiary.vue";
import AccountModal from "@components/AccountModal.vue";
import { AccountType } from "@/constants/AccountType.js";

const { proxy } = getCurrentInstance();
const router = useRouter();
const swal = inject("$swal");
const route = useRoute();

const currentTime = ref(new Date());
const dictionaryBucketPayment = ref([]);
const account = ref([]);
const categorySelected = ref([]);
const showPopupCategory = ref(false);
watch(categorySelected, () => {
    showPopupCategory.value = false;
});
const tripEventSelected = ref({});
const showPopupTripEvent = ref(false);
watch(tripEventSelected, () => {
    showPopupTripEvent.value = false;
});

const beneficiarySelected = ref({});
const showBeneficiary = ref(false);

watch(beneficiarySelected, () => {
    showBeneficiary.value = false;
});

const errMsg = ref("");
const showAccountModal = ref(false);

onMounted(() => {
    proxy.$api
        .get("/dictionary-bucket-payment")
        .then((res) => {
            dictionaryBucketPayment.value = res.result;
            dictionaryBucketPayment.value.forEach((item) => {
                item.accountType = AccountType.find(
                    (type) => type.name === item.accountType
                );
            });
            account.value = dictionaryBucketPayment.value[0];
        })
        .catch((err) => {
            console.log(err);
        });
});

const expense = ref({
    amount: 0,
    location: "",
    interpretation: "",
    expenseDate: "",
    dictionaryBucketPaymentId: "",
    dictionaryExpenseId: "",
    tripEventId: "",
    beneficiaryId: "",
});

function isValid() {
    const numberRegex = /^(0|[1-9]\d*)$/; // Cho phép số 0 hoặc số dương không bắt đầu bằng 0

    if (!numberRegex.test(expense.value.amount)) {
        errMsg.value = "Số tiền không hợp lệ";
        return false;
    }

    if (account.value.length === 0) {
        errMsg.value = "Tài khoản không được để trống";
        return false;
    }

    if (Object.keys(categorySelected.value).length === 0) {
        errMsg.value = "Hạng mục không được để trống";
        return false;
    }

    if (currentTime.value === "" || currentTime.value === null) {
        errMsg.value = "Thời điểm chi tiêu không được để trống";
        return false;
    }

    errMsg.value = "";
    return true;
}

async function createExpense() {
    if (!isValid()) {
        return;
    }

    if (account.value.length !== 0) {
        expense.value.dictionaryBucketPaymentId = account.value.id;
    }

    if (Object.keys(categorySelected.value).length !== 0) {
        expense.value.dictionaryExpenseId = categorySelected.value.id;
    }

    if (Object.keys(tripEventSelected.value).length !== 0) {
        expense.value.tripEventId = tripEventSelected.value.id;
    }

    if (Object.keys(beneficiarySelected.value).length !== 0) {
        expense.value.beneficiaryId = beneficiarySelected.value.id;
    }

    expense.value.expenseDate = currentTime.value instanceof Date
        ? new Date(currentTime.value.getTime() - (currentTime.value.getTimezoneOffset() * 60000))
            .toISOString()
            .slice(0, 19)
            .replace('T', ' ')
        : currentTime.value;

    await proxy.$api.post("/expense-regular", expense.value).then(() => {
        swal.fire({
            title: "Thành công",
            text: "Bạn đã thêm khoản chi tiền thành công!",
            icon: "success",
        });
        router.push("/expense");
    });
}

function handleConfirmCategory(selectedItems) {
    
}

function handleConfirmAccount(selectedAccounts) {
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
                            <v-text-field v-model="expense.amount" label="Số tiền" type="number" hide-details="auto"
                                class="text-red-accent-3 font-weight-bold text-end" bg-color="bg-white"
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
                <v-col cols="3">
                    <div class="flex-center flex-column text-20">
                        <div class="flex-center w-100">
                            <v-text-field label="Tên hạn mức" hide-details="auto" class="text-grey-color text-end"
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
                <v-col cols="3">
                    <v-btn class="cursor-pointer" elevation="4" rounded="xl" size="x-large" @click="showPopupCategory = true">
                        <template v-if="Object.keys(categorySelected).length > 0">
                            <div class="stacked-images">
                                <template v-for="(item,index) in categorySelected" :key="item">
                                    <img v-if="index < 3" :src="item.iconUrl" alt="" 
                                        :class="['stacked-image', `stacked-image-${index + 1}`]" />
                                </template>
                            </div>
                            <template v-for="(item,index) in categorySelected" :key="item">
                                <span v-if="index === 0" class="text-14" style="text-transform: none">{{
                                item.name
                                }}</span>
                                <span v-if="index === 1" class="text-14" style="text-transform: none">{{
                                ", " + item.name
                                }}</span>
                            </template>
                            <span v-if="categorySelected.length > 2" class="text-14" style="text-transform: none">{{
                                " + " + (categorySelected.length - 2) + " hạng mục khác"
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
                    <v-btn class="cursor-pointer" elevation="4" rounded="xl" size="x-large" @click="showAccountModal = true">
                        <template v-if="account.length > 0">
                            <div class="stacked-images">
                                <template v-for="(item,index) in account" :key="item">
                                    <img v-if="index < 3" :src="item.accountType?.icon" alt="" 
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
                <v-col cols="3">
                    <v-btn elevation="4" rounded="xl" size="x-large" @click="showPopupTripEvent = true">
                        <template v-if="Object.keys(tripEventSelected).length > 0">
                            <span class="text-16" style="text-transform: none">{{
                                tripEventSelected.name
                                }}</span>
                            <v-tooltip activator="parent" location="bottom">Chuyến đi/sự kiện</v-tooltip>
                        </template>
                        <template v-else>
                            <font-awesome-icon :icon="['fas', 'house']" class="text-20 mr-2" />
                            <span style="text-transform: none">Chuyến đi/sự kiện</span>
                        </template>
                    </v-btn>
                </v-col>
                <v-col cols="3">
                    <v-btn elevation="4" rounded="xl" size="x-large" @click="showBeneficiary = true">
                        <template v-if="Object.keys(beneficiarySelected).length > 0">
                            <span class="text-16" style="text-transform: none">{{
                                beneficiarySelected.name
                                }}</span>
                            <v-tooltip activator="parent" location="bottom">Chi cho ai</v-tooltip>
                        </template>
                        <template v-else>
                            <font-awesome-icon :icon="['fas', 'user']" class="text-20 mr-2" />
                            <span style="text-transform: none">Chi cho ai</span>
                        </template>
                    </v-btn>
                </v-col>
            </v-row>
            <v-row justify="space-between">
                <v-col cols="3" class="d-flex align-center">
                    <div class="d-flex flex-column align-center justify-center">
                        <span class="text-14 mb-2">Ngày bắt đầu</span>
                        <el-date-picker v-model="currentTime" type="date" placeholder="Ngày bắt đầu" size="large" />
                    </div>
                </v-col>
                <v-col cols="3" class="d-flex align-center">
                    <div class="d-flex flex-column align-center justify-center">
                        <span class="text-14 mb-2">Ngày kết thúc</span>
                        <el-date-picker v-model="currentTime" type="date" placeholder="Ngày kết thúc" size="large" />
                    </div>
                </v-col>
                <v-col cols="3">
                    <v-textarea v-model="expense.interpretation" class="text-grey-color" label="Diễn giải"
                        bg-color="bg-white" rows="1" auto-grow hide-details="auto" clearable>
                        <template v-slot:prepend>
                            <v-avatar class="flex-center">
                                <font-awesome-icon :icon="['far', 'rectangle-list']" />
                            </v-avatar>
                        </template>
                    </v-textarea>
                </v-col>
            </v-row>
            <v-col cols="12">
                <p class="text-red-accent-3 text-center">{{ errMsg }}</p>
            </v-col>
            <v-dialog v-model="showPopupCategory" width="auto">
                <dictionary-expense v-model="categorySelected" :isMultiple="true" @confirm="handleConfirmCategory"></dictionary-expense>
            </v-dialog>
            <v-dialog v-model="showPopupTripEvent" width="auto">
                <trip-event v-model="tripEventSelected"></trip-event>
            </v-dialog>
            <v-dialog v-model="showBeneficiary" width="auto">
                <Beneficiary v-model="beneficiarySelected"></Beneficiary>
            </v-dialog>
            <v-dialog v-model="showAccountModal" width="auto">
                <account-modal v-model="account" @confirm="handleConfirmAccount"></account-modal>
            </v-dialog>
        </div>
        <div class="text-center">
            <button class="bg-primary-color text-white py-2 px-10 rounded d-inline-flex justify-center"
                @click.stop="createExpense">
                <div class="mr-2">
                    <font-awesome-icon :icon="['fas', 'floppy-disk']" />
                </div>
                Ghi
            </button>
        </div>
    </div>
</template>

<style lang="scss" scoped>
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
