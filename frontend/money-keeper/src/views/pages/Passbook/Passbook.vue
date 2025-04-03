<script setup>
import { ref, getCurrentInstance } from "vue";
import { useBaseStore } from "@/store/index.js";
import { useRouter } from "vue-router";

const panel = ref([0, 1]);
const show = ref(true);
</script>

<template>
  <div class="h">
    <h1 class="text-center text-primary mb-10">Sổ tiết kiệm</h1>
    <div>
      <router-link
        to="/passbook/create"
        class="text-decoration-none text-white bg-primary-color pa-2 rounded"
      >
        <button>
          <font-awesome-icon class="mr-2" :icon="['fas', 'plus']" />
          <span>Thêm sổ tiết kiệm</span>
        </button>
      </router-link>
    </div>
    <div v-if="show">
      <div class="text-end mb-2">
        <span class="font-weight-bold text-20">Tổng tiền: 6.000 ₫(2 sổ)</span>
      </div>
      <v-expansion-panels v-model="panel" multiple>
        <v-expansion-panel>
          <v-expansion-panel-title class="font-weight-bold"
            >Ngân hàng TMCP Á Châu (1.000 ₫)</v-expansion-panel-title
          >
          <v-expansion-panel-text>
            <div>
              <div class="d-flex py-2 border-b-sm">
                <div class="flex-1-1">
                  <div class="d-flex">
                    <div
                      class="align-self-center flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <img
                        class="icon-size-thumbnail"
                        src="https://api.vietqr.io/img/ACB.png"
                      />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <div>
                        <span class="font-weight-bold text-grey-color mr-2"
                          >Tên ngân hàng</span
                        >
                        <span
                          class="text-red-accent-2 pa-1 border-sm rounded border-error"
                          >0%</span
                        >
                      </div>
                      <span class="text-grey-lighten-1">1.000 ₫</span>
                    </div>
                  </div>
                </div>
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
                          to="/account/transfer/id"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Gửi thêm</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <router-link
                          class="text-decoration-none text-grey-darken-4"
                          to="/account/adjusted-balance/accountId"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Rút một phần</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
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
                            <span>Tất toán</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
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
                  </v-list>
                </v-menu>
              </div>
            </div>
            <v-tooltip activator="parent" location="bottom start" width="20%">
              <div class="d-flex justify-space-between">
                <span>Số dư còn lại</span>
                <span class="font-weight-bold">1.000 ₫</span>
              </div>
              <div class="d-flex justify-space-between">
                <span
                  ><font-awesome-icon :icon="['far', 'calendar']" /> Ngày
                  gửi</span
                >
                <span>12/03/2025</span>
              </div>
              <div class="d-flex justify-space-between">
                <span
                  ><font-awesome-icon :icon="['far', 'calendar']" /> Kỳ
                  hạn</span
                >
                <span>3 tháng</span>
              </div>
              <div class="d-flex justify-space-between">
                <span>Lãi suất</span>
                <div>
                  <span class="text-red-accent-2 border-sm rounded border-error"
                    >0%</span
                  >
                  <span>/năm</span>
                </div>
              </div>
              <div class="d-flex justify-space-between">
                <span>Số dư bạn đầu</span>
                <span class="font-weight-bold">1.000 ₫</span>
              </div>
            </v-tooltip>
          </v-expansion-panel-text>
        </v-expansion-panel>
        <v-expansion-panel>
          <v-expansion-panel-title class="font-weight-bold"
            >Ngân hàng TMCP Á Châu (1.000 ₫)</v-expansion-panel-title
          >
          <v-expansion-panel-text>
            <div>
              <div class="d-flex py-2 border-b-sm">
                <div class="flex-1-1">
                  <div class="d-flex">
                    <div
                      class="align-self-center flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <img
                        class="icon-size-thumbnail"
                        src="https://api.vietqr.io/img/ACB.png"
                      />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <div>
                        <span class="font-weight-bold text-grey-color mr-2"
                          >Tên ngân hàng</span
                        >
                        <span
                          class="text-red-accent-2 pa-1 border-sm rounded border-error"
                          >0%</span
                        >
                      </div>
                      <span class="text-grey-lighten-1">1.000 ₫</span>
                    </div>
                  </div>
                </div>
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
                          to="/account/transfer/id"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Gửi thêm</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
                      <v-list-item-title>
                        <router-link
                          class="text-decoration-none text-grey-darken-4"
                          to="/account/adjusted-balance/accountId"
                        >
                          <div class="text-12 d-flex">
                            <div class="mr-2">
                              <font-awesome-icon
                                :icon="['fas', 'right-left']"
                              />
                            </div>
                            <span>Rút một phần</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
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
                            <span>Tất toán</span>
                          </div>
                        </router-link>
                      </v-list-item-title>
                    </v-list-item>
                    <v-list-item class="hover-bg-grey">
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
                  </v-list>
                </v-menu>
              </div>
            </div>
            <v-tooltip activator="parent" location="bottom start" width="20%">
              <div class="d-flex justify-space-between">
                <span>Số dư còn lại</span>
                <span class="font-weight-bold">1.000 ₫</span>
              </div>
              <div class="d-flex justify-space-between">
                <span
                  ><font-awesome-icon :icon="['far', 'calendar']" /> Ngày
                  gửi</span
                >
                <span>12/03/2025</span>
              </div>
              <div class="d-flex justify-space-between">
                <span
                  ><font-awesome-icon :icon="['far', 'calendar']" /> Kỳ
                  hạn</span
                >
                <span>3 tháng</span>
              </div>
              <div class="d-flex justify-space-between">
                <span>Lãi suất</span>
                <div>
                  <span class="text-red-accent-2 border-sm rounded border-error"
                    >0%</span
                  >
                  <span>/năm</span>
                </div>
              </div>
              <div class="d-flex justify-space-between">
                <span>Số dư ban đầu</span>
                <span class="font-weight-bold">1.000 ₫</span>
              </div>
            </v-tooltip>
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
