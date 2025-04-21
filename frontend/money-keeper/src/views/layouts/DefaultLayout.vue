<script setup>
import HeaderApp from "./Header.vue";
import Loading from "@components/Loading.vue";
import { computed} from "vue";
import { useRoute } from "vue-router";
import { useBaseStore } from "@/store/index.js";


const route = useRoute();
const store = useBaseStore();

const getRoute = computed(() => {
  const routeArr = route.path.split("/");
  return routeArr[1];
});

const fullPath = computed(() => route.fullPath)
const isLoading = computed(() => store.isLoading);
</script>
<template>
  <Loading v-if="isLoading"/>
  <div class="app min-h-100vh">
    <HeaderApp></HeaderApp>
    <div style="padding-top: 60px">
      <div class="bg-green-accent-3" style="min-height: calc(100vh - 60px)">
        <div class="pa-10">
          <div
            class="bg-white rounded-lg pa-5"
            style="box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px"
          >
            <router-view></router-view>
          </div>
        </div>
      </div>
    </div>
    <div class="position-fixed z-index-99 feature-list user-select-none">
      <template v-if="!fullPath.includes('create') && !fullPath.includes('edit') && !fullPath.includes('info')">
        <router-link v-if="getRoute === 'account'" :to="{path: '/account/create', query: { redirect: route.fullPath }}"
          class="feature-item bg-white rounded-circle flex-center cursor-pointer hover-opacity z-index-99">
          <font-awesome-icon class="text-primary" :icon="['fas', 'plus']" />
          <v-tooltip activator="parent" location="start">Thêm tài khoản</v-tooltip>
        </router-link>
        <router-link v-else-if="getRoute === 'expense-limit'" :to="{path: '/expense-limit/create', query: { redirect: route.fullPath }}"
        class="feature-item bg-white rounded-circle flex-center cursor-pointer hover-opacity z-index-99">
          <font-awesome-icon class="text-primary" :icon="['fas', 'plus']" />
          <v-tooltip activator="parent" location="start">Thêm hạn mức chi</v-tooltip>
        </router-link>
        <router-link v-else-if="getRoute === 'expense'" :to="{path: '/expense/create', query: { redirect: route.fullPath }}"
        class="feature-item bg-white rounded-circle flex-center cursor-pointer hover-opacity z-index-99">
          <font-awesome-icon class="text-primary" :icon="['fas', 'plus']" />
          <v-tooltip activator="parent" location="start">Ghi thu chi</v-tooltip>
        </router-link>
        <router-link v-else :to="{path: '/expense/create', query: { redirect: route.fullPath }}"
        class="feature-item bg-white rounded-circle flex-center cursor-pointer hover-opacity z-index-99">
          <font-awesome-icon class="text-primary" :icon="['fas', 'plus']" />
          <v-tooltip activator="parent" location="start">Ghi thu chi</v-tooltip>
        </router-link>
      </template>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.app{
  .feature-list{
    bottom: 40px;
    right: 30px;
    .feature-item{
      box-shadow: rgba(17, 17, 26, 0.1) 0px 4px 16px, rgba(17, 17, 26, 0.1) 0px 8px 24px, rgba(17, 17, 26, 0.1) 0px 16px 56px;
      height: 40px;
      width: 40px;
    }
  }
}
</style>
