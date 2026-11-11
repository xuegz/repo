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
        <el-button type="warning" :loading="batchAuditing" @click="auditPending">一键通过待审核</el-button>
        <el-button @click="openConfig">会员设置</el-button>
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
      <el-table-column label="会员" width="126">
        <template #default="{ row }">
          <el-tag v-if="!row.expireTime" type="info">未开通</el-tag>
          <el-tag v-else-if="isExpired(row.expireTime)" type="danger">已到期</el-tag>
          <el-tag v-else type="success">{{ formatDate(row.expireTime) }} 到期</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="邀请" width="140">
        <template #default="{ row }">
          <div v-if="row.inviterName" class="cell-sub">邀请人：{{ row.inviterName }}</div>
          <div v-if="row.inviteCount" class="cell-sub">已邀请 {{ row.inviteCount }} 家</div>
          <span v-if="!row.inviterName && !row.inviteCount">-</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="150" :formatter="formatTime" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openDetail(row)">详情</el-button>
          <el-button v-if="row.status === 0" type="success" link @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.status === 0" type="danger" link @click="audit(row, 2)">拒绝</el-button>
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-button v-if="row.status === 1" type="warning" link @click="openMembership(row)">会员</el-button>
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

    <el-dialog v-model="configVisible" title="会员设置" width="480px">
      <el-form label-width="110px">
        <el-form-item label="赠送有效期">
          <el-input-number v-model="configForm.months" :min="1" :max="120" />
          <span class="member-hint">个月</span>
        </el-form-item>
        <el-form-item label="同步存量商户">
          <el-switch v-model="configForm.applyExisting" />
          <span class="member-hint">仅未开通 / 已到期的已通过商户</span>
        </el-form-item>
        <el-form-item label="说明">
          <div class="member-hint">
            推广期审核通过、后台代建档时按此月数赠送会员；保存只影响之后新开通的商户，开启同步会立即应用到符合条件的存量商户。
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configVisible = false">取消</el-button>
        <el-button type="primary" :loading="configLoading" @click="saveConfig">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="memberVisible" title="开通 / 续费会员" width="480px">
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="商户">{{ memberForm.name }}</el-descriptions-item>
        <el-descriptions-item label="当前会员">
          <el-tag v-if="!memberForm.expireTime" type="info">未开通</el-tag>
          <el-tag v-else-if="isExpired(memberForm.expireTime)" type="danger">已到期（{{ formatDate(memberForm.expireTime) }}）</el-tag>
          <el-tag v-else type="success">有效期至 {{ formatDate(memberForm.expireTime) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-form label-width="90px" style="margin-top: 16px">
        <el-form-item label="开通年限">
          <el-input-number v-model="memberForm.years" :min="1" :max="5" />
        </el-form-item>
        <el-form-item label="应收金额">
          <span class="member-amount">¥ {{ memberForm.years * 365 }}</span>
          <span class="member-hint">线下收款，确认后生成缴费记录</span>
        </el-form-item>
        <el-form-item label="新到期时间">
          <span>{{ memberPreview }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberVisible = false">取消</el-button>
        <el-button type="primary" :loading="memberLoading" @click="submitMembership">确认开通</el-button>
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
          <el-descriptions-item label="会员到期">{{ formatDate(detail.stats?.expireTime) }}</el-descriptions-item>
          <el-descriptions-item label="入驻时间">{{ formatTime(null, null, detail.merchant?.createTime) }}</el-descriptions-item>
          <el-descriptions-item label="邀请码">{{ detail.inviteCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邀请人">{{ detail.inviter?.name || '-' }}</el-descriptions-item>
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
          <el-tab-pane :label="`邀请入驻 (${detail.invited?.length || 0})`">
            <el-table :data="detail.invited" size="small" border>
              <el-table-column prop="name" label="被邀请商户" min-width="180" show-overflow-tooltip />
              <el-table-column prop="phone" label="电话" width="130" />
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="inviteTime" label="邀请时间" width="160" :formatter="formatTime" />
              <el-table-column prop="createTime" label="入驻时间" width="160" :formatter="formatTime" />
            </el-table>
            <div v-if="!detail.invited?.length" class="detail-empty">该商户还没有邀请同行入驻</div>
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
const batchAuditing = ref(false)
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
  recentRecords: [],
  inviteCode: '',
  inviter: null,
  invited: []
})

const memberVisible = ref(false)
const memberLoading = ref(false)
const memberForm = reactive({ id: null, name: '', expireTime: null, years: 1 })
const configVisible = ref(false)
const configLoading = ref(false)
const configForm = reactive({ months: 12, applyExisting: false })

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
const isExpired = (value) => !!value && new Date(String(value)).getTime() <= Date.now()
const inquiryStatus = (status) => ({ 0: '待报价', 1: '已报价', 2: '跟进中' }[status] || '未知')

const openDetail = async (row) => {
  detailVisible.value = true
  detailLoading.value = true
  Object.assign(detail, {
    merchant: {},
    stats: {},
    inquiryCounts: {},
    account: null,
    hotProducts: [],
    recentInquiries: [],
    recentRecords: [],
    inviteCode: '',
    inviter: null,
    invited: []
  })
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
  ElMessage.success(status === 1 ? '已通过，商户账号已开通（账号为手机号，初始密码 123456，首次登录需修改）；推广期已按会员设置赠送会员' : '已拒绝')
  load()
}

const auditPending = async () => {
  const confirmed = await ElMessageBox.confirm(
    '将通过全部待审核商户，并为每家开通账号（账号为手机号，初始密码 123456，首次登录需修改）并赠送推广期会员。没有填写联系电话的商户会被跳过。确定继续？',
    '一键通过待审核',
    { type: 'warning', confirmButtonText: '确定通过' }
  ).then(() => true).catch(() => false)
  if (!confirmed) {
    return
  }
  batchAuditing.value = true
  try {
    const res = await request.post('/admin/merchant/audit-pending')
    const { total = 0, approved = 0, skipped = [] } = res.data || {}
    if (!total) {
      ElMessage.info('当前没有待审核商户')
    } else if (skipped.length) {
      ElMessageBox.alert(
        h('div', [
          h('div', `共 ${total} 家待审核，已通过 ${approved} 家。`),
          h('div', { style: 'margin-top:6px' }, `跳过 ${skipped.length} 家（缺少手机号或账号冲突）：`),
          h('div', { style: 'color:#909399;font-size:12px;margin-top:4px' }, skipped.join('、'))
        ]),
        '批量审核完成',
        { type: 'warning' }
      )
    } else {
      ElMessage.success(`已通过 ${approved} 家商户，账号均为手机号 + 初始密码 123456`)
    }
    load()
  } finally {
    batchAuditing.value = false
  }
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

const memberPreview = computed(() => {
  const now = new Date()
  let base = now
  if (memberForm.expireTime) {
    const current = new Date(String(memberForm.expireTime))
    if (current.getTime() > now.getTime()) {
      base = current
    }
  }
  const next = new Date(base.getTime())
  next.setFullYear(next.getFullYear() + (memberForm.years || 1))
  const pad = (n) => String(n).padStart(2, '0')
  return `${next.getFullYear()}-${pad(next.getMonth() + 1)}-${pad(next.getDate())}`
})

const loadConfig = async () => {
  try {
    const res = await request.get('/admin/merchant/config')
    configForm.months = Number(res.data?.membershipMonths || 12)
  } catch (e) {
    // 读取失败沿用默认值
  }
}

const openConfig = () => {
  configForm.applyExisting = false
  configVisible.value = true
}

const saveConfig = async () => {
  configLoading.value = true
  try {
    const res = await request.post('/admin/merchant/config', {
      membershipMonths: configForm.months,
      applyExisting: configForm.applyExisting
    })
    const affected = Number(res.data?.affected || 0)
    ElMessage.success(`已保存：会员有效期 ${res.data?.membershipMonths} 个月` + (affected > 0 ? `，同步 ${affected} 家商户` : ''))
    configVisible.value = false
    load()
  } finally {
    configLoading.value = false
  }
}

const openMembership = (row) => {
  memberForm.id = row.id
  memberForm.name = row.name
  memberForm.expireTime = row.expireTime || null
  memberForm.years = 1
  memberVisible.value = true
}

const submitMembership = async () => {
  memberLoading.value = true
  try {
    const res = await request.post('/admin/merchant/membership', {
      merchantId: memberForm.id,
      years: memberForm.years
    })
    ElMessage.success(`已开通 ${memberForm.years} 年，到期时间 ${formatDate(res.data.expireTime)}`)
    memberVisible.value = false
    load()
  } finally {
    memberLoading.value = false
  }
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
  loadConfig()
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
.detail-empty {
  padding: 18px 0;
  text-align: center;
  color: #909399;
  font-size: 13px;
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
.member-amount {
  margin-right: 10px;
  font-size: 18px;
  font-weight: 600;
  color: var(--el-color-primary);
}
.member-hint {
  color: #909399;
  font-size: 12px;
}
</style>
