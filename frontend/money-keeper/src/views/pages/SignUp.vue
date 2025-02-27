<script setup>
import { ref, getCurrentInstance, onMounted } from "vue";
import { useBaseStore } from "@/store/index.js";
import { useRouter, useRoute } from "vue-router";
import { inject } from "vue";

const swal = inject("$swal");

const { proxy } = getCurrentInstance();

const store = useBaseStore();

const router = useRouter();
const route = useRoute();

const errMsg = ref("");

const showPassword = ref(false)
const showPasswordConfirm = ref(false);

const user = ref({
  username: "",
  password: "",
  email: "",
  fullName: "",
  avatarUrl: "",
  roles: [],
});

const redirect = route.query.redirect ? route.query.redirect : "/home";
onMounted(() => {
  if (store.username !== "") {
    router.push(redirect);
  }
});

// xử lý ảnh
const file = ref(null);
async function handleFileUpload(event) {
  file.value = event.target.files[0];
  if (file.value !== null) {
    await submitFile();
  }
}

async function submitFile() {
  let formData = new FormData();

  formData.append("image", file.value);
  await proxy.$api
    .postFile("/cloudinary/upload/image", formData)
    .then((res) => {
      user.value.avatarUrl = res.url;
      console.log(res.url);
    })
    .catch((error) => console.log(error));
}

function isValidUserInfo() {
  errMsg.value = "";
  if (!/^.{8,}$/.test(user.value.fullName.trim())) {
    errMsg.value = "Họ tên phải có ít nhất 8 ký tự";
    return false;
  }
  if (
    !/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(
      user.value.email.trim()
    )
  ) {
    errMsg.value = "Email không hợp lệ";
    return false;
  }
  if (!/^.{8,}$/.test(user.value.username.trim())) {
    errMsg.value = "Tên đăng nhập phải có ít nhất 8 ký tự";
    return false;
  }
  if (
    !/^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9]).{8,}$/.test(user.value.password.trim())
  ) {
    errMsg.value =
      "Mật khẩu phải có ít nhất 8 ký tự chứa ít nhất một ký tự viết hoa, viết thường và chữ số";
    return false;
  }
  if (passwordConfirm.value !== user.value.password) {
    errMsg.value = "Mật khẩu không khớp";
    return false;
  }
  return true;
}

const passwordConfirm = ref("");
async function signUp() {
  if (!isValidUserInfo()) {
    return;
  }
  if (file.value === null) {
    user.value.avatarUrl = store.avatarUserDefault;
  }

  await proxy.$api
    .post("/users/registration", user.value)
    .then((res) => {
      console.log("sign up");

      if (res.message) {
        errMsg.value = res.message;
      } else {
        swal.fire({
          title: "Đăng ký Thành Công!",
          icon: "success",
        });
        router.push("/auth/sign-in");
      }
    })
    .catch((error) => {
      errMsg.value = error.response.data.message;
      console.log(error);
    });
}
</script>

<template>
  <div class="width-form-login d-flex flex-column mb-10">
    <div class="d-flex w-100">
      <router-link
        to="/auth/sign-in"
        class="text-center bg-blue-darken-1 text-16 pa-5 rounded-ts-xl rounded-te-xl w-50 font-weight-bold text-decoration-none"
      >
        Đăng nhập
      </router-link>
      <div
        class="text-center bg-white text-blue-darken-1 text-16 pa-5 rounded-ts-xl rounded-te-xl w-50 font-weight-bold border-sm border-b-0"
      >
        Đăng ký
      </div>
    </div>
    <div class="bg-white pa-4 border-sm border-t-0">
      <form method="POST" @submit.prevent="signUp()">
        <input
          v-model.trim="user.fullName"
          class="w-100 border-b-sm py-2 pl-1 border-0 form-input text-grey-darken-4 mt-6"
          type="text"
          placeholder="Họ tên"
        />
        <input
          v-model.trim="user.email"
          class="w-100 border-b-sm py-2 pl-1 border-0 form-input text-grey-darken-4 mt-6"
          type="text"
          placeholder="Email"
        />
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
            :type="showPassword? 'text' : 'password'"
            placeholder="Mật khẩu"
          />
          <div class="d-flex align-center cursor-pointer px-2 user-select-none" @click="showPassword = !showPassword">
            <font-awesome-icon v-if="!showPassword" :icon="['fas', 'eye']" />
            <font-awesome-icon v-else :icon="['fas', 'eye-slash']" />
          </div>
        </div>
        <div class="w-100 d-flex mt-6">
          <input
            v-model.trim="passwordConfirm"
            class="w-100 border-b-sm py-2 pl-1 border-0 form-input text-grey-darken-4 mt-6"
            :type="showPasswordConfirm? 'text' : 'password'"
            placeholder="Nhập lại mật khẩu"
          />
          <div class="d-flex align-center cursor-pointer px-2 user-select-none" @click="showPasswordConfirm = !showPasswordConfirm">
            <font-awesome-icon v-if="!showPasswordConfirm" :icon="['fas', 'eye']" />
            <font-awesome-icon v-else :icon="['fas', 'eye-slash']" />
          </div>
        </div>
        <div class="d-flex align-center flex-column">
          <div class="py-4">
            <img
              class="height-avatar width-avatar rounded-circle object-cover object-center"
              :src="
                user.avatarUrl === '' ? store.avatarUserDefault : user.avatarUrl
              "
              alt="avatar"
            />
          </div>
          <input
            id="input-image"
            class="d-none"
            type="file"
            accept=".jpg,.jpeg,.png"
            @change="handleFileUpload($event)"
          />
          <label
            for="input-image"
            class="pa-2 cursor-pointer rounded-sm bg-white border-sm border-solid text-grey-darken-4 box-shadow-none"
          >
            Chọn Ảnh
          </label>
          <div class="text-12 text-grey-lighten-1 mt-3">
            <p class="m-0">Dụng lượng file tối đa 1 MB</p>
            <p class="m-0">Định dạng:.JPEG, .PNG, .JPG</p>
          </div>
        </div>
        <div class="mt-2 text-red-darken-1 text-14">
          {{ errMsg }}
        </div>
        <button
          class="bg-blue-accent-4 w-100 py-3 rounded-lg mt-10"
          type="submit"
        >
          Đăng ký
        </button>
      </form>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
