<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import {
  Drawer,
  Tabs,
  List,
  Avatar,
  Button,
  Checkbox,
  Space,
  Empty,
  Tag,
  Tooltip,
  Modal,
  Switch,
  Select,
  Pagination
} from 'ant-design-vue'
import {
  SettingOutlined,
  CheckCircleOutlined,
  DeleteOutlined,
  ReloadOutlined
} from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { useMessageStore } from '../stores/message'
import type { MessageVO, MessageEventType, MessagePreferenceVO } from '../types/message'

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
}>()

const messageStore = useMessageStore()

const activeTab = ref<'ALL' | 'UNREAD' | 'READ'>('ALL')
const selectedEventType = ref<MessageEventType | undefined>(undefined)
const selectedIds = ref<Set<number>>(new Set())
const pageNum = ref(1)
const pageSize = ref(20)
const prefModalVisible = ref(false)

const messages = computed(() => messageStore.messages)
const total = computed(() => messageStore.total)
const loading = computed(() => messageStore.loading)
const preferences = computed(() => messageStore.preferences)

const eventTypeOptions = [
  { label: '全部类型', value: undefined },
  { label: '审批流转', value: 'APPROVAL_FLOW' },
  { label: '公告发布', value: 'ANNOUNCEMENT' },
  { label: '合同到期', value: 'CONTRACT_EXPIRY' },
  { label: '入职清单逾期', value: 'ONBOARDING_OVERDUE' },
  { label: '考勤异常', value: 'ATTENDANCE_ABNORMAL' },
  { label: '薪资发放', value: 'SALARY_PAID' },
  { label: '绩效提醒', value: 'PERFORMANCE_REMIND' },
  { label: '附件到期', value: 'ATTACHMENT_EXPIRY' },
  { label: '系统通知', value: 'SYSTEM' }
]

