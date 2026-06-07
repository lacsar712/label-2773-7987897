<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterView, useRoute, useRouter } from 'vue-router'
import {
  CalendarOutlined,
  ScheduleOutlined,
  SettingOutlined,
  TeamOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined
} from '@ant-design/icons-vue'

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
</script>

<template>
  <a-layout class="app-layout" style="min-height: 100vh">
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

.app-content {
  padding: 0;
  min-height: 100vh;
}
</style>
