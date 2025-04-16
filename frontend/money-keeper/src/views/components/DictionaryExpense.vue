<script setup>
import { ref, onMounted, defineProps, getCurrentInstance, watch } from "vue";

const { proxy } = getCurrentInstance();

const emit = defineEmits(["update:modelValue", "confirm"]);

const props = defineProps({
  modelValue: {
    type: [Object, Array],
    default: () => ({}),
  },
  isMultiple: {
    type: Boolean,
    default: false
  }
});

const search = ref("");
const dictionaryExpense = ref([]);
const isSelectAll = ref(false);
const isManuallyToggledSelectAll = ref(false);
const itemSelectedMap = ref(new Map());
const itemIdSelectedList = ref([])
const totalElements = ref(0);
const isOnmounted = ref(false);
const dictionaryExpenseTemp = ref([]);

onMounted(async () => {
  await getData();
  isOnmounted.value = true;
});

async function getData(){
  let data = [];
  let text = search.value === null ? "" : search.value;
  await proxy.$api
    .get("/dictionary-expense/without-transfer?search=" + text)
    .then((res) => {
      dictionaryExpenseTemp.value = res.result;
    })
    .catch((error) => console.log(error));
  if(!isOnmounted.value && props.isMultiple){
    totalElements.value = dictionaryExpenseTemp.value.length;
    if(props.modelValue.length === 0){
      isSelectAll.value = true;
      dictionaryExpenseTemp.value.forEach((value) => {
        itemSelectedMap.value.set(value.id, true);
        itemIdSelectedList.value.push(value.id);
      });
    }else{
      isSelectAll.value = props.modelValue.length === totalElements.value;
      props.modelValue.forEach((value) => {
        itemSelectedMap.value.set(value.id, true);
        itemIdSelectedList.value.push(value.id);
      });
    }
  }

  let parents = dictionaryExpenseTemp.value.filter((item) => item.parentId === null);
  parents.forEach((parent) => {
    parent.children = dictionaryExpenseTemp.value.filter((child) => child.parentId === parent.id);
  });
  dictionaryExpense.value = parents;
}

watch(
  search,
  () =>{
    getData();
  }
)

watch(isSelectAll,
  () =>{
      if (!isManuallyToggledSelectAll.value) return; // handle when click select all check box
      itemIdSelectedList.value = [];
      if (isSelectAll.value) {
        dictionaryExpenseTemp.value.forEach((value) => {
          itemSelectedMap.value.set(value.id, true);
          itemIdSelectedList.value.push(value.id);
        });
      } else {
        itemSelectedMap.value.clear();
        itemIdSelectedList.value = [];
      }
      isManuallyToggledSelectAll.value = false; // reset
    }
)

function handleSelectCategory(id) {
  const isSelected = itemSelectedMap.value.get(id);
  if (isSelected) {
    itemSelectedMap.value.delete(id);
    itemIdSelectedList.value = itemIdSelectedList.value.filter(value => value !== id);
  }else{
    itemSelectedMap.value.set(id, true);
    itemIdSelectedList.value.push(id);
  }
  if (itemIdSelectedList.value.length === totalElements.value) {
    isSelectAll.value = true;
  } else {
    isSelectAll.value = false;
  }
}

function handleClickCategory(category) {
  if(!props.isMultiple){
    emit("update:modelValue", category);
  }else{
    handleSelectCategory(category.id);
  }
}

function handleConfirm() {
  const selectedItems = dictionaryExpenseTemp.value.filter(item => 
    itemSelectedMap.value.has(item.id)
  );
  emit("update:modelValue", selectedItems);
  emit("confirm", selectedItems);
}
</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box overflow-y-auto overflow-hidden">
    <h1 class="text-center text-primary mb-8">Chọn hạng mục</h1>
    <div class="d-flex align-center justify-end mr-2 mb-8" :class="props.isMultiple ? 'justify-space-between': 'justify-end'">
      <div v-if="props.isMultiple">
        <v-switch
          v-model="isSelectAll"
          color="green-accent-3"
          label="Chọn tất cả"
          hide-details
          @click="() => isManuallyToggledSelectAll = true"
        ></v-switch>
      </div>
      <div>
        <v-text-field width="300" density="compact" v-model.trim="search" label="Tìm kiếm theo tên hạng mục"
          variant="outlined" hide-details single-line clearable>
          <template v-slot:prepend-inner>
            <v-icon icon="fa:fas fa-magnifying-glass" />
          </template>
        </v-text-field>
      </div>
    </div>
    <div class="mt-10" v-if="dictionaryExpense?.length === 0">
      <h4 class="flex-center font-weight-bold text-grey-darken-1">Không tồn tại tên hạng mục
        chứa "{{ search }}"</h4>
    </div>
    <v-row v-else class="scroll-container" align="stretch">
      <template v-for="category in dictionaryExpense" :key="category.id">
        <v-col cols="6">
          <div class="category-box">
            <v-row class="hover-bg-grey-darken cursor-pointer transition-fast-in-fast-out" 
              @click="handleClickCategory(category)" 
              :class="[
                (props.isMultiple && itemSelectedMap?.has(category.id)) ? 'bg-primary-lighten-5 elevation-2' : '',
                'rounded-t-lg'
              ]">
              <v-col cols="8" class="flex-center">
                <div style="width: 37.5%;" class="flex-center pr-4">
                  <v-img :src="category.iconUrl" width="40" height="40" contain></v-img>
                </div>
                <span class="text-start text-grey-darken-4 font-weight-bold"
                  :class="(props.isMultiple && itemSelectedMap.has(category.id)) ? 'text-primary' : ''"
                  style="width: 62.5%;">{{ category.name }}</span>
              </v-col>
            </v-row>

            <v-row class="rounded-b-lg">
              <template v-for="child in category.children" :key="child.id">
                <v-col cols="3"
                  class="d-flex flex-column align-center align-self-stretch hover-bg-grey-darken cursor-pointer transition-fast-in-fast-out"
                  :class="[
                    (props.isMultiple && itemSelectedMap.has(child.id)) ? 'bg-primary-lighten-5 elevation-2' : '',
                    'pa-4'
                  ]"
                  @click="handleClickCategory(child)">
                  <div class="mb-2">
                    <v-img :src="child.iconUrl" width="32" height="32" contain></v-img>
                  </div>
                  <span class="text-center text-grey-color"
                    :class="(props.isMultiple && itemSelectedMap.has(child.id)) ? 'text-primary font-weight-bold' : ''">{{ child.name }}</span>
                </v-col>
              </template>
            </v-row>
          </div>
        </v-col>
      </template>
    </v-row>
    <div v-if="props.isMultiple" class="d-flex justify-center mt-4">
      <v-btn
        color="primary"
        :disabled="itemIdSelectedList.length === 0"
        @click="handleConfirm"
        size="large"
      >
        Xác nhận
        <font-awesome-icon :icon="['fas', 'check']" />
      </v-btn>
    </div>
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
    flex: 1;

    .category-box {
      background: #f5f5f5;
      border-radius: 8px;
      padding: 16px;
      min-height: 150px;
      display: flex;
      flex-direction: column;
      height: 100%;
      transition: all 0.3s ease;
    }
  }
}

.hover-bg-grey-darken:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.transition-fast-in-fast-out {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
</style>
