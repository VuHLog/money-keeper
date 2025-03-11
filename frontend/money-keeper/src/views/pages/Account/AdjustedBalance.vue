<script setup>
import { ref } from "vue";
import { MainFeature } from "@/constants/MainFeature.js";

const mainFeatureList = ref(MainFeature);
const feature = ref({
  id: 6,
  name: "Điều chỉnh số dư",
  icon: ["fas", "right-left"],
});

const currentTime = ref(new Date());

const myAccountList = ref([]);

const accountSeleted = ref({});

const categorySelected = ref();
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
      <v-row class="mb-2" justify="space-between">
        <v-col cols="2">
          <v-select
            label="Chọn tài khoản"
            variant="solo"
            rounded
            v-model="accountSeleted"
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
        <v-col cols="2" class="d-flex align-center">
          <el-date-picker
            v-model="currentTime"
            type="datetime"
            placeholder="Chọn thời gian"
          />
        </v-col>
      </v-row>
      <v-row no-gutters class="pa-2 mb-2 rounded box-shadow-default">
        <v-col cols="4" class="flex-center border-e-sm">
          <div class="flex-center flex-column text-20">
            <span class="text-grey-color">Số dư trên tài khoản</span>
            <span class="text-grey-color">6.000 ₫</span>
          </div>
        </v-col>
        <v-col cols="4" class="flex-center border-e-sm">
          <div class="flex-center flex-column text-20">
            <span class="text-grey-color">Đã chi</span>
            <span class="text-red-accent-3">6.000 ₫</span>
          </div>
        </v-col>
        <v-col cols="4">
          <div class="flex-center flex-column text-20">
            <span class="text-grey-color">Số dư thực tế</span>
            <div class="flex-center text-20 w-100">
              <v-text-field
                hide-details="auto"
                class="text-blue-accent-3 font-weight-bold"
                bg-color="bg-white"
                max-width="50%"
              ></v-text-field>
              <span class="font-weight-bold text-20">₫</span>
            </div>
          </div>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="3">
          <v-select
            label="Chọn hạng mục"
            variant="solo"
            rounded
            v-model="categorySelected"
            :items="[]"
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
      </v-row>
    </div>
    <div class="text-center">
      <button class="bg-primary-color text-white py-2 px-10 rounded">
        Lưu
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
