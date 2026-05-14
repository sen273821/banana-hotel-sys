import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import http from '../api/http'

interface UserInfo {
  id: number
  username: string
  nickname: string
  phone: string
  role: number
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<UserInfo | null>(
    JSON.parse(localStorage.getItem('user') || 'null')
  )

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role && user.value.role >= 1)

  async function login(username: string, password: string) {
    const res = await http.post<{ token: string; user: UserInfo }>(
      '/api/v1/auth/login',
      { username, password }
    )
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
  }

  async function register(data: {
    username: string
    password: string
    nickname: string
    phone: string
  }) {
    const res = await http.post<{ token: string; user: UserInfo }>(
      '/api/v1/auth/register',
      data
    )
    token.value = res.data.token
    user.value = res.data.user
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, isLoggedIn, isAdmin, login, register, logout }
})
