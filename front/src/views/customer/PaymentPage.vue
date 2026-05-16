<template>
  <div class="mx-auto grid max-w-[1320px] gap-7 px-8 py-8 lg:grid-cols-[1fr_420px]">
    <div class="space-y-6">
      <section class="panel">
        <h1 class="section-title">入住人信息</h1>
        <div v-if="order" class="grid gap-5 md:grid-cols-2">
          <label class="form-field">
            <span>姓名 *</span>
            <input :value="order.guestName" readonly />
          </label>
          <label class="form-field">
            <span>手机号码 *</span>
            <input :value="order.guestPhone" readonly />
          </label>
        </div>
        <div class="mt-6">
          <div class="mb-3 text-sm font-bold text-[#374151]">特殊需求（选填）</div>
          <div class="mb-4 flex flex-wrap gap-3">
            <span class="request-chip">高楼层</span>
            <span class="request-chip">无烟房</span>
            <span class="request-chip">提早入住</span>
          </div>
          <textarea class="h-24 w-full resize-none rounded border border-[#cfd5df] bg-white px-4 py-3 outline-none" placeholder="给礼宾部的额外说明..." />
        </div>
      </section>

      <section class="panel">
        <h2 class="section-title">支付方式</h2>
        <div class="grid gap-4 md:grid-cols-2">
          <label
            class="pay-card"
            :class="payMethod === 'wechat' ? 'border-black bg-[#eef3ff]' : ''"
          >
            <input v-model="payMethod" type="radio" value="wechat" />
            <span class="material-symbols-outlined rounded bg-green-600 p-2 text-white">account_balance_wallet</span>
            <span>
              <strong>WeChat Pay</strong>
              <small>扫码支付，快捷安全</small>
            </span>
            <span class="ml-auto h-4 w-4 rounded-full" :class="payMethod === 'wechat' ? 'border-4 border-black' : 'border border-[#9ca3af]'"></span>
          </label>
          <label
            class="pay-card"
            :class="payMethod === 'alipay' ? 'border-black bg-[#eef3ff]' : ''"
          >
            <input v-model="payMethod" type="radio" value="alipay" />
            <span class="material-symbols-outlined rounded bg-blue-600 p-2 text-white">credit_card</span>
            <span>
              <strong>Alipay</strong>
              <small>全球数字钱包</small>
            </span>
            <span class="ml-auto h-4 w-4 rounded-full" :class="payMethod === 'alipay' ? 'border-4 border-black' : 'border border-[#9ca3af]'"></span>
          </label>
        </div>
        <div class="mt-5 flex items-center gap-3 rounded bg-[#fff8e8] px-4 py-4 text-sm font-bold text-[#7a5a10]">
          <span class="material-symbols-outlined">verified_user</span>
          您的支付通过安全加密渠道处理。我们绝不会存储您的完整支付凭据。
        </div>
      </section>

      <p v-if="error" class="text-sm font-bold text-red-600">{{ error }}</p>
    </div>

    <aside v-if="order" class="h-fit overflow-hidden rounded-lg border border-[#cfd5df] bg-white">
      <div class="relative h-48">
        <img class="h-full w-full object-cover" :src="summaryImage" alt="room" />
        <span class="absolute bottom-4 left-4 rounded bg-white px-3 py-1 text-sm font-black">大视野套房</span>
      </div>
      <div class="p-6">
        <div class="mb-5 flex items-center justify-between border-b border-[#d7dce5] pb-4">
          <h2 class="font-bold">预订摘要</h2>
          <span class="material-symbols-outlined text-[#8c6b16]">event_available</span>
        </div>
        <div class="space-y-4 text-sm">
          <div class="summary-row"><span>入住</span><strong>{{ order.checkInDate }}</strong></div>
          <div class="summary-row"><span>离店</span><strong>{{ order.checkOutDate }}</strong></div>
          <div class="summary-row"><span>入住时长</span><strong>{{ order.nights }} 晚</strong></div>
          <div class="summary-row"><span>入住人数</span><strong>2 成人</strong></div>
        </div>
        <div class="my-5 border-t border-[#d7dce5]"></div>
        <div class="space-y-3 text-sm">
          <div class="summary-row"><span>入住</span><strong>¥{{ order.totalPrice }}</strong></div>
          <div class="summary-row"><span>税费与服务费 (10%)</span><strong>¥{{ serviceFee }}</strong></div>
        </div>
        <div class="mt-5 flex items-end justify-between border-t border-[#d7dce5] pt-4">
          <span class="text-xl font-black">总部额</span>
          <strong class="text-3xl font-black">¥{{ grandTotal }}</strong>
        </div>
        <button
          @click="handlePay"
          :disabled="paying"
          class="mt-5 w-full rounded bg-black py-4 text-sm font-black text-white disabled:opacity-50"
        >
          {{ paying ? '支付中...' : '确认并支付' }}
          <span class="material-symbols-outlined align-middle text-[18px]">lock</span>
        </button>
        <p class="mt-4 text-center text-sm font-bold italic text-[#6b7280]">入住前 48 小时可免费取消。</p>
      </div>
    </aside>

    <div v-if="loading" class="col-span-full py-20 text-center text-[#6b7280]">加载中...</div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../../api/http'

const router = useRouter()
const route = useRoute()

const order = ref<any>(null)
const loading = ref(true)
const paying = ref(false)
const error = ref('')
const payMethod = ref('wechat')
const summaryImage = 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=900&q=85'

const serviceFee = computed(() => (order.value ? Math.round(Number(order.value.totalPrice) * 0.1) : 0))
const grandTotal = computed(() => (order.value ? Number(order.value.totalPrice) + serviceFee.value : 0))

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

onMounted(loadOrder)
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
}

.request-chip {
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
