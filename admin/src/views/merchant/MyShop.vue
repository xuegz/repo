<template>
  <el-card>
    <template #header>
      <span>我的门店信息</span>
    </template>

    <el-form :model="form" label-width="100px" style="max-width: 720px" v-loading="loading">
      <el-form-item label="门店名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-row :gutter="10">
        <el-col :span="12">
          <el-form-item label="联系人"><el-input v-model="form.contactName" /></el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话"><el-input v-model="form.phone" /></el-form-item>
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
        <el-input v-model="form.address" placeholder="如 北二环大明宫建材市场 A 区 1 排 2 号" />
      </el-form-item>
      <el-form-item label="门店坐标">
        <el-row :gutter="10" style="width: 100%">
          <el-col :span="12"><el-input v-model="form.latitude" placeholder="纬度，如 34.3086" /></el-col>
          <el-col :span="12"><el-input v-model="form.longitude" placeholder="经度，如 108.9402" /></el-col>
        </el-row>
        <div class="coord-tip">
          选填，填写后小程序展示「到店导航」按钮。坐标可在
          <a href="https://lbs.qq.com/getPoint/" target="_blank" rel="noreferrer">腾讯地图坐标拾取器</a>
          搜索门店地址后复制。
        </div>
      </el-form-item>
      <el-form-item label="主营品类">
        <el-select v-model="form.categoryId" clearable style="width: 100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.label" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="门头照片">
        <el-upload
          action="/api/merchant/upload"
          :headers="uploadHeaders"
          :show-file-list="false"
          :on-success="onUploadSuccess"
          accept="image/*"
        >
          <img v-if="form.logo" :src="form.logo" class="logo" />
          <el-icon v-else class="uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="门店简介">
        <el-input v-model="form.intro" type="textarea" :rows="3" placeholder="主营范围、经营年限等" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'
import { categorySelectOptions } from '@/utils/category'

const form = ref({})
const categories = ref([])
const markets = ref([])
const loading = ref(false)
const uploadHeaders = { Authorization: localStorage.getItem('token') || '' }

const load = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/shop/info')
    form.value = res.data
  } finally {
    loading.value = false
  }
}

const onUploadSuccess = (res) => {
  form.value.logo = res.data.url
}

const save = async () => {
  const payload = { ...form.value }
  if (payload.latitude === '') payload.latitude = null
  if (payload.longitude === '') payload.longitude = null
  await request.post('/merchant/shop/save', payload)
  ElMessage.success('保存成功')
  load()
}

onMounted(async () => {
  const catRes = await request.get('/merchant/categories')
  categories.value = categorySelectOptions(catRes.data)
  const marketRes = await request.get('/mp/markets')
  markets.value = marketRes.data
  load()
})
</script>

<style scoped>
.coord-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
  margin-top: 4px;
}
.coord-tip a {
  color: var(--el-color-primary);
}
.logo {
  width: 200px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
}
.uploader-icon {
  width: 200px;
  height: 120px;
  font-size: 26px;
  color: #8c939d;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
