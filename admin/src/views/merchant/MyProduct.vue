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
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border v-loading="loading" @sort-change="onSortChange">
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
      <el-form :model="form" label-width="90px">
        <el-form-item label="产品名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="品类">
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
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'
import { categorySelectOptions } from '@/utils/category'
import productPlaceholder from '@/assets/product-placeholder.jpg'

const query = reactive({ pageNum: 1, pageSize: 10, name: '', status: -1, sort: '' })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const counts = ref({})
const categories = ref([])
const dialogVisible = ref(false)
const form = ref({})
const imageList = ref([])
const uploadHeaders = { Authorization: localStorage.getItem('token') || '' }

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
  imageList.value.push(res.data.url)
}

const removeImage = (index) => {
  imageList.value.splice(index, 1)
}

const save = async () => {
  form.value.images = imageList.value.join(',')
  form.value.imageList = [...imageList.value]
  const isNew = !form.value.id
  await request.post('/merchant/product/save', form.value)
  ElMessage.success(isNew ? '已提交，等待平台审核' : '保存成功')
  dialogVisible.value = false
  load()
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
