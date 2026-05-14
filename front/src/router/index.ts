import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // Customer-facing routes
    {
      path: '/',
      component: () => import('../layouts/CustomerLayout.vue'),
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('../views/customer/HomePage.vue'),
          meta: { title: '首页' },
        },
        {
          path: 'rooms',
          name: 'RoomList',
          component: () => import('../views/customer/RoomListPage.vue'),
          meta: { title: '房型预订' },
        },
        {
          path: 'booking/:roomTypeId',
          name: 'Booking',
          component: () => import('../views/customer/BookingPage.vue'),
          meta: { title: '预订', requiresAuth: true },
        },
        {
          path: 'payment/:orderNo',
          name: 'Payment',
          component: () => import('../views/customer/PaymentPage.vue'),
          meta: { title: '支付', requiresAuth: true },
        },
        {
          path: 'booking-success/:orderNo',
          name: 'BookingSuccess',
          component: () => import('../views/customer/BookingSuccessPage.vue'),
          meta: { title: '预订成功', requiresAuth: true },
        },
        {
          path: 'my-orders',
          name: 'MyOrders',
          component: () => import('../views/customer/MyOrdersPage.vue'),
          meta: { title: '我的订单', requiresAuth: true },
        },
      ],
    },
    // Auth routes (no layout)
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/customer/LoginPage.vue'),
      meta: { title: '登录' },
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/customer/RegisterPage.vue'),
      meta: { title: '注册' },
    },
    // Admin routes
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      children: [
        {
          path: '',
          redirect: '/admin/room-status',
        },
        {
          path: 'room-status',
          name: 'RoomStatus',
          component: () => import('../views/room-status/RoomStatusBoard.vue'),
          meta: { title: '房态看板', requiresAdmin: true },
        },
        {
          path: 'orders',
          name: 'AdminOrders',
          component: () => import('../views/admin/OrderManagePage.vue'),
          meta: { title: '订单管理', requiresAdmin: true },
        },
        {
          path: 'checkin',
          name: 'AdminCheckin',
          component: () => import('../views/admin/CheckinPage.vue'),
          meta: { title: '入住办理', requiresAdmin: true },
        },
      ],
    },
  ],
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresAdmin && !token) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

export default router
