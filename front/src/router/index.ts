import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../layouts/AdminLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/room-status',
        },
        {
          path: 'room-status',
          name: 'RoomStatus',
          component: () => import('../views/room-status/RoomStatusBoard.vue'),
          meta: { title: '房态看板' },
        },
      ],
    },
  ],
})

export default router
