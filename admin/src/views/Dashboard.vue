<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :xs="12" :sm="8" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ overview[card.key] ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>
        <span>平台说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="入驻收费">当前免费入驻；会员收费（365 元 / 年）能力已预留，暂未开通</el-descriptions-item>
        <el-descriptions-item label="核心品类">卫浴、防水、油漆涂料、管道、瓷砖、板材、灯具、五金</el-descriptions-item>
        <el-descriptions-item label="平台价值">同行调货 · 跨区域价格对比 · 厂家找渠道 · 商户找货源</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import request from '@/api/request'

const cards = [
  { label: '商户总数', key: 'merchantTotal' },
  { label: '待审商户', key: 'merchantPending' },
  { label: '产品总数', key: 'productTotal' },
  { label: '待审产品', key: 'productPending' },
  { label: '浏览总量', key: 'viewTotal' },
  { label: '转发总量', key: 'forwardTotal' },
  { label: '电话联系', key: 'callTotal' }
]

const overview = ref({})

const load = async () => {
  const res = await request.get('/admin/stat/overview')
  overview.value = res.data
}

onMounted(load)
</script>

<style scoped>
.stat-card {
  text-align: center;
  margin-bottom: 16px;
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
</style>
