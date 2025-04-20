<script setup>
import { getCurrentInstance, computed, ref, watch, onMounted, onBeforeUnmount, inject } from "vue";
import { useBaseStore } from "@/store/index.js";
import { useNotificationStore } from "@/store/NotificationStore";
import { useRouter, useRoute } from "vue-router";
import { jwtDecode } from "jwt-decode";
import ChangePassword from "@components/ChangePassword.vue";
import UserInfo from "@components/UserInfo.vue";


const { proxy } = getCurrentInstance();

const swal = inject("$swal");
const store = useBaseStore();
const notificationStore = useNotificationStore();
const router = useRouter();
const route = useRoute();

const getRoute = computed(() => {
  const routeArr = route.path.split("/");
  return routeArr[1];
});

const fullPath = computed(() => route.fullPath)
const countNewNotifications = computed(() => notificationStore.countNewNotifications)

const showUserMenu = ref(false);
const showNotification = ref(false);

const username = computed(() => store.username);
const avatarUrl = computed(() => store.avatarUrl);
const fullName = computed(() => store.fullName);
const name = ref("");
const showPopupChangePassword = ref(false);
const showUserInfo = ref(false);
const pageSize = ref(4);

onMounted(async () => {
  let token = sessionStorage.getItem("token");
  decodedToken(token);
  const users = await store.getMyInfo();
  store.fullName = users.fullName;
  store.avatarUrl = users.avatarUrl;

  await notificationStore.getNotifications();
  let user = await store.getMyInfo();
  store.connectStompClient();
  store.stompClient.onConnect = (frame) =>{
    console.log("Connected: " + frame);
        store.stompClient.subscribe(
          "/topic/notifications/" + user.id,
          (response) => {
            let responseBody = JSON.parse(response.body);
            notificationStore.notifications.unshift(responseBody);
            notificationStore.countNewNotifications += 1;
            notificationStore.showToastNotify(responseBody, swal);
          }
        );
  }
});

onBeforeUnmount(() => {
  store.disconnectStompClient();
});

function decodedToken(token) {
  if (token) {
    const decoded = jwtDecode(token);
    store.username = decoded.sub;
    avatarUrl.value = decoded.avatarUrl;
    name.value = decoded.name;
    store.isLoggedIn = true;
  } else {
    store.username = "";
    avatarUrl.value = "";
    name.value = "";
  }
}

//isLoggedIn thay đổi thì cập nhật hiển thị thông tin người dùng
const isLoggedIn = computed(() => store.isLoggedIn);
watch(isLoggedIn, (newVal) => {
  if (newVal) {
    const token = sessionStorage.getItem("token");
    decodedToken(token);
  } else {
    decodedToken(null);
  }
});

async function logOut() {
  showUserMenu.value = false;
  const token = sessionStorage.getItem("token");
  if (token) {
    await proxy.$api
      .post("/auth/logout", token)
      .then(() => {
        store.isLoggedIn = false;
        sessionStorage.removeItem("token");
        decodedToken(null);
        router.push("/auth/sign-in");
      })
      .catch();
  }
}

async function handleSeePreviusNotification() {
  console.log(notificationStore.pageSize + pageSize.value);
  await notificationStore.changePageSize(notificationStore.pageSize + pageSize.value);
}

function onNotificationMenuToggle(isOpen) {
  if (isOpen) {
    notificationStore.countNewNotifications = 0;
  }
}

function handleScroll(event) {
  const element = event.target;
  
  if (element.scrollHeight - element.scrollTop <= element.clientHeight + 1) {
    loadMoreNotifications();
  }
}

async function loadMoreNotifications() {
  if (notificationStore.pageNumber < notificationStore.totalPages) {
    await notificationStore.changePageSize(notificationStore.pageSize + pageSize.value);
  }
}
</script>

