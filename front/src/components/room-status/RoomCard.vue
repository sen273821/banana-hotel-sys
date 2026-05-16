<template>
  <div
    class="room-card"
    :style="{ borderLeftColor: statusConfig.color }"
    @contextmenu.prevent="showMenu = true"
  >
    <div class="mb-3 flex items-start justify-between">
      <div class="text-3xl font-black">{{ room.roomNumber }}</div>
      <span class="rounded px-2 py-1 text-xs font-black" :style="{ color: statusConfig.color, backgroundColor: statusConfig.bgColor }">
        {{ statusConfig.label }}
      </span>
    </div>
    <div class="mb-5 font-bold text-[#374151]">{{ room.roomTypeName }}</div>
    <div class="flex items-center justify-between">
      <div class="flex h-8 items-center">
        <span v-if="room.assignment" class="flex h-7 w-7 items-center justify-center rounded-full bg-[#111827] text-xs font-black text-white">
          {{ room.assignment.guestName?.slice(0, 2) }}
        </span>
        <span v-else class="material-symbols-outlined text-[#5f6673]">{{ statusConfig.icon }}</span>
      </div>
      <button class="material-symbols-outlined rounded p-1 hover:bg-[#eef2f8]" @click.stop="showMenu = !showMenu">more_vert</button>
    </div>

    <div v-if="showMenu" class="absolute right-3 top-12 z-20 min-w-[170px] rounded border border-[#d5dae4] bg-white py-2 shadow-xl" @mouseleave="showMenu = false">
      <button
        v-for="action in availableActions"
        :key="action.status"
        class="flex w-full items-center gap-3 px-4 py-3 text-left text-sm font-bold hover:bg-[#f3f6fb]"
        @click.stop="handleAction(action.status)"
      >
        <span class="material-symbols-outlined text-[20px]">{{ ROOM_STATUS_CONFIG[action.status].icon }}</span>
        {{ action.label }}
      </button>
      <button class="flex w-full items-center gap-3 px-4 py-3 text-left text-sm font-bold hover:bg-[#f3f6fb]">
        <span class="material-symbols-outlined text-[20px]">edit</span>
        编辑预订
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { RoomVO } from '../../types/room'
import { ROOM_STATUS_CONFIG, ROOM_STATUS_TRANSITIONS } from '../../constants/roomStatus'
import type { RoomStatus } from '../../types/room'

const props = defineProps<{ room: RoomVO }>()

const emit = defineEmits<{
  changeStatus: [newStatus: string, reason?: string]
}>()

const showMenu = ref(false)

const statusConfig = computed(() => ROOM_STATUS_CONFIG[props.room.status] || ROOM_STATUS_CONFIG.CLEAN)

const availableActions = computed(() => {
  const transitions = ROOM_STATUS_TRANSITIONS[props.room.status] || []
  return transitions.map(status => ({
    status,
    label: `标记为${ROOM_STATUS_CONFIG[status].label}`,
  }))
})

function handleAction(newStatus: RoomStatus) {
  showMenu.value = false
  emit('changeStatus', newStatus)
}
</script>

<style scoped>
.room-card {
  position: relative;
  min-height: 142px;
  border: 1px solid #e3e7ef;
  border-left-width: 4px;
  border-radius: 4px;
  background: #fff;
  padding: 18px;
  cursor: pointer;
  transition: box-shadow 160ms ease, transform 160ms ease;
}

.room-card:hover {
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
  transform: translateY(-1px);
}
</style>
