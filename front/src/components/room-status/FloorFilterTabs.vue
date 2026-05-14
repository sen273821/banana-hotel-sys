<template>
  <div class="flex items-center gap-2">
    <button
      class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
      :class="[
        selectedFloorId === null
          ? 'bg-primary text-on-primary'
          : 'bg-surface border border-outline-variant text-on-surface-variant hover:bg-surface-container'
      ]"
      @click="$emit('select', null)"
    >
      所有楼层
    </button>
    <button
      v-for="floor in floors"
      :key="floor.id"
      class="px-4 py-2 rounded-lg text-sm font-medium transition-colors"
      :class="[
        selectedFloorId === floor.id
          ? 'bg-primary text-on-primary'
          : 'bg-surface border border-outline-variant text-on-surface-variant hover:bg-surface-container'
      ]"
      @click="$emit('select', floor.id)"
    >
      {{ floor.name }}
    </button>
  </div>
</template>

<script setup lang="ts">
import type { FloorVO } from '../../types/room'

defineProps<{
  floors: FloorVO[]
  selectedFloorId: number | null
}>()

defineEmits<{
  select: [floorId: number | null]
}>()
</script>
