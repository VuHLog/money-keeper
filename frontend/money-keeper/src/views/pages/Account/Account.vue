<script setup>
import { ref, getCurrentInstance, onMounted } from "vue";
import { useBaseStore } from "@/store/index.js";
import { useRouter } from "vue-router";
import { AccountType } from "@/constants/AccountType.js";

const { proxy } = getCurrentInstance();

const panel = ref([0, 1]);
const show = ref(false);
const dictionaryBucketPayment = ref([]);
const currentBalance = ref(0);
const currentBalanceUsed = ref(0);
const currentBalanceNotUsed = ref(0);

onMounted(() => {
  proxy.$api.get("/dictionary-bucket-payment").then((res) => {
    dictionaryBucketPayment.value = res.result;
    show.value = true;
    currentBalance.value = dictionaryBucketPayment.value.reduce((acc, curr) => acc + curr.balance, 0);
    currentBalanceUsed.value = dictionaryBucketPayment.value.filter(item => item.haveUse === true).reduce((acc, curr) => acc + curr.balance, 0);
    currentBalanceNotUsed.value = currentBalance.value - currentBalanceUsed.value;
    dictionaryBucketPayment.value.forEach(item => {
      item.accountType = AccountType.find(type => type.name === item.accountType);
    });
  }).catch((err) => {
    console.log(err);
  });
});

async function updateUsageStatus(id, status){
  proxy.$api.patch("/dictionary-bucket-payment/" + id + "/usage-status", {status})
  .then((res) => {
    console.log(res.result.haveUse? "Đang sử dụng": "Ngừng sử dụng")
    dictionaryBucketPayment.value.find(value => value.id === id).haveUse = res.result.haveUse;
    let haveUse = res.result.haveUse;
    let balance = res.result.balance;
    if(haveUse){
      currentBalanceUsed.value += balance;
      currentBalanceNotUsed.value -= balance;
    }else{
      currentBalanceUsed.value -= balance;
      currentBalanceNotUsed.value += balance;
    }
  }).catch((err) => {
    console.log(err);
  });
}
</script>

