<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import {
  CalendarOutlined,
  ScheduleOutlined,
  SettingOutlined,
  TeamOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  LogoutOutlined,
  UserOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)

const menuItems = computed(() => [
  {
    key: 'home',
    icon: CalendarOutlined,
    label: '团队日历',
    path: '/'
  },
  {
    key: 'employees',
    icon: TeamOutlined,
    label: '员工管理',
    path: '/employees'
  },
  {
    key: 'schedule',
    icon: ScheduleOutlined,
    label: '排班管理',
    path: '/schedule'
  },
  {
    key: 'config',
    icon: SettingOutlined,
    label: '系统设置',
    path: '/system/config'
  }
])

const selectedKey = computed(() => {
  if (route.path.startsWith('/employees')) return 'employees'
  if (route.path.startsWith('/schedule')) return 'schedule'
  if (route.path.startsWith('/system')) return 'config'
  return 'home'
})

const handleMenuClick = (e: { key: string }) => {
  const item = menuItems.value.find((m) => m.key === e.key)
  if (item) {
    router.push(item.path)
  }
}

const isSchedulePage = computed(() => route.path.startsWith('/schedule'))
const isLoginPage = computed(() => route.path.startsWith('/login'))

const username = computed(() => localStorage.getItem('username') || '用户')

const userMenuItems = [
  {
    key: 'logout',
    icon: LogoutOutlined,
    label: '退出登录'
  }
]

const handleUserMenuClick = ({ key }: { key: string }) => {
  if (key === 'logout') {
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('username')
    message.success('已退出登录')
    router.push('/login')
  }
}
</script>

<template>
  <RouterView v-if="isLoginPage" />
  <a-layout v-else class="app-layout" style="min-height: 100vh">
    <a-layout-sider
      v-if="!isSchedulePage"
      v-model:collapsed="collapsed"
      :trigger="null"
      collapsible
      width="200"
      class="app-sider"
    >
      <div class="logo">
        <TeamOutlined />
        <span v-if="!collapsed">HR 管理系统</span>
      </div>
      <a-menu
        theme="dark"
        mode="inline"
        :selected-keys="[selectedKey]"
        @click="handleMenuClick"
      >
        <a-menu-item v-for="item in menuItems" :key="item.key">
          <component :is="item.icon" />
          <span>{{ item.label }}</span>
        </a-menu-item>
      </a-menu>
      <div class="sider-trigger" @click="collapsed = !collapsed">
        <MenuFoldOutlined v-if="!collapsed" />
        <MenuUnfoldOutlined v-else />
      </div>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="app-header" v-if="!isSchedulePage">
        <div class="header-right">
          <a-dropdown :menu="{ items: userMenuItems, onClick: handleUserMenuClick }" placement="bottomRight">
            <a class="user-info" data-testid="user-menu">
              <UserOutlined />
              <span>{{ username }}</span>
            </a>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="app-content">
        <RouterView />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<style lang="scss" scoped>
.app-layout {
  background: #f5f7fa;
}

.app-sider {
  background: #001529;
  position: relative;

  .logo {
    height: 56px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  :deep(.ant-menu) {
    border-right: none;
  }

  .sider-trigger {
    position: absolute;
    bottom: 0;
    width: 100%;
    height: 40px;
    line-height: 40px;
    text-align: center;
    color: #fff;
    background: #000c17;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: #1890ff;
    }
  }
}

.app-header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  height: 56px;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  color: #333;
  transition: background-color 0.2s;

  &:hover {
    background: #f5f5f5;
  }
}

.app-content {
  padding: 0;
  min-height: calc(100vh - 56px);
}
</style>