const eventTypeColors: Record<string, string> = {
  APPROVAL_FLOW: 'blue',
  ANNOUNCEMENT: 'gold',
  CONTRACT_EXPIRY: 'red',
  ONBOARDING_OVERDUE: 'orange',
  ATTENDANCE_ABNORMAL: 'magenta',
  SALARY_PAID: 'green',
  PERFORMANCE_REMIND: 'purple',
  ATTACHMENT_EXPIRY: 'volcano',
  SYSTEM: 'cyan'
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

const getEventBgColor = (type: string) => {
  const map: Record<string, string> = {
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
  return map[type] || '#1890ff'
}

const allSelected = computed(() => {
  if (messages.value.length === 0) return false
  return messages.value.every(m => selectedIds.value.has(m.id))
})

const indeterminate = computed(() => {
  const count = messages.value.filter(m => selectedIds.value.has(m.id)).length
  return count > 0 && count < messages.value.length
})

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
  return t.format('YYYY-MM-DD HH:mm')
}

const loadMessages = () => {
  selectedIds.value.clear()
  messageStore.fetchMessages({
    employeeId: messageStore.currentEmployeeId,
    status: activeTab.value,
    eventType: selectedEventType.value,
    pageNum: pageNum.value,
    pageSize: pageSize.value
  })
}

const handleOpenChange = (open: boolean) => {
  emit('update:open', open)
  if (open) {
    loadMessages()
  }
}

const handleTabChange = (key: string) => {
  activeTab.value = key as any
  pageNum.value = 1
  loadMessages()
}

const handleEventTypeChange = () => {
  pageNum.value = 1
  loadMessages()
}

const handlePageChange = (page: number, size: number) => {
  pageNum.value = page
  pageSize.value = size
  loadMessages()
}

const toggleSelectAll = (checked: boolean) => {
  if (checked) {
    messages.value.forEach(m => selectedIds.value.add(m.id))
  } else {
    selectedIds.value.clear()
  }
  selectedIds.value = new Set(selectedIds.value)
}

const toggleSelect = (id: number, checked: boolean) => {
  if (checked) {
    selectedIds.value.add(id)
  } else {
    selectedIds.value.delete(id)
  }
  selectedIds.value = new Set(selectedIds.value)
}

const handleMessageClick = async (msg: MessageVO) => {
  if (!msg.isRead) {
    await messageStore.markAsRead(msg.id)
    loadMessages()
  }
  if (msg.deepLink) {
    console.log('Navigate to deep link:', msg.deepLink)
  }
}

const handleMarkAllRead = async () => {
  const ids = selectedIds.value.size > 0 ? Array.from(selectedIds.value) : undefined
  await messageStore.batchMarkAsRead(ids)
  selectedIds.value.clear()
  loadMessages()
}

const handleClear = async () => {
  const ids = selectedIds.value.size > 0 ? Array.from(selectedIds.value) : undefined
  Modal.confirm({
    title: ids ? '确认清除选中的消息？' : '确认清除当前列表全部消息？',
    content: '清除后消息将被归档，可通过筛选查看归档消息',
    okText: '确认清除',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: async () => {
      await messageStore.batchClear(ids)
      selectedIds.value.clear()
      loadMessages()
    }
  })
}

const handleRefresh = () => {
  messageStore.fetchPreview()
  loadMessages()
}

const openPreferenceModal = () => {
  messageStore.fetchPreferences()
  prefModalVisible.value = true
}

const handlePreferenceChange = async (pref: MessagePreferenceVO, enabled: boolean) => {
  await messageStore.updatePreference({
    employeeId: messageStore.currentEmployeeId,
    eventType: pref.eventType,
    pushEnabled: enabled
  })
}

watch(() => props.open, (open) => {
  if (open) {
    loadMessages()
  }
})

onMounted(() => {
  if (props.open) {
    loadMessages()
  }
})
</script>

<template>
  <a-drawer
    :open="open"
    @update:open="handleOpenChange"
    title="消息中心"
    placement="right"
    :width="520"
    :closable="true"
  >
    <template #extra>
      <a-space>
        <a-tooltip title="刷新">
          <a-button type="text" @click="handleRefresh">
            <template #icon><ReloadOutlined /></template>
          </a-button>
        </a-tooltip>
        <a-tooltip title="消息偏好设置">
          <a-button type="text" @click="openPreferenceModal">
            <template #icon><SettingOutlined /></template>
          </a-button>
        </a-tooltip>
      </a-space>
    </template>

    <div class="message-toolbar">
      <a-select
        v-model:value="selectedEventType"
        style="width: 140px"
        size="small"
        :options="eventTypeOptions"
        @change="handleEventTypeChange"
      />
      <a-space>
        <a-checkbox
          :checked="allSelected"
          :indeterminate="indeterminate"
          @change="(e: any) => toggleSelectAll(e.target.checked)"
        >
          全选
        </a-checkbox>
        <a-button
          type="text"
          size="small"
          :disabled="selectedIds.size === 0 && activeTab !== 'UNREAD'"
          @click="handleMarkAllRead"
        >
          <template #icon><CheckCircleOutlined /></template>
          {{ selectedIds.size > 0 ? '标记已读' : '全部已读' }}
        </a-button>
        <a-button
          type="text"
          size="small"
          danger
          @click="handleClear"
        >
          <template #icon><DeleteOutlined /></template>
          {{ selectedIds.size > 0 ? '清除选中' : '清除全部' }}
        </a-button>
      </a-space>
    </div>

    <a-tabs v-model:activeKey="activeTab" @change="handleTabChange" size="small">
      <a-tab-pane key="ALL" tab="全部" />
      <a-tab-pane key="UNREAD" tab="未读" />
      <a-tab-pane key="READ" tab="已读" />
    </a-tabs>

    <div class="message-list-container">
      <a-spin :spinning="loading">
        <a-empty v-if="messages.length === 0 && !loading" description="暂无消息" />
        <a-list v-else :data-source="messages" size="large" class="message-detail-list">
          <template #renderItem="{ item }">
            <a-list-item
              class="message-detail-item"
              :class="{ unread: !item.isRead }"
              @click="handleMessageClick(item)"
            >
              <div class="item-checkbox" @click.stop>
                <a-checkbox
                  :checked="selectedIds.has(item.id)"
                  @change="(e: any) => toggleSelect(item.id, e.target.checked)"
                />
              </div>
              <a-avatar
                :style="{ backgroundColor: getEventBgColor(item.eventType), flexShrink: 0, marginTop: 2 }"
                size="default"
              >
                {{ getEventIcon(item.eventType) }}
              </a-avatar>
              <div class="item-content">
                <div class="item-header">
                  <span class="item-title" :class="{ 'bold-text': !item.isRead }">
                    {{ item.title }}
                  </span>
                  <a-tag :color="eventTypeColors[item.eventType]" class="item-tag">
                    {{ item.eventTypeName }}
                  </a-tag>
                </div>
                <div class="item-summary">{{ item.summary }}</div>
                <div class="item-footer">
                  <span class="item-time">{{ formatTime(item.createdAt) }}</span>
                  <a-tag v-if="!item.isRead" color="red" class="unread-tag">未读</a-tag>
                </div>
              </div>
            </a-list-item>
          </template>
        </a-list>
      </a-spin>
    </div>

    <div v-if="total > 0" class="pagination-wrapper">
      <a-pagination
        v-model:current="pageNum"
        v-model:pageSize="pageSize"
        :total="total"
        :show-size-changer="false"
        :show-quick-jumper="true"
        size="small"
        @change="handlePageChange"
      />
    </div>

    <a-modal
      v-model:open="prefModalVisible"
      title="消息偏好设置"
      :footer="null"
      width="420"
    >
      <div class="preference-list">
        <div
          v-for="pref in preferences"
          :key="pref.eventType"
          class="preference-item"
        >
          <div class="pref-info">
            <span class="pref-name">{{ pref.eventTypeName }}</span>
            <span class="pref-desc">接收该类型的消息推送</span>
          </div>
          <a-switch
            :checked="pref.pushEnabled"
            @change="(v: boolean) => handlePreferenceChange(pref, v)"
          />
        </div>
      </div>
    </a-modal>
  </a-drawer>
</template>

<style scoped lang="scss">
.message-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 12px;
}

