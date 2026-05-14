<template>
  <div
    class="bg-surface rounded-xl border border-outline-variant p-4 cursor-pointer hover:shadow-md transition-shadow relative"
    :style="{ borderLeftColor: statusConfig.color, borderLeftWidth: '4px' }"
    @contextmenu.prevent="showMenu = true"
  >
    <!-- Header -->
    <div class="flex items-center justify-between mb-3">
      <span class="text-lg font-bold text-on-surface">{{ room.roomNumber }}</span>
      <span
        class="px-2 py-0.5 rounded text-xs font-medium"
        :style="{ color: statusConfig.color, backgroundColor: statusConfig.bgColor }"
      >
        {{ statusConfig.label }}
      </span>
    </div>

    <!-- Room Type -->
    <div class="flex items-center gap-1.5 text-sm text-on-surface-variant mb-2">
      <span class="material-symbols-outlined text-base">{{ room.roomTypeIcon }}</span>
      <span>{{ room.roomTypeName }}</span>
    </div>

    <!-- Guest Info (if occupied/booked) -->
    <div v-if="room.assignment" class="mt-3 pt-3 border-t border-outline-variant">
      <div class="flex items-center gap-2">
        <div class="w-6 h-6 rounded-full bg-secondary-container flex items-center justify-center">
          <span class="text-xs font-bold text-on-secondary-container">
            {{ room.assignment.guestName?.charAt(0) }}
          </span>
        </div>
        <span class="text-sm text-on-surface">{{ room.assignment.guestName }}</span>
      </div>
    </div>

    <!-- Three-dot menu trigger -->
    <button
      class="absolute top-3 right-3 material-symbols-outlined text-on-surface-variant text-base hover:text-on-surface"
      @click.stop="showMenu = !showMenu"
    >
      more_vert
    </button>

    <!-- Context Menu -->
    <div
      v-if="showMenu"
      class="absolute top-10 right-3 z-10 bg-surface rounded-lg shadow-lg border border-outline-variant py-1 min-w-[140px]"
      @mouseleave="showMenu = false"
    >
      <button
        v-for="action in availableActions"
        :key="action.status"
        class="w-full text-left px-4 py-2 text-sm hover:bg-surface-container-low transition-colors"
        @click.stop="handleAction(action.status)"
      >
        {{ action.label }}
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { RoomVO } from '../../types/room'
import { ROOM_STATUS_CONFIG, ROOM_STATUS_TRANSITIONS } from '../../constants/roomStatus'
import type { RoomStatus } from '../../types/room'

const props = defineProps<{
  room: RoomVO
}>()

const emit = defineEmits<{
  changeStatus: [newStatus: string, reason?: string]
}>()

const showMenu = ref(false)

const statusConfig = computed(() => {
  return ROOM_STATUS_CONFIG[props.room.status] || ROOM_STATUS_CONFIG.CLEAN
})

const availableActions = computed(() => {
  const transitions = ROOM_STATUS_TRANSITIONS[props.room.status] || []
  return transitions.map(status => ({
    status,
    label: `设为${ROOM_STATUS_CONFIG[status].label}`,
  }))
})

function handleAction(newStatus: RoomStatus) {
  showMenu.value = false
  emit('changeStatus', newStatus)
}
</script>
