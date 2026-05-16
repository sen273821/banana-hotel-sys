<template>
  <div class="mx-auto grid max-w-[1320px] gap-7 px-8 py-8 lg:grid-cols-[1fr_420px]">
    <div class="space-y-6">
      <section class="panel">
        <h1 class="section-title">入住人信息</h1>
        <form id="booking-form" @submit.prevent="handleSubmit">
          <div class="grid gap-5 md:grid-cols-2">
            <label class="form-field">
              <span>姓名 *</span>
              <input v-model="form.guestName" required placeholder="请输入入住人姓名" />
            </label>
            <label class="form-field">
              <span>手机号码 *</span>
              <input v-model="form.guestPhone" required type="tel" placeholder="+1 (555) 000-0000" />
            </label>
            <label class="form-field">
              <span>入住日期 *</span>
              <input v-model="form.checkInDate" required type="date" :min="today" />
            </label>
            <label class="form-field">
              <span>退房日期 *</span>
              <input v-model="form.checkOutDate" required type="date" :min="form.checkInDate || today" />
            </label>
          </div>
          <div class="mt-6">
            <div class="mb-3 text-sm font-bold text-[#374151]">特殊需求（选填）</div>
            <div class="mb-4 flex flex-wrap gap-3">
              <label v-for="item in requests" :key="item" class="request-chip">
                <input type="checkbox" />
                <span>{{ item }}</span>
              </label>
            </div>
            <textarea class="h-24 w-full resize-none rounded border border-[#cfd5df] bg-white px-4 py-3 outline-none focus:border-black" placeholder="给礼宾部的额外说明..." />
          </div>
        </form>
      </section>

      <section class="panel">
        <h2 class="section-title">支付方式</h2>
        <div class="grid gap-4 md:grid-cols-2">
          <label class="pay-card border-black bg-[#eef3ff]">
            <input checked type="radio" />
            <span class="material-symbols-outlined rounded bg-green-600 p-2 text-white">account_balance_wallet</span>
            <span>
              <strong>WeChat Pay</strong>
              <small>扫码支付，快捷安全</small>
            </span>
            <span class="ml-auto h-4 w-4 rounded-full border-4 border-black"></span>
          </label>
          <label class="pay-card">
            <input type="radio" />
            <span class="material-symbols-outlined rounded bg-blue-600 p-2 text-white">credit_card</span>
            <span>
              <strong>Alipay</strong>
              <small>全球数字钱包</small>
            </span>
            <span class="ml-auto h-4 w-4 rounded-full border border-[#9ca3af]"></span>
          </label>
        </div>
        <div class="mt-5 flex items-center gap-3 rounded bg-[#fff8e8] px-4 py-4 text-sm font-bold text-[#7a5a10]">
          <span class="material-symbols-outlined">verified_user</span>
          您的支付通过安全加密渠道处理。我们绝不会存储您的完整支付凭据。
        </div>
      </section>

      <p v-if="error" class="text-sm font-bold text-red-600">{{ error }}</p>
    </div>

    <aside v-if="roomType" class="h-fit overflow-hidden rounded-lg border border-[#cfd5df] bg-white">
      <div class="relative h-48">
        <img class="h-full w-full object-cover" :src="summaryImage" alt="room" />
        <span class="absolute bottom-4 left-4 rounded bg-white px-3 py-1 text-sm font-black">{{ roomType.name }}</span>
      </div>
      <div class="p-6">
        <div class="mb-5 flex items-center justify-between border-b border-[#d7dce5] pb-4">
          <h2 class="font-bold">预订摘要</h2>
          <span class="material-symbols-outlined text-[#8c6b16]">event_available</span>
        </div>
        <div class="space-y-4 text-sm">
          <div class="summary-row"><span>入住</span><strong>{{ form.checkInDate || '-' }}</strong></div>
          <div class="summary-row"><span>离店</span><strong>{{ form.checkOutDate || '-' }}</strong></div>
          <div class="summary-row"><span>入住时长</span><strong>{{ nights }} 晚</strong></div>
          <div class="summary-row"><span>入住人数</span><strong>2 成人</strong></div>
        </div>
        <div class="my-5 border-t border-[#d7dce5]"></div>
        <div class="space-y-3 text-sm">
          <div class="summary-row"><span>入住</span><strong>¥{{ subtotal }}</strong></div>
          <div class="summary-row"><span>税费与服务费 (10%)</span><strong>¥{{ serviceFee }}</strong></div>
        </div>
        <div class="mt-5 flex items-end justify-between border-t border-[#d7dce5] pt-4">
          <span class="text-xl font-black">总部额</span>
          <strong class="text-3xl font-black">¥{{ totalPrice }}</strong>
        </div>
        <button
          form="booking-form"
          type="submit"
          :disabled="loading || nights <= 0"
          class="mt-5 w-full rounded bg-black py-4 text-sm font-black text-white disabled:opacity-50"
        >
          {{ loading ? '提交中...' : '确认并支付' }}
          <span class="material-symbols-outlined align-middle text-[18px]">lock</span>
        </button>
        <p class="mt-4 text-center text-sm font-bold italic text-[#6b7280]">入住前 48 小时可免费取消。</p>
      </div>
    </aside>
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
const summaryImage = 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=900&q=85'
const requests = ['高楼层', '无烟房', '提早入住']

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

const subtotal = computed(() => (roomType.value ? roomType.value.price * nights.value : 0))
const serviceFee = computed(() => Math.round(subtotal.value * 0.1))
const totalPrice = computed(() => subtotal.value + serviceFee.value)

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

onMounted(loadRoomType)
</script>

<style scoped>
.panel {
  border: 1px solid #cfd5df;
  border-radius: 8px;
  background: #fff;
  padding: 24px;
}

.section-title {
  margin-bottom: 22px;
  border-left: 4px solid #8c6b16;
  padding-left: 12px;
  font-size: 24px;
  font-weight: 900;
}

.form-field span {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 700;
}

.form-field input {
  height: 46px;
  width: 100%;
  border: 1px solid #cfd5df;
  border-radius: 4px;
  padding: 0 14px;
  outline: none;
}

.request-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  background: #eaf1ff;
  padding: 9px 14px;
  font-weight: 700;
}

.pay-card {
  display: flex;
  min-height: 82px;
  align-items: center;
  gap: 14px;
  border: 1px solid #cfd5df;
  border-radius: 8px;
  padding: 16px;
}

.pay-card input {
  display: none;
}

.pay-card strong,
.pay-card small {
  display: block;
}

.pay-card small {
  margin-top: 4px;
  color: #374151;
  font-weight: 700;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}
</style>
