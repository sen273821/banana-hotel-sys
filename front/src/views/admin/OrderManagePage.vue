<template>
  <div class="space-y-7">
    <div class="flex items-start justify-between gap-5">
      <div>
        <h1 class="text-4xl font-black">订单管理</h1>
        <p class="mt-2 font-bold text-[#5f6673]">监督宾客预订及交易状态。</p>
      </div>
      <div class="flex gap-4">
        <button class="rounded-lg border border-[#8c6b16] px-7 py-3 font-black text-[#8c6b16]">
          <span class="material-symbols-outlined align-middle text-[20px]">download</span>
          导出
        </button>
        <button class="rounded-lg bg-black px-7 py-3 font-black text-white">
          <span class="material-symbols-outlined align-middle text-[20px]">add</span>
          快速入住
        </button>
      </div>
    </div>

    <section class="rounded-lg border border-[#cfd5df] bg-white p-5">
      <div class="grid gap-5 md:grid-cols-[1fr_1fr_1fr_auto]">
        <label class="filter-field">
          <span>状态</span>
          <select v-model="filters.status">
            <option value="">所有状态</option>
            <option value="0">待支付</option>
            <option value="1">已支付</option>
            <option value="2">已入住</option>
            <option value="3">已退房</option>
            <option value="4">已取消</option>
          </select>
        </label>
        <label class="filter-field">
          <span>入住日期</span>
          <input type="date" />
        </label>
        <label class="filter-field">
          <span>退房日期</span>
          <input type="date" />
        </label>
        <button class="self-end rounded bg-[#ffd889] px-10 py-3 font-black text-[#5d4201]" @click="loadOrders">
          应用筛选
        </button>
      </div>
    </section>

    <section class="overflow-hidden rounded-lg border border-[#cfd5df] bg-white">
      <table class="w-full table-fixed text-left text-sm">
        <thead class="bg-[#eaf1ff] text-[#293241]">
          <tr>
            <th class="px-6 py-5 font-black">订单号</th>
            <th class="px-6 py-5 font-black">宾客姓名</th>
            <th class="px-6 py-5 font-black">手机号</th>
            <th class="px-6 py-5 font-black">房型</th>
            <th class="px-6 py-5 font-black">入住日期</th>
            <th class="px-6 py-5 font-black">总价</th>
            <th class="px-6 py-5 font-black">状态</th>
            <th class="px-6 py-5 font-black">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id" class="border-t border-[#dfe4ed]">
            <td class="px-6 py-6 font-mono font-black text-xs break-words">{{ order.orderNo }}</td>
            <td class="px-6 py-6">
              <div class="flex items-center gap-3">
                <span class="flex h-8 w-8 items-center justify-center rounded-full bg-[#ffe0a6] text-xs font-black">
                  {{ order.guestName?.slice(0, 2) }}
                </span>
                <strong>{{ order.guestName }}</strong>
              </div>
            </td>
            <td class="px-6 py-6">{{ order.guestPhone }}</td>
            <td class="px-6 py-6 font-bold">{{ order.roomTypeName || roomTypeLabel(order.roomTypeId) }}</td>
            <td class="px-6 py-6 font-bold">{{ order.checkInDate }} - {{ order.checkOutDate }}<br /><span class="text-xs text-[#6b7280]">{{ order.nights }}晚</span></td>
            <td class="px-6 py-6 font-black">¥{{ order.totalPrice }}</td>
            <td class="px-6 py-6">
              <span class="status-pill" :class="statusClass(order.status)">{{ statusLabel(order.status) }}</span>
            </td>
            <td class="px-6 py-6">
              <div class="flex gap-4">
                <button v-if="order.status === 1" class="material-symbols-outlined" @click="handleCheckin(order)">login</button>
                <button v-if="order.status === 2" class="material-symbols-outlined" @click="handleCheckout(order)">logout</button>
                <button class="material-symbols-outlined">edit</button>
                <button class="material-symbols-outlined">close</button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="orders.length === 0" class="py-12 text-center text-[#6b7280]">暂无订单数据</div>
      <div class="flex items-center justify-between border-t border-[#dfe4ed] bg-[#eaf1ff] px-6 py-5 text-sm font-bold">
        <span>显示第 1 至 {{ orders.length }} 条，共 {{ total }} 条订单</span>
        <div class="flex gap-2">
          <button :disabled="page <= 1" @click="page--; loadOrders()" class="page-btn">‹</button>
          <button class="page-btn active">{{ page }}</button>
          <button :disabled="page * pageSize >= total" @click="page++; loadOrders()" class="page-btn">›</button>
        </div>
      </div>
    </section>

    <div class="grid gap-6 md:grid-cols-3">
      <div class="metric-card bg-black text-white">
        <div class="mb-8 flex justify-between"><span>待处理款项</span><span class="material-symbols-outlined">payments</span></div>
        <div class="text-4xl font-black">$12,450</div>
        <div class="text-sm text-white/60">较昨日上涨 12%</div>
      </div>
      <div class="metric-card bg-[#ffd889] text-[#5d4201]">
        <div class="mb-8 flex justify-between"><span>预计入住</span><span class="material-symbols-outlined">accessibility_new</span></div>
        <div class="text-4xl font-black">28 位宾客</div>
        <div class="text-sm opacity-70">12 位已到达</div>
      </div>
      <div class="metric-card bg-white">
        <div class="mb-8 flex justify-between"><span>出租率</span><span class="material-symbols-outlined text-[#8c6b16]">trending_up</span></div>
        <div class="text-4xl font-black">92.4%</div>
        <div class="mt-4 h-2 rounded bg-[#dfe4ed]"><div class="h-full w-[92%] rounded bg-[#8c6b16]"></div></div>
      </div>
    </div>

    <div v-if="checkinOrder" class="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div class="w-[420px] rounded-lg bg-white p-6">
        <h3 class="mb-4 text-2xl font-black">办理入住</h3>
        <p class="mb-2 text-sm text-[#6b7280]">订单号：{{ checkinOrder.orderNo }}</p>
        <p class="mb-5 text-sm text-[#6b7280]">入住人：{{ checkinOrder.guestName }}</p>
        <label class="filter-field mb-5 block">
          <span>分配房间</span>
          <select v-model="checkinRoomId">
            <option value="">请选择房间</option>
            <option v-for="room in availableRooms" :key="room.id" :value="room.id">
              {{ room.roomNumber }} - {{ room.roomTypeName }}
            </option>
          </select>
        </label>
        <div class="flex gap-3">
          <button class="flex-1 rounded border border-[#cfd5df] py-3 font-black" @click="checkinOrder = null">取消</button>
          <button class="flex-1 rounded bg-black py-3 font-black text-white" @click="confirmCheckin">确认入住</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import http from '../../api/http'

const orders = ref<any[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(20)
const checkinOrder = ref<any>(null)
const checkinRoomId = ref('')
const availableRooms = ref<any[]>([])

const filters = reactive({ status: '', keyword: '' })

const STATUS_MAP: Record<number, { label: string; class: string }> = {
  0: { label: '待支付', class: 'pending' },
  1: { label: '已支付', class: 'paid' },
  2: { label: '已入住', class: 'checked' },
  3: { label: '已退房', class: 'done' },
  4: { label: '已取消', class: 'cancelled' },
}

function statusLabel(status: number) {
  return STATUS_MAP[status]?.label || '未知'
}

function statusClass(status: number) {
  return STATUS_MAP[status]?.class || 'done'
}

function roomTypeLabel(roomTypeId: number) {
  return ['豪华大床房', '行政大床房', '总统一套房', '高级双床房'][Number(roomTypeId || 1) - 1] || '标准房'
}

async function loadOrders() {
  try {
    const params: Record<string, any> = { page: page.value, size: pageSize.value }
    if (filters.status !== '') params.status = filters.status
    if (filters.keyword) params.keyword = filters.keyword
    const res = await http.get<any>('/api/v1/admin/orders', { params })
    orders.value = res.data.records || res.data
    total.value = res.data.total || orders.value.length
  } catch (e) {
    console.error(e)
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
  loadAvailableRooms()
}

async function confirmCheckin() {
  try {
    await http.put(`/api/v1/admin/orders/${checkinOrder.value.orderNo}/checkin`, {
      roomId: Number(checkinRoomId.value),
    })
    checkinOrder.value = null
    await loadOrders()
  } catch (e) {
    console.error(e)
  }
}

async function handleCheckout(order: any) {
  try {
    await http.put(`/api/v1/admin/orders/${order.orderNo}/checkout`)
    await loadOrders()
  } catch (e) {
    console.error(e)
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.filter-field span {
  display: block;
  margin-bottom: 8px;
  font-weight: 900;
}

.filter-field input,
.filter-field select {
  height: 44px;
  width: 100%;
  border: 1px solid #cfd5df;
  border-radius: 4px;
  background: white;
  padding: 0 14px;
  outline: none;
}

.status-pill {
  display: inline-flex;
  border-radius: 999px;
  padding: 7px 11px;
  font-size: 12px;
  font-weight: 900;
}

.status-pill.pending {
  background: #fff4ce;
  color: #8a5b00;
}

.status-pill.paid {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-pill.checked {
  background: #dcfce7;
  color: #15803d;
}

.status-pill.done {
  background: #f3f4f6;
  color: #374151;
}

.status-pill.cancelled {
  background: #fee2e2;
  color: #b91c1c;
}

.page-btn {
  min-width: 38px;
  border: 1px solid #cfd5df;
  border-radius: 4px;
  background: white;
  padding: 8px 12px;
}

.page-btn.active {
  background: black;
  color: white;
}

.metric-card {
  min-height: 150px;
  border: 1px solid #cfd5df;
  border-radius: 8px;
  padding: 24px;
  font-weight: 900;
}
</style>
