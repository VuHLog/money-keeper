<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { MainFeature } from "@/constants/MainFeature.js";
import { AccountType } from "@/constants/AccountType.js";

const { proxy } = getCurrentInstance();

const mainFeatureList = ref(MainFeature);
const feature = ref({
  id: 5,
  name: "Chuyển khoản",
  icon: ["fas", "right-left"],
  href: "/account/transfer/id",
});

const currentTime = ref(new Date());
const fromAccount = ref();
const toAccount = ref();
const dictionaryBucketPayment = ref([]);
const errMsg = ref("");

const transfer = ref({
  amount: "",
  fromAccount: "",
  toAccount: "",
  interpretation: "",
});

function isValid() {
  const numberRegex = /^[1-9]\d*$/; // Cho phép số, không có số 0 ở đầu

  if (!numberRegex.test(transfer.value.amount)) {
    errMsg.value = "Số tiền không hợp lệ";
    return false;
  }
  if (Object.keys(fromAccount.value).length === 0 || Object.keys(toAccount.value).length === 0) {
    errMsg.value = "Tài khoản không được để trống";
    return false;
  }
  if (JSON.stringify(fromAccount.value) === JSON.stringify(toAccount.value)) {
    errMsg.value = "Tài khoản không được trùng nhau";
    return false;
  }
  errMsg.value = "";
  return true;
}

async function handleClickSave() {
  if (!isValid()) {
    return;
  }
}

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
    })
    .catch((err) => {
      console.log(err);
    });
});
</script>

<template>
  <div class="account-info text-grey-color">
    <div
      class="text-primary-color d-flex justify-center position-relative mb-10"
    >
      <router-link
        to="/account"
        class="position-absolute top-0 left-0 text-primary-color text-decoration-none"
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
                <font-awesome-icon
                  class="text-primary-color"
                  :icon="item.raw.icon"
                />
              </v-avatar>
            </template>
          </v-list-item>
        </template>
        <template v-slot:selection="{ item }">
          <div>
            <v-avatar start>
              <font-awesome-icon
                class="text-primary-color"
                :icon="item.raw.icon"
              />
            </v-avatar>
            <span class="text-primary-color text-20">{{ item.raw.name }}</span>
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
                v-model="transfer.amount"
                label="Số tiền"
                hide-details="auto"
                class="text-blue-accent-3 font-weight-bold text-end"
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
            label="Từ tài khoản"
            variant="solo"
            rounded
            v-model="fromAccount"
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
                :prepend-avatar="item.raw.accountType.icon"
                :title="item.raw.accountName"
              ></v-list-item>
            </template>
            <template v-slot:selection="{ item }">
              <div>
                <v-avatar start>
                  <img
                    class="icon-size"
                    :src="item.raw.accountType.icon"
                    alt="icon"
                  />
                </v-avatar>
                <span class="text-grey-color">{{ item.raw.accountName }}</span>
              </div>
            </template>
          </v-select>
        </v-col>
        <v-col cols="3">
          <v-select
            label="Tới tài khoản"
            variant="solo"
            rounded
            v-model="toAccount"
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
                :prepend-avatar="item.raw.accountType.icon"
                :title="item.raw.accountName"
              ></v-list-item>
            </template>
            <template v-slot:selection="{ item }">
              <div>
                <v-avatar start>
                  <img
                    class="icon-size"
                    :src="item.raw.accountType.icon"
                    alt="icon"
                  />
                </v-avatar>
                <span class="text-grey-color">{{ item.raw.accountName }}</span>
              </div>
            </template>
          </v-select>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="4">
          <v-textarea
            v-model="transfer.interpretation"
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
    </div>
    <div class="text-center">
      <button class="bg-primary-color text-white py-2 px-10 rounded" @click.stop="handleClickSave()">
        Lưu
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
