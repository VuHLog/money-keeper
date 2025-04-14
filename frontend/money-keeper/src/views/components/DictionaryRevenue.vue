<script setup>
import { ref, onMounted, defineProps, getCurrentInstance } from "vue";

const { proxy } = getCurrentInstance();

const emit = defineEmits(["update:modelValue"]);

const props = defineProps({
  modelValue: {
    type: Object,
    default: {},
  },
});

const dictionaryRevenue = ref([]);
onMounted(async () => {
  let data = [];
  await proxy.$api
    .get("/dictionary-revenue/without-transfer")
    .then((res) => {
      data = res.result;
    })
    .catch((error) => console.log(error));

  let parents = data.filter((item) => item.parentId === null);
  parents.forEach((parent) => {
    parent.children = data.filter((child) => child.parentId === parent.id);
  });
  dictionaryRevenue.value = parents;
});

function handleClickCategory(category) {
  emit("update:modelValue", category);
}
</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box overflow-y-auto overflow-hidden">
    <h1 class="text-center text-primary mb-8">Chọn hạng mục</h1>

    <v-row class="scroll-container" align="stretch">
      <template v-for="category in dictionaryRevenue" :key="category.id">
        <v-col cols="6">
          <div class="category-box">
            <v-row class="hover-bg-grey-darken cursor-pointer" @click="handleClickCategory(category)">
              <v-col
                cols="3"
                class="flex-center"
              >
                <div>
                  <img :src="category.iconUrl" alt="" />
                </div>
              </v-col>
              <v-col
                cols="5"
                class="d-flex align-center text-center position-relative"
              >
                <span class="text-center text-grey-darken-4 font-weight-bold position-absolute" style="left: -20px; top: 50%; transform: translateY(-50%);">{{ category.name }}</span>
              </v-col>
            </v-row>

            <v-row v-if="category.children && category.children.length > 0">
              <template v-for="child in category.children" :key="child.id">
                <v-col
                  cols="3"
                  class="d-flex flex-column align-center align-self-start hover-bg-grey-darken cursor-pointer"
                  @click="handleClickCategory(child)"
                >
                  <div>
                    <img :src="child.iconUrl" alt="" />
                  </div>
                  <span class="text-center text-grey-color">{{ child.name }}</span>
                </v-col>
              </template>
            </v-row>
          </div>
        </v-col>
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
    .category-box {
      background: #f5f5f5;
      border-radius: 8px;
      padding: 16px;
      min-height: 150px;
      display: flex;
      flex-direction: column;
      height: 100%;
    }
  }
}
</style>