<template>
  <div class="h">
    <h1 class="text-center text-primary mb-10">Tài khoản</h1>
    <div>
      <router-link
        to="/account/create"
        class="text-decoration-none text-white bg-primary-color pa-2 rounded"
      >
        <button>
          <font-awesome-icon class="mr-2" :icon="['fas', 'plus']" />
          <span>Thêm tài khoản</span>
        </button>
      </router-link>
    </div>
    <div v-if="show">
      <div class="text-end mb-2">
        <span class="font-weight-bold text-20">Tổng tiền: {{currentBalance}} ₫</span>
      </div>
      <v-expansion-panels v-model="panel" multiple>
        <v-expansion-panel>
          <v-expansion-panel-title class="font-weight-bold"
            >Đang sử dụng ({{ currentBalanceUsed }} ₫)</v-expansion-panel-title
          >
          <v-expansion-panel-text>
            <div v-for="item in dictionaryBucketPayment" :key="item.id">
              <div class="d-flex py-2 border-b-sm" v-if="item.haveUse === true">
                <router-link
                  :to="`/account/info/${item.id}`"
                  class="flex-1-1 text-decoration-none"
                >
                  <div class="d-flex">
                    <div
                      class="align-self-center flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <img
                        class="icon-size-thumbnail"
                        :src="item.accountType.icon"
                      />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <span class="font-weight-bold text-grey-color">{{ item.accountName }}</span>
                      <span class="text-grey-lighten-1">{{ item.balance }} ₫</span>
                    </div>
                  </div>
                </router-link>
                <v-menu>
                  <template v-slot:activator="{ props }">
                    <div
                      class="flex-center pa-3"
                      v-bind="props"
                      style="font-size: 20px"
                    >
                      <font-awesome-icon
                        class="text-grey-color"
                        :icon="['fas', 'ellipsis-vertical']"
                      />
                    </div>
                  </template>
                  <v-list>
                    <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <router-link
                          class="text-decoration-none text-grey-darken-4"
                          :to="`/account/transfer/${item.id}`"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Chuyển khoản</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <router-link
                          class="text-decoration-none text-grey-darken-4"
                          :to="`/account/adjusted-balance/${item.id}`"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Điều chỉnh số dư</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <!-- <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <router-link
                          class="text-decoration-none text-grey-darken-4"
                          to="/account"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Chia sẻ tài khoản</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item> -->
                    <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <router-link
                          class="text-decoration-none text-grey-darken-4"
                          :to="`/account/edit/${item.id}`"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Sửa</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <div
                          class="text-12 d-flex text-grey-darken-4 cursor-pointer"
                        >
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Xóa</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey" @click="updateUsageStatus(item.id, false)">
                      <v-list-item-title>
                        <div
                          class="text-12 d-flex text-grey-darken-4 cursor-pointer"
                        >
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Ngừng sử dụng</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
            </div>
          </v-expansion-panel-text>
        </v-expansion-panel>

        <v-expansion-panel>
          <v-expansion-panel-title class="font-weight-bold"
            >Ngừng sử dụng ({{ currentBalanceNotUsed }} ₫)</v-expansion-panel-title
          >
          <v-expansion-panel-text>
            <div v-for="item in dictionaryBucketPayment" :key="item.id">
              <div class="d-flex py-2 border-b-sm" v-if="item.haveUse === false">
                <router-link
                  :to="`/account/info/${item.id}`"
                  class="flex-1-1 text-decoration-none"
                >
                  <div class="d-flex">
                    <div
                      class="align-self-center icon-size-thumbnail flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <font-awesome-icon :icon="['fas', 'wallet']" />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <span class="font-weight-bold text-grey-color">{{ item.accountName }}</span>
                      <span class="text-grey-lighten-1">{{ item.balance }} ₫</span>
                    </div>
                  </div>
                </router-link>
                <v-menu>
                  <template v-slot:activator="{ props }">
                    <div
                      class="flex-center pa-3"
                      v-bind="props"
                      style="font-size: 20px"
                    >
                      <font-awesome-icon
                        class="text-grey-color"
                        :icon="['fas', 'ellipsis-vertical']"
                      />
                    </div>
                  </template>
                  <v-list>
                    <v-list-item>
                      <v-list-item-title>
                        <div class="text-12 d-flex text-disable">
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Chuyển khoản</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                      <v-list-item-title>
                        <div class="text-12 d-flex text-disable">
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Điều chỉnh số dư</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                    <!-- <v-list-item>
                      <v-list-item-title>
                        <div class="text-12 d-flex text-disable">
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Chia sẻ tài khoản</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item> -->
                    <v-list-item>
                      <v-list-item-title>
                        <div class="text-12 d-flex text-disable">
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Sửa</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item>
                      <v-list-item-title>
                        <div class="text-12 d-flex text-disable">
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Xóa</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey" @click="updateUsageStatus(item.id, true)">
                      <v-list-item-title>
                        <div
                          class="text-12 d-flex text-grey-darken-4 cursor-pointer"
                        >
                          <div class="mr-2">
                            <font-awesome-icon
                              :icon="['fas', 'unlock-keyhole']"
                            />
                          </div>
                          <span>Sử dụng lại</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
              </div>
            </div>
          </v-expansion-panel-text>
        </v-expansion-panel>
      </v-expansion-panels>
    </div>
    <div class="flex-center flex-column" v-else>
      <div class="text-disable mb-3">
        <img
          src="@images/logo/logo-disable.png"
          alt="Sổ thu chi"
          class="text-disable"
          style="height: 100px"
        />
      </div>
      <p class="text-20 text-disable mb-3">Bạn chưa có tài khoản nào</p>
      <router-link
        to="/account/create"
        class="text-decoration-none text-primary text-20 hover-opacity-06"
      >
        <font-awesome-icon class="mr-2" :icon="['fas', 'plus']" />
        <span>Thêm tài khoản</span>
      </router-link>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
