<script setup>
console.log("Loading component loaded")
</script>

<template>
    <div class="loading-container d-flex justify-center align-center">
      <v-progress-circular
        indeterminate
        color="primary"
        size="64"
        width="6"
      ></v-progress-circular>
    </div>
  </template>

<style scoped>
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 9999;
}
</style>