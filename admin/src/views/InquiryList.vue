<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ overview[card.key] ?? 0 }}{{ card.suffix || '' }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="联系方式 / 需求 / OpenID"
            clearable
            style="width: 220px"
            @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item label="商户">
          <el-input v-model="query.merchantName" placeholder="商户名称" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待报价" :value="0" />
            <el-option label="已报价" :value="1" />
            <el-option label="跟进中" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="提交时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button :loading="exporting" @click="exportCsv">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="detail-box">
              <el-descriptions :column="2" border size="small">
                <el-descriptions-item label="需求说明">{{ row.remark || '-' }}</el-descriptions-item>
                <el-descriptions-item label="期望到货">{{ row.expectDate || '-' }}</el-descriptions-item>
                <el-descriptions-item label="报价单价">{{ money(row.quotePrice) }}</el-descriptions-item>
                <el-descriptions-item label="报价总价">{{ money(row.quoteTotal) }}</el-descriptions-item>
                <el-descriptions-item label="报价说明">{{ row.quoteRemark || '-' }}</el-descriptions-item>
                <el-descriptions-item label="报价有效期">{{ row.quoteValidUntil || '-' }}</el-descriptions-item>
                <el-descriptions-item label="报价时间">{{ formatTime(row.quoteTime) }}</el-descriptions-item>
                <el-descriptions-item label="用户 OpenID">{{ row.openid || '-' }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="商品" min-width="190">
          <template #default="{ row }">
            <div class="product-cell">
              <el-image
                v-if="row.productImage"
                :src="row.productImage"
                fit="cover"
                class="thumb"
                :preview-src-list="[row.productImage]"
                preview-teleported
              />
              <span>{{ row.productName || '已删除产品' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="商户" min-width="150">
          <template #default="{ row }">
            <div>{{ row.merchantName || '未知商户' }}</div>
            <div class="cell-sub">{{ row.merchantPhone || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="130">
          <template #default="{ row }">
            <div>{{ row.userNickname || '未设置昵称' }}</div>
            <div class="cell-sub">{{ shortOpenid(row.openid) }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="contact" label="联系方式" width="120" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="报价" width="120">
          <template #default="{ row }">
            <span v-if="row.quoteTotal || row.quotePrice">{{ money(row.quoteTotal || row.quotePrice) }}</span>
            <span v-else class="cell-sub">待报价</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="160" :formatter="formatTime" />
      </el-table>

      <el-pagination
        class="pager"
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        v-model:current-page="query.pageNum"
        v-model:page-size="query.pageSize"
        @current-change="load"
        @size-change="load"
      />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const cards = [
  { label: '询价总数', key: 'total' },
  { label: '待报价', key: 'pending' },
  { label: '已报价', key: 'quoted' },
  { label: '今日新增', key: 'today' }
]

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  merchantName: '',
  status: null
})
const dateRange = ref([])
const overview = ref({})
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const exporting = ref(false)

const statusText = (s) => ({ 0: '待报价', 1: '已报价', 2: '跟进中' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'primary' }[s] || 'info')
const formatTime = (row, column, value) => (value || '').replace('T', ' ').slice(0, 19) || '-'
const money = (value) => (value === null || value === undefined || value === '' ? '-' : `¥${value}`)
const shortOpenid = (value) => (value && value.length > 12 ? `${value.slice(0, 4)}***${value.slice(-4)}` : value || '-')

const params = () => ({
  keyword: query.keyword || undefined,
  merchantName: query.merchantName || undefined,
  status: query.status === null ? undefined : query.status,
  startDate: dateRange.value?.[0] || undefined,
  endDate: dateRange.value?.[1] || undefined
})

const loadOverview = async () => {
  const res = await request.get('/admin/inquiry/overview')
  overview.value = res.data || {}
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/inquiry/page', {
      params: { pageNum: query.pageNum, pageSize: query.pageSize, ...params() }
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  load()
  loadOverview()
}

const reset = () => {
  query.keyword = ''
  query.merchantName = ''
  query.status = null
  dateRange.value = []
  query.pageNum = 1
  load()
  loadOverview()
}

const exportCsv = async () => {
  exporting.value = true
  try {
    const res = await axios.get('/api/admin/inquiry/export', {
      params: params(),
      responseType: 'blob',
      headers: { Authorization: localStorage.getItem('token') }
    })
    const url = URL.createObjectURL(res.data)
    const link = document.createElement('a')
    link.href = url
    link.download = `询价线索-${new Date().toISOString().slice(0, 10)}.csv`
    link.click()
    URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } finally {
    exporting.value = false
  }
}

onMounted(() => {
  loadOverview()
  load()
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
  color: var(--el-color-primary);
}
.search-bar {
  margin-bottom: 8px;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.cell-sub {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}
.product-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.thumb {
  width: 44px;
  height: 33px;
  border-radius: 4px;
  flex-shrink: 0;
}
.detail-box {
  padding: 4px 16px 8px;
}
</style>
