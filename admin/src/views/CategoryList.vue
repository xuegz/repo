<template>
  <el-card>
    <div class="toolbar">
      <el-button @click="openAdd">＋ 添加一级品类</el-button>
      <div class="toolbar-right">
        <span class="hint">最多三级，点击名称前的箭头展开 / 收起</span>
        <el-button link @click="expandAll(true)">全部展开</el-button>
        <el-button link @click="expandAll(false)">全部收起</el-button>
      </div>
    </div>

    <el-table
      ref="tableRef"
      :data="treeData"
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children' }"
      default-expand-all
      class="category-table"
    >
      <el-table-column prop="name" label="品类名称" min-width="320" />
      <el-table-column label="层级" width="100">
        <template #default="{ row }">
          <span class="muted">{{ levelText(row.level) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="下级数量" width="100">
        <template #default="{ row }">
          <span class="muted">{{ row.children ? row.children.length : 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sort" label="排序" width="90">
        <template #default="{ row }">
          <span class="muted">{{ row.sort }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light">
            {{ row.status === 1 ? '正常启用' : '已停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="right" header-align="right">
        <template #default="{ row }">
          <el-button v-if="row.level < 3" type="primary" link @click="openAddChild(row)">新增子类</el-button>
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该品类？" @confirm="remove(row)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="480px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="品类名称">
          <el-input v-model="form.name" maxlength="32" show-word-limit />
        </el-form-item>
        <el-form-item label="上级品类">
          <el-select v-model="form.parentId" style="width: 100%">
            <el-option label="无（作为一级品类）" :value="0" />
            <el-option
              v-for="o in parentOptions"
              :key="o.id"
              :label="o.label"
              :value="o.id"
              :disabled="o.disabled"
            />
          </el-select>
          <div class="form-tip">只有一级、二级品类可以继续添加子类</div>
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
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { buildCategoryTree, flattenCategoryTree } from '@/utils/category'

const tableRef = ref(null)
const treeData = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({})

const levelText = (level) => ({ 1: '一级', 2: '二级', 3: '三级' }[level] || '-')

const flatNodes = computed(() => flattenCategoryTree(treeData.value))

const dialogTitle = computed(() => (form.value.id ? '编辑品类' : '新增品类'))

// 编辑时禁止把品类挂到自己或自己的后代下；三级品类不能作为父级
const parentOptions = computed(() => {
  const editingId = form.value.id
  const blocked = new Set()
  if (editingId) {
    const collect = (node) => {
      blocked.add(node.id)
      ;(node.children || []).forEach(collect)
    }
    const self = flatNodes.value.find((n) => n.id === editingId)
    if (self) collect(self)
  }
  return flatNodes.value.map((n) => ({
    id: n.id,
    label: '　'.repeat(n.level - 1) + n.name,
    disabled: n.level >= 3 || blocked.has(n.id)
  }))
})

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/category/list')
    treeData.value = buildCategoryTree(res.data)
  } finally {
    loading.value = false
  }
}

const openAdd = () => {
  form.value = { name: '', parentId: 0, sort: 0, status: 1 }
  dialogVisible.value = true
}

const openAddChild = (row) => {
  form.value = { name: '', parentId: row.id, sort: 0, status: 1 }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { id: row.id, name: row.name, parentId: row.parentId || 0, sort: row.sort, status: row.status }
  dialogVisible.value = true
}

const save = async () => {
  if (!form.value.name || !form.value.name.trim()) {
    ElMessage.warning('请填写品类名称')
    return
  }
  try {
    await request.post('/admin/category/save', form.value)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (e) {
    // 错误提示已由请求拦截器统一弹出
  }
}

const remove = async (row) => {
  try {
    await request.post(`/admin/category/remove/${row.id}`)
    ElMessage.success('删除成功')
    load()
  } catch (e) {
    // 错误提示已由请求拦截器统一弹出
  }
}

const expandAll = (expand) => {
  flatNodes.value.forEach((row) => {
    if (row.children && row.children.length) {
      tableRef.value.toggleRowExpansion(row, expand)
    }
  })
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.hint {
  color: #909399;
  font-size: 13px;
}

.form-tip {
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}

.category-table {
  --el-table-border-color: transparent;
  --el-table-header-bg-color: #fff;
  --el-table-row-hover-bg-color: #f7f8fa;
  font-size: 14px;
}

.category-table :deep(th.el-table__cell) {
  background: #fff;
  color: #909399;
  font-weight: 500;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.category-table :deep(td.el-table__cell) {
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
  color: #303133;
}

.category-table :deep(.el-table__expand-icon) {
  color: #b1b3b8;
}

.muted {
  color: #606266;
}
</style>
