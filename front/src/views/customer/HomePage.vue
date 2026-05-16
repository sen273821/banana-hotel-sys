<template>
  <div>
    <section class="relative h-[590px] overflow-hidden">
      <img class="absolute inset-0 h-full w-full object-cover" :src="heroImage" alt="luxury hotel lobby" />
      <div class="absolute inset-0 bg-black/35"></div>
      <div class="relative z-10 mx-auto flex h-full max-w-[1440px] flex-col items-center justify-center px-8 pt-8">
        <h1 class="mb-8 text-center text-5xl font-black leading-tight text-white drop-shadow md:text-6xl">
          寻找您的下一场非凡下榻
        </h1>
        <div class="grid w-full max-w-[1100px] grid-cols-1 gap-4 rounded-lg bg-white p-6 shadow-2xl md:grid-cols-[1fr_1fr_0.7fr_auto]">
          <label class="field-block">
            <span>目的地</span>
            <div class="field-shell">
              <span class="material-symbols-outlined">location_on</span>
              <input v-model="searchCity" placeholder="您想去哪里？" />
            </div>
          </label>
          <label class="field-block">
            <span>入住 - 退房</span>
            <div class="field-shell">
              <span class="material-symbols-outlined">calendar_today</span>
              <input :value="dateLabel" readonly placeholder="选择日期" />
            </div>
          </label>
          <label class="field-block">
            <span>入住人数</span>
            <div class="field-shell">
              <span class="material-symbols-outlined">group</span>
              <input value="2位成人, 0名儿童" readonly />
            </div>
          </label>
          <button class="self-end rounded bg-black px-8 py-4 text-sm font-bold text-white hover:bg-[#1f2937]" @click="goSearch">
            搜索
          </button>
        </div>
      </div>
    </section>

    <section class="mx-auto max-w-[1440px] px-8 py-9">
      <h2 class="mb-7 text-2xl font-black">热门目的地</h2>
      <div class="grid grid-cols-3 gap-8 md:grid-cols-6">
        <button
          v-for="city in destinations"
          :key="city.name"
          class="group flex flex-col items-center gap-3"
          @click="searchCity = city.name; goSearch()"
        >
          <img class="h-24 w-24 rounded-lg object-cover ring-1 ring-black/5 transition group-hover:ring-2 group-hover:ring-[#8c6b16]" :src="city.image" :alt="city.name" />
          <span class="text-sm font-bold">{{ city.name }}</span>
        </button>
      </div>
    </section>

    <section class="mx-auto max-w-[1440px] px-8 pb-9">
      <div class="grid gap-10 rounded-lg bg-[#121c2e] p-8 text-white md:grid-cols-[1fr_1.2fr]">
        <div class="flex flex-col justify-center">
          <span class="mb-4 w-fit rounded-full bg-[#8c6b16] px-4 py-1 text-xs font-bold">早鸟优惠</span>
          <h2 class="mb-3 text-3xl font-black">提前预订，畅享 20% 折扣</h2>
          <p class="max-w-xl text-sm leading-6 text-white/75">
            提前 30 天预订您的下一次奢华之旅，即可在参与活动的酒店享受专属折扣。
          </p>
          <button class="mt-7 w-fit rounded bg-[#8c6b16] px-7 py-3 text-sm font-bold text-white hover:bg-[#74570f]">
            立即探索
          </button>
        </div>
        <img class="h-72 w-full rounded object-cover" :src="promoImage" alt="infinity pool" />
      </div>
    </section>

    <section class="mx-auto max-w-[1440px] px-8 pb-10">
      <div class="mb-7 flex items-end justify-between">
        <div>
          <h2 class="text-2xl font-black">精选推荐酒店</h2>
          <p class="mt-1 text-sm text-[#5f6673]">为您量身打造的顶级下榻之选</p>
        </div>
        <router-link to="/rooms" class="text-sm font-bold text-[#8c6b16]">查看全部</router-link>
      </div>
      <div class="grid grid-cols-1 gap-7 md:grid-cols-3">
        <article
          v-for="hotel in hotels"
          :key="hotel.name"
          class="overflow-hidden rounded-lg border border-[#d3d7df] bg-white transition hover:shadow-lg"
        >
          <div class="relative h-72 overflow-hidden">
            <img class="h-full w-full object-cover transition duration-500 hover:scale-105" :src="hotel.image" :alt="hotel.name" />
            <div class="absolute right-4 top-4 flex items-center gap-1 rounded-full bg-white/95 px-3 py-1 text-sm font-bold">
              <span class="material-symbols-outlined filled text-[18px] text-[#8c6b16]">star</span>
              {{ hotel.rating }}
            </div>
          </div>
          <div class="p-6">
            <h3 class="mb-2 text-xl font-black">{{ hotel.name }}</h3>
            <div class="mb-5 flex items-center gap-1 text-sm text-[#5f6673]">
              <span class="material-symbols-outlined text-[18px]">location_on</span>
              {{ hotel.location }}
            </div>
            <div class="mb-6 flex gap-4 text-[#5f6673]">
              <span v-for="icon in hotel.icons" :key="icon" class="material-symbols-outlined text-[22px]">{{ icon }}</span>
            </div>
            <div class="flex items-end justify-between border-t border-[#e3e6ec] pt-5">
              <div>
                <div class="text-xs font-bold text-[#6b7280]">每晚起价</div>
                <div class="text-2xl font-black text-black">¥{{ hotel.price }}</div>
              </div>
              <button class="rounded bg-[#8c6b16] px-6 py-3 text-sm font-bold text-white" @click="$router.push('/rooms')">预订</button>
            </div>
          </div>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchCity = ref('')
const checkIn = ref('')
const checkOut = ref('')

const heroImage = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=2200&q=85'
const promoImage = 'https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=1600&q=85'

const dateLabel = computed(() => {
  if (checkIn.value && checkOut.value) return `${checkIn.value} - ${checkOut.value}`
  return ''
})

function goSearch() {
  router.push({ path: '/rooms', query: { city: searchCity.value, checkIn: checkIn.value, checkOut: checkOut.value } })
}

const destinations = [
  { name: '上海', image: 'https://images.unsplash.com/photo-1538428494232-9c0d8a3ab403?auto=format&fit=crop&w=400&q=80' },
  { name: '东京', image: 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?auto=format&fit=crop&w=400&q=80' },
  { name: '伦敦', image: 'https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=400&q=80' },
  { name: '巴黎', image: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=400&q=80' },
  { name: '纽约', image: 'https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=400&q=80' },
  { name: '迪拜', image: 'https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=400&q=80' },
]

const hotels = [
  {
    name: '纽约广场大酒店',
    location: '纽约, 第五大道',
    rating: '4.9',
    price: '3,580',
    image: 'https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?auto=format&fit=crop&w=1000&q=85',
    icons: ['pool', 'spa', 'local_dining', 'wifi'],
  },
  {
    name: '阿丽拉乌鲁瓦图别墅',
    location: '巴厘岛, 乌鲁瓦图',
    rating: '4.8',
    price: '4,200',
    image: 'https://images.unsplash.com/photo-1571896349842-33c89424de2d?auto=format&fit=crop&w=1000&q=85',
    icons: ['pool', 'self_improvement', 'restaurant', 'concierge'],
  },
  {
    name: '阿曼东京酒店',
    location: '东京, 大手町',
    rating: '5.0',
    price: '6,800',
    image: 'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1000&q=85',
    icons: ['pool', 'business_center', 'book', 'local_bar'],
  },
]
</script>
