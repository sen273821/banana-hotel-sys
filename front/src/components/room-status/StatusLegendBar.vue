<template>
  <div class="flex items-center gap-4 flex-wrap">
    <div
      v-for="(config, status) in ROOM_STATUS_CONFIG"
      :key="status"
      class="flex items-center gap-1.5"
    >
      <span
        class="w-3 h-3 rounded-full"
        :style="{ backgroundColor: config.color }"
      ></span>
      <span class="text-xs text-on-surface-variant">
        {{ config.label }}({{ summary[status] || 0 }})
      </span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ROOM_STATUS_CONFIG } from '../../constants/roomStatus'

defineProps<{
  summary: Record<string, number>
}>()
</script>
