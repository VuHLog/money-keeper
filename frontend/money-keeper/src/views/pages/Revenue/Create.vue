<script setup>
import { ref, watch, inject, onMounted, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import { MainFeature } from "@/constants/MainFeature.js";
import DictionaryRevenue from "@components/DictionaryRevenue.vue";
import TripEvent from "@components/TripEvent.vue";
import CollectMoneyWho from "@components/CollectMoneyWho.vue";
import { AccountType } from "@/constants/AccountType.js";

const { proxy } = getCurrentInstance();
const swal = inject("$swal");
const router = useRouter();
const mainFeatureList = ref(MainFeature);
const feature = ref(mainFeatureList.value.find((value) => value.id === 2));
const currentTime = ref(new Date());
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
const collectMoneyWhoSelected = ref({});
const showCollectMoneyWho = ref(false);
watch(collectMoneyWhoSelected, () => {
  showCollectMoneyWho.value = false;
});

const errMsg = ref("");

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

const revenue = ref({
  amount: 0,
  location: "",
  interpretation: "",
  revenueDate: "",
  dictionaryBucketPaymentId: "",
  dictionaryRevenueId: "",
  tripEventId: "",
  collectMoneyWhoId: "",
});

function isValid() {
  const numberRegex = /^(0|[1-9]\d*)$/; // Cho phép số 0 hoặc số dương không bắt đầu bằng 0

  if (!numberRegex.test(revenue.value.amount)) {
    errMsg.value = "Số tiền không hợp lệ";
    return false;
  }

  if(Object.keys(account.value).length === 0) {
    errMsg.value = "Bạn phải chọn tài khoản";
    return false;
  }

  if(Object.keys(categorySelected.value).length === 0) {
    errMsg.value = "Bạn phải chọn hạng mục";
    return false;
  }

  if(currentTime.value === "") {
    errMsg.value = "Bạn phải chọn thời điểm chi tiêu";
    return false;
  }
    
  errMsg.value = "";
  return true;
}

async function createRevenue() {
  if (!isValid()) {
    return;
  }

  if(Object.keys(account.value).length !== 0) {
    revenue.value.dictionaryBucketPaymentId = account.value.id;
  }

  if(Object.keys(categorySelected.value).length !== 0) {
    revenue.value.dictionaryRevenueId = categorySelected.value.id;
  }

  if(Object.keys(tripEventSelected.value).length !== 0) {
    revenue.value.tripEventId = tripEventSelected.value.id;
  }

  if(Object.keys(collectMoneyWhoSelected.value).length !== 0) {
    revenue.value.collectMoneyWhoId = collectMoneyWhoSelected.value.id;
  }

  revenue.value.revenueDate = currentTime.value instanceof Date 
    ? new Date(currentTime.value.getTime() - (currentTime.value.getTimezoneOffset() * 60000))
        .toISOString()
        .slice(0, 19)
        .replace('T', ' ')
    : currentTime.value;

  await proxy.$api.post("/revenue-regular", revenue.value).then(() => {
    swal.fire({
      title: "Thành công",
      text: "Bạn đã thêm khoản thu tiền thành công!",
      icon: "success",
    });
    router.push("/revenue");
  });
}
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
                v-model="revenue.amount"
                label="Số tiền"
                type="number"
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
                  <img class="icon-size" :src="item.raw?.accountType?.icon" alt="icon" />
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
                v-model="revenue.location"
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
            @click="showCollectMoneyWho = true"
          >
            <template v-if="Object.keys(collectMoneyWhoSelected).length > 0">
              <span class="text-16" style="text-transform: none">{{
                collectMoneyWhoSelected.name
              }}</span>
              <v-tooltip activator="parent" location="bottom"
                >Thu từ ai</v-tooltip
              >
            </template>
            <template v-else>
              <font-awesome-icon :icon="['fas', 'user']" class="text-20 mr-2" />
              <span style="text-transform: none">Thu từ ai</span>
            </template>
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="3">
          <v-textarea
            v-model="revenue.interpretation"
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
        <dictionary-revenue v-model="categorySelected"></dictionary-revenue>
      </v-dialog>
      <v-dialog v-model="showPopupTripEvent" width="auto">
        <trip-event v-model="tripEventSelected"></trip-event>
      </v-dialog>
      <v-dialog v-model="showCollectMoneyWho" width="auto">
        <collect-money-who v-model="collectMoneyWhoSelected"></collect-money-who>
      </v-dialog>
    </div>
    <div class="text-center">
      <button class="bg-primary-color text-white py-2 px-10 rounded" @click.stop="createRevenue">
        Lưu
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
