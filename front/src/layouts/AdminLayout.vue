<template>
  <div class="flex h-screen bg-background">
    <!-- Sidebar -->
    <aside class="w-64 bg-primary-container flex flex-col">
      <div class="p-lg">
        <h1 class="text-white font-bold text-xl">香蕉酒店</h1>
        <p class="text-white/60 text-sm mt-1">管理后台</p>
      </div>
      <nav class="flex-1 px-sm mt-md">
        <router-link
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="flex items-center gap-md px-md py-sm rounded-lg mb-xs transition-colors"
          :class="[
            isActive(item.path)
              ? 'bg-white/15 text-white'
              : 'text-white/60 hover:bg-white/10 hover:text-white'
          ]"
        >
          <span class="material-symbols-outlined text-[20px]">{{ item.icon }}</span>
          <span class="text-sm font-medium">{{ item.label }}</span>
        </router-link>
      </nav>
    </aside>

    <!-- Main Content -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- Header -->
      <header class="h-16 bg-surface border-b border-outline-variant flex items-center justify-between px-xl">
        <div class="flex items-center gap-md">
          <h2 class="text-lg font-semibold text-on-surface">{{ currentTitle }}</h2>
        </div>
        <div class="flex items-center gap-md">
          <button class="material-symbols-outlined text-on-surface-variant p-2 hover:bg-surface-container-low rounded-full">
            notifications
          </button>
          <div class="flex items-center gap-sm">
            <div class="w-8 h-8 rounded-full bg-secondary flex items-center justify-center">
              <span class="text-on-secondary text-xs font-bold">管</span>
            </div>
            <span class="text-sm text-on-surface">管理员</span>
          </div>
        </div>
      </header>

      <!-- Page Content -->
      <main class="flex-1 overflow-auto p-xl bg-surface-container-low">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const menuItems = [
  { path: '/admin/room-status', label: '房态看板', icon: 'grid_view' },
  { path: '/admin/orders', label: '订单管理', icon: 'receipt_long' },
  { path: '/admin/checkin', label: '入住办理', icon: 'how_to_reg' },
]

const currentTitle = computed(() => {
  const item = menuItems.find(m => route.path.startsWith(m.path))
  return item?.label || '管理后台'
})

function isActive(path: string) {
  return route.path.startsWith(path)
}
</script>
