<template>
  <div class="min-h-screen bg-gray-50 flex flex-col">
    <!-- Top Navigation -->
    <header class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <router-link to="/" class="flex items-center gap-2">
          <span class="text-2xl">🍌</span>
          <span class="text-lg font-bold text-gray-900">香蕉酒店</span>
        </router-link>
        <nav class="flex items-center gap-6">
          <router-link to="/rooms" class="text-sm text-gray-600 hover:text-primary-600">
            房型预订
          </router-link>
          <router-link v-if="authStore.isLoggedIn" to="/my-orders" class="text-sm text-gray-600 hover:text-primary-600">
            我的订单
          </router-link>
          <template v-if="authStore.isLoggedIn">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 rounded-full bg-amber-400 flex items-center justify-center">
                <span class="text-white text-xs font-bold">{{ authStore.user?.nickname?.charAt(0) || '客' }}</span>
              </div>
              <span class="text-sm text-gray-700">{{ authStore.user?.nickname }}</span>
              <button @click="authStore.logout()" class="text-xs text-gray-400 hover:text-red-500 ml-2">退出</button>
            </div>
          </template>
          <template v-else>
            <router-link to="/login" class="text-sm text-primary-600 font-medium">登录</router-link>
            <router-link to="/register" class="text-sm bg-amber-400 text-white px-4 py-2 rounded-lg hover:bg-amber-500">
              注册
            </router-link>
          </template>
        </nav>
      </div>
    </header>

    <!-- Page Content -->
    <main class="flex-1">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="bg-gray-800 text-gray-400 py-8">
      <div class="max-w-7xl mx-auto px-4 text-center text-sm">
        <p>&copy; 2026 香蕉酒店 All Rights Reserved</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/authStore'

const authStore = useAuthStore()
</script>
