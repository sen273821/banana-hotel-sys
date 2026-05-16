<template>
  <div v-if="loading" class="flex items-center justify-center py-20">
    <span class="text-on-surface-variant">加载中...</span>
  </div>
  <div
    v-else
    class="grid grid-cols-2 gap-6 md:grid-cols-3 xl:grid-cols-6"
  >
    <RoomCard
      v-for="room in rooms"
      :key="room.id"
      :room="room"
      @change-status="(newStatus, reason) => $emit('changeStatus', room.id, newStatus, reason)"
      @click="$emit('viewDetail', room)"
    />
  </div>
  <div v-if="!loading && rooms.length === 0" class="text-center py-20 text-on-surface-variant">
    暂无房间数据
  </div>
</template>

<script setup lang="ts">
import type { RoomVO } from '../../types/room'
import RoomCard from './RoomCard.vue'

defineProps<{
  rooms: RoomVO[]
  loading: boolean
}>()

defineEmits<{
  changeStatus: [roomId: number, newStatus: string, reason?: string]
  viewDetail: [room: RoomVO]
}>()
</script>
