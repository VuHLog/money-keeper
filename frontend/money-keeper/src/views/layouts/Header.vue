<script setup>
import { getCurrentInstance, computed, ref, watch, onMounted } from "vue";
import { useBaseStore } from "@/store/index.js";
import { useRouter, useRoute } from "vue-router";
import { jwtDecode } from "jwt-decode";

const { proxy } = getCurrentInstance();
const store = useBaseStore();
const router = useRouter();
const route = useRoute();

const getRoute = computed(() => {
  const routeArr = route.path.split("/");
  return routeArr[1];
});

const showUserMenu = ref(false);

const username = computed(() => store.username);
const avatarUrl = ref("");
const name = ref("");

onMounted(() => {
  let token = sessionStorage.getItem("token");
  decodedToken(token);
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
    const token = localStorage.getItem("token");
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

const userSettingList = ref([
  { title: "Thông tin", href: "/user/" },
  { title: "Đổi mật khẩu", href: "/user/" },
  { title: "Tài khoản", href: "/user/" },
  {},
]);
</script>

<template>
  <div class="header bg-white z-index-99 position-fixed w-100">
    <div class="d-flex h-100 mx-10 d-flex justify-space-between">
      <div class="d-flex">
        <div class="header-logo h-100 pa-2 mr-6">
          <router-link
            class="d-flex h-100 align-center text-decoration-none text-primary-color"
            to="/home"
          >
            <img class="h-100" src="@images/logo/logo.png" alt="Sổ thu chi" />
            <h3>Sổ thu chi</h3>
          </router-link>
        </div>
        <div>
          <ul class="d-flex h-100 nav-list">
            <li class="nav-item">
              <router-link
                :class="getRoute === 'home' ? 'border-b-active' : ''"
                class="nav-link d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten pr-3"
                to="/home"
              >
                Trang chủ
              </router-link>
            </li>
            <li class="nav-item">
              <v-menu open-on-hover>
                <template v-slot:activator="{ props }">
                  <div
                    :class="getRoute === 'account' ? 'border-b-active' : ''"
                    class="nav-link user-select-none d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten px-2"
                    to="/account"
                    v-bind="props"
                  >
                    Tài khoản
                    <font-awesome-icon
                      style="color: #bdc3c7; font-size: 0.8rem"
                      :icon="['fas', 'angle-down']"
                    />
                  </div>
                </template>
                <v-list>
                  <v-list-item class="hover-bg-grey">
                    <v-list-item-title>
                      <router-link
                        class="text-decoration-none text-grey-darken-4"
                        to="/account"
                        >Tài khoản
                      </router-link>
                    </v-list-item-title>
                  </v-list-item>
                  <v-list-item class="hover-bg-grey">
                    <v-list-item-title>
                      <router-link
                        class="text-decoration-none text-grey-darken-4"
                        to="/account"
                        >Sổ tiết kiệm
                      </router-link>
                    </v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-menu>
            </li>
            <li class="nav-item">
              <router-link
                :class="getRoute === '' ? 'border-b-active' : ''"
                class="nav-link d-flex h-100 align-center text-decoration-none text-grey-darken-1 font-weight-bold text-14 text-uppercase hover-green-lighten px-2"
                to="/home"
              >
                Báo cáo
              </router-link>
            </li>
          </ul>
        </div>
      </div>
      <div class="d-flex align-center">
        <span class="font-weight-bold text-grey-darken-4"
          >Chào {{ name }}!</span
        >
        <v-menu v-if="isLoggedIn">
          <template v-slot:activator="{ props }">
            <div
              class="d-flex align-center cursor-pointer h-100 pa-3 user-none"
              @click="showUserMenu = !showUserMenu"
              v-bind="props"
            >
              <img class="rounded-circle h-100" :src="avatarUrl" alt="avatar" />
              <font-awesome-icon
                style="color: #bdc3c7; font-size: 0.8rem"
                :icon="['fas', 'angle-down']"
              />
            </div>
          </template>
          <v-list>
            <v-list-item
              v-for="(item, index) in userSettingList"
              :key="index"
              :value="index"
            >
              <v-list-item-title v-if="index <= userSettingList.length - 2">
                <router-link
                  class="text-decoration-none text-grey-darken-4"
                  :to="item.href"
                  >{{ item.title }}
                </router-link>
              </v-list-item-title>
              <v-list-item-title @click="logOut()" v-else>
                Đăng xuất
              </v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </div>
    </div>
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
</style>
