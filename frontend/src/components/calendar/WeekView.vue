<script setup lang="ts">
import { computed } from 'vue'
import { useCalendarStore } from '../../stores/calendar'
import type { CalendarEvent } from '../../types/calendar'

const store = useCalendarStore()

const hours = Array.from({ length: 24 }, (_, i) => i)

interface WeekDay {
  date: Date
  dateStr: string
  label: string
  dayOfWeek: string
  isToday: boolean
  events: CalendarEvent[]
}

const weekDays = computed<WeekDay[]>(() => {
  const current = store.currentDate
  const day = current.getDay()
  const diff = current.getDate() - day + (day === 0 ? -6 : 1)
  const monday = new Date(current.getFullYear(), current.getMonth(), diff)
  const today = new Date()
  const todayStr = formatDateStr(today)
  const dayLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const result: WeekDay[] = []
  for (let i = 0; i < 7; i++) {
    const d = new Date(monday)
    d.setDate(d.getDate() + i)
    const dateStr = formatDateStr(d)
    const dayStart = new Date(d)
    dayStart.setHours(0, 0, 0, 0)
    const dayEnd = new Date(d)
    dayEnd.setHours(23, 59, 59, 999)
    const dayEvents = store.filteredEvents.filter(e => {
      const es = new Date(e.startTime).getTime()
      const ee = new Date(e.endTime).getTime()
      return es <= dayEnd.getTime() && ee >= dayStart.getTime()
    })
    result.push({
      date: d,
      dateStr,
      label: `${d.getMonth() + 1}/${d.getDate()}`,
      dayOfWeek: dayLabels[i] ?? '',
      isToday: dateStr === todayStr,
      events: dayEvents
    })
  }
  return result
})

function formatDateStr(date: Date): string {
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function getEventStyle(event: CalendarEvent, day: WeekDay) {
  const dayStart = new Date(day.date)
  dayStart.setHours(0, 0, 0, 0)
  const dayEnd = new Date(day.date)
  dayEnd.setHours(23, 59, 59, 999)

  if (event.isAllDay) {
    return {
      top: '0px',
      height: '36px',
      backgroundColor: event.color + '22',
      borderLeftColor: event.color,
      borderLeft: `3px solid ${event.color}`
    }
  }

  const es = new Date(event.startTime)
  const ee = new Date(event.endTime)
  const actualStart = Math.max(es.getTime(), dayStart.getTime())
  const actualEnd = Math.min(ee.getTime(), dayEnd.getTime())
  const startMin = (actualStart - dayStart.getTime()) / 60000
  const duration = (actualEnd - actualStart) / 60000
  const top = (startMin / 60) * 48
  const height = Math.max((duration / 60) * 48, 24)
  return {
    top: `${top}px`,
    height: `${height}px`,
    backgroundColor: event.color + '33',
    borderLeft: `3px solid ${event.color}`
  }
}

const handleDateClick = (day: WeekDay) => {
  store.fetchDateEvents(day.dateStr)
}

const formatHour = (h: number) => `${h.toString().padStart(2, '0')}:00`
</script>

<template>
  <div class="week-view">
    <div class="week-header">
      <div class="time-gutter"></div>
      <div
        v-for="day in weekDays"
        :key="day.dateStr"
        class="day-header"
        :class="{ today: day.isToday }"
      >
        <div class="dow">{{ day.dayOfWeek }}</div>
        <div class="date-num" :class="{ 'today-num': day.isToday }">
          {{ day.label }}
        </div>
      </div>
    </div>
    <div class="week-body">
      <div class="time-column">
        <div v-for="h in hours" :key="h" class="time-slot">
          <span class="time-label">{{ formatHour(h) }}</span>
        </div>
      </div>
      <div class="days-column">
        <div
          v-for="day in weekDays"
          :key="day.dateStr"
          class="day-column"
          :class="{ today: day.isToday }"
          @click="handleDateClick(day)"
        >
          <div class="all-day-area">
            <div
              v-for="ev in day.events.filter(e => e.isAllDay)"
              :key="'a-' + ev.id"
              class="event-block all-day-event"
              :style="getEventStyle(ev, day)"
              :title="ev.title"
            >
              {{ ev.title }}
            </div>
          </div>
          <div class="time-area">
            <div v-for="h in hours" :key="h" class="hour-slot"></div>
            <div
              v-for="ev in day.events.filter(e => !e.isAllDay)"
              :key="'t-' + ev.id"
              class="event-block"
              :style="getEventStyle(ev, day)"
              :title="ev.title"
            >
              <div class="ev-title">{{ ev.title }}</div>
              <div class="ev-time">
                {{ new Date(ev.startTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }}
                -
                {{ new Date(ev.endTime).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }) }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.week-view {
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.week-header {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
  background: #fafafa;
}

.time-gutter {
  width: 60px;
  flex-shrink: 0;
  border-right: 1px solid #f0f0f0;
}

.day-header {
  flex: 1;
  padding: 10px 0;
  text-align: center;
  border-right: 1px solid #f0f0f0;

  &:last-child {
    border-right: none;
  }

  &.today {
    background: #e6f4ff;
  }

  .dow {
    font-size: 13px;
    color: #666;
    margin-bottom: 4px;
  }

  .date-num {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    display: inline-block;
    min-width: 36px;
    height: 32px;
    line-height: 32px;
    border-radius: 16px;

    &.today-num {
      background: #1677ff;
      color: #fff;
    }
  }
}

.week-body {
  display: flex;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

.time-column {
  width: 60px;
  flex-shrink: 0;
  border-right: 1px solid #f0f0f0;
  background: #fafafa;
}

.time-slot {
  height: 48px;
  border-bottom: 1px solid #f0f0f0;
  position: relative;

  .time-label {
    position: absolute;
    top: -8px;
    right: 6px;
    font-size: 11px;
    color: #999;
  }
}

.days-column {
  display: flex;
  flex: 1;
  min-width: 0;
}

.day-column {
  flex: 1;
  border-right: 1px solid #f0f0f0;
  position: relative;
  min-width: 0;
  cursor: pointer;

  &:last-child {
    border-right: none;
  }

  &.today {
    background: rgba(22, 119, 255, 0.03);
  }
}

.all-day-area {
  min-height: 40px;
  border-bottom: 1px solid #f0f0f0;
  padding: 2px;
  background: #fafafa;
  position: relative;
}

.time-area {
  position: relative;
}

.hour-slot {
  height: 48px;
  border-bottom: 1px solid #f0f0f0;
}

.event-block {
  position: absolute;
  left: 2px;
  right: 2px;
  border-radius: 4px;
  padding: 4px 8px;
  overflow: hidden;
  cursor: pointer;
  z-index: 1;
  transition: transform 0.15s, box-shadow 0.15s;

  &:hover {
    transform: scale(1.02);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
    z-index: 2;
  }
}

.all-day-event {
  font-size: 12px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 28px;
  position: relative;
  top: auto !important;
  height: 32px !important;
  margin-bottom: 2px;
}

.ev-title {
  font-size: 12px;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ev-time {
  font-size: 10px;
  color: #666;
  margin-top: 2px;
}
</style>
