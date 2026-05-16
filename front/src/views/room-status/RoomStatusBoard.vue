<template>
  <div class="space-y-8">
    <div class="flex flex-wrap items-center justify-between gap-5">
      <div class="flex rounded border border-[#cfd5df] bg-white p-1">
        <button class="floor-tab" :class="{ active: store.selectedFloorId === null }" @click="store.setFloorFilter(null)">所有楼层</button>
        <button
          v-for="floor in store.floors"
          :key="floor.id"
          class="floor-tab"
          :class="{ active: store.selectedFloorId === floor.id }"
          @click="store.setFloorFilter(floor.id)"
        >
          {{ floor.name }}
        </button>
        <button class="floor-tab">顶层套房</button>
      </div>
      <div class="flex flex-wrap items-center gap-4 rounded border border-[#cfd5df] bg-white px-4 py-3">
        <span v-for="item in legendItems" :key="item.key" class="flex items-center gap-2 text-sm font-bold">
          <span class="h-3 w-3 rounded-full" :style="{ background: item.color }"></span>
          {{ item.label }} ({{ store.statusSummary[item.key] || 0 }})
        </span>
      </div>
    </div>

    <RoomGrid
      :rooms="store.filteredRooms"
      :loading="store.loading"
      @change-status="handleStatusChange"
      @view-detail="handleViewDetail"
    />

    <div class="grid gap-6 lg:grid-cols-[340px_1fr]">
      <div class="rounded-lg border border-[#cfd5df] bg-white p-8">
        <div class="flex items-center gap-6">
          <div class="flex h-14 w-14 items-center justify-center rounded-lg bg-[#dce5fb]">
            <span class="material-symbols-outlined">analytics</span>
          </div>
          <div>
            <div class="text-sm font-bold text-[#5f6673]">出租率</div>
            <div class="text-3xl font-black">{{ store.occupancyRate }}%</div>
          </div>
        </div>
      </div>
      <div class="flex items-center justify-between rounded-lg bg-[#111827] p-8 text-white">
        <div>
          <h2 class="mb-2 text-3xl font-black">当班表现概览</h2>
          <p class="text-white/70">当前班次已处理 {{ occupiedCount }} 笔入住和 {{ dirtyCount }} 笔退房。</p>
        </div>
        <button class="rounded bg-white px-7 py-3 font-black text-black">查看完整报告</button>
      </div>
    </div>

    <RoomDetailDrawer v-model:visible="drawerVisible" :room="selectedRoom" :logs="roomLogs" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRoomBoardStore } from '../../stores/roomBoardStore'
import { changeRoomStatus, fetchRoomLogs } from '../../api/roomApi'
import type { RoomVO, RoomStatusLogVO } from '../../types/room'
import RoomGrid from '../../components/room-status/RoomGrid.vue'
import RoomDetailDrawer from '../../components/room-status/RoomDetailDrawer.vue'
import { ElMessage } from 'element-plus'

const store = useRoomBoardStore()
const drawerVisible = ref(false)
const selectedRoom = ref<RoomVO | null>(null)
const roomLogs = ref<RoomStatusLogVO[]>([])

let eventSource: EventSource | null = null

const legendItems = [
  { key: 'CLEAN', label: '空净', color: '#22c55e' },
  { key: 'OCCUPIED', label: '在住', color: '#60a5fa' },
  { key: 'DIRTY', label: '脏房', color: '#ef4444' },
  { key: 'RESERVED', label: '预留', color: '#facc15' },
  { key: 'OUT_OF_SERVICE', label: '维修', color: '#9ca3af' },
]

const occupiedCount = computed(() => store.statusSummary.OCCUPIED || 0)
const dirtyCount = computed(() => store.statusSummary.DIRTY || 0)

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
    ElMessage.success(`房间 ${room.roomNumber} 状态已更新`)
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

<style scoped>
.floor-tab {
  min-width: 92px;
  border-radius: 4px;
  padding: 10px 18px;
  font-weight: 900;
  color: #374151;
}

.floor-tab.active {
  background: #fff;
  box-shadow: 0 1px 4px rgba(15, 23, 42, 0.12);
  color: #111827;
}
</style>
