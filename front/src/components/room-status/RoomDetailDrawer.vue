<template>
  <el-drawer
    :model-value="visible"
    title="房间详情"
    direction="rtl"
    size="400px"
    @close="$emit('update:visible', false)"
  >
    <template v-if="room">
      <!-- Room Info -->
      <div class="mb-6">
        <div class="flex items-center gap-3 mb-4">
          <span class="text-2xl font-bold">{{ room.roomNumber }}</span>
          <span
            class="px-2 py-0.5 rounded text-xs font-medium"
            :style="{ color: statusConfig.color, backgroundColor: statusConfig.bgColor }"
          >
            {{ statusConfig.label }}
          </span>
        </div>
        <div class="flex items-center gap-2 text-sm text-gray-500">
          <span class="material-symbols-outlined text-base">{{ room.roomTypeIcon }}</span>
          <span>{{ room.roomTypeName }}</span>
        </div>
      </div>

      <!-- Guest Info -->
      <div v-if="room.assignment" class="mb-6 p-4 bg-gray-50 rounded-lg">
        <h4 class="text-sm font-semibold mb-3">入住信息</h4>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-500">入住人</span>
            <span>{{ room.assignment.guestName }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">手机号</span>
            <span>{{ room.assignment.guestPhone }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">入住时间</span>
            <span>{{ formatTime(room.assignment.checkInTime) }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">退房时间</span>
            <span>{{ formatTime(room.assignment.checkOutTime) }}</span>
          </div>
        </div>
      </div>

      <!-- Status Logs -->
      <div>
        <h4 class="text-sm font-semibold mb-3">操作记录</h4>
        <div v-if="logs.length === 0" class="text-sm text-gray-400 text-center py-4">
          暂无操作记录
        </div>
        <div v-else class="space-y-3">
          <div
            v-for="log in logs"
            :key="log.id"
            class="flex items-start gap-3 text-sm border-l-2 border-gray-200 pl-3"
          >
            <div class="flex-1">
              <p>
                <span class="text-gray-500">{{ log.operatorName }}</span>
                将状态从
                <span class="font-medium">{{ getStatusLabel(log.previousStatus) }}</span>
                改为
                <span class="font-medium">{{ getStatusLabel(log.newStatus) }}</span>
              </p>
              <p v-if="log.reason" class="text-gray-400 mt-0.5">原因: {{ log.reason }}</p>
              <p class="text-gray-400 text-xs mt-0.5">{{ formatTime(log.createdAt) }}</p>
            </div>
          </div>
        </div>
      </div>
    </template>
  </el-drawer>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { RoomVO, RoomStatusLogVO, RoomStatus } from '../../types/room'
import { ROOM_STATUS_CONFIG } from '../../constants/roomStatus'

const props = defineProps<{
  visible: boolean
  room: RoomVO | null
  logs: RoomStatusLogVO[]
}>()

defineEmits<{
  'update:visible': [value: boolean]
}>()

const statusConfig = computed(() => {
  if (!props.room) return ROOM_STATUS_CONFIG.CLEAN
  return ROOM_STATUS_CONFIG[props.room.status] || ROOM_STATUS_CONFIG.CLEAN
})

function getStatusLabel(status: string): string {
  const config = ROOM_STATUS_CONFIG[status as RoomStatus]
  return config?.label || status
}

function formatTime(time: string | undefined): string {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>
