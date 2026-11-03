<template>
  <el-card>
    <el-tabs v-model="activeTab">
      <!-- 首页轮播 -->
      <el-tab-pane label="首页轮播" name="banner">
        <el-button type="primary" style="margin-bottom: 12px" @click="openAdd('banner')">新增轮播</el-button>
        <el-table :data="banners" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="图片" width="160">
            <template #default="{ row }">
              <el-image
                v-if="row.image"
                :src="row.image"
                :preview-src-list="[row.image]"
                fit="cover"
                preview-teleported
                style="width: 140px; height: 60px; border-radius: 4px"
              >
                <template #error>
                  <img :src="bannerPlaceholder" class="ph-banner" />
                </template>
              </el-image>
              <img v-else :src="bannerPlaceholder" class="ph-banner" />
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="link" label="跳转链接" min-width="160" />
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openEdit('banner', row)">编辑</el-button>
              <el-popconfirm title="确定删除？" @confirm="remove('banner', row)">
                <template #reference><el-button type="danger" link>删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 广告位 -->
      <el-tab-pane label="广告位" name="ad">
        <el-button type="primary" style="margin-bottom: 12px" @click="openAdd('ad')">新增广告</el-button>
        <el-table :data="ads" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="图片" width="160">
            <template #default="{ row }">
              <el-image
                v-if="row.image"
                :src="row.image"
                :preview-src-list="[row.image]"
                fit="cover"
                preview-teleported
                style="width: 140px; height: 60px; border-radius: 4px"
              >
                <template #error>
                  <img :src="bannerPlaceholder" class="ph-banner" />
                </template>
              </el-image>
              <img v-else :src="bannerPlaceholder" class="ph-banner" />
            </template>
          </el-table-column>
          <el-table-column label="位置" width="110">
            <template #default="{ row }">
              <el-tag>{{ positionText(row.position) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="link" label="跳转链接" min-width="140" />
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openEdit('ad', row)">编辑</el-button>
              <el-popconfirm title="确定删除？" @confirm="remove('ad', row)">
                <template #reference><el-button type="danger" link>删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 市场公告 -->
      <el-tab-pane label="市场公告" name="notice">
        <el-button type="primary" style="margin-bottom: 12px" @click="openAdd('notice')">新增公告</el-button>
        <el-table :data="notices" border>
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="标题" min-width="180" />
          <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
          <el-table-column prop="sort" label="排序" width="80" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="openEdit('notice', row)">编辑</el-button>
              <el-popconfirm title="确定删除？" @confirm="remove('notice', row)">
                <template #reference><el-button type="danger" link>删除</el-button></template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item v-if="currentType !== 'notice'" label="图片">
          <el-upload
            action="/api/admin/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="onUploadSuccess"
            accept="image/*"
          >
            <img v-if="form.image" :src="form.image" class="thumb" />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="currentType === 'ad'" label="展示位置">
          <el-select v-model="form.position" style="width: 100%">
            <el-option label="首页" value="home" />
            <el-option label="产品列表页" value="list" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item v-if="currentType === 'notice'" label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="公告正文，将展示在小程序首页" />
        </el-form-item>
        <el-form-item v-if="currentType !== 'notice'" label="跳转链接"><el-input v-model="form.link" placeholder="小程序内路径或网页链接" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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
import { Plus } from '@element-plus/icons-vue'
import request from '@/api/request'
import bannerPlaceholder from '@/assets/shop-wide-placeholder.jpg'

const activeTab = ref('banner')
const banners = ref([])
const ads = ref([])
const notices = ref([])
const dialogVisible = ref(false)
const currentType = ref('banner')
const form = ref({})

const uploadHeaders = { Authorization: localStorage.getItem('token') || '' }
const typeText = { banner: '轮播', ad: '广告', notice: '公告' }
const dialogTitle = computed(() => `${form.value.id ? '编辑' : '新增'}${typeText[currentType.value] || ''}`)
const positionText = (p) => ({ home: '首页', list: '产品列表页' }[p] || p)

const loadBanner = async () => {
  const res = await request.get('/admin/banner/list')
  banners.value = res.data
}
const loadAd = async () => {
  const res = await request.get('/admin/ad/list')
  ads.value = res.data
}
const loadNotice = async () => {
  const res = await request.get('/admin/notice/list')
  notices.value = res.data
}

const openAdd = (type) => {
  currentType.value = type
  form.value = { title: '', image: '', link: '', content: '', sort: 0, status: 1, position: 'home' }
  dialogVisible.value = true
}
const openEdit = (type, row) => {
  currentType.value = type
  form.value = { ...row }
  dialogVisible.value = true
}

const onUploadSuccess = (res) => {
  form.value.image = res.data.url
}

const save = async () => {
  const urlMap = { banner: '/admin/banner/save', ad: '/admin/ad/save', notice: '/admin/notice/save' }
  await request.post(urlMap[currentType.value], form.value)
  ElMessage.success('保存成功')
  dialogVisible.value = false
  ;({ banner: loadBanner, ad: loadAd, notice: loadNotice })[currentType.value]()
}

const remove = async (type, row) => {
  const urlMap = {
    banner: `/admin/banner/remove/${row.id}`,
    ad: `/admin/ad/remove/${row.id}`,
    notice: `/admin/notice/remove/${row.id}`
  }
  await request.post(urlMap[type])
  ElMessage.success('删除成功')
  ;({ banner: loadBanner, ad: loadAd, notice: loadNotice })[type]()
}

onMounted(() => {
  loadBanner()
  loadAd()
  loadNotice()
})
</script>

<style scoped>
.thumb {
  width: 240px;
  height: 100px;
  object-fit: cover;
  border-radius: 6px;
}
.ph-banner {
  width: 140px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}
.uploader-icon {
  width: 240px;
  height: 100px;
  font-size: 28px;
  color: #8c939d;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
