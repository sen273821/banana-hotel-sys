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
        <h2 class="text-xl font-bold text-gray-900 mb-6">注册</h2>
        <form @submit.prevent="handleRegister">
          <div class="mb-4">
            <label class="block text-sm text-gray-600 mb-1">用户名</label>
            <input
              v-model="form.username"
              type="text"
              placeholder="请输入用户名（3-50个字符）"
              class="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
              required
              minlength="3"
              maxlength="50"
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm text-gray-600 mb-1">昵称</label>
            <input
              v-model="form.nickname"
              type="text"
              placeholder="请输入昵称"
              class="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
              required
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm text-gray-600 mb-1">手机号</label>
            <input
              v-model="form.phone"
              type="tel"
              placeholder="请输入手机号"
              class="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
              required
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm text-gray-600 mb-1">密码</label>
            <input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              class="w-full border border-gray-200 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
              required
              minlength="6"
            />
          </div>
          <div class="mb-6">
            <label class="block text-sm text-gray-600 mb-1">确认密码</label>
            <input
              v-model="confirmPassword"
              type="password"
              placeholder="请再次输入密码"
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
            {{ loading ? '注册中...' : '注册' }}
          </button>
        </form>
        <p class="text-center text-sm text-gray-500 mt-6">
          已有账号？
          <router-link to="/login" class="text-amber-600 hover:underline">立即登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({ username: '', password: '', nickname: '', phone: '' })
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

async function handleRegister() {
  if (form.username.length < 3 || form.username.length > 50) {
    error.value = '用户名长度需在3-50个字符之间'
    return
  }
  if (form.password !== confirmPassword.value) {
    error.value = '两次密码输入不一致'
    return
  }
  loading.value = true
  error.value = ''
  try {
    await authStore.register(form)
    router.push('/')
  } catch (e: any) {
    error.value = e.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>
