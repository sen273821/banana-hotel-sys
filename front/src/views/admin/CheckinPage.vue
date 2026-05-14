<template>
  <div>
    <h2 class="text-lg font-bold text-gray-900 mb-4">待入住订单</h2>
    <p class="text-sm text-gray-500 mb-6">以下订单已支付，等待办理入住</p>

    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>

    <div v-else-if="orders.length === 0" class="text-center py-12 text-gray-400">
      暂无待入住订单
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="order in orders"
        :key="order.id"
        class="bg-white rounded-xl shadow-sm p-5 flex items-center justify-between"
      >
        <div class="flex-1">
          <div class="flex items-center gap-3 mb-2">
            <span class="font-mono text-xs text-gray-400">{{ order.orderNo }}</span>
            <span class="text-xs bg-blue-100 text-blue-700 px-2 py-0.5 rounded-full">已支付</span>
          </div>
          <div class="grid grid-cols-4 gap-4 text-sm">
            <div>
              <p class="text-gray-400 text-xs">入住人</p>
              <p class="text-gray-900">{{ order.guestName }}</p>
            </div>
            <div>
              <p class="text-gray-400 text-xs">手机号</p>
              <p class="text-gray-900">{{ order.guestPhone }}</p>
            </div>
            <div>
              <p class="text-gray-400 text-xs">入住日期</p>
              <p class="text-gray-900">{{ order.checkInDate }}</p>
            </div>
            <div>
              <p class="text-gray-400 text-xs">退房日期</p>
              <p class="text-gray-900">{{ order.checkOutDate }}</p>
            </div>
          </div>
        </div>
        <div class="ml-4">
          <button
            @click="handleCheckin(order)"
            class="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600 text-sm font-medium"
          >
            办理入住
          </button>
        </div>
      </div>
    </div>

    <!-- Checkin Dialog -->
    <div v-if="checkinOrder" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-96">
        <h3 class="text-lg font-bold mb-4">办理入住</h3>
        <div class="bg-gray-50 rounded-lg p-3 mb-4 text-sm space-y-1">
          <p><span class="text-gray-500">订单号：</span>{{ checkinOrder.orderNo }}</p>
          <p><span class="text-gray-500">入住人：</span>{{ checkinOrder.guestName }}</p>
          <p><span class="text-gray-500">入住日期：</span>{{ checkinOrder.checkInDate }} ~ {{ checkinOrder.checkOutDate }}</p>
        </div>
        <div class="mb-4">
          <label class="block text-sm text-gray-600 mb-1">分配房间</label>
          <select
            v-model="checkinRoomId"
            class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
          >
            <option value="">请选择房间</option>
            <option v-for="room in availableRooms" :key="room.id" :value="room.id">
              {{ room.roomNumber }} - {{ room.roomTypeName }}
            </option>
          </select>
        </div>
        <p v-if="error" class="text-red-500 text-sm mb-3">{{ error }}</p>
        <div class="flex gap-3">
          <button
            @click="checkinOrder = null; error = ''"
            class="flex-1 border border-gray-300 py-2 rounded-lg text-sm"
          >
            取消
          </button>
          <button
            @click="confirmCheckin"
            :disabled="!checkinRoomId"
            class="flex-1 bg-green-500 text-white py-2 rounded-lg text-sm hover:bg-green-600 disabled:opacity-50"
          >
            确认入住
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import http from '../../api/http'

const orders = ref<any[]>([])
const loading = ref(true)
const checkinOrder = ref<any>(null)
const checkinRoomId = ref('')
const availableRooms = ref<any[]>([])
const error = ref('')

async function loadOrders() {
  try {
    const res = await http.get<any>('/api/v1/admin/orders', { params: { status: 1 } })
    orders.value = res.data.records || res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function loadAvailableRooms() {
  try {
    const res = await http.get<any>('/api/v1/rooms/board')
    const allRooms = res.data.rooms || []
    availableRooms.value = allRooms.filter((r: any) => r.status === 'CLEAN')
  } catch (e) {
    console.error(e)
  }
}

function handleCheckin(order: any) {
  checkinOrder.value = order
  checkinRoomId.value = ''
  error.value = ''
  loadAvailableRooms()
}

async function confirmCheckin() {
  error.value = ''
  try {
    await http.put(`/api/v1/admin/orders/${checkinOrder.value.orderNo}/checkin`, {
      roomId: Number(checkinRoomId.value),
    })
    checkinOrder.value = null
    await loadOrders()
  } catch (e: any) {
    error.value = e.message || '入住办理失败'
  }
}

onMounted(() => {
  loadOrders()
})
</script>
