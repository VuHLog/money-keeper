<script setup>
import { ref, onMounted, getCurrentInstance, inject } from "vue";
import { AccountType } from "@/constants/AccountType.js";
import { useRouter, useRoute } from "vue-router";

const { proxy } = getCurrentInstance();
const router = useRouter();
const swal = inject("$swal");
const accountTypeList = ref(AccountType);
const accountType = ref();
const route = useRoute();
const accountId = route.params.accountId;

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
    await proxy.$api.get("/dictionary-bucket-payment/" + accountId).then((res) => {
        let result = res.result;
        bucketPayment.value.balance = result.balance;
        bucketPayment.value.creditLimit = result?.creditLimit;
        bucketPayment.value.accountName = result.accountName;
        bucketPayment.value.interpretation = result.interpretation;
        accountType.value = accountTypeList.value.find((item) => item.name === result.accountType)
        bank.value = result.bank;
    })
    await proxy.$api.get("/banks").then((res) => {
        banks.value = res.result;
    });
});

function isValid() {
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
        (accountType.value.id === 2 && Object.keys(bank.value).length === 0) ||
        (accountType.value.id === 3 && Object.keys(bank.value).length === 0)
    ) {
        errMsg.value = "Ngân hàng không được để trống";
        return false;
    }
    errMsg.value = "";
    return true;
}

async function updateAccount() {
    if (!isValid()) {
        return;
    }

    bucketPayment.value.accountType = accountType.value.name;
    if (accountType.value.id === 2 || accountType.value.id === 3) {
        bucketPayment.value.bankId = bank.value.id;
    }
    await proxy.$api
        .put("/dictionary-bucket-payment/" + accountId, bucketPayment.value)
        .then(() => {
            swal.fire({
                title: "Thành công",
                text: "Bạn đã cập nhật tài khoản thành công!",
                icon: "success",
            });
            router.push("/account")
        });
}

async function handleDeleteAccount() {
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
          .delete("/dictionary-bucket-payment/" + accountId, {})
          .then(() => {
            swal.fire({
              title: "Thành công",
              text: "Bạn đã xóa tài khoản thành công!",
              icon: "success",
            });
            router.push("/account");
          });
      }
    });
}
</script>

<template>
    <div>
        <div class="text-primary position-relative mb-10">
            <div class="text-primary d-flex justify-space-between position-relative mb-10">
                <v-btn icon variant="text" class="back-button d-flex align-center text-primary"
                    to="/account">
                    <font-awesome-icon :icon="['fas', 'angle-left']" style="font-size: 32px" />
                </v-btn>
                <h1>Thông tin tài khoản</h1>
                <router-link :to="{ path: '/account/info/' + accountId, query: { redirect: route.fullPath } }"
                    class="text-primary d-flex align-center text-decoration-none">
                    <font-awesome-icon :icon="['fas', 'clock-rotate-left']" style="font-size: 32px" />
                    <v-tooltip activator="parent" location="bottom">Lịch sử ghi chép</v-tooltip>
                </router-link>
            </div>
        </div>
        <v-row class="mb-10">
            <v-col cols="6">
                <v-text-field v-model="bucketPayment.balance" class="text-blue-accent-3" hide-details="auto"
                    label="Số dư" :readonly="true"></v-text-field>
            </v-col>
            <v-col cols="6" v-show="accountType?.id === 3">
                <v-text-field v-model="bucketPayment.creditLimit" hide-details="auto"
                    label="Hạn mức tín dụng"></v-text-field>
            </v-col>
            <v-col cols="6">
                <v-text-field v-model="bucketPayment.accountName" hide-details="auto"
                    label="Tên tài khoản"></v-text-field>
            </v-col>
            <v-col cols="6">
                <v-autocomplete v-model="accountType" :items="accountTypeList" color="blue-grey-lighten-2"
                    item-title="name" :return-object="true" label="Loại tài khoản" no-data-text="Không tìm thấy"
                    class="text-grey-color">
                    <template v-slot:item="{ props, item }">
                        <v-list-item v-bind="props" :prepend-avatar="item.raw.icon"
                            :title="item.raw.name"></v-list-item>
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
                <v-autocomplete v-model="bank" :items="banks" color="blue-grey-lighten-2" item-title="shortName"
                    :return-object="true" label="Ngân hàng" no-data-text="Không tìm thấy" class="text-grey-color"
                    clearable>
                    <template v-slot:item="{ props, item }">
                        <v-list-item v-bind="props" :prepend-avatar="item.raw.logo" :title="item.raw.shortName"
                            :subtitle="item.raw.name"></v-list-item>
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
                <v-autocomplete v-model="bank" :items="banks" color="blue-grey-lighten-2" item-title="shortName"
                    :return-object="true" label="Nhà cung cấp" no-data-text="Không tìm thấy" class="text-grey-color"
                    clearable>
                    <template v-slot:item="{ props, item }">
                        <v-list-item v-bind="props" :prepend-avatar="item.raw.logo" :title="item.raw.shortName"
                            :subtitle="item.raw.name"></v-list-item>
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
                <v-textarea v-model="bucketPayment.interpretation" class="text-grey-color" label="Diễn giải"
                    bg-color="bg-white" rows="1" auto-grow hide-details="auto" clearable>
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
            <div>
                <button
                    class="bg-white text-red-accent-3 border-sm py-2 px-10 rounded d-inline-flex justify-center mr-10 hover-bg-grey-darken"
                    style="border-color: #ff1744 !important" @click.stop="handleDeleteAccount">
                    <div class="mr-2">
                        <font-awesome-icon :icon="['far', 'trash-can']" />
                    </div>
                    Xóa
                </button>
                <button
                    class="bg-blue-lighten-1 text-white py-2 px-10 rounded d-inline-flex justify-center hover-opacity-06"
                    @click.stop="updateAccount">
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
