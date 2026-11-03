<template>
  <el-card>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="商户名称">
        <el-input v-model="query.name" placeholder="名称关键词" clearable />
      </el-form-item>
      <el-form-item label="所属市场">
        <el-select v-model="query.marketId" placeholder="全部市场" clearable filterable style="width: 220px">
          <el-option v-for="m in markets" :key="m.id" :label="m.name" :value="m.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openAdd">新增商户</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="name" label="商户名称" min-width="170" show-overflow-tooltip />
      <el-table-column label="联系人 / 电话" width="140">
        <template #default="{ row }">
          <div>{{ row.contactName || '-' }}</div>
          <div class="cell-sub">{{ row.phone }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="market" label="所属市场" min-width="150" show-overflow-tooltip />
      <el-table-column prop="categoryName" label="主营品类" width="96" />
      <el-table-column prop="productCount" label="产品数" width="70" />
      <el-table-column label="状态" width="86">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="150" :formatter="formatTime" />
      <el-table-column label="操作" width="184" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="success" link @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.status === 0" type="danger" link @click="audit(row, 2)">拒绝</el-button>
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该商户？" @confirm="remove(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑商户' : '新增商户'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="商户名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="电话"><el-input v-model="form.phone" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="所属市场">
          <el-select v-model="form.marketId" placeholder="请选择建材市场" clearable filterable style="width: 100%">
            <el-option v-for="m in markets" :key="m.id" :label="m.name" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="省份"><el-input v-model="form.province" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="城市"><el-input v-model="form.city" /></el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="详细地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="主营品类">
          <el-select v-model="form.categoryId" clearable style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.label" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.intro" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { categorySelectOptions } from '@/utils/category'

const query = reactive({ pageNum: 1, pageSize: 10, name: '', marketId: null, status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const categories = ref([])
const markets = ref([])
const dialogVisible = ref(false)
const form = ref({})

const statusText = (s) => ({ 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const formatTime = (row, column, value) => (value || '').replace('T', ' ').slice(0, 16)

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/merchant/page', { params: query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.name = ''
  query.marketId = null
  query.status = null
  query.pageNum = 1
  load()
}

const openAdd = () => {
  form.value = { name: '', contactName: '', phone: '', marketId: null, market: '', province: '', city: '', address: '', categoryId: null, intro: '' }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const save = async () => {
  await request.post('/admin/merchant/save', form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const audit = async (row, status) => {
  await request.post('/admin/merchant/audit', { id: row.id, status })
  ElMessage.success(status === 1 ? '已通过，商户账号已开通（账号为手机号，初始密码 123456）' : '已拒绝')
  load()
}

const remove = async (row) => {
  await request.post(`/admin/merchant/remove/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

const loadCategories = async () => {
  const res = await request.get('/admin/category/list')
  categories.value = categorySelectOptions(res.data)
}

const loadMarkets = async () => {
  const res = await request.get('/admin/market/options')
  markets.value = res.data
}

onMounted(() => {
  load()
  loadCategories()
  loadMarkets()
})
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
