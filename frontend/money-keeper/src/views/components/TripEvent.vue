<script setup>
import { ref, onMounted, defineProps, getCurrentInstance, computed } from "vue";
import { materialColors } from "@/common/RandomMaterialColor.js";

const { proxy } = getCurrentInstance();

const emit = defineEmits(["update:modelValue"]);

const getRandomColor = computed(() => {
  const randomIndex = Math.floor(Math.random() * materialColors.length);
  return `-${materialColors[randomIndex]}-accent-4`;
});

const props = defineProps({
  modelValue: {
    type: Object,
    default: {},
  },
});

const showCreateInput = ref(false);
const tripEventTextInput = ref("");

const tripEvents = ref([
  {
    id: 1,
    name: "Chuyen di",
  },
  {
    id: 2,
    name: "Sự kiện 2",
  },
  {
    id: 3,
    name: "Sự kiện 3",
  },
  {
    id: 4,
    name: "Sự kiện 4",
  },
]);

function handleClickTripEvent(tripEvent) {
  emit("update:modelValue", tripEvent);
}

async function handleClickCreateTripEvent(){
  tripEvents.value.push({id: 10, name: tripEventTextInput.value })
  tripEventTextInput.value = '';
  showCreateInput.value = false;
}

async function handleClickDeleteTripEvent(tripEvent) {
  tripEvent.isDeleted = true;
  setTimeout(() => {
    tripEvents.value = tripEvents.value.filter(
      (value) => value.id != tripEvent.id
    );
  }, 1000);
}
</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box">
    <h1 class="text-center text-primary-color mb-8">Chuyến đi/Sự kiện</h1>

    <div class="d-flex justify-end">
      <v-expand-x-transition>
        <div class="flex-center" v-show="showCreateInput">
          <div class="mr-2 pa-2 cursor-pointer" @click="handleClickCreateTripEvent()">
            <font-awesome-icon :icon="['fas', 'pen-to-square']" />
            <v-tooltip activator="parent" location="bottom"
              >Thêm Chuyến đi/Sự kiện</v-tooltip
            >
          </div>
          <v-text-field
            v-model="tripEventTextInput"
            hide-details="auto"
            class="text-blue-accent-3 font-weight-bold mr-2"
            width="300px"
            max-width="300px"
            label="Nhập chuyến đi/sự kiện"
            density="compact"
          ></v-text-field>
        </div>
      </v-expand-x-transition>
      <button
        class="pa-2 bg-blue-accent-3 flex-center rounded-circle text-white hover-opacity"
        style="height: 40px; width: 40px"
        @click="
          showCreateInput = !showCreateInput;
          tripEventTextInput = '';
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

    <v-row class="pa-3 scroll-container" align="stretch" no-gutters>
      <template v-for="item in tripEvents" :key="item.id">
        <v-slide-x-transition>
          <v-col cols="4" v-if="!item?.isDeleted">
            <div
              class="d-flex align-center rounded-xl elevation-4 ma-2 overflow-hidden justify-space-between align-center"
            >
              <span
                class="pa-3 mr-2 text-white d-flex align-center align-self-stretch cursor-pointer hover-opacity-06"
                :class="'bg' + getRandomColor"
                @click="handleClickTripEvent(item)"
              >
                {{ item.name.charAt(0) }}
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
                  />
                  <v-tooltip activator="parent" location="bottom">{{
                    item.isEditing ? "Lưu" : "Sửa"
                  }}</v-tooltip>
                </div>
                <div
                  class="cursor-pointer text-red-accent-3"
                  @click="handleClickDeleteTripEvent(item)"
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
