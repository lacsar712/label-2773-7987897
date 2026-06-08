<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <TeamOutlined class="logo-icon" />
        <h1>HR 管理系统</h1>
        <p class="subtitle">员工管理平台登录</p>
      </div>
      <a-form
        :model="loginForm"
        layout="vertical"
        @finish="handleLogin"
        class="login-form"
      >
        <a-form-item
          label="用户名"
          name="username"
          :rules="[{ required: true, message: '请输入用户名' }]"
        >
          <a-input
            v-model:value="loginForm.username"
            size="large"
            placeholder="请输入用户名"
            :prefix="UserOutlined"
            data-testid="login-username"
          />
        </a-form-item>
        <a-form-item
          label="密码"
          name="password"
          :rules="[{ required: true, message: '请输入密码' }]"
        >
          <a-input-password
            v-model:value="loginForm.password"
            size="large"
            placeholder="请输入密码"
            data-testid="login-password"
          />
        </a-form-item>
        <a-form-item>
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            block
            :loading="loading"
            data-testid="login-submit"
          >
            登录
          </a-button>
        </a-form-item>
      </a-form>
      <div class="login-tips">
        <p>提示：使用 admin / admin123 登录</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { TeamOutlined, UserOutlined } from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const handleLogin = async () => {
  loading.value = true
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    if (loginForm.username === 'admin' && loginForm.password === 'admin123') {
      localStorage.setItem('isLoggedIn', 'true')
      localStorage.setItem('username', loginForm.username)
      message.success('登录成功')
      const redirect = (route.query.redirect as string) || '/'
      router.push(redirect)
    } else {
      message.error('用户名或密码错误')
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  background: #fff;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  width: 400px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;

  .logo-icon {
    font-size: 48px;
    color: #1677ff;
    margin-bottom: 12px;
  }

  h1 {
    margin: 0 0 8px 0;
    font-size: 24px;
    color: #1a1a1a;
  }

  .subtitle {
    margin: 0;
    color: #8c8c8c;
    font-size: 14px;
  }
}

.login-form {
  margin-bottom: 20px;
}

.login-tips {
  text-align: center;
  color: #8c8c8c;
  font-size: 12px;

  p {
    margin: 0;
  }
}
</style>
