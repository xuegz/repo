<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :span="3">
        <el-card shadow="hover" class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ overview[card.key] ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>最近动态（浏览 / 转发 / 电话联系）</span>
          <el-button text type="primary" @click="loadRecords">刷新</el-button>
        </div>
      </template>

      <el-table :data="records" border max-height="360">
        <el-table-column label="时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="类型" width="110">
          <template #default="{ row }">
            <el-tag :type="tagType(row.action)" size="small">{{ row.action }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetName" label="对象" min-width="220" />
        <el-table-column prop="visitor" label="访客" width="180" />
      </el-table>
    </el-card>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>热门产品（按浏览量）</span>
          <span class="expire">
            会员到期时间：
            <el-tag v-if="overview.expireTime" type="warning">{{ overview.expireTime }}</el-tag>
            <el-tag v-else type="success">免费入驻期</el-tag>
          </span>
        </div>
      </template>

      <el-table :data="hotProducts" border>
        <el-table-column prop="name" label="产品名称" min-width="180" />
        <el-table-column prop="categoryName" label="品类" width="110" />
        <el-table-column prop="brand" label="品牌" width="110" />
        <el-table-column label="价格" width="110">
          <template #default="{ row }">
            <span v-if="row.price != null">{{ row.price }}{{ row.unit ? '/' + row.unit : '' }}</span>
            <span v-else style="color: #c0c4cc">面议</span>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="被浏览" width="90" />
        <el-table-column prop="forwards" label="被转发" width="90" />
        <el-table-column prop="calls" label="被电话联系" width="110" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '@/api/request'

const cards = [
  { label: '产品总数', key: 'productTotal' },
  { label: '在售产品', key: 'productOnSale' },
  { label: '待审核', key: 'productPending' },
  { label: '被浏览', key: 'viewTotal' },
  { label: '被转发', key: 'forwardTotal' },
  { label: '被电话联系', key: 'callTotal' },
  { label: '全场曝光', key: 'merchantViews' }
]

const overview = ref({})
const hotProducts = ref([])
const records = ref([])

onMounted(() => {
  loadOverview()
  loadHot()
  loadRecords()
})

async function loadOverview() {
  const res = await request.get('/merchant/stat/overview')
  overview.value = res.data
  overview.value.expireTime = res.data.expireTime || ''
}

async function loadHot() {
  const hot = await request.get('/merchant/stat/hot')
  hotProducts.value = hot.data
}

async function loadRecords() {
  const res = await request.get('/merchant/stat/records', { params: { limit: 20 } })
  records.value = res.data
}

function formatTime(t) {
  if (!t) return '-'
  return String(t).replace('T', ' ').slice(0, 19)
}

function tagType(action) {
  if (action === '电话联系') return 'danger'
  if (action === '转发') return 'warning'
  return 'success'
}
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.label {
  color: #909399;
  font-size: 13px;
}
.value {
  margin-top: 8px;
  font-size: 26px;
  font-weight: bold;
  color: #409eff;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.expire {
  font-size: 13px;
  color: #606266;
}
</style>
