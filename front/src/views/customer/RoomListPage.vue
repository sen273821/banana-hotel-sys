<template>
  <div class="max-w-7xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">房型预订</h1>

    <!-- Date Selection -->
    <div class="bg-white rounded-xl shadow-sm p-4 mb-6 flex gap-4 items-end">
      <div>
        <label class="text-xs text-gray-500 block mb-1">入住日期</label>
        <input
          v-model="checkIn"
          type="date"
          :min="today"
          class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
        />
      </div>
      <div>
        <label class="text-xs text-gray-500 block mb-1">退房日期</label>
        <input
          v-model="checkOut"
          type="date"
          :min="checkIn || today"
          class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
        />
      </div>
      <button
        @click="loadRoomTypes"
        class="bg-amber-500 text-white px-4 py-2 rounded-lg hover:bg-amber-600 text-sm"
      >
        查询可用房型
      </button>
    </div>

    <!-- Room Type Cards -->
    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div
        v-for="room in roomTypes"
        :key="room.id"
        class="bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-md transition-shadow"
      >
        <div class="h-48 flex items-center justify-center bg-amber-50">
          <span class="text-6xl">🛏️</span>
        </div>
        <div class="p-5">
          <div class="flex items-center justify-between mb-2">
            <h3 class="text-lg font-semibold text-gray-900">{{ room.name }}</h3>
            <span class="text-xs text-gray-400">最多{{ room.maxGuests }}人</span>
          </div>
          <p class="text-sm text-gray-500 mb-4">{{ room.description || '舒适客房' }}</p>
          <div class="flex items-center justify-between">
            <p class="text-amber-600 font-bold text-xl">
              ¥{{ room.price }}<span class="text-xs text-gray-400 font-normal">/晚</span>
            </p>
            <button
              @click="goBooking(room.id)"
              class="bg-amber-500 text-white px-5 py-2 rounded-lg hover:bg-amber-600 text-sm font-medium"
            >
              立即预订
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="!loading && roomTypes.length === 0" class="text-center py-12 text-gray-400">
      暂无可用房型，请选择日期后查询
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../../api/http'

const router = useRouter()
const route = useRoute()

const today = new Date().toISOString().split('T')[0]
const checkIn = ref((route.query.checkIn as string) || '')
const checkOut = ref((route.query.checkOut as string) || '')
const roomTypes = ref<any[]>([])
const loading = ref(false)

async function loadRoomTypes() {
  loading.value = true
  try {
    const res = await http.get<any[]>('/api/v1/room-types')
    roomTypes.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goBooking(roomTypeId: number) {
  const query: Record<string, string> = {}
  if (checkIn.value) query.checkIn = checkIn.value
  if (checkOut.value) query.checkOut = checkOut.value
  router.push({ path: `/booking/${roomTypeId}`, query })
}

onMounted(() => {
  loadRoomTypes()
})
</script>
