<script setup>
import { ref } from "vue";

const spendingLimit = ref({
  name: "Long",
  avatar: "path/to/avatar.jpg",
  limit: 10000000,
  current: 10000,
  daysLeft: 21,
  remaining: 9999993
});
</script>

<template>
  <v-card class="pa-4">
    <h3 class="mb-4">Hạn mức chi</h3>
    <div class="d-flex align-center mb-4">
      <v-avatar class="mr-2" color="pink">
        <span class="text-h6">{{ spendingLimit.name[0] }}</span>
      </v-avatar>
      <span class="text-h6">{{ spendingLimit.name }}</span>
    </div>

    <v-progress-linear
      :model-value="(spendingLimit.current / spendingLimit.limit) * 100"
      color="success"
      height="20"
      rounded
    >
      <template v-slot:default="{ value }">
        <span class="white--text">{{ Math.ceil(value) }}%</span>
      </template>
    </v-progress-linear>

    <div class="d-flex justify-space-between mt-2">
      <div class="text-body-2">Còn {{ spendingLimit.daysLeft }} ngày</div>
      <div class="text-body-2">{{ spendingLimit.remaining }}đ</div>
    </div>
  </v-card>
</template> 