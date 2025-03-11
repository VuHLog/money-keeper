<script setup>
import { ref } from "vue";
import { MainFeature } from "@/constants/MainFeature.js";

const mainFeatureList = ref(MainFeature);
const feature = ref({
  id: 5,
  name: "Chuyển khoản",
  icon: ["fas", "right-left"],
  href: "/account/transfer/id",
});

const currentTime = ref(new Date());

const myAccountList = ref([]);

const fromAccount = ref();

const toAccount = ref();

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
        <v-col cols="3">
          <v-select
            label="Tới tài khoản"
            variant="solo"
            rounded
            v-model="toAccount"
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
        <v-col cols="3">
          <div class="flex-center flex-column text-20">
            <div class="flex-center w-100">
              <v-text-field
                label="Số tiền"
                type="number"
                hide-details="auto"
                class="text-blue-accent-3 font-weight-bold text-end"
                bg-color="bg-white"
                hide-spin-buttons
              ></v-text-field>
              <span class="font-weight-bold text-20">₫</span>
            </div>
          </div>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="4">
          <v-textarea
            label="Diễn giải"
            rows="1"
            auto-grow
            hide-details="auto"
            clearable
          ></v-textarea>
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
