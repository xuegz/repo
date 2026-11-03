<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-radio-group v-model="query.status" @change="reload">
        <el-radio-button :label="-1">
          全部<span class="tab-count" v-if="counts.all">{{ counts.all }}</span>
        </el-radio-button>
        <el-radio-button :label="0">
          待报价<span class="tab-count warn" v-if="counts.pending">{{ counts.pending }}</span>
        </el-radio-button>
        <el-radio-button :label="1">
          已报价<span class="tab-count" v-if="counts.quoted">{{ counts.quoted }}</span>
        </el-radio-button>
        <el-radio-button :label="2">
          跟进中<span class="tab-count" v-if="counts.follow">{{ counts.follow }}</span>
        </el-radio-button>
      </el-radio-group>
      <el-button :icon="Refresh" circle @click="load" />
    </div>

    <el-table :data="tableData" v-loading="loading" row-key="id" class="inquiry-table">
      <el-table-column label="时间" width="104">
        <template #default="{ row }">
          <div class="time-date">{{ datePart(row.createTime) }}</div>
          <div class="time-clock">{{ timePart(row.createTime) }}</div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="84">
        <template #default="{ row }">
          <el-tag :type="statusMeta(row.status).type" effect="light" round>
            {{ statusMeta(row.status).text }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="询价产品" min-width="150">
        <template #default="{ row }">
          <div class="product-cell">
            <el-image
              v-if="row.productImage"
              :src="row.productImage"
              fit="cover"
              preview-teleported
              :preview-src-list="[row.productImage]"
              class="product-img"
            >
              <template #error>
                <img :src="productPlaceholder" class="product-img" />
              </template>
            </el-image>
            <img v-else :src="productPlaceholder" class="product-img" />
            <span class="product-name">{{ row.productName || '通用询价' }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量/规格" width="96" show-overflow-tooltip />
      <el-table-column prop="contact" label="联系方式" width="120" show-overflow-tooltip />
      <el-table-column label="需求说明" min-width="150">
        <template #default="{ row }">
          <div class="remark-cell">
            <div class="remark-text" :title="row.remark || ''">{{ row.remark || '-' }}</div>
            <div class="remark-date">期望到货 {{ row.expectDate || '未填' }}</div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="我的报价" min-width="150">
        <template #default="{ row }">
          <template v-if="row.quotePrice != null || row.quoteTotal != null">
            <div class="quote-main" :class="{ expired: isExpired(row) }">
              <span v-if="row.quotePrice != null">单价 {{ money(row.quotePrice) }}</span>
              <span v-if="row.quoteTotal != null" class="quote-total">总价 {{ money(row.quoteTotal) }}</span>
            </div>
            <div v-if="row.quoteValidUntil" class="quote-sub" :class="{ expired: isExpired(row) }">
              <span v-if="isExpired(row)" class="quote-expired">已过期</span>
              <span v-else>有效期至</span>
              {{ row.quoteValidUntil }}
            </div>
            <div v-if="row.quoteRemark" class="quote-sub" :title="row.quoteRemark">{{ row.quoteRemark }}</div>
          </template>
          <span v-else class="muted">未报价</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="162" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openQuote(row)">
            {{ row.status === 0 ? '报价' : '调整报价' }}
          </el-button>
          <el-button v-if="row.status !== 2" type="info" link @click="handle(row)">跟进</el-button>
          <el-popconfirm title="确定删除该询价线索？" @confirm="remove(row)">
            <template #reference><el-button type="danger" link>删除</el-button></template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      background
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      @current-change="load"
    />

    <el-dialog v-model="dialogVisible" title="提交报价" width="460px">
      <div v-if="currentRow" class="quote-target">
        询价产品：{{ currentRow.productName || '通用询价' }}
        <span v-if="currentRow.quantity" class="muted">（{{ currentRow.quantity }}）</span>
      </div>
      <div v-if="currentRow" class="quote-brief">
        <div class="brief-row">
          <span class="brief-label">联系方式</span>
          <span>{{ currentRow.contact || '-' }}</span>
        </div>
        <div class="brief-row" v-if="currentRow.expectDate">
          <span class="brief-label">期望到货</span>
          <span>{{ currentRow.expectDate }}</span>
        </div>
        <div class="brief-row" v-if="currentRow.remark">
          <span class="brief-label">需求说明</span>
          <span class="brief-text">{{ currentRow.remark }}</span>
        </div>
      </div>
      <el-form :model="quoteForm" label-width="90px">
        <el-form-item label="单价">
          <el-input-number v-model="quoteForm.quotePrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="元" />
        </el-form-item>
        <el-form-item label="总价">
          <el-input-number v-model="quoteForm.quoteTotal" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="元" />
        </el-form-item>
        <el-form-item label="有效期至">
          <el-date-picker
            v-model="quoteForm.quoteValidUntil"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="可不填"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="报价说明">
          <el-input v-model="quoteForm.quoteRemark" type="textarea" :rows="3" maxlength="500" show-word-limit placeholder="如含税、运费、起订量等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitQuote">提交报价</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/api/request'
import productPlaceholder from '@/assets/product-placeholder.jpg'

const STATUS_META = {
  0: { text: '待报价', type: 'warning' },
  1: { text: '已报价', type: 'success' },
  2: { text: '跟进中', type: 'info' }
}

const query = reactive({ pageNum: 1, pageSize: 10, status: -1 })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const counts = ref({ all: 0, pending: 0, quoted: 0, follow: 0 })

const dialogVisible = ref(false)
const submitting = ref(false)
const currentRow = ref(null)
const quoteForm = reactive({ quotePrice: null, quoteTotal: null, quoteValidUntil: '', quoteRemark: '' })

const load = async () => {
  loading.value = true
  try {
    const params = { ...query, status: query.status === -1 ? null : query.status }
    const res = await request.get('/merchant/inquiry/page', { params })
    tableData.value = res.data.records
    total.value = Number(res.data.total)
  } finally {
    loading.value = false
    loadCounts()
  }
}

const loadCounts = async () => {
  try {
    const res = await request.get('/merchant/inquiry/counts')
    counts.value = res.data || counts.value
  } catch (e) {
    // 角标失败不影响列表
  }
}

const reload = () => {
  query.pageNum = 1
  load()
}

const openQuote = (row) => {
  currentRow.value = row
  quoteForm.quotePrice = row.quotePrice != null ? Number(row.quotePrice) : null
  quoteForm.quoteTotal = row.quoteTotal != null ? Number(row.quoteTotal) : null
  quoteForm.quoteValidUntil = row.quoteValidUntil || ''
  quoteForm.quoteRemark = row.quoteRemark || ''
  dialogVisible.value = true
}

const submitQuote = async () => {
  if (quoteForm.quotePrice == null && quoteForm.quoteTotal == null) {
    ElMessage.warning('请至少填写单价或总价')
    return
  }
  submitting.value = true
  try {
    await request.post(`/merchant/inquiry/quote/${currentRow.value.id}`, { ...quoteForm })
    ElMessage.success('报价已提交，客户可在小程序查看')
    dialogVisible.value = false
    load()
  } finally {
    submitting.value = false
  }
}

const handle = async (row) => {
  await request.post(`/merchant/inquiry/handle/${row.id}`)
  ElMessage.success('已标记为跟进中')
  load()
}

const remove = async (row) => {
  await request.post(`/merchant/inquiry/remove/${row.id}`)
  ElMessage.success('已删除')
  load()
}

const statusMeta = (status) => STATUS_META[status] || { text: '未知', type: 'info' }

const money = (v) => `¥${Number(v).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`

const datePart = (t) => (t ? String(t).slice(0, 10) : '-')

const timePart = (t) => (t ? String(t).replace('T', ' ').slice(11, 16) : '')

const localDate = () => {
  const d = new Date()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}-${m}-${day}`
}

const isExpired = (row) => !!(row.quoteValidUntil && row.quoteValidUntil < localDate())

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.tab-count {
  margin-left: 4px;
  padding: 0 5px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.22);
  font-size: 11px;
  line-height: 16px;
  vertical-align: 1px;
}

.tab-count.warn {
  background: #fdf6ec;
  color: #e6a23c;
}

.inquiry-table :deep(.el-table__cell) {
  padding: 10px 0;
}

.time-date {
  font-size: 13px;
  color: #303133;
}

.time-clock {
  font-size: 12px;
  color: #909399;
}

.remark-cell {
  min-width: 0;
}

.remark-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.remark-date {
  margin-top: 2px;
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-img {
  width: 38px;
  height: 38px;
  border-radius: 4px;
  flex-shrink: 0;
  object-fit: cover;
  display: block;
}

.product-name {
  line-height: 1.4;
}

.quote-main {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  color: #303133;
}

.quote-total {
  color: #d46b08;
}

.quote-main.expired {
  color: #909399;
}

.quote-main.expired .quote-total {
  color: #909399;
}

.quote-sub {
  margin-top: 2px;
  font-size: 12px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.quote-sub.expired {
  color: #f56c6c;
}

.quote-expired {
  margin-right: 4px;
  padding: 0 4px;
  border-radius: 3px;
  background: #fef0f0;
  font-size: 11px;
}

.muted {
  color: #c0c4cc;
}

.quote-target {
  margin-bottom: 10px;
  padding: 8px 12px;
  border-radius: 6px;
  background: #f7f8fa;
  color: #606266;
}

.quote-brief {
  margin-bottom: 14px;
  padding: 8px 12px;
  border: 1px solid #f0f2f5;
  border-radius: 6px;
  font-size: 13px;
  color: #303133;
}

.brief-row {
  display: flex;
  gap: 8px;
  padding: 2px 0;
}

.brief-label {
  flex-shrink: 0;
  width: 60px;
  color: #909399;
}

.brief-text {
  word-break: break-all;
}

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
