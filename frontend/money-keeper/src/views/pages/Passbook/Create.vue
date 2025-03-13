<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { AccountType } from "@/constants/AccountType.js";
import { Banks } from "@/constants/Banks.js";

const accountTypeList = ref(AccountType);
const accountType = ref({
  id: 1,
  name: "Tiền mặt",
  icon: "https://res.cloudinary.com/cloud1412/image/upload/v1741273907/cash_ifor6b.png",
});

const banks = ref(Banks);
const bank = ref();

const currentTime = ref(new Date());

const termDepositList = ref([
  {
    id: 1,
    value: "1 tuần",
  },
  {
    id: 2,
    value: "2 tuần",
  },
  {
    id: 3,
    value: "3 tuần",
  },
  {
    id: 4,
    value: "1 tháng",
  },
  {
    id: 5,
    value: "3 tháng",
  },
  {
    id: 6,
    value: "6 tháng",
  },
  {
    id: 7,
    value: "12 tháng",
  },
  {
    id: 8,
    value: "Khác",
  },
]);

const termDeposit = ref({
  id: 5,
  value: "3 tháng",
});

const interestRate = ref(0);
const nonTermInterest = ref(0.05);
const interestCalculationDays = ref(365);

const payInterest = ref({
  name: "Cuối kỳ",
});
const payInterestList = ref([
  {
    name: "Đầu kỳ",
  },
  {
    name: "Cuối kỳ",
  },
  {
    name: "Định kỳ hàng tháng",
  },
]);

const payInterestWhenDue = ref({
  name: "Tái tục gốc và lãi",
});
const payInterestWhenDueList = ref([
  {
    name: "Tái tục gốc và lãi",
  },
  {
    name: "Tái tục gốc",
  },
  {
    name: "Tất toán sổ",
  },
]);

const fromAccount = ref();
const myAccountList = ref([]);
</script>

<template>
  <div>
    <div class="text-primary-color position-relative mb-10">
      <router-link
        to="/passbook"
        class="position-absolute top-0 left-0 text-primary-color text-decoration-none"
      >
        <font-awesome-icon
          :icon="['fas', 'angle-left']"
          style="font-size: 32px"
        />
      </router-link>
      <h1 class="text-center">Thêm sổ tiết kiệm</h1>
    </div>
    <v-row class="mb-10 align-center">
      <v-col cols="6">
        <v-text-field hide-details="auto" label="Số dư ban đầu"></v-text-field>
      </v-col>
      <v-col cols="6">
        <v-text-field hide-details="auto" label="Tên tài khoản"></v-text-field>
      </v-col>
      <v-col cols="6">
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
      </v-col>
      <v-col cols="6">
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
      <v-col cols="3">
        <el-date-picker
          v-model="currentTime"
          type="date"
          placeholder="Chọn ngày gửi"
        />
      </v-col>
      <v-col cols="6" class="d-flex">
        <v-select
          label="Kỳ hạn"
          variant="solo"
          rounded
          v-model="termDeposit"
          :items="termDepositList"
          item-title="value"
          :return-object="true"
          class="text-grey-color d-inline-block mr-2"
          width="20%"
          max-width="20%"
          hide-details="true"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props" :to="item.raw.href"> </v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <span>{{ item.raw.value }}</span>
            </div>
          </template>
        </v-select>
        <v-text-field
          v-show="termDeposit.id === 8"
          label="Kỳ hạn/tháng"
          type="number"
          hide-details="auto"
          class="font-weight-bold text-end"
          bg-color="bg-white"
          hide-spin-buttons
          width="20%"
          max-width="20%"
        ></v-text-field>
      </v-col>
      <v-col cols="3 "></v-col>
      <v-col cols="2">
        <v-text-field
          v-model="interestRate"
          label="Lãi suất %/năm"
          type="number"
          hide-details="auto"
          class="text-end"
          bg-color="bg-white"
          hide-spin-buttons
        ></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-text-field
          v-model="nonTermInterest"
          label="Lãi suất không kỳ hạn %/năm"
          type="number"
          hide-details="auto"
          class="text-end"
          bg-color="bg-white"
          hide-spin-buttons
        ></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-text-field
          v-model="interestCalculationDays"
          label="Số ngày tính lãi / năm"
          type="number"
          hide-details="auto"
          class="text-end"
          bg-color="bg-white"
          hide-spin-buttons
        ></v-text-field>
      </v-col>
      <v-col cols="2">
        <v-select
          label="Trả lãi"
          variant="solo"
          rounded
          v-model="payInterest"
          :items="payInterestList"
          item-title="name"
          :return-object="true"
          class="text-grey-color d-inline-block mr-2"
          hide-details="true"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props" :to="item.raw.href"> </v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <span>{{ item.raw.name }}</span>
            </div>
          </template>
        </v-select>
      </v-col>
      <v-col cols="2">
        <v-select
          label="Khi đến hạn"
          variant="solo"
          rounded
          v-model="payInterestWhenDue"
          :items="payInterestWhenDueList"
          item-title="name"
          :return-object="true"
          class="text-grey-color d-inline-block mr-2"
          hide-details="true"
        >
          <template v-slot:item="{ props, item }">
            <v-list-item v-bind="props" :to="item.raw.href"> </v-list-item>
          </template>
          <template v-slot:selection="{ item }">
            <div>
              <span>{{ item.raw.name }}</span>
            </div>
          </template>
        </v-select>
      </v-col>
      <v-col cols="2">
          <v-select
            label="Từ tài khoản"
            variant="solo"
            rounded
            v-model="fromAccount"
            :items="myAccountList"
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
          </v-select>
        </v-col>
      <v-col cols="12">
        <v-textarea label="Diễn giải" rows="1" auto-grow clearable></v-textarea>
      </v-col>
    </v-row>
    <div class="text-center">
      <button class="bg-primary-color text-white py-2 px-10 rounded">
        Lưu
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
