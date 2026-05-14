<template>
  <div>
    <!-- Filters -->
    <div class="bg-white rounded-xl shadow-sm p-4 mb-6 flex flex-wrap gap-4 items-end">
      <div>
        <label class="text-xs text-gray-500 block mb-1">订单状态</label>
        <select
          v-model="filters.status"
          class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
        >
          <option value="">全部</option>
          <option value="0">待支付</option>
          <option value="1">已支付</option>
          <option value="2">已入住</option>
          <option value="3">已退房</option>
          <option value="4">已取消</option>
        </select>
      </div>
      <div>
        <label class="text-xs text-gray-500 block mb-1">关键词</label>
        <input
          v-model="filters.keyword"
          type="text"
          placeholder="订单号/入住人/手机号"
          class="border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
        />
      </div>
      <button
        @click="loadOrders"
        class="bg-amber-500 text-white px-4 py-2 rounded-lg hover:bg-amber-600 text-sm"
      >
        查询
      </button>
    </div>

    <!-- Orders Table -->
    <div class="bg-white rounded-xl shadow-sm overflow-hidden">
      <table class="w-full text-sm">
        <thead class="bg-gray-50 text-gray-500">
          <tr>
            <th class="text-left px-4 py-3 font-medium">订单号</th>
            <th class="text-left px-4 py-3 font-medium">入住人</th>
            <th class="text-left px-4 py-3 font-medium">手机号</th>
            <th class="text-left px-4 py-3 font-medium">入住日期</th>
            <th class="text-left px-4 py-3 font-medium">退房日期</th>
            <th class="text-left px-4 py-3 font-medium">金额</th>
            <th class="text-left px-4 py-3 font-medium">状态</th>
            <th class="text-left px-4 py-3 font-medium">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-gray-100">
          <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50">
            <td class="px-4 py-3 font-mono text-xs">{{ order.orderNo }}</td>
            <td class="px-4 py-3">{{ order.guestName }}</td>
            <td class="px-4 py-3">{{ order.guestPhone }}</td>
            <td class="px-4 py-3">{{ order.checkInDate }}</td>
            <td class="px-4 py-3">{{ order.checkOutDate }}</td>
            <td class="px-4 py-3 text-amber-600 font-medium">¥{{ order.totalPrice }}</td>
            <td class="px-4 py-3">
              <span
                class="text-xs px-2 py-1 rounded-full"
                :class="statusClass(order.status)"
              >
                {{ statusLabel(order.status) }}
              </span>
            </td>
            <td class="px-4 py-3">
              <button
                v-if="order.status === 1"
                @click="handleCheckin(order)"
                class="text-xs bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600"
              >
                办理入住
              </button>
              <button
                v-if="order.status === 2"
                @click="handleCheckout(order)"
                class="text-xs bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600"
              >
                办理退房
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      <div v-if="orders.length === 0" class="text-center py-8 text-gray-400">
        暂无订单数据
      </div>
    </div>

    <!-- Pagination -->
    <div class="flex justify-between items-center mt-4 text-sm text-gray-500">
      <span>共 {{ total }} 条</span>
      <div class="flex gap-2">
        <button
          :disabled="page <= 1"
          @click="page--; loadOrders()"
          class="px-3 py-1 border rounded disabled:opacity-50"
        >
          上一页
        </button>
        <button
          :disabled="page * pageSize >= total"
          @click="page++; loadOrders()"
          class="px-3 py-1 border rounded disabled:opacity-50"
        >
          下一页
        </button>
      </div>
    </div>

    <!-- Checkin Dialog -->
    <div v-if="checkinOrder" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
      <div class="bg-white rounded-xl p-6 w-96">
        <h3 class="text-lg font-bold mb-4">办理入住</h3>
        <p class="text-sm text-gray-500 mb-2">订单号：{{ checkinOrder.orderNo }}</p>
        <p class="text-sm text-gray-500 mb-4">入住人：{{ checkinOrder.guestName }}</p>
        <div class="mb-4">
          <label class="block text-sm text-gray-600 mb-1">分配房间号</label>
          <input
            v-model="checkinRoomId"
            type="number"
            placeholder="输入房间ID"
            class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
          />
        </div>
        <div class="flex gap-3">
          <button
            @click="checkinOrder = null"
            class="flex-1 border border-gray-300 py-2 rounded-lg text-sm"
          >
            取消
          </button>
          <button
            @click="confirmCheckin"
            class="flex-1 bg-green-500 text-white py-2 rounded-lg text-sm hover:bg-green-600"
          >
            确认入住
          </button>
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

const filters = reactive({ status: '', keyword: '' })

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

function handleCheckin(order: any) {
  checkinOrder.value = order
  checkinRoomId.value = ''
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

onMounted(() => {
  loadOrders()
})
</script>
