<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { BellOutlined } from '@ant-design/icons-vue'
import { Badge, Dropdown, List, Avatar, Button, Popover } from 'ant-design-vue'
import dayjs from 'dayjs'
import { useMessageStore } from '../stores/message'
import type { MessageVO } from '../types/message'
import MessageCenterDrawer from './MessageCenterDrawer.vue'

const messageStore = useMessageStore()
const drawerVisible = ref(false)
const popoverVisible = ref(false)

const unreadCount = computed(() => messageStore.preview.unreadCount)
const latestMessages = computed(() => messageStore.preview.latestMessages)

const eventTypeColors: Record<string, string> = {
  APPROVAL_FLOW: '#1890ff',
  ANNOUNCEMENT: '#faad14',
  CONTRACT_EXPIRY: '#f5222d',
  ONBOARDING_OVERDUE: '#fa8c16',
  ATTENDANCE_ABNORMAL: '#eb2f96',
  SALARY_PAID: '#52c41a',
  PERFORMANCE_REMIND: '#722ed1',
  ATTACHMENT_EXPIRY: '#fa541c',
  SYSTEM: '#13c2c2'
}

const getEventIcon = (type: string) => {
  const map: Record<string, string> = {
    APPROVAL_FLOW: '审',
    ANNOUNCEMENT: '公',
    CONTRACT_EXPIRY: '合',
    ONBOARDING_OVERDUE: '入',
    ATTENDANCE_ABNORMAL: '勤',
    SALARY_PAID: '薪',
    PERFORMANCE_REMIND: '绩',
    ATTACHMENT_EXPIRY: '附',
    SYSTEM: '系'
  }
  return map[type] || '消'
}

const handleOpenCenter = () => {
  popoverVisible.value = false
  drawerVisible.value = true
}

const handleMessageClick = async (msg: MessageVO) => {
  if (!msg.isRead) {
    await messageStore.markAsRead(msg.id)
  }
  if (msg.deepLink) {
    console.log('Deep link to:', msg.deepLink)
  }
  popoverVisible.value = false
  drawerVisible.value = true
}

const formatTime = (time: string) => {
  const now = dayjs()
  const t = dayjs(time)
  const diffMin = now.diff(t, 'minute')
  const diffHour = now.diff(t, 'hour')
  const diffDay = now.diff(t, 'day')
  if (diffMin < 1) return '刚刚'
  if (diffMin < 60) return `${diffMin}分钟前`
  if (diffHour < 24) return `${diffHour}小时前`
  if (diffDay < 7) return `${diffDay}天前`
  return t.format('MM-DD HH:mm')
}

onMounted(() => {
  messageStore.fetchPreview()
})
</script>

<template>
  <div class="message-bell-wrapper">
    <Popover
      v-model:open="popoverVisible"
      placement="bottomRight"
      trigger="click"
      :overlayStyle="{ width: '360px', padding: 0 }"
    >
      <template #content>
        <div class="message-popover">
          <div class="popover-header">
            <span class="header-title">消息通知</span>
            <a @click="handleOpenCenter" class="view-all">查看全部</a>
          </div>
          <div v-if="latestMessages.length === 0" class="empty-state">
            暂无新消息
          </div>
          <div v-else>
            <a-list size="small" :bordered="false" class="message-list">
              <a-list-item
                v-for="msg in latestMessages"
                :key="msg.id"
                class="message-item"
                :class="{ unread: !msg.isRead }"
                @click="handleMessageClick(msg)"
              >
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar
                      :style="{ backgroundColor: eventTypeColors[msg.eventType] || '#1890ff', verticalAlign: 'middle' }"
                      size="small"
                    >
                      {{ getEventIcon(msg.eventType) }}
                    </a-avatar>
                  </template>
                  <template #title>
                    <div class="msg-title-row">
                      <span class="msg-title" :class="{ 'font-bold': !msg.isRead }">{{ msg.title }}</span>
                      <span class="msg-time">{{ formatTime(msg.createdAt) }}</span>
                    </div>
                  </template>
                  <template #description>
                    <div class="msg-summary">{{ msg.summary }}</div>
                    <div class="msg-type-tag" :style="{ color: eventTypeColors[msg.eventType] }">
                      {{ msg.eventTypeName }}
                    </div>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </a-list>
          </div>
          <div class="popover-footer">
            <a-button type="link" block @click="handleOpenCenter">
              查看全部消息
            </a-button>
          </div>
        </div>
      </template>
      <div class="bell-icon">
        <a-badge :count="unreadCount" :overflow-count="99" :offset="[-4, 4]">
          <bell-outlined style="font-size: 20px; cursor: pointer; color: #595959" />
        </a-badge>
      </div>
    </Popover>

    <MessageCenterDrawer v-model:open="drawerVisible" />
  </div>
</template>

<style scoped lang="scss">
.message-bell-wrapper {
  display: inline-block;
  position: relative;
}

.bell-icon {
  padding: 4px 8px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.message-popover {
  .popover-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
    background: #fafafa;

    .header-title {
      font-weight: 600;
      font-size: 15px;
      color: #262626;
    }

    .view-all {
      font-size: 13px;
      color: #1890ff;
      cursor: pointer;

      &:hover {
        color: #40a9ff;
      }
    }
  }

  .empty-state {
    padding: 40px 20px;
    text-align: center;
    color: #8c8c8c;
    font-size: 14px;
  }

  .message-list {
    max-height: 360px;
    overflow-y: auto;
    padding: 0;

    :deep(.ant-list-items) {
      padding: 0;
    }
  }

  .message-item {
    cursor: pointer;
    padding: 12px 16px !important;
    border-bottom: 1px solid #f5f5f5;
    transition: background-color 0.2s;

    &:hover {
      background-color: #f5f5f5;
    }

    &.unread {
      background-color: #e6f7ff30;
    }
  }

  .msg-title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 8px;

    .msg-title {
      font-size: 14px;
      color: #262626;
      line-height: 1.4;
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      &.font-bold {
        font-weight: 600;
      }
    }

    .msg-time {
      font-size: 12px;
      color: #8c8c8c;
      flex-shrink: 0;
    }
  }

  .msg-summary {
    font-size: 13px;
    color: #595959;
    line-height: 1.5;
    margin: 4px 0;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }

  .msg-type-tag {
    font-size: 12px;
    font-weight: 500;
  }

  .popover-footer {
    border-top: 1px solid #f0f0f0;
    padding: 8px 0;

    :deep(.ant-btn) {
      font-size: 13px;
    }
  }
}
</style>
