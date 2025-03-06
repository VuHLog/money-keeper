<script setup>
import HeaderApp from "./Header.vue";
import { computed} from "vue";
import { useRoute } from "vue-router";
const route = useRoute();

const getRoute = computed(() => {
  const routeArr = route.path.split("/");
  return routeArr[1];
});
</script>
<template>
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
    <div v-if="getRoute !== 'income-and-expenditure'" class="position-fixed z-index-99 feature-list user-select-none">
      <router-link to="/income-and-expenditure" class="feature-item bg-white rounded-circle flex-center cursor-pointer hover-opacity z-index-99">
        <font-awesome-icon class="text-primary-color" :icon="['fas', 'plus']" />
        <v-tooltip activator="parent" location="start">Ghi thu chi</v-tooltip>
      </router-link>
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
