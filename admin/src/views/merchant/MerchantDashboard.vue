<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ overview[card.key] ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>热门产品（按浏览量）</span>
          <span class="expire">
            会员到期时间：
            <el-tag v-if="overview.expireTime" type="warning">{{ overview.expireTime }}</el-tag>
            <el-tag v-else type="info">未生效（365 元 / 年）</el-tag>
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
  { label: '门店浏览', key: 'viewTotal' },
  { label: '门店转发', key: 'forwardTotal' },
  { label: '全场曝光', key: 'merchantViews' }
]

const overview = ref({})
const hotProducts = ref([])

onMounted(async () => {
  const res = await request.get('/merchant/stat/overview')
  overview.value = res.data
  overview.value.expireTime = res.data.expireTime || ''

  const hot = await request.get('/merchant/stat/hot')
  hotProducts.value = hot.data
})
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
