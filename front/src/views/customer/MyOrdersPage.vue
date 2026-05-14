<template>
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">我的订单</h1>

    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>

    <div v-else-if="orders.length === 0" class="text-center py-12">
      <p class="text-gray-400 mb-4">暂无订单</p>
      <router-link to="/rooms" class="text-amber-600 hover:underline text-sm">去预订房间</router-link>
    </div>

    <div v-else class="space-y-4">
      <div
        v-for="order in orders"
        :key="order.id"
        class="bg-white rounded-xl shadow-sm p-5"
      >
        <div class="flex items-center justify-between mb-3">
          <span class="text-xs text-gray-400 font-mono">{{ order.orderNo }}</span>
          <span
            class="text-xs px-2 py-1 rounded-full"
            :class="statusClass(order.status)"
          >
            {{ statusLabel(order.status) }}
          </span>
        </div>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-3 text-sm">
          <div>
            <p class="text-gray-400 text-xs">入住人</p>
            <p class="text-gray-900">{{ order.guestName }}</p>
          </div>
          <div>
            <p class="text-gray-400 text-xs">入住日期</p>
            <p class="text-gray-900">{{ order.checkInDate }}</p>
          </div>
          <div>
            <p class="text-gray-400 text-xs">退房日期</p>
            <p class="text-gray-900">{{ order.checkOutDate }}</p>
          </div>
          <div>
            <p class="text-gray-400 text-xs">金额</p>
            <p class="text-amber-600 font-bold">¥{{ order.totalPrice }}</p>
          </div>
        </div>
        <div class="flex gap-2 mt-4" v-if="order.status === 0">
          <button
            @click="goPayment(order.orderNo)"
            class="text-xs bg-amber-500 text-white px-4 py-1.5 rounded-lg hover:bg-amber-600"
          >
            去支付
          </button>
          <button
            @click="cancelOrder(order.orderNo)"
            class="text-xs border border-gray-300 text-gray-600 px-4 py-1.5 rounded-lg hover:bg-gray-50"
          >
            取消订单
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../api/http'

const router = useRouter()
const orders = ref<any[]>([])
const loading = ref(true)

const STATUS_MAP: Record<number, { label: string; class: string }> = {
  0: { label: '待支付', class: 'bg-yellow-100 text-yellow-700' },
  1: { label: '已支付', class: 'bg-blue-100 text-blue-700' },
  2: { label: '已入住', class: 'bg-green-100 text-green-700' },
  3: { label: '已退房', class: 'bg-gray-100 text-gray-700' },
  4: { label: '已取消', class: 'bg-red-100 text-red-700' },
}

function statusLabel(status: number) {
  return STATUS_MAP[status]?.label || '未知'
}

function statusClass(status: number) {
  return STATUS_MAP[status]?.class || 'bg-gray-100 text-gray-700'
}

function goPayment(orderNo: string) {
  router.push(`/payment/${orderNo}`)
}

async function cancelOrder(orderNo: string) {
  try {
    await http.put(`/api/v1/orders/${orderNo}/cancel`)
    await loadOrders()
  } catch (e) {
    console.error(e)
  }
}

async function loadOrders() {
  try {
    const res = await http.get<any[]>('/api/v1/orders/my')
    orders.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOrders()
})
</script>
