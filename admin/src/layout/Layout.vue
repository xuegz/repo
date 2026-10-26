<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">建材商户平台</div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#1f2937"
        text-color="#c0c4cc"
        active-text-color="#ffd04b"
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="page-title">{{ route.meta.title }}</span>
        <div class="user-box">
          <span>{{ realName }}</span>
          <el-tag size="small" type="success" effect="dark" style="margin: 0 10px">
            {{ role === 1 ? '平台管理员' : '商户' }}
          </el-tag>
          <el-button link type="primary" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const role = Number(localStorage.getItem('role'))
const realName = localStorage.getItem('realName') || '未登录'

const adminMenus = [
  { path: '/dashboard', title: '数据总览', icon: 'DataLine' },
  { path: '/merchant', title: '商户管理', icon: 'Shop' },
  { path: '/product', title: '产品管理', icon: 'Goods' },
  { path: '/category', title: '品类管理', icon: 'Menu' },
  { path: '/content', title: '内容管理', icon: 'Picture' }
]

const merchantMenus = [
  { path: '/m/dashboard', title: '数据看板', icon: 'DataLine' },
  { path: '/m/shop', title: '我的门店', icon: 'Shop' },
  { path: '/m/product', title: '我的产品', icon: 'Goods' }
]

const menus = computed(() => (role === 1 ? adminMenus : merchantMenus))

const logout = () => {
  ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    .then(() => {
      localStorage.clear()
      router.push('/login')
    })
    .catch(() => {})
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background: #1f2937;
  overflow-x: hidden;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #18222f;
  letter-spacing: 1px;
}
.aside .el-menu {
  border-right: none;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}
.page-title {
  font-size: 16px;
  font-weight: bold;
}
.user-box {
  display: flex;
  align-items: center;
}
.main {
  background: #f5f7fa;
}
</style>
