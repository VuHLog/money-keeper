<script setup>
import { ref, onMounted, defineProps, getCurrentInstance, computed, inject } from "vue";
import { materialColors } from "@/common/RandomMaterialColor.js";

const { proxy } = getCurrentInstance();

const emit = defineEmits(["update:modelValue"]);

const getRandomColor = computed(() => {
  const randomIndex = Math.floor(Math.random() * materialColors.length);
  return `-${materialColors[randomIndex]}-accent-4`;
});

const beneficiaryList = ref([]);

const props = defineProps({
  modelValue: {
    type: Object,
    default: {},
  },
});

onMounted(() => {
  proxy.$api.get("/beneficiaries").then((res) => {
    beneficiaryList.value = res.result;
  });
});

const showCreateInput = ref(false);
const createTextInput = ref("");

async function handleClickCreateItem(){
  await proxy.$api.post("/beneficiaries", { name: createTextInput.value }).then((res) => {
    beneficiaryList.value.push(res.result);
    createTextInput.value = '';
    showCreateInput.value = false;
  })
}

function handleClickItem(item) {
  emit("update:modelValue", item);
}

async function handleClickSaveItem(item) {
  await proxy.$api.put(`/beneficiaries/${item.id}`, { name: item.name }).then((res) => {
    item.isEditing = false;
  })
}

async function handleClickDeleteItem(item) {
  item.isDeleted = true;
  await proxy.$api.delete(`/beneficiaries/${item.id}`).then(() => {
    beneficiaryList.value = beneficiaryList.value.filter(
      (value) => value.id != item.id
    );
  })
}
</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box overflow-y-auto overflow-hidden">
    <h1 class="text-center text-primary-color mb-8">Chi cho ai</h1>

    <div class="d-flex justify-end">
      <v-expand-x-transition>
        <div class="flex-center" v-show="showCreateInput">
          <div class="mr-2 pa-2 cursor-pointer" @click="handleClickCreateItem()">
            <font-awesome-icon :icon="['fas', 'pen-to-square']" />
            <v-tooltip activator="parent" location="bottom"
              >Thêm đối tượng</v-tooltip
            >
          </div>
          <v-text-field
            v-model="createTextInput"
            hide-details="auto"
            class="text-blue-accent-3 font-weight-bold text-12 mr-2"
            width="300px"
            max-width="300px"
            label="Nhập đối tượng"
            density="compact"
          ></v-text-field>
        </div>
      </v-expand-x-transition>
      <button
        class="pa-2 bg-blue-accent-3 flex-center rounded-circle text-white hover-opacity"
        style="height: 40px; width: 40px"
        @click="
          showCreateInput = !showCreateInput;
          createTextInput = '';
        "
      >
        <font-awesome-icon
          v-show="showCreateInput === false"
          :icon="['fas', 'plus']"
        />
        <font-awesome-icon
          v-show="showCreateInput === true"
          :icon="['fas', 'xmark']"
        />
      </button>
    </div>

    <v-row class="pa-3 scroll-container" no-gutters>
      <template v-for="item in beneficiaryList" :key="item.id">
        <v-slide-x-transition>
          <v-col cols="4" v-if="!item?.isDeleted">
            <div
              class="d-flex align-center rounded-xl elevation-4 ma-2 overflow-hidden justify-space-between align-center"
            >
              <span
                class="pa-3 mr-2 text-white d-flex align-center align-self-stretch cursor-pointer hover-opacity-06"
                :class="'bg' + getRandomColor"
                @click="handleClickItem(item)"
              >
                {{ item.name.charAt(0).toUpperCase() }}
              </span>
              <span v-show="!item?.isEditing">{{ item.name }}</span>
              <v-text-field
                v-model="item.name"
                v-show="item?.isEditing"
                hide-details="true"
                class="text-blue-accent-3 font-weight-bold"
                width="auto"
                max-width="100%"
                label="Chuyến đi/Sự kiện"
                density="comfortable"
              ></v-text-field>
              <div class="flex-center mr-2">
                <div
                  class="mr-2 pa-2 cursor-pointer"
                  @click="item.isEditing = !item.isEditing"
                >
                  <font-awesome-icon
                    v-show="!item?.isEditing"
                    :icon="['fas', 'pen-to-square']"
                  />
                  <font-awesome-icon
                    v-show="item?.isEditing"
                    :icon="['fas', 'check']"
                    @click="handleClickSaveItem(item)"
                  />
                  <v-tooltip activator="parent" location="bottom">{{
                    item.isEditing ? "Lưu" : "Sửa"
                  }}</v-tooltip>
                </div>
                <div
                  class="cursor-pointer"
                  @click="handleClickDeleteItem(item)"
                >
                  <font-awesome-icon :icon="['fas', 'trash']" />
                  <v-tooltip activator="parent" location="bottom"
                    >Xóa</v-tooltip
                  >
                </div>
              </div>
            </div>
          </v-col>
        </v-slide-x-transition>
      </template>
    </v-row>
  </div>
</template>

<style lang="scss" scoped>
.container-box {
  width: 1000px;
  height: 80vh;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  .scroll-container {
    max-height: 60vh;
    overflow-y: auto;
  }
}
</style>
