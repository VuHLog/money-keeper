<script setup>
import { ref, getCurrentInstance, onMounted } from "vue";
import { inject } from "vue";

const emit = defineEmits(['hide-option']);

const { proxy } = getCurrentInstance();
const swal = inject("$swal");
const file = ref(null);
const errMsg = ref("");
const previewImage = ref("");

const props = defineProps({
  modelValue: {
    type: Object,
    default: {},
  },
});

const user = ref({
  password: "",
  email: "",
  fullName: "",
  avatarUrl: "",
  roles: [],
});

onMounted(() => {
  proxy.$api
    .get("/users/myInfo")
    .then((res) => {
      Object.assign(user.value, res.result);
      delete user.user_roles;
      user.value.roles = res.result.user_roles.map((role) => role.role);
    })
    .catch((error) => console.log(error));
});

async function handleFileUpload(event) {
  file.value = event.target.files[0];
  if (file.value) {
    // Tạo URL tạm thời cho ảnh vừa chọn
    previewImage.value = URL.createObjectURL(file.value);
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

function isValid() {
  if (!/^.{8,}$/.test(user.value.fullName.trim())) {
    errMsg.value = "Họ tên phải có ít nhất 8 ký tự";
    return false;
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(user.value.email.trim())) {
    errMsg.value = "Email không hợp lệ";
    return false;
  }
  errMsg.value = "";
  return true;
}

async function updateUser() {
  if (!isValid()) return;

  if (file.value !== null) {
    await submitFile();
  }

  await proxy.$api
    .put("/users/" + user.value.id, user.value)
    .then((res) => {
      emit("update:modelValue", false);
      swal.fire({
        title: "Thành công",
        text: "Bạn đã thay đổi thông tin tài khoản thành công!",
        icon: "success",
      });
    })
    .catch((error) => console.log(error));
}
</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box overflow-y-auto overflow-hidden">
    <h1 class="text-center text-primary mb-8">Thông tin tài khoản</h1>

    <div>
      <v-row justify="center">
        <v-col cols="7">
          <v-text-field v-model="user.username" label="Tên đăng nhập" hide-details="true" disabled
            class="text-blue-accent-3 font-weight-bold text-end" bg-color="bg-white" hide-spin-buttons>
          </v-text-field>
        </v-col>
        <v-col cols="7">
          <v-text-field v-model="user.fullName" label="Họ tên" hide-details="true"
            class="text-blue-accent-3 font-weight-bold text-end" bg-color="bg-white" hide-spin-buttons>
          </v-text-field>
        </v-col>
        <v-col cols="7">
          <v-text-field v-model="user.email" label="Email" hide-details="auto"
            class="text-blue-accent-3 font-weight-bold text-end" bg-color="bg-white" hide-spin-buttons>
          </v-text-field>
        </v-col>
        <v-col cols="12">
          <div class="d-flex align-center flex-column">
            <div class="py-4">
              <img class="height-avatar-thumbnail width-avatar-thumbnail rounded-circle object-cover object-center"
                :src="previewImage || user.avatarUrl" alt="avatar" />
            </div>
            <input id="input-image" class="d-none" type="file" accept=".jpg,.jpeg,.png"
              @change="handleFileUpload($event)" />
            <label for="input-image"
              class="pa-2 cursor-pointer rounded-sm bg-white border-sm border-solid text-grey-darken-4 box-shadow-none">
              Chọn Ảnh
            </label>
            <div class="text-12 text-grey-lighten-1 mt-3">
              <p class="m-0">Dụng lượng file tối đa 1 MB</p>
              <p class="m-0">Định dạng:.JPEG, .PNG, .JPG</p>
            </div>
          </div>
        </v-col>
        <v-col cols="12" v-show="errMsg.length > 0">
          <p class="text-red-accent-3 text-center">{{ errMsg }}</p>
        </v-col>
      </v-row>
      <div class="text-center mt-2">
        <button class="bg-primary-color text-white py-2 px-10 rounded d-inline-flex justify-center" @click.stop="updateUser">
        <div class="mr-2">
          <font-awesome-icon :icon="['fas', 'floppy-disk']" />
        </div>
        Lưu
      </button>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.container-box {
  width: 1000px;
  min-height: 60vh;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}
</style>
