<template>
  <div class="flex h-screen bg-[#f7f8fc] text-[#111827]">
    <aside class="flex w-[280px] flex-col border-r border-[#d5dae4] bg-[#edf3ff]">
      <div class="px-6 py-7">
        <h1 class="flex items-center gap-3 text-2xl font-black">
          <span class="material-symbols-outlined">apartment</span>
          Grand Plaza
        </h1>
        <div class="mt-8 flex items-center gap-3">
          <div class="flex h-10 w-10 items-center justify-center rounded-lg bg-[#111827] text-white">
            <span class="material-symbols-outlined">account_circle</span>
          </div>
          <div>
            <div class="text-sm font-black">Staff Portal</div>
            <div class="text-xs text-[#5f6673]">Administrator</div>
          </div>
        </div>
      </div>
      <nav class="flex-1 px-4">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="mb-2 flex items-center gap-4 rounded px-4 py-3 font-bold transition-colors"
          :class="[
            isActive(item.path)
              ? 'bg-[#dce9ff] text-black'
              : 'text-[#293241] hover:bg-white/60'
          ]"
        >
          <span class="material-symbols-outlined text-[22px]">{{ item.icon }}</span>
          <span class="text-sm">{{ item.label }}</span>
        </router-link>
      </nav>
      <div class="border-t border-[#cdd5e2] p-4">
        <button class="mb-5 w-full rounded bg-[#ffd889] py-4 font-black text-[#5d4201]">所有楼层</button>
        <div class="space-y-4 px-4 text-sm font-bold text-[#293241]">
          <div class="flex items-center gap-3"><span class="material-symbols-outlined">settings</span>设置</div>
          <div class="flex items-center gap-3"><span class="material-symbols-outlined">help</span>客服支持</div>
        </div>
      </div>
    </aside>

    <div class="flex flex-1 flex-col overflow-hidden">
      <header class="flex h-16 items-center justify-between border-b border-[#d5dae4] bg-[#f7f8fc] px-8">
        <h2 class="text-2xl font-black">香蕉酒店后台</h2>
        <div class="flex items-center gap-5">
          <label class="hidden h-11 w-[300px] items-center gap-3 rounded-lg border border-[#c8d1df] bg-[#eaf1ff] px-4 md:flex">
            <span class="material-symbols-outlined text-[#5f6673]">search</span>
            <input class="w-full border-0 bg-transparent text-sm font-bold outline-none" placeholder="搜索房间..." />
          </label>
          <button class="material-symbols-outlined relative">notifications</button>
          <button class="material-symbols-outlined">account_circle</button>
        </div>
      </header>

      <main class="flex-1 overflow-auto bg-[#f7f8fc] p-8">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'

const route = useRoute()

const menuItems = [
  { path: '/admin/room-status', label: '房态看板', icon: 'dashboard' },
  { path: '/admin/orders', label: '订单管理', icon: 'receipt_long' },
  { path: '/admin/checkin', label: '员工管理', icon: 'groups' },
]

function isActive(path: string) {
  return route.path.startsWith(path)
}
</script>
