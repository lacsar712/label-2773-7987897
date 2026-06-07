<script setup lang="ts">
import { computed } from 'vue'
import { useCalendarStore } from '../../stores/calendar'
import type { CalendarEvent } from '../../types/calendar'

const store = useCalendarStore()

const weekDays = ['一', '二', '三', '四', '五', '六', '日']

interface DayCell {
  date: Date
  dateStr: string
  day: number
  isCurrentMonth: boolean
  isToday: boolean
  events: CalendarEvent[]
}

const days = computed<DayCell[]>(() => {
  const current = store.currentDate
  const year = current.getFullYear()
  const month = current.getMonth()
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  const firstDayWeek = (firstDay.getDay() + 6) % 7
  const totalDays = lastDay.getDate()
  const cells: DayCell[] = []
  const today = new Date()
  const todayStr = formatDateStr(today)

  const prevMonth = new Date(year, month, 0)
  const prevMonthDays = prevMonth.getDate()
  for (let i = firstDayWeek - 1; i >= 0; i--) {
    const d = new Date(year, month - 1, prevMonthDays - i)
    cells.push(createCell(d, false, todayStr))
  }

  for (let i = 1; i <= totalDays; i++) {
    const d = new Date(year, month, i)
    cells.push(createCell(d, true, todayStr))
  }

  const remaining = 42 - cells.length
  for (let i = 1; i <= remaining; i++) {
    const d = new Date(year, month + 1, i)
    cells.push(createCell(d, false, todayStr))
  }
  return cells
})

function formatDateStr(date: Date): string {
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function createCell(date: Date, isCurrentMonth: boolean, todayStr: string): DayCell {
  const dateStr = formatDateStr(date)
  const dayStart = new Date(date)
  dayStart.setHours(0, 0, 0, 0)
  const dayEnd = new Date(date)
  dayEnd.setHours(23, 59, 59, 999)
  const dayEvents = store.filteredEvents.filter(e => {
    const es = new Date(e.startTime).getTime()
    const ee = new Date(e.endTime).getTime()
    return es <= dayEnd.getTime() && ee >= dayStart.getTime()
  })
  return {
    date,
    dateStr,
    day: date.getDate(),
    isCurrentMonth,
    isToday: dateStr === todayStr,
    events: dayEvents
  }
}

const handleDateClick = (cell: DayCell) => {
  store.fetchDateEvents(cell.dateStr)
}

const formatEventTime = (event: CalendarEvent): string => {
  if (event.isAllDay) return '全天'
  const d = new Date(event.startTime)
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${pad(d.getHours())}:${pad(d.getMinutes())}`
}
</script>

<template>
  <div class="month-view">
    <div class="week-header">
      <div v-for="w in weekDays" :key="w" class="week-cell">{{ w }}</div>
    </div>
    <div class="days-grid">
      <div
        v-for="cell in days"
        :key="cell.dateStr"
        class="day-cell"
        :class="{
          'other-month': !cell.isCurrentMonth,
          'today': cell.isToday
        }"
        @click="handleDateClick(cell)"
      >
        <div class="day-number" :class="{ 'today-num': cell.isToday }">{{ cell.day }}</div>
        <div class="day-events">
          <div
            v-for="ev in cell.events.slice(0, 3)"
            :key="ev.id"
            class="event-item"
            :style="{ backgroundColor: ev.color + '22', borderLeftColor: ev.color }"
            :title="ev.title"
          >
            <span class="event-time">{{ formatEventTime(ev) }}</span>
            <span class="event-title">{{ ev.title }}</span>
          </div>
          <div v-if="cell.events.length > 3" class="more-events">
            +{{ cell.events.length - 3 }} 更多
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.month-view {
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.week-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.week-cell {
  padding: 12px 0;
  text-align: center;
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-auto-rows: 1fr;
  flex: 1;
  min-height: 0;
}

.day-cell {
  border-right: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  padding: 6px;
  cursor: pointer;
  min-height: 100px;
  overflow: hidden;
  transition: background-color 0.2s;
  display: flex;
  flex-direction: column;

  &:hover {
    background-color: #f5f5f5;
  }

  &:nth-child(7n) {
    border-right: none;
  }

  &.other-month {
    background-color: #fafafa;
    .day-number {
      color: #ccc;
    }
  }

  &.today {
    background-color: #e6f4ff;
  }
}

.day-number {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;

  &.today-num {
    background: #1677ff;
    color: #fff;
  }
}

.day-events {
  flex: 1;
  min-height: 0;
  overflow: hidden;
}

.event-item {
  font-size: 12px;
  padding: 2px 6px;
  margin-bottom: 3px;
  border-radius: 3px;
  border-left: 3px solid;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.6;
}

.event-time {
  color: #666;
  margin-right: 4px;
}

.event-title {
  color: #333;
}

.more-events {
  font-size: 11px;
  color: #999;
  padding: 2px 6px;
}
</style>
