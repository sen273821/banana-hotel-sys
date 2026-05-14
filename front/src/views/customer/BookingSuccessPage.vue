<template>
  <div class="max-w-lg mx-auto px-4 py-16 text-center">
    <div class="bg-white rounded-xl shadow-sm p-8">
      <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
        <span class="text-green-600 text-3xl">✓</span>
      </div>
      <h1 class="text-2xl font-bold text-gray-900 mb-2">预订成功</h1>
      <p class="text-gray-500 mb-6">您的订单已支付成功，请按时办理入住</p>

      <div v-if="order" class="bg-gray-50 rounded-lg p-4 text-left mb-6">
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-500">订单号</span>
            <span class="text-gray-900 font-mono">{{ order.orderNo }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">入住日期</span>
            <span class="text-gray-900">{{ order.checkInDate }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">退房日期</span>
            <span class="text-gray-900">{{ order.checkOutDate }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">入住人</span>
            <span class="text-gray-900">{{ order.guestName }}</span>
          </div>
        </div>
      </div>

      <div class="flex gap-3">
        <router-link
          to="/my-orders"
          class="flex-1 border border-amber-500 text-amber-600 py-2 rounded-lg hover:bg-amber-50 text-sm font-medium"
        >
          查看订单
        </router-link>
        <router-link
          to="/"
          class="flex-1 bg-amber-500 text-white py-2 rounded-lg hover:bg-amber-600 text-sm font-medium"
        >
          返回首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '../../api/http'

const route = useRoute()
const order = ref<any>(null)

async function loadOrder() {
  try {
    const res = await http.get<any>(`/api/v1/orders/${route.params.orderNo}`)
    order.value = res.data
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  loadOrder()
})
</script>
