<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <div class="w-full max-w-md">
      <div class="text-center mb-8">
        <router-link to="/" class="inline-flex items-center gap-2">
          <span class="text-3xl">🍌</span>
          <span class="text-xl font-bold text-gray-900">香蕉酒店</span>
        </router-link>
      </div>
      <div class="bg-white rounded-xl shadow-sm p-8">
        <h2 class="text-xl font-bold text-gray-900 mb-6">登录</h2>
        <form @submit.prevent="handleLogin">
          <div class="mb-4">
            <label class="block text-sm text-gray-600 mb-1">用户名</label>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名"
              class="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
              required
            />
          </div>
          <div class="mb-6">
            <label class="block text-sm text-gray-600 mb-1">密码</label>
            <input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              class="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
              required
            />
          </div>
          <p v-if="error" class="text-red-500 text-sm mb-4">{{ error }}</p>
          <button
            type="submit"
            :disabled="loading"
            class="w-full bg-amber-500 text-white py-3 rounded-lg hover:bg-amber-600 font-medium disabled:opacity-50"
          >
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
        <p class="text-center text-sm text-gray-500 mt-6">
          还没有账号？
          <router-link to="/register" class="text-amber-600 hover:underline">立即注册</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const form = reactive({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    await authStore.login(form.username, form.password)
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: any) {
    error.value = e.message || '登录失败'
  } finally {
    loading.value = false
  }
}
</script>
