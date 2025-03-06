<script setup>
import { ref, getCurrentInstance } from "vue";
import { useBaseStore } from "@/store/index.js";
import { useRouter } from "vue-router";

const panel = ref([0, 1]);
const show = ref(true);
</script>

<template>
  <div class="h">
    <h1 class="text-center text-primary-color mb-5">Tài khoản</h1>
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
        <span class="font-weight-bold text-20">Tổng tiền: 6.000 ₫</span>
      </div>
      <v-expansion-panels v-model="panel" multiple>
        <v-expansion-panel>
          <v-expansion-panel-title class="font-weight-bold"
            >Đang sử dụng (2.000 ₫)</v-expansion-panel-title
          >
          <v-expansion-panel-text>
            <div>
              <div class="d-flex py-2 border-b-sm">
                <router-link
                  to="/account/accountId"
                  class="flex-1-1 text-decoration-none"
                >
                  <div class="d-flex">
                    <div
                      class="align-self-center flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <img
                        class="icon-size"
                        src="../../../assets/img/icon/wallet.png"
                      />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <span class="font-weight-bold text-grey-color">Long</span>
                      <span class="text-grey-lighten-1">1.000 ₫</span>
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
                          to="/account"
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
                          to="/account"
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
                            <span>Chia sẻ tài khoản</span>
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
                    <v-list-item class="hover-bg-grey">
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
              <div class="d-flex py-2 border-b-sm">
                <router-link
                  to="/account/accountId"
                  class="flex-1-1 text-decoration-none"
                >
                  <div class="d-flex">
                    <div
                      class="align-self-center icon-size flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <font-awesome-icon :icon="['fas', 'wallet']" />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <span class="font-weight-bold text-grey-color">Long</span>
                      <span class="text-grey-lighten-1">1.000 ₫</span>
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
                          to="/account"
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
                          to="/account"
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
                            <span>Chia sẻ tài khoản</span>
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
                    <v-list-item class="hover-bg-grey">
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
            >Ngừng sử dụng (1.000 ₫)</v-expansion-panel-title
          >
          <v-expansion-panel-text>
            <div>
              <div class="d-flex py-2 border-b-sm">
                <router-link
                  to="/account/accountId"
                  class="flex-1-1 text-decoration-none"
                >
                  <div class="d-flex">
                    <div
                      class="align-self-center icon-size flex-center rounded-circle bg-grey-darken-1 mr-4"
                    >
                      <font-awesome-icon :icon="['fas', 'wallet']" />
                    </div>
                    <div class="d-flex flex-column justify-space-between">
                      <span class="font-weight-bold text-grey-color">Long</span>
                      <span class="text-grey-lighten-1">1.000 ₫</span>
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
                    <v-list-item>
                      <v-list-item-title>
                        <div class="text-12 d-flex text-disable">
                          <div class="mr-2">
                            <font-awesome-icon :icon="['fas', 'right-left']" />
                          </div>
                          <span>Chia sẻ tài khoản</span>
                        </div>
                      </v-list-item-title>
                    </v-list-item>
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
                    <v-list-item class="hover-bg-grey">
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
        class="text-decoration-none text-primary-color text-20 hover-opacity-06"
      >
        <font-awesome-icon class="mr-2" :icon="['fas', 'plus']" />
        <span>Thêm tài khoản</span>
      </router-link>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
