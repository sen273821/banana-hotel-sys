<template>
  <div class="max-w-lg mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">订单支付</h1>

    <div v-if="loading" class="text-center py-12 text-gray-400">加载中...</div>

    <template v-else-if="order">
      <!-- Order Summary -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 class="text-sm text-gray-500 mb-3">订单信息</h2>
        <div class="space-y-2 text-sm">
          <div class="flex justify-between">
            <span class="text-gray-500">订单号</span>
            <span class="text-gray-900 font-mono">{{ order.orderNo }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">入住人</span>
            <span class="text-gray-900">{{ order.guestName }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">入住日期</span>
            <span class="text-gray-900">{{ order.checkInDate }} ~ {{ order.checkOutDate }}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-500">住宿天数</span>
            <span class="text-gray-900">{{ order.nights }}晚</span>
          </div>
          <div class="flex justify-between border-t pt-2 mt-2">
            <span class="text-gray-900 font-medium">应付金额</span>
            <span class="text-amber-600 font-bold text-xl">¥{{ order.totalPrice }}</span>
          </div>
        </div>
      </div>

      <!-- Payment Methods -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 class="text-sm text-gray-500 mb-3">选择支付方式</h2>
        <div class="space-y-3">
          <label
            class="flex items-center gap-3 p-3 border rounded-lg cursor-pointer transition-colors"
            :class="payMethod === 'wechat' ? 'border-green-500 bg-green-50' : 'border-gray-200'"
          >
            <input v-model="payMethod" type="radio" value="wechat" class="hidden" />
            <span class="text-xl">💚</span>
            <span class="text-sm font-medium">微信支付</span>
          </label>
          <label
            class="flex items-center gap-3 p-3 border rounded-lg cursor-pointer transition-colors"
            :class="payMethod === 'alipay' ? 'border-blue-500 bg-blue-50' : 'border-gray-200'"
          >
            <input v-model="payMethod" type="radio" value="alipay" class="hidden" />
            <span class="text-xl">💙</span>
            <span class="text-sm font-medium">支付宝</span>
          </label>
        </div>
      </div>

      <p v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</p>
      <button
        @click="handlePay"
        :disabled="paying"
        class="w-full bg-amber-500 text-white py-3 rounded-lg hover:bg-amber-600 font-medium disabled:opacity-50"
      >
        {{ paying ? '支付中...' : `确认支付 ¥${order.totalPrice}` }}
      </button>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../../api/http'

const router = useRouter()
const route = useRoute()

const order = ref<any>(null)
const loading = ref(true)
const paying = ref(false)
const error = ref('')
const payMethod = ref('wechat')

async function loadOrder() {
  try {
    const res = await http.get<any>(`/api/v1/orders/${route.params.orderNo}`)
    order.value = res.data
  } catch (e: any) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}

async function handlePay() {
  paying.value = true
  error.value = ''
  try {
    await http.put(`/api/v1/orders/${route.params.orderNo}/pay`, {
      payMethod: payMethod.value,
    })
    router.push(`/booking-success/${route.params.orderNo}`)
  } catch (e: any) {
    error.value = e.message || '支付失败'
  } finally {
    paying.value = false
  }
}

onMounted(() => {
  loadOrder()
})
</script>