.message-list-container {
  max-height: calc(100vh - 260px);
  overflow-y: auto;
  min-height: 300px;
}

.pagination-wrapper {
  padding: 16px 0 0;
  text-align: center;
  border-top: 1px solid #f0f0f0;
  margin-top: 12px;
}

.message-detail-list {
  padding: 0;

  :deep(.ant-list-items) {
    padding: 0;
  }
}

.message-detail-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 14px 12px !important;
  cursor: pointer;
  border-radius: 6px;
  margin-bottom: 4px;
  transition: background-color 0.2s;

  &:hover {
    background-color: #f5f5f5;
  }

  &.unread {
    background-color: #e6f7ff40;
  }

  .item-checkbox {
    padding-top: 4px;
  }

  .item-content {
    flex: 1;
    min-width: 0;

    .item-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 4px;

      .item-title {
        font-size: 14px;
        color: #262626;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;

        &.bold-text {
          font-weight: 600;
        }
      }

      .item-tag {
        margin: 0;
        font-size: 11px;
        padding: 0 6px;
        height: 20px;
        line-height: 18px;
      }
    }

    .item-summary {
      font-size: 13px;
      color: #595959;
      line-height: 1.5;
      margin-bottom: 6px;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .item-footer {
      display: flex;
      align-items: center;
      gap: 8px;

      .item-time {
        font-size: 12px;
        color: #8c8c8c;
      }

      .unread-tag {
        margin: 0;
        font-size: 11px;
        padding: 0 6px;
        height: 18px;
        line-height: 16px;
      }
    }
  }
}

.preference-list {
  .preference-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .pref-info {
      .pref-name {
        display: block;
        font-size: 14px;
        color: #262626;
        font-weight: 500;
        margin-bottom: 2px;
      }

      .pref-desc {
        font-size: 12px;
        color: #8c8c8c;
      }
    }
  }
}
</style>
