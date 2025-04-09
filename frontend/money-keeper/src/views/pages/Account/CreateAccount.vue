<script setup>
import { ref, onMounted, getCurrentInstance, inject } from "vue";
import { AccountType } from "@/constants/AccountType.js";
import { useRouter } from "vue-router";

const { proxy } = getCurrentInstance();
const router = useRouter();
const swal = inject("$swal");
const accountTypeList = ref(AccountType);
const accountType = ref(accountTypeList.value.find((item) => item.id === 1));

const banks = ref([]);
const bank = ref();

const bucketPayment = ref({
  balance: "",
  creditLimit: "",
  accountName: "",
  accountType: "",
  interpretation: "",
  bankId: "",
});

const errMsg = ref("");

onMounted(async () => {
  await proxy.$api.get("/banks").then((res) => {
    banks.value = res.result;
  });
});

function isValid() {
  const numberRegex = /^-?[1-9]\d*$/; // Cho phép số, không có số 0 ở đầu, có thể có dấu '-'

  if (!numberRegex.test(bucketPayment.value.balance)) {
    errMsg.value = "Số dư ban đầu không hợp lệ";
    return false;
  }

  if (
    bucketPayment.value.accountType.id === 3 &&
    !numberRegex.test(bucketPayment.value.creditLimit)
  ) {
    errMsg.value = "Hạn mức tín dụng không hợp lệ";
    return false;
  }
  if (bucketPayment.value.accountName === "") {
    errMsg.value = "Tên tài khoản không được để trống";
    return false;
  }

  if (Object.keys(accountType.value).length === 0) {
    errMsg.value = "Loại tài khoản không được để trống";
    return false;
  }

  if (
    accountType.value.id === 2 ||
    (accountType.value.id === 3 && Object.keys(bank.value).length === 0)
  ) {
    errMsg.value = "Ngân hàng không được để trống";
    return false;
  }
  errMsg.value = "";
  return true;
}

async function createAccount() {
  if (!isValid()) {
    return;
  }

  bucketPayment.value.accountType = accountType.value.name;
  if(accountType.value.id === 2 || accountType.value.id === 3) {
    bucketPayment.value.bankId = bank.value.id;
  }
  await proxy.$api
    .post("/dictionary-bucket-payment", bucketPayment.value)
    .then(() => {
      swal.fire({
        title: "Thành công",
        text: "Bạn đã thêm tài khoản thành công!",
        icon: "success",
      });
      router.push("/account")
    });
}
</script>

<template>
  <div>
    <div class="text-primary position-relative mb-10">
      <router-link
        to="/account"
        class="position-absolute back-button top-0 left-0 text-primary text-decoration-none"
      >
        <font-awesome-icon
          :icon="['fas', 'angle-left']"
          style="font-size: 32px"
        />
      </router-link>
      <h1 class="text-center">Thêm tài khoản</h1>
    </div>
    <v-row class="mb-10">
      <v-col cols="6">
        <v-text-field
          v-model="bucketPayment.balance"
          class="text-blue-accent-3"
          hide-details="auto"
          label="Số dư ban đầu"
        ></v-text-field>
      </v-col>
      <v-col cols="6" v-show="accountType?.id === 3">
        <v-text-field
          v-model="bucketPayment.creditLimit"
          hide-details="auto"
          label="Hạn mức tín dụng"
        ></v-text-field>
      </v-col>
      <v-col cols="6">
        <v-text-field
          v-model="bucketPayment.accountName"
          hide-details="auto"
          label="Tên tài khoản"
        ></v-text-field>
      </v-col>
      <v-col cols="6">
        <v-autocomplete
          v-model="accountType"
          :items="accountTypeList"
          color="blue-grey-lighten-2"
          item-title="name"
          :return-object="true"
          label="Loại tài khoản"
          no-data-text="Không tìm thấy"
          class="text-grey-color"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item
              v-bind="props"
              :prepend-avatar="item.raw.icon"
              :title="item.raw.name"
            ></v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <v-avatar start>
                <img class="icon-size" :src="item.raw.icon" alt="icon" />
              </v-avatar>
              <span class="text-grey-color">{{ item.raw.name }}</span>
            </div>
          </template>
        </v-autocomplete>
      </v-col>
      <v-col cols="6" v-show="accountType?.id === 2 || accountType?.id === 3">
        <v-autocomplete
          v-model="bank"
          :items="banks"
          color="blue-grey-lighten-2"
          item-title="shortName"
          :return-object="true"
          label="Ngân hàng"
          no-data-text="Không tìm thấy"
          class="text-grey-color"
          clearable
        >
          <template v-slot:item="{ props, item }">
            <v-list-item
              v-bind="props"
              :prepend-avatar="item.raw.logo"
              :title="item.raw.shortName"
              :subtitle="item.raw.name"
            ></v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <v-avatar start>
                <img :src="item.raw.logo" alt="icon" style="width: 50px" />
              </v-avatar>
              <span class="text-grey-color">{{ item.raw.shortName }}</span>
            </div>
          </template>
        </v-autocomplete>
      </v-col>
      <v-col cols="6" v-show="accountType?.id === 4">
        <v-autocomplete
          v-model="bank"
          :items="banks"
          color="blue-grey-lighten-2"
          item-title="shortName"
          :return-object="true"
          label="Nhà cung cấp"
          no-data-text="Không tìm thấy"
          class="text-grey-color"
          clearable
        >
          <template v-slot:item="{ props, item }">
            <v-list-item
              v-bind="props"
              :prepend-avatar="item.raw.logo"
              :title="item.raw.shortName"
              :subtitle="item.raw.name"
            ></v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <v-avatar start>
                <img :src="item.raw.logo" alt="icon" style="width: 50px" />
              </v-avatar>
              <span class="text-grey-color">{{ item.raw.shortName }}</span>
            </div>
          </template>
        </v-autocomplete>
      </v-col>
      <!-- <v-col cols="6">
        <v-autocomplete
          v-model="accountType"
          :items="accountTypeList"
          color="blue-grey-lighten-2"
          item-title="name"
          item-value="name"
          label="Loại tiền tệ"
          no-data-text="Không tìm thấy"
          class="text-grey-color"
          clearable
        >
          <template v-slot:item="{ props, item }">
            <v-list-item
              v-bind="props"
              :prepend-avatar="item.raw.icon"
              :title="item.raw.name"
            ></v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <v-avatar start>
                <img class="h-100 py-1" :src="item.raw.icon" alt="icon" />
              </v-avatar>
              <span class="text-grey-color">{{ item.raw.name }}</span>
            </div>
          </template>
        </v-autocomplete>
      </v-col> -->
      <v-col cols="12">
        <v-textarea
          v-model="bucketPayment.interpretation"
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
      <v-col cols="12">
        <p class="text-red-accent-3 text-center">{{ errMsg }}</p>
      </v-col>
    </v-row>
    <div class="text-center">
      <button class="bg-primary-color text-white py-2 px-10 rounded d-inline-flex justify-center" @click.stop="createAccount">
        <div class="mr-2">
          <font-awesome-icon :icon="['fas', 'floppy-disk']" />
        </div>
        Lưu
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
