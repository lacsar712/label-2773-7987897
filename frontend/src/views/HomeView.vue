<template>
  <div class="calendar-app">
    <header class="app-header">
      <div class="header-left">
        <h1 class="app-title">
          <CalendarOutlined class="title-icon" />
          团队日历
        </h1>
        <span class="current-user">
          <UserOutlined /> {{ store.currentUserName }}
        </span>
      </div>
      <div class="header-right">
        <a-button @click="showSubscribeModal = true">
          <UserAddOutlined /> 订阅同事
        </a-button>
        <a-button type="primary" @click="showExportModal = true">
          <DownloadOutlined /> 导出 iCal
        </a-button>
        <a-button type="primary" @click="handleQuickCreate">
          <PlusOutlined /> 新建事件
        </a-button>
      </div>
    </header>

    <div class="app-body">
      <aside class="sidebar">
        <FilterPanel />
      </aside>

      <main class="main-area">
        <div class="calendar-toolbar">
          <div class="nav-group">
            <a-button @click="store.goToPrev">
              <LeftOutlined />
            </a-button>
            <a-button @click="store.goToToday" type="primary" ghost>今天</a-button>
            <a-button @click="store.goToNext">
              <RightOutlined />
            </a-button>
            <span class="current-label">{{ currentLabel }}</span>
          </div>
          <a-segmented
            :options="viewOptions"
            :value="store.viewMode"
            @change="(v: any) => store.setViewMode(v)"
          />
        </div>

        <div class="calendar-content">
          <MonthView v-if="store.viewMode === 'month'" />
          <WeekView v-else />
        </div>
      </main>
    </div>

    <DayTimelineDrawer
      @edit="handleEditEvent"
      @create="handleCreateWithDate"
    />

    <EventFormModal
      v-model:visible="showEventModal"
      :event="editingEvent"
      :default-date-str="defaultDateStr"
      @success="handleEventSuccess"
    />

    <ExportModal v-model:visible="showExportModal" />
    <SubscribeModal v-model:visible="showSubscribeModal" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  CalendarOutlined,
  UserOutlined,
  UserAddOutlined,
  DownloadOutlined,
  PlusOutlined,
  LeftOutlined,
  RightOutlined
} from '@ant-design/icons-vue'
import { useCalendarStore } from '../stores/calendar'
import MonthView from '../components/calendar/MonthView.vue'
import WeekView from '../components/calendar/WeekView.vue'
import DayTimelineDrawer from '../components/calendar/DayTimelineDrawer.vue'
import EventFormModal from '../components/calendar/EventFormModal.vue'
import FilterPanel from '../components/calendar/FilterPanel.vue'
import ExportModal from '../components/calendar/ExportModal.vue'
import SubscribeModal from '../components/calendar/SubscribeModal.vue'
import type { CalendarEvent } from '../types/calendar'

const store = useCalendarStore()

const showEventModal = ref(false)
const showExportModal = ref(false)
const showSubscribeModal = ref(false)
const editingEvent = ref<CalendarEvent | null>(null)
const defaultDateStr = ref('')

const viewOptions = [
  { label: '月视图', value: 'month' },
  { label: '周视图', value: 'week' }
]

const currentLabel = computed(() => {
  const d = store.currentDate
  if (store.viewMode === 'month') {
    return `${d.getFullYear()} 年 ${d.getMonth() + 1} 月`
  } else {
    const day = d.getDay()
    const diff = d.getDate() - day + (day === 0 ? -6 : 1)
    const monday = new Date(d.getFullYear(), d.getMonth(), diff)
    const sunday = new Date(monday)
    sunday.setDate(sunday.getDate() + 6)
    const sameMonth = monday.getMonth() === sunday.getMonth()
    if (sameMonth) {
      return `${monday.getFullYear()}年${monday.getMonth() + 1}月${monday.getDate()}日 - ${sunday.getDate()}日`
    } else {
      return `${monday.getFullYear()}年${monday.getMonth() + 1}月${monday.getDate()}日 - ${sunday.getMonth() + 1}月${sunday.getDate()}日`
    }
  }
})

onMounted(async () => {
  try {
    await store.fetchEventTypes()
    await store.fetchEmployees()
    await store.fetchSubscriptions()
    await store.fetchEvents()
  } catch (e) {
    console.error(e)
    message.warning('无法连接到后端服务，已使用前端演示数据')
  }
})

const handleQuickCreate = () => {
  editingEvent.value = null
  defaultDateStr.value = ''
  showEventModal.value = true
}

const handleCreateWithDate = (dateStr: string) => {
  editingEvent.value = null
  defaultDateStr.value = dateStr
  showEventModal.value = true
}

const handleEditEvent = (event: CalendarEvent) => {
  editingEvent.value = event
  defaultDateStr.value = ''
  showEventModal.value = true
}

const handleEventSuccess = () => {
  editingEvent.value = null
  defaultDateStr.value = ''
}
</script>

<style lang="scss" scoped>
.calendar-app {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  background: #f5f7fa;
  overflow: hidden;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 24px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  flex-shrink: 0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.app-title {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #1a1a1a;
  display: flex;
  align-items: center;
  gap: 8px;

  .title-icon {
    color: #1677ff;
    font-size: 22px;
  }
}

.current-user {
  font-size: 13px;
  color: #666;
  background: #f5f5f5;
  padding: 4px 12px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.header-right {
  display: flex;
  gap: 8px;
}

.app-body {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.sidebar {
  width: 260px;
  background: #fff;
  border-right: 1px solid #f0f0f0;
  flex-shrink: 0;
  overflow: hidden;
}

.main-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  padding: 16px 20px;
  overflow: hidden;
}

.calendar-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-shrink: 0;
}

.nav-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.current-label {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-left: 8px;
}

.calendar-content {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}
</style>
