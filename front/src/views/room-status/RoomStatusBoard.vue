<template>
  <div class="space-y-6">
    <!-- Stats Row -->
    <div class="flex items-center justify-between">
      <div class="flex items-center gap-4">
        <div class="bg-surface rounded-xl border border-outline-variant px-6 py-4">
          <p class="text-sm text-on-surface-variant">出租率</p>
          <p class="text-2xl font-bold text-on-surface">{{ store.occupancyRate }}%</p>
        </div>
        <div class="bg-surface rounded-xl border border-outline-variant px-6 py-4">
          <p class="text-sm text-on-surface-variant">总房间数</p>
          <p class="text-2xl font-bold text-on-surface">{{ store.rooms.length }}</p>
        </div>
      </div>
    </div>

    <!-- Floor Filter + Status Legend -->
    <div class="flex items-center justify-between flex-wrap gap-4">
      <FloorFilterTabs
        :floors="store.floors"
        :selected-floor-id="store.selectedFloorId"
        @select="store.setFloorFilter"
      />
      <StatusLegendBar :summary="store.statusSummary" />
    </div>

    <!-- Room Grid -->
    <RoomGrid
      :rooms="store.filteredRooms"
      :loading="store.loading"
      @change-status="handleStatusChange"
      @view-detail="handleViewDetail"
    />

    <!-- Detail Drawer -->
    <RoomDetailDrawer
      v-model:visible="drawerVisible"
      :room="selectedRoom"
      :logs="roomLogs"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue'
import { useRoomBoardStore } from '../../stores/roomBoardStore'
import { changeRoomStatus, fetchRoomLogs } from '../../api/roomApi'
import type { RoomVO, RoomStatusLogVO } from '../../types/room'
import FloorFilterTabs from '../../components/room-status/FloorFilterTabs.vue'
import StatusLegendBar from '../../components/room-status/StatusLegendBar.vue'
import RoomGrid from '../../components/room-status/RoomGrid.vue'
import RoomDetailDrawer from '../../components/room-status/RoomDetailDrawer.vue'
import { ElMessage } from 'element-plus'

const store = useRoomBoardStore()
const drawerVisible = ref(false)
const selectedRoom = ref<RoomVO | null>(null)
const roomLogs = ref<RoomStatusLogVO[]>([])

let eventSource: EventSource | null = null

onMounted(() => {
  store.loadBoardData()
  connectSSE()
})

onUnmounted(() => {
  eventSource?.close()
})

function connectSSE() {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  eventSource = new EventSource(`${baseUrl}/api/v1/rooms/board/stream`)

  eventSource.addEventListener('room_status_changed', (event) => {
    const data = JSON.parse(event.data)
    store.updateRoomStatus(data.roomId, data.newStatus)
  })

  eventSource.onerror = () => {
    eventSource?.close()
    setTimeout(connectSSE, 5000)
  }
}

async function handleStatusChange(roomId: number, newStatus: string, reason?: string) {
  const room = store.rooms.find(r => r.id === roomId)
  if (!room) return

  const prevStatus = room.status
  store.updateRoomStatus(roomId, newStatus)

  try {
    await changeRoomStatus(roomId, { newStatus, reason })
    ElMessage.success('房态更新成功')
  } catch (e: any) {
    store.updateRoomStatus(roomId, prevStatus)
    ElMessage.error(e.message || '操作失败')
  }
}

async function handleViewDetail(room: RoomVO) {
  selectedRoom.value = room
  drawerVisible.value = true
  try {
    const res = await fetchRoomLogs(room.id)
    roomLogs.value = res.data
  } catch {
    roomLogs.value = []
  }
}
</script>
