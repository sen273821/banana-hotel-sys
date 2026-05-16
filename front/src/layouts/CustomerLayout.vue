<template>
  <div class="min-h-screen bg-[#f7f8fc] text-[#111827] flex flex-col">
    <header class="sticky top-0 z-50 border-b border-[#d8dce5] bg-[#f7f8fc]/95 backdrop-blur">
      <div class="mx-auto flex h-16 max-w-[1440px] items-center justify-between px-8">
        <router-link to="/" class="flex items-center gap-3">
          <span class="text-3xl font-black tracking-normal text-black">香蕉酒店</span>
        </router-link>
        <nav class="hidden items-center gap-10 md:flex">
          <router-link to="/" class="nav-link">
            探索
          </router-link>
          <router-link to="/rooms" class="nav-link">
            订单
          </router-link>
          <router-link to="/booking-success/demo" class="nav-link">
            入住
          </router-link>
          <router-link v-if="authStore.isLoggedIn" to="/my-orders" class="nav-link">个人中心</router-link>
          <router-link v-else to="/login" class="nav-link">个人中心</router-link>
        </nav>
        <div class="flex items-center gap-4">
          <button class="material-symbols-outlined icon-button">notifications</button>
          <router-link v-if="!authStore.isLoggedIn" to="/login" class="material-symbols-outlined icon-button">account_circle</router-link>
          <template v-if="authStore.isLoggedIn">
            <div class="flex items-center gap-3">
              <div class="flex h-9 w-9 items-center justify-center rounded-full border border-[#c8ced9] bg-white">
                <span class="text-sm font-bold">{{ authStore.user?.nickname?.charAt(0) || '客' }}</span>
              </div>
              <button @click="authStore.logout()" class="text-sm text-[#6b7280] hover:text-black">退出</button>
            </div>
          </template>
        </div>
      </div>
    </header>

    <main class="flex-1">
      <router-view />
    </main>

    <footer class="bg-[#111827] py-10 text-[#d5d9e2]">
      <div class="mx-auto grid max-w-[1440px] grid-cols-1 gap-8 px-8 md:grid-cols-4">
        <div>
          <div class="mb-4 text-2xl font-black text-white">LuxeStay</div>
          <p class="max-w-xs text-sm leading-6 text-white/60">定义全球奢华住宿的新标准，提供高品质酒店预订与入住服务。</p>
        </div>
        <div>
          <h4 class="mb-3 font-bold text-white">关于我们</h4>
          <div class="space-y-2 text-sm text-white/60">
            <p>品牌故事</p>
            <p>联系我们</p>
            <p>新闻中心</p>
          </div>
        </div>
        <div>
          <h4 class="mb-3 font-bold text-white">合作伙伴</h4>
          <div class="space-y-2 text-sm text-white/60">
            <p>酒店加盟</p>
            <p>企业预订</p>
            <p>开发者 API</p>
          </div>
        </div>
        <div>
          <h4 class="mb-3 font-bold text-white">法律与隐私</h4>
          <div class="space-y-2 text-sm text-white/60">
            <p>隐私政策</p>
            <p>使用条款</p>
            <p>Cookie 声明</p>
          </div>
        </div>
      </div>
      <div class="mx-auto mt-8 max-w-[1440px] border-t border-white/10 px-8 pt-5 text-center text-xs text-white/40">
        © 2026 LuxeStay Manager. 保留所有权利。
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { useAuthStore } from '../stores/authStore'

const authStore = useAuthStore()
</script>

<style scoped>
.nav-link {
  border-bottom: 2px solid transparent;
  padding: 22px 2px 18px;
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
}

.nav-link.router-link-active {
  border-color: #000;
  color: #000;
}

.icon-button {
  display: inline-flex;
  height: 40px;
  width: 40px;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  color: #111827;
}

.icon-button:hover {
  background: #eef2f8;
}
</style>
