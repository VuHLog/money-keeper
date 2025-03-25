<script setup>
import { ref, onMounted, defineProps, getCurrentInstance, inject } from "vue";

const { proxy } = getCurrentInstance();
const swal = inject("$swal");

const emit = defineEmits(["update:modelValue"]);

const props = defineProps({
  modelValue: {
    type: Object,
    default: {},
  },
});
const errMsg = ref("");

function isValid(){

  errMsg.value = "";
  return true;
}

async function handleClickChangePassword() {
  if (!isValid()) return;

  
  await proxy.$api.put("/users", {
    currPassword: password.value,
    newPassword: newPassword.value,
  }).then(() => {
    emit("update:modelValue", false);
    swal.fire({
      title: "Thành công",
      text: "Bạn đã thay đổi thông tin thành công!",
      icon: "success",
    });
  }).catch((error) => {
    if(error.response.data.code === 1008){
      errMsg.value = "Mật khẩu hiện tại không chính xác";
    }else{
      errMsg.value = "Đã xảy ra lỗi";
    }
  });
}



</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box">
    <h1 class="text-center text-primary-color mb-8">Mật khẩu</h1>

    <v-row justify="center">
      <v-col cols="7">
        <v-text-field v-model="password" label="Mật khẩu hiện tại" type="password" hide-details="true"
          class="text-blue-accent-3 font-weight-bold text-end" bg-color="bg-white" hide-spin-buttons>
        </v-text-field>
      </v-col>
      <v-col cols="7">
        <v-text-field v-model="newPassword" label="Mật khẩu mới" type="password" hide-details="auto"
          class="text-blue-accent-3 font-weight-bold text-end" bg-color="bg-white" hide-spin-buttons>
        </v-text-field>
      </v-col>
      <v-col cols="7">
        <v-text-field v-model="confirmNewPassword" label="Nhập lại mật khẩu" type="password" hide-details="auto"
          class="text-blue-accent-3 font-weight-bold text-end" bg-color="bg-white" hide-spin-buttons>
        </v-text-field>
      </v-col>
      <v-col cols="12" v-show="errMsg.length > 0">
        <p class="text-red-accent-3 text-center">{{ errMsg }}</p>
      </v-col>
    </v-row>

    <div class="text-center">
      <button class="bg-primary-color text-white py-2 px-10 rounded" @click.stop="handleClickChangePassword">
        Lưu
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.container-box {
  width: 1000px;
  height: 60vh;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}
</style>
