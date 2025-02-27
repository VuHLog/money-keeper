<script setup>
import {computed} from "vue"
import {useBaseStore} from '@/store/index.js'

const store = useBaseStore();
const showFooter = computed(()=>store.showFooter);
</script>

<template>
  <footer class="footer bg-black px-16" v-if="showFooter">
      <div class="d-flex justify-space-between py-8 ">
        <div>
          <span>
            © 2024 Made With By longdz@gmail.com
          </span>
        </div>
      </div>
  </footer>
</template>

<style lang="scss" scoped>
</style>