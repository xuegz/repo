<template>
  <el-card>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="市场名称">
        <el-input v-model="query.keyword" placeholder="名称关键词" clearable @keyup.enter="load" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openAdd">新增市场</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="name" label="市场名称" min-width="200" show-overflow-tooltip />
      <el-table-column label="城市" width="86">
        <template #default="{ row }">{{ row.city || '西安市' }}</template>
      </el-table-column>
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column label="坐标" width="140">
        <template #default="{ row }">
          <span v-if="row.latitude && row.longitude">{{ Number(row.latitude).toFixed(3) }}, {{ Number(row.longitude).toFixed(3) }}</span>
          <span v-else class="muted">未标注</span>
        </template>
      </el-table-column>
      <el-table-column prop="merchantCount" label="商户数" width="84" />
      <el-table-column prop="sort" label="排序" width="70" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该市场？" @confirm="remove(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑市场' : '新增市场'" width="640px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="市场名称">
          <el-input v-model="form.name" placeholder="如 大明宫建材家居城·北二环店" />
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
        <el-form-item label="市场坐标">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="12"><el-input v-model="form.latitude" placeholder="纬度，如 34.3168" /></el-col>
            <el-col :span="12"><el-input v-model="form.longitude" placeholder="经度，如 108.9690" /></el-col>
          </el-row>
          <div class="coord-tip">
            选填，标注后小程序市场页可直接导航。坐标可在
            <a href="https://lbs.qq.com/getPoint/" target="_blank" rel="noreferrer">腾讯地图坐标拾取器</a>
            获取。
          </div>
        </el-form-item>
        <el-form-item label="市场简介">
          <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="经营品类、规模、交通等" />
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
            </el-form-item>
          </el-col>
        </el-row>
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

const query = reactive({ keyword: '', status: null })
const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/market/list', {
      params: { keyword: query.keyword || undefined, status: query.status }
    })
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.keyword = ''
  query.status = null
  load()
}

const openAdd = () => {
  form.value = {
    name: '',
    province: '陕西省',
    city: '西安市',
    address: '',
    latitude: '',
    longitude: '',
    intro: '',
    sort: 0,
    status: 1
  }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const save = async () => {
  const payload = { ...form.value }
  if (payload.latitude === '') payload.latitude = null
  if (payload.longitude === '') payload.longitude = null
  await request.post('/admin/market/save', payload)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const remove = async (row) => {
  await request.post(`/admin/market/remove/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.search-bar {
  margin-bottom: 8px;
}
.coord-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
  margin-top: 4px;
}
.coord-tip a {
  color: var(--el-color-primary);
}
.muted {
  color: #b0b3b8;
}
</style>
