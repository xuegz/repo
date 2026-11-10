<template>
  <el-card>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="账号/姓名">
        <el-input v-model="query.keyword" placeholder="关键词" clearable @keyup.enter="load" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openAdd">新增子账号</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="username" label="登录账号" width="180" />
      <el-table-column prop="realName" label="姓名" width="160" />
      <el-table-column prop="phone" label="手机号" width="150">
        <template #default="{ row }">{{ row.phone || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
          <el-tag v-if="row.mustChangePassword === 1" type="warning" effect="plain" style="margin-left: 6px">
            待改密
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="170" :formatter="formatTime" />
      <el-table-column label="操作" min-width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="resetPassword(row)">重置密码</el-button>
          <el-button
            v-if="row.status === 1"
            type="danger"
            link
            :disabled="row.id === currentUserId"
            @click="toggleStatus(row, 0)"
          >禁用</el-button>
          <el-button v-else type="success" link @click="toggleStatus(row, 1)">启用</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="新增平台子账号" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="登录账号">
          <el-input v-model="form.username" placeholder="建议用姓名拼音，登录时输入" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="对外展示的操作人姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="选填，便于联系" />
        </el-form-item>
      </el-form>
      <div class="tip">创建后会生成一次性临时密码，首次登录需强制修改。</div>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">创建</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pwdVisible" title="临时密码" width="420px">
      <div class="pwd-tip">请把账号和临时密码线下告知对方，首次登录会强制修改密码。</div>
      <div class="pwd-box">
        <span class="pwd-account">{{ pwd.username }}</span>
        <span class="pwd-code">{{ pwd.password }}</span>
      </div>
      <template #footer>
        <el-button @click="pwdVisible = false">关闭</el-button>
        <el-button type="primary" @click="copyPassword">复制密码</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const currentUserId = Number(localStorage.getItem('userId'))
const query = reactive({ keyword: '' })
const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({ username: '', realName: '', phone: '' })
const pwdVisible = ref(false)
const pwd = reactive({ username: '', password: '' })

const formatTime = (row, column, value) => (value ? String(value).replace('T', ' ').slice(0, 19) : '-')

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/account/list', {
      params: { keyword: query.keyword || undefined }
    })
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.keyword = ''
  load()
}

const openAdd = () => {
  form.username = ''
  form.realName = ''
  form.phone = ''
  dialogVisible.value = true
}

const save = async () => {
  if (!form.username.trim()) {
    ElMessage.warning('请填写登录账号')
    return
  }
  if (!form.realName.trim()) {
    ElMessage.warning('请填写姓名')
    return
  }
  const res = await request.post('/admin/account/create', {
    username: form.username.trim(),
    realName: form.realName.trim(),
    phone: form.phone.trim()
  })
  dialogVisible.value = false
  pwd.username = res.data.username
  pwd.password = res.data.password
  pwdVisible.value = true
  load()
}

const resetPassword = async (row) => {
  await ElMessageBox.confirm(`确定重置「${row.realName || row.username}」的密码？`, '提示', { type: 'warning' })
  const res = await request.post('/admin/account/reset-password', { id: row.id })
  pwd.username = res.data.username
  pwd.password = res.data.password
  pwdVisible.value = true
  load()
}

const toggleStatus = async (row, status) => {
  await ElMessageBox.confirm(
    `确定${status === 1 ? '启用' : '禁用'}「${row.realName || row.username}」？`,
    '提示',
    { type: 'warning' }
  )
  await request.post('/admin/account/status', { id: row.id, status })
  ElMessage.success(status === 1 ? '已启用' : '已禁用')
  load()
}

const copyPassword = async () => {
  try {
    await navigator.clipboard.writeText(pwd.password)
    ElMessage.success('已复制密码')
  } catch (e) {
    ElMessage.warning('复制失败，请手动选择密码复制')
  }
}

onMounted(load)
</script>

<style scoped>
.tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}
.pwd-tip {
  color: #606266;
  font-size: 13px;
  line-height: 1.7;
  margin-bottom: 12px;
}
.pwd-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 12px 16px;
  background: #f5f7fa;
  border-radius: 6px;
}
.pwd-account {
  color: #606266;
}
.pwd-code {
  font-family: Consolas, Monaco, monospace;
  font-size: 18px;
  letter-spacing: 1px;
  color: #303133;
}
</style>
