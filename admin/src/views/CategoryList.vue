<template>
  <el-card>
    <el-button type="primary" style="margin-bottom: 16px" @click="openAdd">新增品类</el-button>

    <el-table :data="tableData" border v-loading="loading" row-key="id">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="品类名称" min-width="160" />
      <el-table-column label="上级品类" width="140">
        <template #default="{ row }">
          <span>{{ parentName(row.parentId) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该品类？" @confirm="remove(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑品类' : '新增品类'" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="品类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="上级品类">
          <el-select v-model="form.parentId" style="width: 100%">
            <el-option label="作为一级品类" :value="0" />
            <el-option v-for="c in tableData" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="启用" inactive-text="停用" />
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
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const tableData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})

const parentName = (parentId) => {
  if (!parentId) return '一级品类'
  const parent = tableData.value.find((c) => c.id === parentId)
  return parent ? parent.name : '-'
}

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/category/list')
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  form.value = { name: '', parentId: 0, sort: 0, status: 1 }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const save = async () => {
  await request.post('/admin/category/save', form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  load()
}

const remove = async (row) => {
  await request.post(`/admin/category/remove/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>
