<script setup>
defineProps({
  savings: {
    type: Array,
    default: () => []
  }
});
</script>

<template>
  <v-card class="pa-4">
    <h3 class="mb-4">Số tiết kiệm</h3>
    <v-list>
      <v-list-item v-for="saving in savings" :key="saving.name">
        <v-list-item-title>{{ saving.name }}</v-list-item-title>
        <v-list-item-subtitle class="text-h6 font-weight-bold mt-2">
          {{ saving.amount }}đ
        </v-list-item-subtitle>
        <div class="d-flex justify-space-between mt-2">
          <span class="text-caption">Đáo hạn: {{ saving.dueDate }}</span>
          <span class="text-caption">{{ saving.remainingTime }}</span>
        </div>
      </v-list-item>
    </v-list>
  </v-card>
</template> 