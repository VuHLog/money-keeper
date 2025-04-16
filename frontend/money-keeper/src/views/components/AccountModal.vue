<script setup>
import { ref, onMounted, defineProps, getCurrentInstance, watch } from "vue";

const { proxy } = getCurrentInstance();

const emit = defineEmits(["update:modelValue", "confirm"]);

const props = defineProps({
  modelValue: {
    type: Array,
    default: [],
  }
});

const search = ref("");
const selectedAccounts = ref(new Map());
const accountList = ref([]);
const isSelectAll = ref(false);
const isManuallyToggledSelectAll = ref(false);
const totalElements = ref(0);
const isOnmounted = ref(false);
const accountListTemp = ref([]);

onMounted(async () => {
  await getData();
  isOnmounted.value = true;
});

async function getData() {
  let text = search.value === null ? "" : search.value;
  await proxy.$api
    .get("/dictionary-bucket-payment?search=" + text)
    .then((res) => {
      accountList.value = res.result;
    })
    .catch((error) => console.log(error));
  if(!isOnmounted.value){
    accountListTemp.value = accountList.value;
    totalElements.value = accountListTemp.value.length;
    if(props.modelValue.length === 0){
      isSelectAll.value = true;
      accountListTemp.value.forEach((value) => {
        selectedAccounts.value.set(value.id, true);
      });
    }else{
      isSelectAll.value = props.modelValue.length === totalElements.value;
      props.modelValue.forEach((value) => {
        selectedAccounts.value.set(value.id, true);
      });
    }
  }
}

watch(isSelectAll,
  () =>{
      if (!isManuallyToggledSelectAll.value) return; // handle when click select all check box
      if (isSelectAll.value) {
        accountListTemp.value.forEach((value) => {
          selectedAccounts.value.set(value.id, true);
        });
      } else {
        selectedAccounts.value.clear();
      }
      isManuallyToggledSelectAll.value = false; // reset
    }
)

watch(search, () => {
  getData();
});

function handleSelectAccount(account) {
  const isSelected = selectedAccounts.value.has(account.id);
  if (isSelected) {
    selectedAccounts.value.delete(account.id);
  } else {
    selectedAccounts.value.set(account.id, account);
  }
  if (selectedAccounts.value.size === totalElements.value) {
    isSelectAll.value = true;
  } else {
    isSelectAll.value = false;
  }
}

function handleConfirm() {
  const selectedItems = accountListTemp.value.filter(item => 
    selectedAccounts.value.has(item.id)
  );
  emit("update:modelValue", selectedItems);
  emit("confirm", selectedItems);
}
</script>

<template>
  <div class="bg-white rounded-lg pa-3 position-relative container-box overflow-y-auto overflow-hidden">
    <h1 class="text-center text-primary mb-8">Chọn tài khoản</h1>
    <div class="d-flex align-center justify-space-between mr-2 mb-8">
      <v-switch
        v-model="isSelectAll"
        color="green-accent-3"
        label="Chọn tất cả"
        hide-details
        @click="() => isManuallyToggledSelectAll = true"
      ></v-switch>
      <div>
        <v-text-field width="300" density="compact" v-model.trim="search" label="Tìm kiếm theo tên tài khoản"
          variant="outlined" hide-details single-line clearable>
          <template v-slot:prepend-inner>
            <v-icon icon="fa:fas fa-magnifying-glass" />
          </template>
        </v-text-field>
      </div>
    </div>
    <div class="mt-10" v-if="accountList?.length === 0">
      <h4 class="flex-center font-weight-bold text-grey-darken-1">Không tìm thấy tài khoản chứa "{{ search }}"</h4>
    </div>
    <v-row v-else class="scroll-container" align="stretch">
      <v-col cols="12">
        <v-list>
          <v-list-item
            v-for="account in accountList"
            :key="account.id"
            class="account-item"
            :class="{ 'selected': selectedAccounts.has(account.id) }"
            @click="handleSelectAccount(account)"
          >
            <template v-slot:prepend>
              <v-avatar>
                <img class="icon-size" :src="account.iconUrl" :alt="account.accountName" />
              </v-avatar>
            </template>
            <v-list-item-title class="text-grey-darken-4 font-weight-bold">
              {{ account.accountName }}
            </v-list-item-title>
            <template v-slot:append>
              <font-awesome-icon v-if="selectedAccounts.has(account.id)" class="text-green-darken-3" :icon="['fas', 'circle-check']" />
              <font-awesome-icon v-else class="text-grey" :icon="['far', 'circle']" />
            </template>
          </v-list-item>
        </v-list>
      </v-col>
    </v-row>
    <div class="d-flex justify-center mt-4">
      <v-btn
        color="primary"
        :disabled="selectedAccounts.size === 0"
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
  width: 600px;
  height: 80vh;
  max-height: 80vh;
  display: flex;
  flex-direction: column;

  .scroll-container {
    max-height: 60vh;
    overflow-y: auto;
    flex: 1;
  }
}

.account-item {
  border-radius: 8px;
  margin-bottom: 8px;
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    background-color: rgba(0, 0, 0, 0.05);
  }

  &.selected {
    background-color: var(--v-primary-lighten-5);
  }
}
</style>
