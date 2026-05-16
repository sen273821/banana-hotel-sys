<template>
  <div class="mx-auto max-w-[1440px] px-8 py-8">
    <section class="mb-7 rounded-lg border border-[#cfd5df] bg-white p-5">
      <div class="grid grid-cols-1 items-end gap-5 md:grid-cols-[1fr_1fr_1fr_auto]">
        <label class="search-meta">
          <span>日期</span>
          <div>
            <span class="material-symbols-outlined">calendar_today</span>
            <input v-model="checkIn" type="date" :min="today" />
          </div>
        </label>
        <label class="search-meta">
          <span>退房</span>
          <div>
            <span class="material-symbols-outlined">event_available</span>
            <input v-model="checkOut" type="date" :min="checkIn || today" />
          </div>
        </label>
        <div class="search-meta">
          <span>地点</span>
          <div>
            <span class="material-symbols-outlined">location_on</span>
            <strong>{{ route.query.city || 'Grand Plaza, New York' }}</strong>
          </div>
        </div>
        <button class="rounded bg-black px-9 py-4 text-sm font-bold text-white" @click="loadRoomTypes">
          更新搜索
        </button>
      </div>
    </section>

    <div v-if="loading" class="py-20 text-center text-[#6b7280]">加载中...</div>
    <div v-else class="grid gap-7 lg:grid-cols-[2fr_1fr]">
      <article
        v-for="(room, index) in roomTypes"
        :key="room.id"
        class="room-card"
        :class="index === 0 ? 'lg:col-span-2 lg:grid lg:grid-cols-[380px_1fr]' : ''"
      >
        <div class="relative min-h-[260px] overflow-hidden">
          <img class="h-full min-h-[260px] w-full object-cover" :src="roomImages[index % roomImages.length]" :alt="room.name" />
          <span class="absolute left-5 top-5 rounded bg-[#f6e9bd] px-3 py-1 text-sm font-bold text-[#7a5a10]">
            {{ index === 0 ? `剩余 ${Math.max(1, 3 - index)} 间` : index === 1 ? '最后一间' : '尊享' }}
          </span>
        </div>
        <div class="flex min-h-[260px] flex-col p-7">
          <div class="mb-4 flex items-start justify-between gap-5">
            <div>
              <h2 class="mb-3 text-3xl font-black">{{ room.name }}</h2>
              <p class="max-w-2xl text-base leading-7 text-[#4b5563]">{{ room.description || '舒适客房，配备高品质寝具与城市景观。' }}</p>
            </div>
            <div class="text-right">
              <div class="text-sm text-[#6b7280] line-through">¥{{ Number(room.price || 0) + 35 }}</div>
              <div class="text-3xl font-black">¥{{ room.price }}<span class="text-base font-normal">/晚</span></div>
            </div>
          </div>
          <div class="mb-6 flex flex-wrap gap-5 text-[#374151]">
            <span class="amenity"><span class="material-symbols-outlined">wifi</span>免费无线网络</span>
            <span class="amenity"><span class="material-symbols-outlined">ac_unit</span>A/C</span>
            <span class="amenity"><span class="material-symbols-outlined">restaurant</span>含早餐</span>
            <span class="amenity"><span class="material-symbols-outlined">tv</span>4K 智能电视</span>
          </div>
          <div class="mt-auto flex items-center gap-4">
            <button class="flex-1 rounded bg-black py-4 text-sm font-bold text-white" @click="goBooking(room.id)">
              立即预订
            </button>
            <button class="flex h-14 w-16 items-center justify-center rounded border border-[#cfd5df] bg-white">
              <span class="material-symbols-outlined text-3xl">favorite</span>
            </button>
          </div>
        </div>
      </article>
    </div>

    <div v-if="!loading && roomTypes.length === 0" class="py-20 text-center text-[#6b7280]">
      暂无可用房型，请选择日期后查询
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import http from '../../api/http'

const router = useRouter()
const route = useRoute()

const today = new Date().toISOString().split('T')[0]
const checkIn = ref((route.query.checkIn as string) || '')
const checkOut = ref((route.query.checkOut as string) || '')
const roomTypes = ref<any[]>([])
const loading = ref(false)

const roomImages = [
  'https://images.unsplash.com/photo-1618773928121-c32242e63f39?auto=format&fit=crop&w=1200&q=85',
  'https://images.unsplash.com/photo-1590490360182-c33d57733427?auto=format&fit=crop&w=1200&q=85',
  'https://images.unsplash.com/photo-1598928506311-c55ded91a20c?auto=format&fit=crop&w=1200&q=85',
  'https://images.unsplash.com/photo-1566665797739-1674de7a421a?auto=format&fit=crop&w=1200&q=85',
]

async function loadRoomTypes() {
  loading.value = true
  try {
    const res = await http.get<any[]>('/api/v1/room-types')
    roomTypes.value = res.data
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function goBooking(roomTypeId: number) {
  const query: Record<string, string> = {}
  if (checkIn.value) query.checkIn = checkIn.value
  if (checkOut.value) query.checkOut = checkOut.value
  router.push({ path: `/booking/${roomTypeId}`, query })
}

onMounted(() => {
  loadRoomTypes()
})
</script>

<style scoped>
.search-meta > span {
  display: block;
  margin-bottom: 6px;
  color: #4b5563;
  font-size: 14px;
  font-weight: 700;
}

.search-meta > div {
  display: flex;
  min-height: 42px;
  align-items: center;
  gap: 10px;
  color: #111827;
}

.search-meta input {
  width: 100%;
  border: 0;
  background: transparent;
  font-weight: 800;
  outline: none;
}

.room-card {
  overflow: hidden;
  border: 1px solid #cfd5df;
  border-radius: 8px;
  background: #fff;
}

.amenity {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 15px;
  font-weight: 700;
}

.amenity .material-symbols-outlined {
  color: #8c6b16;
  font-size: 20px;
}
</style>
