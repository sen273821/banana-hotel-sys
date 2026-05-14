<template>
  <div class="max-w-3xl mx-auto px-4 py-8">
    <h1 class="text-2xl font-bold text-gray-900 mb-6">填写预订信息</h1>

    <div v-if="roomType" class="bg-white rounded-xl shadow-sm p-6 mb-6">
      <h2 class="text-lg font-semibold text-gray-900 mb-1">{{ roomType.name }}</h2>
      <p class="text-sm text-gray-500">{{ roomType.description }}</p>
      <p class="text-amber-600 font-bold text-xl mt-2">¥{{ roomType.price }}<span class="text-xs text-gray-400 font-normal">/晚</span></p>
    </div>

    <form @submit.prevent="handleSubmit" class="bg-white rounded-xl shadow-sm p-6">
      <div class="grid grid-cols-2 gap-4 mb-4">
        <div>
          <label class="block text-sm text-gray-600 mb-1">入住日期</label>
          <input
            v-model="form.checkInDate"
            type="date"
            :min="today"
            class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
            required
          />
        </div>
        <div>
          <label class="block text-sm text-gray-600 mb-1">退房日期</label>
          <input
            v-model="form.checkOutDate"
            type="date"
            :min="form.checkInDate || today"
            class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
            required
          />
        </div>
      </div>
      <div class="mb-4">
        <label class="block text-sm text-gray-600 mb-1">入住人姓名</label>
        <input
          v-model="form.guestName"
          type="text"
          placeholder="请输入入住人真实姓名"
          class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
          required
        />
      </div>
      <div class="mb-6">
        <label class="block text-sm text-gray-600 mb-1">手机号</label>
        <input
          v-model="form.guestPhone"
          type="tel"
          placeholder="请输入联系手机号"
          class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
          required
        />
      </div>

      <!-- Price Summary -->
      <div v-if="nights > 0 && roomType" class="bg-gray-50 rounded-lg p-4 mb-6">
        <div class="flex justify-between text-sm text-gray-600 mb-2">
          <span>¥{{ roomType.price }} x {{ nights }}晚</span>
          <span>¥{{ totalPrice }}</span>
        </div>
        <div class="flex justify-between font-bold text-gray-900 border-t pt-2">
          <span>合计</span>
          <span class="text-amber-600 text-lg">¥{{ totalPrice }}</span>
        </div>
      </div>

      <p v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</p>
      <button
        type="submit"
        :disabled="loading || nights <= 0"
        class="w-full bg-amber-500 text-white py-3 rounded-lg hover:bg-amber-600 font-medium disabled:opacity-50"
      >
        {{ loading ? '提交中...' : '提交预订' }}
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../../api/http'

const router = useRouter()
const route = useRoute()

const today = new Date().toISOString().split('T')[0]
const roomType = ref<any>(null)
const loading = ref(false)
const error = ref('')

const form = reactive({
  checkInDate: (route.query.checkIn as string) || '',
  checkOutDate: (route.query.checkOut as string) || '',
  guestName: '',
  guestPhone: '',
})

const nights = computed(() => {
  if (!form.checkInDate || !form.checkOutDate) return 0
  const diff = new Date(form.checkOutDate).getTime() - new Date(form.checkInDate).getTime()
  return Math.max(0, Math.ceil(diff / (1000 * 60 * 60 * 24)))
})

const totalPrice = computed(() => {
  if (!roomType.value) return 0
  return roomType.value.price * nights.value
})

async function loadRoomType() {
  try {
    const res = await http.get<any[]>('/api/v1/room-types')
    const id = Number(route.params.roomTypeId)
    roomType.value = res.data.find((r: any) => r.id === id)
  } catch (e) {
    console.error(e)
  }
}

async function handleSubmit() {
  loading.value = true
  error.value = ''
  try {
    const res = await http.post<{ orderNo: string }>('/api/v1/orders', {
      roomTypeId: Number(route.params.roomTypeId),
      guestName: form.guestName,
      guestPhone: form.guestPhone,
      checkInDate: form.checkInDate,
      checkOutDate: form.checkOutDate,
    })
    router.push(`/payment/${res.data.orderNo}`)
  } catch (e: any) {
    error.value = e.message || '预订失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadRoomType()
})
</script>
