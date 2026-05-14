<template>
  <div>
    <!-- Hero Section -->
    <section class="relative bg-gradient-to-br from-amber-400 to-orange-500 py-20">
      <div class="max-w-7xl mx-auto px-4 text-center">
        <h1 class="text-4xl font-bold text-white mb-4">找到您的理想住所</h1>
        <p class="text-white/80 text-lg mb-8">精选优质酒店，让每次出行都是享受</p>
        <div class="max-w-2xl mx-auto bg-white rounded-xl shadow-lg p-4 flex gap-3 items-end">
          <div class="flex-1">
            <label class="text-xs text-gray-500 block mb-1">目的地</label>
            <input
              v-model="searchCity"
              type="text"
              placeholder="输入城市名称"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
            />
          </div>
          <div class="flex-1">
            <label class="text-xs text-gray-500 block mb-1">入住日期</label>
            <input
              v-model="checkIn"
              type="date"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
            />
          </div>
          <div class="flex-1">
            <label class="text-xs text-gray-500 block mb-1">退房日期</label>
            <input
              v-model="checkOut"
              type="date"
              class="w-full border border-gray-200 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-amber-400"
            />
          </div>
          <button
            @click="goSearch"
            class="bg-amber-500 text-white px-6 py-2 rounded-lg hover:bg-amber-600 text-sm font-medium whitespace-nowrap"
          >
            搜索房型
          </button>
        </div>
      </div>
    </section>

    <!-- Hot Destinations -->
    <section class="max-w-7xl mx-auto px-4 py-12">
      <h2 class="text-xl font-bold text-gray-900 mb-6">热门目的地</h2>
      <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div
          v-for="city in hotCities"
          :key="city.name"
          class="relative rounded-xl overflow-hidden h-32 cursor-pointer group"
          @click="searchCity = city.name; goSearch()"
        >
          <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent"></div>
          <div class="absolute inset-0 flex items-center justify-center" :class="city.bg">
            <span class="text-3xl">{{ city.icon }}</span>
          </div>
          <div class="absolute bottom-3 left-3">
            <p class="text-white font-medium">{{ city.name }}</p>
            <p class="text-white/70 text-xs">{{ city.count }}家酒店</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Recommended Hotels -->
    <section class="max-w-7xl mx-auto px-4 py-12">
      <h2 class="text-xl font-bold text-gray-900 mb-6">精选推荐</h2>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div
          v-for="hotel in recommendedHotels"
          :key="hotel.name"
          class="bg-white rounded-xl shadow-sm overflow-hidden hover:shadow-md transition-shadow cursor-pointer"
          @click="$router.push('/rooms')"
        >
          <div class="h-40 flex items-center justify-center" :class="hotel.bg">
            <span class="text-5xl">{{ hotel.icon }}</span>
          </div>
          <div class="p-4">
            <h3 class="font-medium text-gray-900">{{ hotel.name }}</h3>
            <p class="text-sm text-gray-500 mt-1">{{ hotel.location }}</p>
            <div class="flex items-center justify-between mt-3">
              <div class="flex items-center gap-1">
                <span class="text-amber-500 text-sm">★</span>
                <span class="text-sm text-gray-700">{{ hotel.rating }}</span>
              </div>
              <p class="text-amber-600 font-bold">¥{{ hotel.price }}<span class="text-xs text-gray-400 font-normal">/晚</span></p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchCity = ref('')
const checkIn = ref('')
const checkOut = ref('')

function goSearch() {
  router.push({ path: '/rooms', query: { checkIn: checkIn.value, checkOut: checkOut.value } })
}

const hotCities = [
  { name: '上海', count: 2860, icon: '🏙️', bg: 'bg-blue-100' },
  { name: '北京', count: 3200, icon: '🏛️', bg: 'bg-red-100' },
  { name: '广州', count: 1850, icon: '🌺', bg: 'bg-pink-100' },
  { name: '深圳', count: 1620, icon: '🌃', bg: 'bg-purple-100' },
]

const recommendedHotels = [
  { name: '香蕉酒店·外滩店', location: '上海市黄浦区', rating: '4.9', price: 299, icon: '🍌', bg: 'bg-amber-50' },
  { name: '香蕉酒店·国贸店', location: '北京市朝阳区', rating: '4.8', price: 399, icon: '🍌', bg: 'bg-yellow-50' },
  { name: '香蕉酒店·珠江店', location: '广州市天河区', rating: '4.7', price: 259, icon: '🍌', bg: 'bg-orange-50' },
]
</script>
