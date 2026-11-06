<template>
  <div>
    <el-row :gutter="16">
      <el-col v-for="card in cards" :key="card.key" :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="label">{{ card.label }}</div>
          <div class="value">{{ overview[card.key] ?? 0 }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="用户">
          <el-input
            v-model="query.keyword"
            placeholder="昵称 / OpenID"
            clearable
            style="width: 240px"
            @keyup.enter="search"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button :loading="exporting" @click="exportCsv">导出</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="用户" min-width="190">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="38" :src="row.avatar || undefined">
                <el-icon><UserFilled /></el-icon>
              </el-avatar>
              <div class="user-info">
                <div class="nickname">{{ row.nickname || '未设置昵称' }}</div>
                <div class="cell-sub">{{ row.nickname ? '微信用户' : '仅登录，未完善资料' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="openid" label="OpenID" min-width="260" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="首次登录" width="170" :formatter="formatTime" />
        <el-table-column prop="lastLoginTime" label="最近登录" width="170" :formatter="formatTime" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
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

    <el-drawer v-model="drawerVisible" title="用户详情" size="720px">
      <div v-loading="detailLoading">
        <div class="detail-head" v-if="detail.user">
          <el-avatar :size="56" :src="detail.user.avatar || undefined">
            <el-icon><UserFilled /></el-icon>
          </el-avatar>
          <div class="head-info">
            <div class="head-name">{{ detail.user.nickname || '未设置昵称' }}</div>
            <div class="cell-sub">OpenID：{{ detail.user.openid }}</div>
            <div class="cell-sub">首次登录：{{ formatTime(null, null, detail.user.createTime) }}</div>
          </div>
        </div>

        <el-row :gutter="12" class="count-row">
          <el-col v-for="item in detailCards" :key="item.key" :span="4">
            <el-statistic :title="item.label" :value="detail.counts?.[item.key] ?? 0" />
          </el-col>
          <el-col :span="4">
            <div class="stat-plain">
              <div class="label">最近登录</div>
              <div class="plain-value">{{ formatTime(null, null, detail.user?.lastLoginTime) || '-' }}</div>
            </div>
          </el-col>
        </el-row>

        <el-form label-width="60px" class="remark-form">
          <el-form-item label="备注">
            <el-input v-model="remark" maxlength="200" show-word-limit placeholder="仅平台内部可见" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="remarkSaving" @click="saveRemark">保存备注</el-button>
          </el-form-item>
        </el-form>

        <el-tabs>
          <el-tab-pane label="浏览">
            <el-table :data="detail.recentViews" size="small" border>
              <el-table-column prop="action" label="行为" width="70" />
              <el-table-column prop="targetName" label="对象" min-width="200" />
              <el-table-column prop="createTime" label="时间" width="170" :formatter="formatTime" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="收藏">
            <el-table :data="detail.recentFavorites" size="small" border>
              <el-table-column prop="action" label="行为" width="70" />
              <el-table-column prop="targetName" label="对象" min-width="200" />
              <el-table-column prop="createTime" label="时间" width="170" :formatter="formatTime" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="电话">
            <el-table :data="detail.recentCalls" size="small" border>
              <el-table-column prop="action" label="行为" width="70" />
              <el-table-column prop="targetName" label="对象" min-width="200" />
              <el-table-column prop="createTime" label="时间" width="170" :formatter="formatTime" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="询价">
            <el-table :data="detail.recentInquiries" size="small" border>
              <el-table-column prop="productName" label="商品" min-width="160" />
              <el-table-column prop="merchantName" label="商户" min-width="140" />
              <el-table-column prop="status" label="状态" width="80" />
              <el-table-column prop="quoteTotal" label="报价" width="90" />
              <el-table-column prop="createTime" label="时间" width="170" :formatter="formatTime" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const cards = [
  { label: '用户总数', key: 'userTotal' },
  { label: '今日新增', key: 'userToday' },
  { label: '今日活跃', key: 'userActiveToday' },
  { label: '近 7 日活跃', key: 'userActive7d' }
]

const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const overview = ref({})
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const exporting = ref(false)
const drawerVisible = ref(false)
const detailLoading = ref(false)
const remarkSaving = ref(false)
const remark = ref('')
const detail = reactive({
  user: {},
  counts: {},
  recentViews: [],
  recentFavorites: [],
  recentCalls: [],
  recentInquiries: []
})
const detailCards = [
  { label: '浏览', key: 'views' },
  { label: '转发', key: 'forwards' },
  { label: '电话', key: 'calls' },
  { label: '收藏', key: 'favorites' },
  { label: '询价', key: 'inquiries' }
]

const formatTime = (row, column, value) => (value || '').replace('T', ' ').slice(0, 19) || '-'

const loadOverview = async () => {
  const res = await request.get('/admin/user/overview')
  overview.value = res.data || {}
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/user/page', { params: query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const search = () => {
  query.pageNum = 1
  load()
}

const reset = () => {
  query.keyword = ''
  query.pageNum = 1
  load()
}

const openDetail = async (row) => {
  drawerVisible.value = true
  detailLoading.value = true
  try {
    const res = await request.get('/admin/user/detail', { params: { openid: row.openid } })
    Object.assign(detail, res.data)
    remark.value = res.data.user?.remark || ''
  } finally {
    detailLoading.value = false
  }
}

const saveRemark = async () => {
  remarkSaving.value = true
  try {
    await request.post('/admin/user/remark', { openid: detail.user.openid, remark: remark.value })
    detail.user.remark = remark.value
    ElMessage.success('备注已保存')
    load()
  } finally {
    remarkSaving.value = false
  }
}

const exportCsv = async () => {
  exporting.value = true
  try {
    const res = await axios.get('/api/admin/user/export', {
      params: { keyword: query.keyword || undefined },
      responseType: 'blob',
      headers: { Authorization: localStorage.getItem('token') }
    })
    const url = URL.createObjectURL(res.data)
    const link = document.createElement('a')
    link.href = url
    link.download = `小程序用户-${new Date().toISOString().slice(0, 10)}.csv`
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
  color: #409eff;
}
.search-bar {
  margin-bottom: 8px;
}
.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}
.user-info {
  min-width: 0;
}
.nickname {
  font-weight: 500;
  line-height: 1.4;
}
.cell-sub {
  color: #909399;
  font-size: 12px;
  line-height: 1.4;
}
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
.detail-head {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-bottom: 16px;
}
.head-name {
  font-size: 18px;
  font-weight: 600;
  line-height: 1.6;
}
.count-row {
  padding: 12px 0;
  border-top: 1px solid #f0f2f5;
  border-bottom: 1px solid #f0f2f5;
}
.stat-plain {
  text-align: center;
}
.plain-value {
  margin-top: 6px;
  font-size: 14px;
  color: #303133;
  line-height: 1.4;
}
.remark-form {
  margin-top: 16px;
}
</style>
