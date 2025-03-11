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
</script>

<template>
  <div>
    <div class="text-primary-color position-relative mb-10">
      <router-link
        to="/account"
        class="position-absolute top-0 left-0 text-primary-color text-decoration-none"
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
        <v-text-field hide-details="auto" label="Số dư ban đầu"></v-text-field>
      </v-col>
      <v-col cols="6" v-show="accountType?.id === 3">
        <v-text-field
          hide-details="auto"
          label="Hạn mức tín dụng"
        ></v-text-field>
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