<template>
  <div class="header bg-white z-index-99 position-fixed w-100">
    <div class="d-flex h-100 mx-10 d-flex justify-space-between">
      <div class="d-flex">
        <div class="header-logo h-100 pa-2 mr-6">
          <router-link class="d-flex h-100 align-center text-decoration-none text-primary-color" to="/home">
            <img class="h-100" src="@images/logo/logo.png" alt="Sổ thu chi" />
            <h3>Sổ thu chi</h3>
          </router-link>
        </div>
        <div>
          <ul class="d-flex h-100 nav-list">
            <li class="nav-item">
              <router-link :class="getRoute === 'home' ? 'border-b-active' : ''"
                class="nav-link d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten pr-3"
                to="/home">
                Trang chủ
              </router-link>
            </li>
            <li class="nav-item">
              <v-menu open-on-hover>
                <template v-slot:activator="{ props }">
                  <div :class="getRoute === 'account' ? 'border-b-active' : ''"
                    class="nav-link user-select-none d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten px-2"
                    to="/account" v-bind="props">
                    Tài khoản
                    <font-awesome-icon style="color: #bdc3c7; font-size: 0.8rem" :icon="['fas', 'angle-down']" />
                  </div>
                </template>
                <v-list>
                  <v-list-item class="hover-bg-grey">
                    <v-list-item-title>
                      <router-link class="text-decoration-none text-grey-darken-4" to="/account">Tài khoản
                      </router-link>
                    </v-list-item-title>
                  </v-list-item>
                  <v-list-item class="hover-bg-grey">
                    <v-list-item-title>
                      <router-link class="text-decoration-none text-grey-darken-4" to="/passbook">Sổ tiết kiệm
                      </router-link>
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </li>
            <li class="nav-item">
              <v-menu open-on-hover>
                <template v-slot:activator="{ props }">
                  <div :class="fullPath === '/expense/create' || fullPath === '/revenue/create' ? 'border-b-active' : ''"
                    class="nav-link user-select-none d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten px-2"
                    to="/account" v-bind="props">
                    Ghi thu chi
                    <font-awesome-icon style="color: #bdc3c7; font-size: 0.8rem" :icon="['fas', 'angle-down']" />
                  </div>
                </template>
                <v-list>
                  <v-list-item class="hover-bg-grey">
                    <v-list-item-title>
                      <router-link class="text-decoration-none text-grey-darken-4" to="/expense/create">Khoản chi
                      </router-link>
                    </v-list-item-title>
                  </v-list-item>
                  <v-list-item class="hover-bg-grey">
                    <v-list-item-title>
                      <router-link class="text-decoration-none text-grey-darken-4" to="/revenue/create">Khoản thu
                      </router-link>
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </li>
            <li class="nav-item">
              <router-link :class="getRoute === '' ? 'border-b-active' : ''"
                class="nav-link d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten px-2"
                to="/home">
                Báo cáo
              </router-link>
            </li>
          </ul>
        </div>
      </div>
      <div class="d-flex align-center">
        <v-menu v-if="isLoggedIn" @update:modelValue="onNotificationMenuToggle">
          <template v-slot:activator="{ props }">
            <div v-if="notificationStore.countNewNotifications > 0">
              <v-badge
                :content="notificationStore.countNewNotifications"
                color="red"
                overlap
                v-bind="props"
              >
                <v-btn icon variant="text" size="small" elevation="2" @click="showNotification = !showNotification">
                  <font-awesome-icon :icon="['fas', 'bell']" style="font-size: 24px" />
                </v-btn>
              </v-badge>
            </div>
            <div v-bind="props" v-else>
              <v-btn icon variant="text" size="small" elevation="2" @click="showNotification = !showNotification">
                <font-awesome-icon :icon="['fas', 'bell']" style="font-size: 24px" />
              </v-btn>
            </div>
          </template>
          <div class="elevation-2 bg-white rounded-lg pa-3" style="width: 360px" v-if="notificationStore.notifications.length > 0">
            <v-list class="scrollable-list" @scroll="handleScroll">
              <template v-for="(notification) in notificationStore.notifications" :key="notification">
                <v-list-item class="border-b">
                  <v-list-item-title>
                    <div class="d-flex align-center w-100">
                      <a :href="'http://localhost:5173/'+ notification.href" class="d-inline-flex w-100 align-center text-decoration-none">
                        <v-avatar class="mr-2">
                          <img class="icon-size" src="https://res.cloudinary.com/cloud1412/image/upload/v1745068565/logo_mpkmjj.png" />
                        </v-avatar>
                        <div class="mr-2 flex-grow-1">
                          <h5 class="text-primary text-16 mb-1">{{ notification.title }}</h5>
                          <p class="text-14 text-grey-darken-4 multiline-truncate" style="max-width: 200px;" v-html="notification.content"></p>
                          <span class="text-12 d-inline-block text-grey-lighten-1">{{notification.createdAt}}</span>
                        </div>
                        <div v-if="!notification.readStatus" class="ml-auto">
                          <font-awesome-icon class="text-primary" :icon="['fas', 'circle']" />
                        </div>
                      </a>
                    </div>
                  </v-list-item-title>
                </v-list-item>
              </template>
            </v-list>
            <v-list-item v-if="notificationStore.pageNumber < notificationStore.totalPages && notificationStore.pageSize === 4">
              <v-list-item-title class="d-flex justify-center">
                <div class="d-inline-flexhover-bg-grey cursor-pointer mt-3 py-2 px-3 bg-grey-lighten-3 rounded-lg" @click.stop="handleSeePreviusNotification()">Xem thông báo trước đó</div>
              </v-list-item-title>
            </v-list-item>
          </div>
          <div v-else class="d-flex justify-center align-center" style="width: 360px; height: 520px;">
            <h4 class="text-grey-darken-4">Không có thông báo nào</h4>
          </div>
        </v-menu>
        <v-menu v-if="isLoggedIn">
          <template v-slot:activator="{ props }">
            <div class="d-flex align-center cursor-pointer h-100 pa-3 user-none" @click="showUserMenu = !showUserMenu"
              v-bind="props">
              <img class="rounded-circle icon-size-thumbnail object-cover" :src="avatarUrl" alt="avatar" />
              <font-awesome-icon style="color: #bdc3c7; font-size: 0.8rem" :icon="['fas', 'angle-down']" />
            </div>
          </template>
          <v-list>
            <v-list-item class="border-b">
              <v-list-item-title>
                <span class="font-weight-bold text-grey-darken-4">{{ fullName }}</span>
              </v-list-item-title>
            </v-list-item>
            <v-list-item class="hover-bg-grey cursor-pointer" @click="showUserInfo = true">
              <v-list-item-title>
                <div>Thông tin tài khoản</div>
              </v-list-item-title>
            </v-list-item>
            <v-list-item class="hover-bg-grey cursor-pointer" @click="showPopupChangePassword = true">
              <v-list-item-title>
                <div>Đổi mật khẩu</div>
              </v-list-item-title>
            </v-list-item>
            <v-list-item class="hover-bg-grey cursor-pointer">
              <v-list-item-title @click="logOut()">
                Đăng xuất
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </div>
    <v-dialog v-model="showPopupChangePassword" width="auto">
      <change-password v-model="showPopupChangePassword"></change-password>
    </v-dialog>
    <v-dialog v-model="showUserInfo" width="auto">
      <user-info v-model="showUserInfo"></user-info>
    </v-dialog>
  </div>
</template>

<style lang="scss" scoped>
.header {
  height: 60px;
  box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;

  .nav-list {
    .nav-item {
      .nav-link {
        &.border-b-active {
          border-bottom: solid 3px var(--primary-color);
        }
      }
    }
  }
}
.scrollable-list {
  max-height: 520px; 
  overflow-y: auto;
  max-width: 360px;
}

.bg-grey-lighten-3:hover {
  background-color: #E0E0E0 !important;
}
</style>
