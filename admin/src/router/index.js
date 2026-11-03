import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      // 平台端
      { path: 'dashboard', component: () => import('@/views/Dashboard.vue'), meta: { title: '数据总览', roles: [1] } },
      { path: 'merchant', component: () => import('@/views/MerchantList.vue'), meta: { title: '商户管理', roles: [1] } },
      { path: 'market', component: () => import('@/views/MarketList.vue'), meta: { title: '市场管理', roles: [1] } },
      { path: 'product', component: () => import('@/views/ProductAudit.vue'), meta: { title: '产品管理', roles: [1] } },
      { path: 'category', component: () => import('@/views/CategoryList.vue'), meta: { title: '品类管理', roles: [1] } },
      { path: 'content', component: () => import('@/views/ContentManage.vue'), meta: { title: '内容管理', roles: [1] } },
      // 商户端
      { path: 'm/dashboard', component: () => import('@/views/merchant/MerchantDashboard.vue'), meta: { title: '数据看板', roles: [2] } },
      { path: 'm/shop', component: () => import('@/views/merchant/MyShop.vue'), meta: { title: '我的门店', roles: [2] } },
      { path: 'm/product', component: () => import('@/views/merchant/MyProduct.vue'), meta: { title: '我的产品', roles: [2] } },
      { path: 'm/inquiry', component: () => import('@/views/merchant/InquiryList.vue'), meta: { title: '询价线索', roles: [2] } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.public) {
    return true
  }
  const token = localStorage.getItem('token')
  if (!token) {
    return '/login'
  }
  const role = Number(localStorage.getItem('role'))
  const home = role === 1 ? '/dashboard' : '/m/dashboard'
  if (to.path === '/' || to.path === '/login') {
    return home
  }
  if (to.meta.roles && !to.meta.roles.includes(role)) {
    return home
  }
  return true
})

export default router
