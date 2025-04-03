<script setup>
import { ref, watch, getCurrentInstance, onMounted, inject } from "vue";
import { useRouter, useRoute } from "vue-router";
import { MainFeature } from "@/constants/MainFeature.js";
import DictionaryExpense from "@components/DictionaryExpense.vue";
import TripEvent from "@components/TripEvent.vue";
import Beneficiary from "@components/Beneficiary.vue";
import { AccountType } from "@/constants/AccountType.js";

const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();
const swal = inject("$swal");
const expenseId = route.params.expenseId;

const mainFeatureList = ref(MainFeature);
const feature = ref(mainFeatureList.value.find((value) => value.id === 1));
const currentTime = ref();
const dictionaryBucketPayment = ref([]);
const account = ref({});
const categorySelected = ref({});
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

onMounted(async () => {
  await proxy.$api
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

  await proxy.$api.get("/expense-regular/" + expenseId).then((res) => {
    expense.value = res.result;
    account.value = dictionaryBucketPayment.value.find(
      (d) => d.id === expense.value.dictionaryBucketPayment.id
    );
    Object.assign(categorySelected.value, expense.value.dictionaryExpense);
    Object.assign(tripEventSelected.value, expense.value.tripEvent);
    Object.assign(beneficiarySelected.value, expense.value.beneficiary);
    currentTime.value = expense.value.expenseDate;
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

  if (Object.keys(account.value).length === 0) {
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

async function handleUpdateExpense() {
  if (!isValid()) {
    return;
  }

  if (Object.keys(account.value).length !== 0) {
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

  expense.value.expenseDate =
    currentTime.value instanceof Date
      ? new Date(
          currentTime.value.getTime() -
            currentTime.value.getTimezoneOffset() * 60000
        )
          .toISOString()
          .slice(0, 19)
          .replace("T", " ")
      : currentTime.value;

  await proxy.$api
    .put("/expense-regular/" + expenseId, expense.value)
    .then(() => {
      swal.fire({
        title: "Thành công",
        text: "Bạn đã chỉnh sửa khoản chi thành công!",
        icon: "success",
      });
      router.push(route.query.redirect ? route.query.redirect : "/home");
    })
    .catch((error) => {
      if (error.response.data.code === 8001) {
        errMsg.value = "Không được sửa khoản chi đã quá 1 tháng";
      }
    });
}

async function handleDeleteExpense() {
  swal
    .fire({
      title: "Lưu ý",
      text: "Dữ liệu bị xóa sẽ không thể khôi phục sau khi xóa. Bạn có muốn tiếp tục ?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      cancelButtonText: "Không",
      confirmButtonText: "Có",
      reverseButtons: true,
    })
    .then(async (result) => {
      if (result.isConfirmed) {
        await proxy.$api
          .delete("/expense-regular/" + expenseId, {})
          .then(() => {
            swal.fire({
              title: "Thành công",
              text: "Bạn đã xóa khoản chi thành công!",
              icon: "success",
            });
            router.push(route.query.redirect ? route.query.redirect : "/home");
          });
      }
    });
}
</script>

<template>
  <div class="account-info text-grey-color">
    <div class="text-primary d-flex justify-center position-relative mb-10">
      <router-link
        to="/account"
        class="position-absolute top-0 left-0 text-primary text-decoration-none"
      >
        <font-awesome-icon
          :icon="['fas', 'clock-rotate-left']"
          style="font-size: 32px"
        />
      </router-link>
      <v-select
        variant="solo"
        rounded
        v-model="feature"
        :items="mainFeatureList"
        item-title="name"
        :return-object="true"
        class="text-grey-color d-inline-block"
        width="20%"
        max-width="20%"
        hide-details="true"
      >
        <template v-slot:item="{ props, item }">
          <v-list-item v-bind="props" :to="item.raw.href">
            <template v-slot:prepend>
              <v-avatar start>
                <font-awesome-icon class="text-primary" :icon="item.raw.icon" />
              </v-avatar>
            </template>
          </v-list-item>
        </template>
        <template v-slot:selection="{ item }">
          <div>
            <v-avatar start>
              <font-awesome-icon class="text-primary" :icon="item.raw.icon" />
            </v-avatar>
            <span class="text-primary text-20">{{ item.raw.name }}</span>
          </div>
        </template>
      </v-select>
    </div>
    <div class="mb-10">
      <v-row justify="end">
        <v-col cols="2" class="d-flex align-center">
          <el-date-picker
            v-model="currentTime"
            type="datetime"
            placeholder="Select date and time"
          />
        </v-col>
      </v-row>
      <v-row class="mb-2 align-center" justify="space-between">
        <v-col cols="3">
          <div class="flex-center flex-column text-20">
            <div class="flex-center w-100">
              <v-text-field
                v-model="expense.amount"
                label="Số tiền"
                type="number"
                hide-details="auto"
                class="text-red-accent-3 font-weight-bold text-end"
                bg-color="bg-white"
                hide-spin-buttons
              >
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
          <v-select
            label="Chọn tài khoản"
            variant="solo"
            rounded
            v-model="account"
            :items="dictionaryBucketPayment"
            item-title="name"
            :return-object="true"
            class="text-grey-color d-inline-block"
            width="100%"
            hide-details="true"
            no-data-text="Không tìm thấy"
          >
            <template v-slot:item="{ props, item }">
              <v-list-item
                v-bind="props"
                :prepend-avatar="item.raw?.accountType?.icon"
                :title="item.raw?.accountName"
              ></v-list-item>
            </template>
            <template v-slot:selection="{ item }">
              <div>
                <v-avatar start>
                  <img
                    class="icon-size"
                    :src="item.raw?.accountType?.icon"
                    alt="icon"
                  />
                </v-avatar>
                <span class="text-grey-color">{{ item.raw?.accountName }}</span>
              </div>
            </template>
          </v-select>
        </v-col>
        <v-col cols="3">
          <v-btn
            elevation="4"
            rounded="xl"
            size="x-large"
            @click="showPopupCategory = true"
          >
            <template v-if="Object.keys(categorySelected).length > 0">
              <div>
                <img :src="categorySelected.iconUrl" alt="" />
              </div>
              <span class="text-16" style="text-transform: none">{{
                categorySelected.name
              }}</span>
              <v-tooltip activator="parent" location="bottom"
                >Hạng mục</v-tooltip
              >
            </template>
            <template v-else>
              <font-awesome-icon
                :icon="['fas', 'circle-question']"
                class="text-20 mr-2"
              />
              <span style="text-transform: none">Chọn hạng mục</span>
            </template>
          </v-btn>
        </v-col>
      </v-row>
      <v-row class="mb-2 align-center" justify="space-between">
        <v-col cols="3">
          <div class="flex-center flex-column text-20">
            <div class="flex-center w-100">
              <v-text-field
                label="Địa điểm"
                hide-details="auto"
                class="text-grey-color text-end"
                bg-color="bg-white"
                hide-spin-buttons
              >
                <template v-slot:prepend>
                  <v-avatar class="flex-center">
                    <font-awesome-icon :icon="['fas', 'location-dot']" />
                  </v-avatar>
                </template>
              </v-text-field>
            </div>
          </div>
        </v-col>
        <v-col cols="3">
          <v-btn
            elevation="4"
            rounded="xl"
            size="x-large"
            @click="showPopupTripEvent = true"
          >
            <template v-if="Object.keys(tripEventSelected).length > 0">
              <span class="text-16" style="text-transform: none">{{
                tripEventSelected.name
              }}</span>
              <v-tooltip activator="parent" location="bottom"
                >Chuyến đi/sự kiện</v-tooltip
              >
            </template>
            <template v-else>
              <font-awesome-icon
                :icon="['fas', 'house']"
                class="text-20 mr-2"
              />
              <span style="text-transform: none">Chuyến đi/sự kiện</span>
            </template>
          </v-btn>
        </v-col>
        <v-col cols="3">
          <v-btn
            elevation="4"
            rounded="xl"
            size="x-large"
            @click="showBeneficiary = true"
          >
            <template v-if="Object.keys(beneficiarySelected).length > 0">
              <span class="text-16" style="text-transform: none">{{
                beneficiarySelected.name
              }}</span>
              <v-tooltip activator="parent" location="bottom"
                >Chi cho ai</v-tooltip
              >
            </template>
            <template v-else>
              <font-awesome-icon :icon="['fas', 'user']" class="text-20 mr-2" />
              <span style="text-transform: none">Chi cho ai</span>
            </template>
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="3">
          <v-textarea
            v-model="expense.interpretation"
            class="text-grey-color"
            label="Diễn giải"
            bg-color="bg-white"
            rows="1"
            auto-grow
            hide-details="auto"
            clearable
          >
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
        <dictionary-expense v-model="categorySelected"></dictionary-expense>
      </v-dialog>
      <v-dialog v-model="showPopupTripEvent" width="auto">
        <trip-event v-model="tripEventSelected"></trip-event>
      </v-dialog>
      <v-dialog v-model="showBeneficiary" width="auto">
        <Beneficiary v-model="beneficiarySelected"></Beneficiary>
      </v-dialog>
    </div>
    <div class="text-center">
      <div>
        <button
          class="bg-white text-red-accent-3 border-sm py-2 px-10 rounded d-inline-flex justify-center mr-10 hover-bg-grey-darken"
          style="border-color: #ff1744 !important"
          @click.stop="handleDeleteExpense"
        >
          <div class="mr-2">
            <font-awesome-icon :icon="['far', 'trash-can']" />
          </div>
          Xóa
        </button>
        <button
          class="bg-blue-lighten-1 text-white py-2 px-10 rounded d-inline-flex justify-center hover-opacity-06"
          @click.stop="handleUpdateExpense"
        >
          <div class="mr-2">
            <font-awesome-icon :icon="['fas', 'floppy-disk']" />
          </div>
          Lưu
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
