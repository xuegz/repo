<template>
  <el-card class="password-card">
    <template #header>
      <div class="card-header">
        <span>修改密码</span>
        <el-tag v-if="forced" type="warning" size="small">首次登录必须修改初始密码</el-tag>
      </div>
    </template>

    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" @keyup.enter="submit">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password autocomplete="current-password" />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input
          v-model="form.newPassword"
          type="password"
          show-password
          autocomplete="new-password"
          placeholder="6-32 位，不能与原密码相同"
        />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" show-password autocomplete="new-password" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submit">保存</el-button>
        <el-button v-if="!forced" @click="router.back()">返回</el-button>
      </el-form-item>
    </el-form>

    <div v-if="forced" class="tip">为保障账号安全，修改密码后方可使用平台功能。</div>
  </el-card>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const forced = localStorage.getItem('mustChangePassword') === '1'
const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const rules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 32, message: '新密码长度需为 6-32 位', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        callback(value && value === form.oldPassword ? new Error('新密码不能与原密码相同') : undefined)
      },
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        callback(value !== form.newPassword ? new Error('两次输入的新密码不一致') : undefined)
      },
      trigger: 'blur'
    }
  ]
}

const submit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }
  loading.value = true
  try {
    await request.post('/account/password', {
      oldPassword: form.oldPassword,
      newPassword: form.newPassword
    })
    localStorage.removeItem('mustChangePassword')
    ElMessage.success('密码修改成功')
    const role = Number(localStorage.getItem('role'))
    router.replace(role === 1 ? '/dashboard' : '/m/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.password-card {
  max-width: 560px;
  margin: 24px auto;
}
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.tip {
  margin-top: 4px;
  padding-left: 90px;
  color: #909399;
  font-size: 12px;
}
</style>
