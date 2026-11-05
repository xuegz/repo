<template>
  <div class="login-page">
    <div class="login-box">
      <h2>建材商户平台</h2>
      <p class="sub">管理后台 / 商户端</p>

      <el-form size="large" @keyup.enter="submit">
        <el-form-item>
          <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>

      <div class="tip">平台管理员：admin / 123456　商户：merchant / 123456<br />商户首次登录需修改初始密码</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import request from '@/api/request'

const router = useRouter()
const form = reactive({ username: '', password: '' })
const loading = ref(false)

const submit = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/auth/login', form)
    const data = res.data
    localStorage.setItem('token', data.token)
    localStorage.setItem('role', data.role)
    localStorage.setItem('realName', data.realName || '')
    localStorage.setItem('merchantId', data.merchantId || '')
    localStorage.setItem('mustChangePassword', data.mustChangePassword ? '1' : '')
    ElMessage.success('登录成功')
    if (data.mustChangePassword) {
      router.push('/change-password')
    } else {
      router.push(data.role === 1 ? '/dashboard' : '/m/dashboard')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1f2937, #409eff);
}
.login-box {
  width: 380px;
  padding: 40px 36px 30px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
h2 {
  margin: 0;
  text-align: center;
  font-size: 22px;
}
.sub {
  margin: 6px 0 24px;
  text-align: center;
  color: #909399;
  font-size: 13px;
}
.login-btn {
  width: 100%;
}
.tip {
  margin-top: 18px;
  text-align: center;
  color: #909399;
  font-size: 12px;
}
</style>
