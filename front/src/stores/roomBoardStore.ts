import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { FloorVO, RoomVO, RoomStatus } from '../types/room'
import { fetchRoomBoard } from '../api/roomApi'

export const useRoomBoardStore = defineStore('roomBoard', () => {
  const floors = ref<FloorVO[]>([])
  const rooms = ref<RoomVO[]>([])
  const statusSummary = ref<Record<string, number>>({})
  const occupancyRate = ref(0)
  const selectedFloorId = ref<number | null>(null)
  const loading = ref(false)

  const filteredRooms = computed(() => {
    if (!selectedFloorId.value) return rooms.value
    return rooms.value.filter(r => r.floorId === selectedFloorId.value)
  })

  async function loadBoardData() {
    loading.value = true
    try {
      const res = await fetchRoomBoard(selectedFloorId.value ?? undefined)
      floors.value = res.data.floors
      rooms.value = res.data.rooms
      statusSummary.value = res.data.statusSummary
      occupancyRate.value = res.data.occupancyRate
    } finally {
      loading.value = false
    }
  }

  function setFloorFilter(floorId: number | null) {
    selectedFloorId.value = floorId
  }

  function updateRoomStatus(roomId: number, newStatus: string) {
    const room = rooms.value.find(r => r.id === roomId)
    if (room) {
      room.status = newStatus as RoomStatus
    }
  }

  return {
    floors,
    rooms,
    statusSummary,
    occupancyRate,
    selectedFloorId,
    loading,
    filteredRooms,
    loadBoardData,
    setFloorFilter,
    updateRoomStatus,
  }
})
