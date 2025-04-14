<template>
    <div>
        <v-progress-linear class="animated-progress" :model-value="animatedProgress" height="8" :color="props.color"
            bg-color="grey-darken-1" rounded></v-progress-linear>
    </div>
</template>

<script setup lang="ts">
import { defineProps, onMounted, ref } from 'vue'

const props = defineProps({
    progress: {
        type: Number,
        required: true,
    },
    color: {
        type: String,
        default: 'primary',
    }
})

const animatedProgress = ref(0)

onMounted(() => {
    // animation bằng transition CSS
    requestAnimationFrame(() => {
        animatedProgress.value = props.progress
    })
})
</script>

<style lang="scss" scoped>
.animated-progress {
    .v-progress-linear__determinate {
        transition: width 2s linear;
    }
}
</style>