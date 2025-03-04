<script setup>
import { ref, getCurrentInstance, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useBaseStore } from "@/store/index.js";
import TokenService from "@/service/TokenService.js";
import { OAuthConfig } from "@/config/OAuthConfig.js";

const { proxy } = getCurrentInstance();

const store = useBaseStore();

const router = useRouter();
const route = useRoute();

const showPassword = ref(false);

const redirect = route.query.redirect ? route.query.redirect : "/home";

onMounted(async () => {
  if (store.username !== "") {
    router.push(redirect);
  }

  await signInWithGoogle();
});

const errMsg = ref("");
const user = ref({
  username: "",
  password: "",
});
async function signIn() {
  errMsg.value = "";
  let userVal = user.value;
  if (userVal.username === "" || userVal.password === "") {
    errMsg.value = "Phải nhập tài khoản và mật khẩu";
    return;
  }

  await proxy.$api
    .post("/auth/token", userVal)
    .then((res) => {
      TokenService.setSessionAccessToken(res.result.token);
      store.isLoggedIn = true;
      router.push(redirect);
    })
    .catch(() => (errMsg.value = "Tài khoản hoặc mật khẩu không chính xác"));
}

function handleContinueWithGoogle() {
  const targetUrl = `${OAuthConfig.authUri}?redirect_uri=${OAuthConfig.redirectUri}&response_type=code&client_id=${OAuthConfig.clientId}&scope=openid%20email%20profile`;

  window.location.href = targetUrl;
}

async function signInWithGoogle() {
  const authCodeRegex = /code=([^&]+)/;
  const isMatch = window.location.href.match(authCodeRegex);

  if (isMatch) {
    const authCode = isMatch[1];

    await proxy.$api
      .post("/auth/outbound/authentication?code=" + authCode, {})
      .then((res) => {
        TokenService.setSessionAccessToken(res.result.token);
        store.isLoggedIn = true;
        router.push(redirect);
      })
      .catch(() => errMsg.value = "Đăng nhập bằng Google không thành công");
  }
}

</script>

<template>
  <div class="width-form-login d-flex flex-column">
    <div class="d-flex w-100">
      <div
        class="text-center bg-white text-green-darken-1 text-16 pa-5 rounded-ts-xl rounded-te-xl w-50 font-weight-bold border-sm border-b-0"
      >
        Đăng nhập
      </div>
      <router-link
        to="/auth/sign-up"
        class="text-center bg-green-darken-1 text-16 pa-5 rounded-ts-xl rounded-te-xl w-50 font-weight-bold text-decoration-none"
      >
        Đăng ký
      </router-link>
    </div>
    <div class="bg-white pa-4 border-sm border-t-0">
      <form method="POST" @submit.prevent="signIn()">
        <input
          v-model.trim="user.username"
          class="w-100 border-b-sm py-2 pl-1 border-0 form-input text-grey-darken-4 mt-6"
          type="text"
          placeholder="Tên đăng nhập"
        />
        <div class="w-100 d-flex mt-6">
          <input
            v-model.trim="user.password"
            class="h-100 py-2 pl-1 border-b-sm border-0 form-input text-grey-darken-4 flex-fill"
            :type="showPassword ? 'text' : 'password'"
            placeholder="Mật khẩu"
          />
          <div
            class="d-flex align-center cursor-pointer px-2 user-select-none"
            @click="showPassword = !showPassword"
          >
            <font-awesome-icon v-if="!showPassword" :icon="['fas', 'eye']" />
            <font-awesome-icon v-else :icon="['fas', 'eye-slash']" />
          </div>
        </div>
        <div class="mt-2 text-red-darken-1 text-14">
          {{ errMsg }}
        </div>
        <button
          class="bg-green-accent-4 text-white w-100 py-3 rounded-lg mt-10"
          type="submit"
        >
          Đăng nhập
        </button>
      </form>
      <div class="d-flex align-center my-3">
        <div class="p__MPF"></div>
        <div class="px-3">Hoặc</div>
        <div class="p__MPF"></div>
      </div>
      <div class="d-flex align-center justify-center">
        <button class="gsi-material-button" @click="handleContinueWithGoogle">
          <div class="gsi-material-button-state"></div>
          <div class="gsi-material-button-content-wrapper">
            <div class="gsi-material-button-icon">
              <svg
                version="1.1"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 48 48"
                xmlns:xlink="http://www.w3.org/1999/xlink"
                style="display: block"
              >
                <path
                  fill="#EA4335"
                  d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
                ></path>
                <path
                  fill="#4285F4"
                  d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
                ></path>
                <path
                  fill="#FBBC05"
                  d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
                ></path>
                <path
                  fill="#34A853"
                  d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.19C6.51 42.62 14.62 48 24 48z"
                ></path>
                <path fill="none" d="M0 0h48v48H0z"></path>
              </svg>
            </div>
            <span class="gsi-material-button-contents"
              >Đăng nhập bằng Google</span
            >
          </div>
        </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
