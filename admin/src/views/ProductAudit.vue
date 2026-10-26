<template>
  <el-card>
    <el-form :inline="true" :model="query" class="search-bar">
      <el-form-item label="产品名称">
        <el-input v-model="query.name" placeholder="名称关键词" clearable />
      </el-form-item>
      <el-form-item label="品类">
        <el-select v-model="query.categoryId" placeholder="全部" clearable style="width: 140px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width: 140px">
          <el-option label="待审核" :value="0" />
          <el-option label="在售" :value="1" />
          <el-option label="已拒绝" :value="2" />
          <el-option label="已下架" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column label="产品图" width="90">
        <template #default="{ row }">
          <el-image
            v-if="row.imageList && row.imageList.length"
            :src="row.imageList[0]"
            :preview-src-list="row.imageList"
            fit="cover"
            style="width: 56px; height: 56px; border-radius: 4px"
            preview-teleported
          />
          <span v-else style="color: #c0c4cc">无图</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="产品名称" min-width="150" />
      <el-table-column prop="merchantName" label="所属商户" min-width="150" />
      <el-table-column prop="categoryName" label="品类" width="100" />
      <el-table-column prop="brand" label="品牌" width="100" />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          <span v-if="row.price != null">{{ row.price }}{{ row.unit ? '/' + row.unit : '' }}</span>
          <span v-else style="color: #c0c4cc">面议</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="views" label="浏览" width="70" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" type="success" link @click="audit(row, 1)">通过</el-button>
          <el-button v-if="row.status === 0" type="warning" link @click="audit(row, 2)">拒绝</el-button>
          <el-button v-if="row.status === 1" type="info" link @click="audit(row, 3)">下架</el-button>
          <el-button v-if="row.status === 3" type="success" link @click="audit(row, 1)">上架</el-button>
          <el-popconfirm title="确定删除该产品？" @confirm="remove(row)">
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
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const query = reactive({ pageNum: 1, pageSize: 10, name: '', categoryId: null, status: null })
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const categories = ref([])

const statusText = (s) => ({ 0: '待审核', 1: '在售', 2: '已拒绝', 3: '已下架' }[s] || '未知')
const statusType = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }[s] || 'info')

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/product/page', { params: query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const reset = () => {
  query.name = ''
  query.categoryId = null
  query.status = null
  query.pageNum = 1
  load()
}

const audit = async (row, status) => {
  let reason = ''
  if (status === 2) {
    try {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝产品', {
        inputPattern: /.+/,
        inputErrorMessage: '原因不能为空'
      })
      reason = value
    } catch (e) {
      return
    }
  }
  await request.post('/admin/product/audit', { id: row.id, status, reason })
  ElMessage.success('操作成功')
  load()
}

const remove = async (row) => {
  await request.post(`/admin/product/remove/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(() => {
  load()
  request.get('/admin/category/list').then((res) => {
    categories.value = res.data
  })
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
</style>
