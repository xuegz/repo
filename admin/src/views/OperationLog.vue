<template>
  <el-card>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="操作类型">
        <el-select v-model="query.action" placeholder="全部" clearable style="width: 150px">
          <el-option v-for="item in actionOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人">
        <el-input v-model="query.username" placeholder="账号" clearable style="width: 150px" />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="createTime" label="时间" width="160" :formatter="formatTime" />
      <el-table-column label="操作人" width="150">
        <template #default="{ row }">
          <div>{{ row.realName || '-' }}</div>
          <div class="cell-sub">{{ row.username || '-' }}</div>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="100">
        <template #default="{ row }">
          <el-tag :type="row.role === 1 ? 'primary' : 'success'" effect="plain">
            {{ row.role === 1 ? '平台管理员' : '商户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作类型" width="120">
        <template #default="{ row }">{{ actionText(row.action) }}</template>
      </el-table-column>
      <el-table-column prop="detail" label="操作内容" min-width="260" show-overflow-tooltip />
      <el-table-column prop="ip" label="IP" width="130" show-overflow-tooltip />
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
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import request from '@/api/request'

const actionOptions = [
  { value: 'merchant_audit', label: '商户审核' },
  { value: 'merchant_reset_password', label: '重置密码' },
  { value: 'product_audit', label: '产品审核' }
]
const query = reactive({ pageNum: 1, pageSize: 10, action: '', username: '' })
const dateRange = ref([])
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

const actionText = (code) => (actionOptions.find((item) => item.value === code) || {}).label || code
const formatTime = (row, column, value) => (value || '').replace('T', ' ').slice(0, 19)

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/log/page', {
      params: {
        ...query,
        start: dateRange.value && dateRange.value[0],
        end: dateRange.value && dateRange.value[1]
      }
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
}

const reset = () => {
  query.action = ''
  query.username = ''
  query.pageNum = 1
  dateRange.value = []
  load()
}

onMounted(load)
</script>

<style scoped>
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
</style>
