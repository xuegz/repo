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
        <div class="card-header">
          <span>业务趋势</span>
          <el-radio-group v-model="days" size="small" @change="loadTrend">
            <el-radio-button :value="7">近 7 天</el-radio-button>
            <el-radio-button :value="30">近 30 天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div class="legend">
        <span><i class="dot views"></i>浏览</span>
        <span><i class="dot inquiries"></i>询价</span>
        <span><i class="dot users"></i>新增用户</span>
        <span class="legend-tip">悬停查看当日明细</span>
      </div>
      <div class="chart" v-loading="trendLoading">
        <div v-for="(item, index) in trend" :key="item.date" class="chart-col">
          <div class="bars">
            <el-tooltip
              :content="`${item.date}｜浏览 ${item.views}｜询价 ${item.inquiries}｜新增用户 ${item.newUsers}`"
              placement="top"
            >
              <div class="bar-group">
                <div class="bar views" :style="{ height: barHeight(item.views, 'views') }"></div>
                <div class="bar inquiries" :style="{ height: barHeight(item.inquiries, 'inquiries') }"></div>
                <div class="bar users" :style="{ height: barHeight(item.newUsers, 'users') }"></div>
              </div>
            </el-tooltip>
          </div>
          <div class="chart-label">{{ label(item.date, index) }}</div>
        </div>
      </div>
    </el-card>

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
import { computed, onMounted, ref } from 'vue'
import request from '@/api/request'

const cards = [
  { label: '商户总数', key: 'merchantTotal' },
  { label: '待审商户', key: 'merchantPending' },
  { label: '用户总数', key: 'userTotal' },
  { label: '产品总数', key: 'productTotal' },
  { label: '待审产品', key: 'productPending' },
  { label: '浏览总量', key: 'viewTotal' },
  { label: '转发总量', key: 'forwardTotal' },
  { label: '电话联系', key: 'callTotal' }
]

const overview = ref({})
const trend = ref([])
const days = ref(7)
const trendLoading = ref(false)

const maxValues = computed(() => {
  const max = { views: 1, inquiries: 1, users: 1 }
  trend.value.forEach((item) => {
    max.views = Math.max(max.views, Number(item.views) || 0)
    max.inquiries = Math.max(max.inquiries, Number(item.inquiries) || 0)
    max.users = Math.max(max.users, Number(item.newUsers) || 0)
  })
  return max
})

const barHeight = (value, key) => {
  const number = Number(value) || 0
  if (number <= 0) {
    return '0%'
  }
  return `${Math.max(6, Math.round((number / maxValues.value[key]) * 100))}%`
}

const label = (date, index) => {
  const text = (date || '').slice(5)
  if (days.value === 30 && index % 5 !== 4) {
    return ''
  }
  return text
}

const load = async () => {
  const res = await request.get('/admin/stat/overview')
  overview.value = res.data
}

const loadTrend = async () => {
  trendLoading.value = true
  try {
    const res = await request.get('/admin/stat/trend', { params: { days: days.value } })
    trend.value = res.data || []
  } finally {
    trendLoading.value = false
  }
}

onMounted(() => {
  load()
  loadTrend()
})
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
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.legend {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 12px;
  color: #606266;
  margin-bottom: 12px;
}
.dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}
.dot.views {
  background: #409eff;
}
.dot.inquiries {
  background: #67c23a;
}
.dot.users {
  background: #e6a23c;
}
.legend-tip {
  color: #c0c4cc;
}
.chart {
  display: flex;
  align-items: flex-end;
  gap: 6px;
  height: 220px;
  padding: 8px 4px 0;
  border-bottom: 1px solid #ebeef5;
  overflow-x: auto;
}
.chart-col {
  flex: 1;
  min-width: 18px;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}
.bars {
  height: 190px;
  display: flex;
  align-items: flex-end;
}
.bar-group {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 2px;
  cursor: default;
}
.bar {
  width: 6px;
  border-radius: 2px 2px 0 0;
  transition: height 0.3s;
}
.bar.views {
  background: #409eff;
}
.bar.inquiries {
  background: #67c23a;
}
.bar.users {
  background: #e6a23c;
}
.chart-label {
  height: 18px;
  line-height: 18px;
  font-size: 11px;
  color: #909399;
  text-align: center;
  white-space: nowrap;
}
</style>
