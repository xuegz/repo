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
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDetail(row)">详情</el-button>
          <el-button v-if="row.status === 0" type="success" link @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.status === 0" type="danger" link @click="audit(row, 2)">拒绝</el-button>
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 1" type="warning" link @click="resetPassword(row)">重置密码</el-button>
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

    <el-drawer v-model="detailVisible" :title="`商户详情 - ${detail.merchant?.name || ''}`" size="760px">
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="状态">
            <el-tag :type="statusType(detail.merchant?.status)">{{ statusText(detail.merchant?.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="所属市场">{{ detail.merchant?.market || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系人">{{ detail.merchant?.contactName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="电话">{{ detail.merchant?.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="地址" :span="2">
            {{ [detail.merchant?.province, detail.merchant?.city, detail.merchant?.address].filter(Boolean).join(' ') || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="登录账号">{{ detail.account?.username || '未开通' }}</el-descriptions-item>
          <el-descriptions-item label="账号状态">
            <el-tag v-if="detail.account" :type="detail.account.status === 1 ? 'success' : 'danger'">
              {{ detail.account.status === 1 ? '正常' : '禁用' }}
            </el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="到期时间">{{ formatDate(detail.stats?.expireTime) }}</el-descriptions-item>
          <el-descriptions-item label="入驻时间">{{ formatTime(null, null, detail.merchant?.createTime) }}</el-descriptions-item>
        </el-descriptions>

        <el-row :gutter="12" class="detail-stats">
          <el-col v-for="item in detailCards" :key="item.label" :span="6">
            <el-card shadow="never" class="mini-card">
              <div class="mini-label">{{ item.label }}</div>
              <div class="mini-value">{{ item.value }}</div>
            </el-card>
          </el-col>
        </el-row>

        <el-tabs>
          <el-tab-pane label="热门产品">
            <el-table :data="detail.hotProducts" size="small" border>
              <el-table-column prop="name" label="产品" min-width="180" show-overflow-tooltip />
              <el-table-column prop="price" label="价格" width="100" />
              <el-table-column prop="views" label="浏览" width="80" />
              <el-table-column prop="calls" label="电话" width="80" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="最近询价">
            <el-table :data="detail.recentInquiries" size="small" border>
              <el-table-column prop="productName" label="商品" min-width="150" />
              <el-table-column prop="contact" label="联系方式" width="120" />
              <el-table-column label="状态" width="80">
                <template #default="{ row }">{{ inquiryStatus(row.status) }}</template>
              </el-table-column>
              <el-table-column prop="createTime" label="时间" width="160" :formatter="formatTime" />
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="最近动态">
            <el-table :data="detail.recentRecords" size="small" border>
              <el-table-column prop="action" label="行为" width="90" />
              <el-table-column prop="targetName" label="对象" min-width="180" />
              <el-table-column prop="visitor" label="访客" width="120" />
              <el-table-column prop="createTime" label="时间" width="160" :formatter="formatTime" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { computed, h, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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
const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = reactive({
  merchant: {},
  stats: {},
  inquiryCounts: {},
  account: null,
  hotProducts: [],
  recentInquiries: [],
  recentRecords: []
})

const detailCards = computed(() => [
  { label: '产品总数', value: detail.stats?.productTotal ?? 0 },
  { label: '浏览总量', value: detail.stats?.viewTotal ?? 0 },
  { label: '电话联系', value: detail.stats?.callTotal ?? 0 },
  { label: '询价线索', value: detail.inquiryCounts?.total ?? 0 },
  { label: '待报价', value: detail.inquiryCounts?.pending ?? 0 },
  { label: '已报价', value: detail.inquiryCounts?.quoted ?? 0 },
  { label: '跟进中', value: detail.inquiryCounts?.follow ?? 0 },
  { label: '转发总量', value: detail.stats?.forwardTotal ?? 0 }
])

const statusText = (s) => ({ 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')
const formatTime = (row, column, value) => (value || '').replace('T', ' ').slice(0, 16)
const formatDate = (value) => (value ? String(value).replace('T', ' ').slice(0, 10) : '-')
const inquiryStatus = (status) => ({ 0: '待报价', 1: '已报价', 2: '跟进中' }[status] || '未知')

const openDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  try {
    const res = await request.get(`/admin/merchant/detail/${row.id}`)
    Object.assign(detail, res.data)
  } finally {
    detailLoading.value = false
  }
}

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
  ElMessage.success(status === 1 ? '已通过，商户账号已开通（账号为手机号，初始密码 123456，首次登录需修改）' : '已拒绝')
  load()
}

const resetPassword = async (row) => {
  const confirmed = await ElMessageBox.confirm(`确定重置「${row.name}」的登录密码？`, '重置密码', { type: 'warning' })
    .then(() => true)
    .catch(() => false)
  if (!confirmed) {
    return
  }
  const res = await request.post('/admin/merchant/reset-password', { merchantId: row.id })
  ElMessageBox.alert(
    h('div', [
      h('div', `账号：${res.data.username}`),
      h('div', ['临时密码：', h('b', res.data.password)]),
      h('div', { style: 'color:#909399;font-size:12px;margin-top:6px' }, '请告知商户，首次登录后需修改密码')
    ]),
    '密码已重置',
    { type: 'success' }
  )
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
.detail-stats {
  margin: 16px 0 4px;
}
.mini-card {
  text-align: center;
  margin-bottom: 12px;
  background: #fafbfc;
}
.mini-label {
  color: #909399;
  font-size: 12px;
}
.mini-value {
  margin-top: 6px;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-color-primary);
}
</style>
