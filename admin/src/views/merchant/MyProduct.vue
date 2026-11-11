<template>
  <el-card>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="产品名称">
        <el-input v-model="query.name" placeholder="名称关键词" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-radio-group v-model="query.status" @change="onStatusChange">
          <el-radio-button :label="-1">全部</el-radio-button>
          <el-radio-button :label="1">在售({{ counts[1] || 0 }})</el-radio-button>
          <el-radio-button :label="0">待审核({{ counts[0] || 0 }})</el-radio-button>
          <el-radio-button :label="2">未通过({{ counts[2] || 0 }})</el-radio-button>
          <el-radio-button :label="3">已下架({{ counts[3] || 0 }})</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openAdd">发布产品</el-button>
        <el-button :loading="exporting" @click="exportProducts">导出商品</el-button>
        <el-button @click="openImport">导入商品</el-button>
      </el-form-item>
    </el-form>

    <div class="batch-bar" v-if="selection.length">
      <span class="batch-tip">已选 {{ selection.length }} 项</span>
      <el-button size="small" type="success" @click="batchStatus(1)">批量上架</el-button>
      <el-button size="small" type="warning" @click="batchStatus(3)">批量下架</el-button>
      <el-button size="small" type="danger" @click="batchRemove">批量删除</el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading" @sort-change="onSortChange" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45" />
      <el-table-column label="产品图" width="90">
        <template #default="{ row }">
          <el-image
            v-if="row.imageList && row.imageList.length"
            :src="row.imageList[0]"
            :preview-src-list="row.imageList"
            fit="cover"
            preview-teleported
            style="width: 56px; height: 56px; border-radius: 4px"
          >
            <template #error>
              <img :src="productPlaceholder" class="ph-img" />
            </template>
          </el-image>
          <img v-else :src="productPlaceholder" class="ph-img" />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="产品名称" min-width="150" />
      <el-table-column prop="categoryName" label="品类" width="100" />
      <el-table-column prop="brand" label="品牌" width="100" />
      <el-table-column label="价格" width="110">
        <template #default="{ row }">
          <span v-if="row.price != null">{{ row.price }}{{ row.unit ? '/' + row.unit : '' }}</span>
          <span v-else style="color: #c0c4cc">面议</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          <div v-if="row.status === 2 && row.rejectReason" class="reason">原因：{{ row.rejectReason }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="views" label="被浏览" width="95" sortable="custom" />
      <el-table-column prop="forwards" label="被转发" width="95" sortable="custom" />
      <el-table-column prop="calls" label="被电话联系" width="115" sortable="custom" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 1" type="info" link @click="changeStatus(row, 3)">下架</el-button>
          <el-button v-if="row.status === 3" type="success" link @click="changeStatus(row, 1)">上架</el-button>
          <el-button type="primary" link @click="openEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该产品？" @confirm="remove(row)">
            <template #reference><el-button type="danger" link>删除</el-button></template>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑产品' : '发布产品'" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="产品名称" prop="name">
          <el-input v-model="form.name" maxlength="128" show-word-limit />
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="品类" prop="categoryId">
              <el-select v-model="form.categoryId" clearable style="width: 100%">
                <el-option v-for="c in categories" :key="c.id" :label="c.label" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型"><el-input v-model="form.typeName" placeholder="如 马桶 / 淋浴房" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="8">
            <el-form-item label="品牌"><el-input v-model="form.brand" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="产地"><el-input v-model="form.origin" /></el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="规格"><el-input v-model="form.spec" /></el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位"><el-input v-model="form.unit" placeholder="如 件 / 平方米" /></el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="产品图片">
          <el-upload
            action="/api/merchant/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="onUploadSuccess"
            accept="image/*"
            multiple
          >
            <el-button type="primary" size="small">选择图片</el-button>
          </el-upload>
          <div class="img-list">
            <div v-for="(img, index) in imageList" :key="img" class="img-item">
              <img :src="img" />
              <span class="del" @click="removeImage(index)">×</span>
            </div>
          </div>
        </el-form-item>

        <el-form-item label="产品描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="importVisible" title="导入商品（CSV）" width="580px">
      <el-alert
        type="info"
        :closable="false"
        class="import-tip"
        title="必填列：商品名称、品类；可选列：品牌、产地、规格、型号、价格、单位、图片、描述"
        description="可先「导出商品」获取标准表头；导入后产品进入待审核，平台审核通过后自动上架。单次最多 2000 条，文件不超过 5MB。"
      />
      <el-upload
        drag
        action="/api/merchant/product/import"
        :headers="uploadHeaders"
        :show-file-list="false"
        accept=".csv"
        :on-success="onImportSuccess"
        :on-error="onImportError"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">选择 CSV 文件或拖拽到此处</div>
      </el-upload>
      <template v-if="importResult">
        <el-alert
          class="import-tip"
          :type="importResult.imported > 0 ? 'success' : 'error'"
          :closable="false"
          :title="`共 ${importResult.total} 行，成功导入 ${importResult.imported} 条`"
        />
        <el-table
          v-if="importResult.errors && importResult.errors.length"
          :data="importResult.errors"
          size="small"
          max-height="220"
        >
          <el-table-column prop="row" label="行号" width="70" />
          <el-table-column prop="message" label="错误原因" />
        </el-table>
      </template>
      <template #footer>
        <el-button @click="importVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import axios from 'axios'
import request from '@/api/request'
import { categorySelectOptions } from '@/utils/category'
import { useUploadHeaders } from '@/utils/upload'
import productPlaceholder from '@/assets/product-placeholder.jpg'

const query = reactive({ pageNum: 1, pageSize: 10, name: '', status: -1, sort: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const counts = ref({})
const categories = ref([])
const dialogVisible = ref(false)
const form = ref({})
const formRef = ref()
const rules = {
  name: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择产品品类', trigger: 'change' }]
}
const imageList = ref([])
const MAX_IMAGES = 9
const uploadHeaders = useUploadHeaders()
const exporting = ref(false)
const selection = ref([])
const importVisible = ref(false)
const importResult = ref(null)

const statusText = (s) => ({ 0: '待审核', 1: '在售', 2: '已拒绝', 3: '已下架' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }[s] || 'info')

const load = async () => {
  loading.value = true
  try {
    const params = { ...query, status: query.status === -1 ? null : query.status }
    const res = await request.get('/merchant/product/page', { params })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const loadStatusCount = async () => {
  const res = await request.get('/merchant/product/status-count')
  counts.value = res.data || {}
}

const onStatusChange = () => {
  query.pageNum = 1
  load()
}

const onSortChange = ({ prop, order }) => {
  if (!order) {
    query.sort = ''
  } else if (prop === 'price') {
    query.sort = order === 'ascending' ? 'price_asc' : 'price_desc'
  } else {
    query.sort = prop
  }
  load()
}

const reset = () => {
  query.name = ''
  query.status = -1
  query.sort = ''
  query.pageNum = 1
  load()
  loadStatusCount()
}

const openAdd = () => {
  form.value = {
    name: '',
    categoryId: null,
    typeName: '',
    brand: '',
    origin: '',
    spec: '',
    price: null,
    unit: '',
    description: ''
  }
  imageList.value = []
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { ...row }
  imageList.value = row.imageList ? [...row.imageList] : []
  dialogVisible.value = true
}

const onUploadSuccess = (res) => {
  if (res.code !== 200) {
    ElMessage.error(res.msg || '图片上传失败')
    return
  }
  if (imageList.value.length >= MAX_IMAGES) {
    ElMessage.warning(`最多上传 ${MAX_IMAGES} 张图片`)
    return
  }
  imageList.value.push(res.data.url)
}

const removeImage = (index) => {
  imageList.value.splice(index, 1)
}

const save = async () => {
  await formRef.value.validate()
  form.value.name = (form.value.name || '').trim()
  form.value.images = imageList.value.join(',')
  form.value.imageList = [...imageList.value]
  const isNew = !form.value.id
  await request.post('/merchant/product/save', form.value)
  ElMessage.success(isNew ? '已提交，等待平台审核' : '保存成功')
  dialogVisible.value = false
  load()
  loadStatusCount()
}

// ---------------- 批量操作 ----------------
const onSelectionChange = (rows) => {
  selection.value = rows
}

const batchStatus = async (status) => {
  const ids = selection.value.map((r) => r.id)
  await ElMessageBox.confirm(
    `确定对选中的 ${ids.length} 个产品批量${status === 1 ? '上架' : '下架'}？未通过审核的产品会自动跳过`,
    '批量操作',
    { type: 'warning' }
  )
  const res = await request.post('/merchant/product/batch-status', { ids, status })
  const { updated, skipped } = res.data
  ElMessage.success(`已${status === 1 ? '上架' : '下架'} ${updated} 个${skipped ? `，跳过 ${skipped} 个` : ''}`)
  load()
  loadStatusCount()
}

const batchRemove = async () => {
  const ids = selection.value.map((r) => r.id)
  await ElMessageBox.confirm(`确定删除选中的 ${ids.length} 个产品？删除后不可恢复`, '批量删除', {
    type: 'warning',
    confirmButtonText: '删除',
    confirmButtonClass: 'el-button--danger'
  })
  const res = await request.post('/merchant/product/batch-remove', { ids })
  ElMessage.success(`已删除 ${res.data} 个产品`)
  load()
  loadStatusCount()
}

// ---------------- CSV 导入 ----------------
const openImport = () => {
  importResult.value = null
  importVisible.value = true
}

const onImportSuccess = (res) => {
  if (res.code !== 200) {
    ElMessage.error(res.msg || '导入失败')
    return
  }
  importResult.value = res.data
  if (res.data.imported > 0) {
    ElMessage.success(`成功导入 ${res.data.imported} 条`)
    load()
    loadStatusCount()
  }
}

const onImportError = () => {
  ElMessage.error('上传失败，请检查网络后重试')
}

const changeStatus = async (row, status) => {
  await request.post('/merchant/product/status', { id: row.id, status })
  ElMessage.success(status === 1 ? '已上架' : '已下架')
  load()
}

const remove = async (row) => {
  await request.post(`/merchant/product/remove/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

const exportProducts = async () => {
  exporting.value = true
  try {
    const res = await axios.get('/api/merchant/product/export', {
      params: {
        name: query.name || undefined,
        status: query.status === -1 ? undefined : query.status
      },
      responseType: 'blob',
      headers: { Authorization: localStorage.getItem('token') }
    })
    const url = URL.createObjectURL(res.data)
    const link = document.createElement('a')
    link.href = url
    link.download = `我的商品-${new Date().toISOString().slice(0, 10)}.csv`
    link.click()
    URL.revokeObjectURL(url)
  } finally {
    exporting.value = false
  }
}

onMounted(async () => {
  const catRes = await request.get('/merchant/categories')
  categories.value = categorySelectOptions(catRes.data)
  load()
  loadStatusCount()
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
.reason {
  font-size: 12px;
  color: #f56c6c;
}
.batch-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}
.batch-tip {
  font-size: 13px;
  color: #909399;
  margin-right: 4px;
}
.import-tip {
  margin-bottom: 12px;
}
.img-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
.img-item {
  position: relative;
  width: 80px;
  height: 80px;
  border-radius: 6px;
  overflow: hidden;
  border: 1px solid #dcdfe6;
}
.img-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.del {
  position: absolute;
  top: 0;
  right: 0;
  width: 18px;
  height: 18px;
  line-height: 18px;
  text-align: center;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}
.ph-img {
  width: 56px;
  height: 56px;
  border-radius: 4px;
  object-fit: cover;
  display: block;
}
</style>
