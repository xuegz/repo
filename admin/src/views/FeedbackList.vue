<template>
  <div>
    <el-row :gutter="12" class="stat-row">
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">待处理</div>
          <div class="stat-value pending">{{ stats.pending }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">今日新增</div>
          <div class="stat-value">{{ stats.today }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">累计反馈</div>
          <div class="stat-value">{{ stats.total }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-form :inline="true" :model="query" class="search-bar">
        <el-form-item label="来源">
          <el-select v-model="query.source" placeholder="全部" clearable style="width: 130px">
            <el-option label="意见反馈" :value="1" />
            <el-option label="会话举报" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="内容 / 用户 / 联系方式" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border v-loading="loading">
        <el-table-column prop="createTime" label="提交时间" width="160" :formatter="formatTime" />
        <el-table-column label="来源" width="100">
          <template #default="{ row }">
            <el-tag :type="row.source === 2 ? 'warning' : 'primary'" effect="plain">
              {{ row.source === 2 ? '会话举报' : '意见反馈' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="用户" width="150">
          <template #default="{ row }">
            <div>{{ row.userName || row.merchantName || '-' }}</div>
            <div class="cell-sub">{{ row.openid || '-' }}</div>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="110">
          <template #default="{ row }">{{ row.category || '-' }}</template>
        </el-table-column>
        <el-table-column label="内容" min-width="260">
          <template #default="{ row }">
            <div class="cell-content">{{ row.content }}</div>
            <div v-if="row.contact" class="cell-sub">联系方式：{{ row.contact }}</div>
            <div v-if="row.reply" class="cell-reply">回复：{{ row.reply }}</div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" effect="plain">
              {{ row.status === 1 ? '已处理' : '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openHandle(row)">处理</el-button>
            <el-button v-if="row.conversationId" link type="primary" @click="openChat(row)">会话记录</el-button>
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

    <el-dialog v-model="dialogVisible" title="处理反馈" width="560px">
      <div v-if="current" class="detail-box">
        <div class="detail-line">
          <span class="detail-label">来源</span>
          {{ current.source === 2 ? '会话举报' : '意见反馈' }} · {{ current.category || '未分类' }}
        </div>
        <div class="detail-line" v-if="current.merchantName">
          <span class="detail-label">涉及商户</span>{{ current.merchantName }}
        </div>
        <div class="detail-content">{{ current.content }}</div>
        <div class="detail-line" v-if="current.contact">
          <span class="detail-label">联系方式</span>{{ current.contact }}
        </div>
      </div>
      <el-form label-position="top" style="margin-top: 12px">
        <el-form-item label="回复内容（用户在小程序「我的反馈」可见）">
          <el-input v-model="reply" type="textarea" :rows="4" maxlength="500" show-word-limit placeholder="填写处理结果或回复说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button v-if="current && current.status === 1" @click="submitHandle(0)">重新打开</el-button>
        <el-button type="primary" :loading="submitting" @click="submitHandle(1)">提交处理</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="chatVisible" title="会话记录" size="46%">
      <div v-loading="chatLoading" class="chat-list">
        <div v-if="!chatLoading && chatMessages.length === 0" class="empty-tip">暂无消息</div>
        <div v-for="msg in chatMessages" :key="msg.id" class="chat-item">
          <div class="chat-head">
            <span class="chat-sender">{{ msg.sender === 'merchant' ? '商户' : '用户' }}</span>
            <span class="chat-time">{{ formatTime(null, null, msg.createTime) }}</span>
          </div>
          <div v-if="msg.type === 'text'" class="chat-text">{{ msg.content }}</div>
          <div v-else-if="msg.type === 'image'" class="chat-text">[图片] {{ msg.content }}</div>
          <div v-else class="chat-text">[商品卡片] {{ msg.content }}</div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const query = reactive({ pageNum: 1, pageSize: 10, source: null, status: null, keyword: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const stats = reactive({ pending: 0, today: 0, total: 0 })

const dialogVisible = ref(false)
const submitting = ref(false)
const current = ref(null)
const reply = ref('')

const chatVisible = ref(false)
const chatLoading = ref(false)
const chatMessages = ref([])

const formatTime = (row, column, value) => (value || '').replace('T', ' ').slice(0, 19)

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/feedback/page', { params: query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadStats = async () => {
  const res = await request.get('/admin/feedback/stats')
  Object.assign(stats, res.data || {})
}

const search = () => {
  query.pageNum = 1
  load()
}

const reset = () => {
  query.source = null
  query.status = null
  query.keyword = ''
  query.pageNum = 1
  load()
}

const openHandle = (row) => {
  current.value = row
  reply.value = row.reply || ''
  dialogVisible.value = true
}

const submitHandle = async (status) => {
  if (!current.value) return
  submitting.value = true
  try {
    await request.post('/admin/feedback/handle', {
      id: current.value.id,
      status,
      reply: reply.value
    })
    ElMessage.success(status === 1 ? '已标记为已处理' : '已重新打开')
    dialogVisible.value = false
    await Promise.all([load(), loadStats()])
  } finally {
    submitting.value = false
  }
}

const openChat = async (row) => {
  chatVisible.value = true
  chatLoading.value = true
  chatMessages.value = []
  try {
    const res = await request.get(`/admin/feedback/chat/${row.conversationId}`)
    chatMessages.value = res.data || []
  } finally {
    chatLoading.value = false
  }
}

onMounted(() => {
  load()
  loadStats()
})
</script>

<style scoped>
.stat-row {
  margin-bottom: 12px;
}
.stat-card {
  text-align: center;
}
.stat-label {
  color: #909399;
  font-size: 13px;
}
.stat-value {
  margin-top: 6px;
  font-size: 26px;
  font-weight: 600;
  color: #303133;
}
.stat-value.pending {
  color: #f56c6c;
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
  line-height: 1.5;
  word-break: break-all;
}
.cell-content {
  line-height: 1.5;
  word-break: break-all;
}
.cell-reply {
  margin-top: 4px;
  color: #67c23a;
  font-size: 12px;
  line-height: 1.5;
  word-break: break-all;
}
.detail-box {
  background: #f7f8fa;
  border-radius: 6px;
  padding: 12px 14px;
}
.detail-line {
  font-size: 13px;
  color: #606266;
  margin-bottom: 6px;
}
.detail-label {
  display: inline-block;
  width: 72px;
  color: #909399;
}
.detail-content {
  margin: 8px 0;
  padding: 10px 12px;
  background: #fff;
  border-radius: 4px;
  line-height: 1.6;
  color: #303133;
  word-break: break-all;
}
.chat-list {
  min-height: 120px;
}
.chat-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f2f5;
}
.chat-head {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}
.chat-sender {
  font-weight: 600;
  color: #606266;
}
.chat-text {
  margin-top: 6px;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  word-break: break-all;
}
.empty-tip {
  text-align: center;
  color: #c0c4cc;
  padding: 40px 0;
}
</style>
