<template>
  <div class="mx-auto max-w-[1320px] px-8 py-10">
    <div class="mb-8 flex items-start justify-between gap-5">
      <div>
        <div class="mb-3 text-sm font-black">我的订单 › Order #{{ order?.orderNo || route.params.orderNo }}</div>
        <h1 class="text-4xl font-black">订单详情与入住办理</h1>
      </div>
      <div class="rounded-full bg-[#f4d28a] px-5 py-3 text-sm font-black text-[#5d4201]">
        <span class="material-symbols-outlined filled align-middle text-[18px]">check_circle</span>
        已确认并支付
      </div>
    </div>

    <div class="grid gap-7 lg:grid-cols-[2fr_1fr]">
      <section class="overflow-hidden rounded-lg border border-[#cfd5df] bg-white md:grid md:grid-cols-[300px_1fr]">
        <img class="h-full min-h-[280px] w-full object-cover" :src="roomImage" alt="suite" />
        <div class="p-7">
          <div class="mb-5 flex justify-between gap-4">
            <div>
              <h2 class="mb-3 text-3xl font-black">行政天际套房</h2>
              <p class="max-w-xl text-base leading-7">体验无与伦比的奢华，180度俯瞰城市天际线，配备私人水疗浴缸和设计师品牌设施。</p>
            </div>
            <strong class="text-3xl font-black text-[#8c6b16]">¥{{ order?.totalPrice || '1,240.00' }}</strong>
          </div>
          <div class="grid border-y border-[#d7dce5] py-5 md:grid-cols-2">
            <div>
              <div class="font-black">入住日期</div>
              <div class="text-3xl font-black">{{ order?.checkInDate || 'Oct 24, 2024' }}</div>
              <div class="font-bold">15:00 之后</div>
            </div>
            <div>
              <div class="font-black">离店日期</div>
              <div class="text-3xl font-black">{{ order?.checkOutDate || 'Oct 27, 2024' }}</div>
              <div class="font-bold">11:00 之前</div>
            </div>
          </div>
          <div class="mt-6 flex flex-wrap gap-8 text-[#8c6b16]">
            <span class="amenity"><span class="material-symbols-outlined">wifi</span>高速无线网络</span>
            <span class="amenity"><span class="material-symbols-outlined">local_parking</span>含代客泊车</span>
          </div>
        </div>
      </section>

      <section class="rounded-lg bg-[#111827] p-7 text-white">
        <div class="mb-8 flex justify-between text-white/50">
          <span>实时状态</span>
          <span class="h-2 w-2 rounded-full bg-[#f4d28a]"></span>
        </div>
        <h2 class="mb-4 text-3xl font-black">您已到达</h2>
        <p class="mb-8 leading-7 text-white/55">系统检测到您已到达 Grand Plaza 附近。您现在可以完成入住并解锁您的数字房卡。</p>
        <button class="flex w-full items-center justify-center gap-3 rounded bg-[#8c6b16] py-4 font-black">
          立即办理入住
          <span class="material-symbols-outlined">arrow_forward</span>
        </button>
        <p class="mt-5 text-center text-sm font-bold italic text-white/35">前台预计等待时间：12分钟</p>
      </section>

      <section class="rounded-lg border border-[#cfd5df] bg-white p-7">
        <h2 class="mb-7 text-3xl font-black">身份验证</h2>
        <div class="grid gap-6 md:grid-cols-2">
          <div class="verify-box border-dashed">
            <span class="material-symbols-outlined text-5xl">badge</span>
            <strong>扫描身份证 / 护照</strong>
            <small>自动 OCR 安全处理</small>
          </div>
          <div class="verify-box">
            <span class="material-symbols-outlined text-5xl text-[#c5cbd6]">draw</span>
            <strong>签名预览</strong>
            <small>完成入住流程以进行签名</small>
          </div>
        </div>
        <div class="mt-7 flex items-start gap-4 rounded border border-[#cfd5df] p-5 text-sm font-bold">
          <span class="material-symbols-outlined text-[#8c6b16]">verified_user</span>
          您的身份信息已加密并直接传输至我们的安全 PMS 系统。数据将在退房 24 小时后自动清除。
        </div>
      </section>

      <section class="rounded-lg border border-[#cfd5df] bg-white p-7 text-center">
        <h2 class="mb-7 text-3xl font-black">数字房卡</h2>
        <div class="mx-auto mb-7 flex h-60 w-60 items-center justify-center rounded-2xl bg-[#d9e6fb]">
          <div class="flex h-44 w-44 items-center justify-center rounded-lg border border-[#b6c1d4] bg-white">
            <div class="flex h-32 w-32 items-center justify-center rounded bg-gray-200 text-sm font-black text-gray-700">已锁定</div>
          </div>
        </div>
        <div class="text-lg font-black">1402 房 (天际塔)</div>
        <p class="mb-6 mt-2 font-bold">房卡将在身份验证后激活。</p>
        <button class="w-full rounded border border-[#cfd5df] py-3 font-bold">
          <span class="material-symbols-outlined align-middle text-[18px]">wallet</span>
          添加到 Apple/Google 钱包
        </button>
      </section>
    </div>

    <section class="mt-12">
      <h2 class="mb-10 text-3xl font-black">入住时间轴</h2>
      <div class="mx-auto grid max-w-5xl grid-cols-4 items-start">
        <div v-for="step in steps" :key="step.label" class="relative text-center">
          <div class="absolute left-1/2 top-6 h-px w-full bg-[#cfd5df]" :class="step.done ? 'bg-[#8c6b16]' : ''"></div>
          <div class="relative z-10 mx-auto flex h-12 w-12 items-center justify-center rounded-lg border bg-white" :class="step.done ? 'border-[#8c6b16] bg-[#8c6b16] text-white' : 'border-[#cfd5df]'">
            <span class="material-symbols-outlined">{{ step.icon }}</span>
          </div>
          <div class="mt-3 font-black">{{ step.label }}</div>
          <div class="text-sm text-[#6b7280]">{{ step.detail }}</div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import http from '../../api/http'

const route = useRoute()
const order = ref<any>(null)
const roomImage = 'https://images.unsplash.com/photo-1591088398332-8a7791972843?auto=format&fit=crop&w=1000&q=85'

const steps = [
  { label: '预订', detail: '已确认', icon: 'check', done: true },
  { label: '入住', detail: '办理中', icon: 'pin_drop', done: true },
  { label: '入住中', detail: 'Oct 24 - 27', icon: 'bed', done: false },
  { label: '退房', detail: 'Oct 27', icon: 'logout', done: false },
]

async function loadOrder() {
  if (route.params.orderNo === 'demo') return
  try {
    const res = await http.get<any>(`/api/v1/orders/${route.params.orderNo}`)
    order.value = res.data
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadOrder)
</script>

<style scoped>
.amenity {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 900;
}

.verify-box {
  display: flex;
  min-height: 170px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: 1px solid #cfd5df;
  border-radius: 8px;
}

.verify-box strong,
.verify-box small {
  display: block;
}

.verify-box small {
  color: #6b7280;
  font-weight: 700;
}
</style>
