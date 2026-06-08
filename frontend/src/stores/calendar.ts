import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CalendarEvent, EventType, CalendarSubscription, ViewMode, Employee } from '../types/calendar'
import request from '../utils/request'

interface Result<T> {
  code: number
  message: string
  data: T
}

export const useCalendarStore = defineStore('calendar', () => {
  const currentDate = ref(new Date())
  const viewMode = ref<ViewMode>('month')
  const events = ref<CalendarEvent[]>([])
  const selectedDateEvents = ref<CalendarEvent[]>([])
  const selectedDate = ref<string | null>(null)
  const eventTypes = ref<EventType[]>([])
  const enabledEventTypes = ref<string[]>([])
  const subscriptions = ref<CalendarSubscription[]>([])
  const employees = ref<Employee[]>([])
  const enabledEmployeeIds = ref<number[]>([])
  const currentUserId = ref(1)
  const currentUserName = ref('张三')

  const getDefaultColor = (eventType: string): string => {
    const found = eventTypes.value.find(t => t.value === eventType)
    return found?.color || '#F759AB'
  }

  const getEventTypeLabel = (eventType: string): string => {
    const found = eventTypes.value.find(t => t.value === eventType)
    return found?.label || '自定义'
  }

  const filteredEvents = computed(() => {
    return events.value.filter(e => {
      const typeOk = enabledEventTypes.value.length === 0 || enabledEventTypes.value.includes(e.eventType)
      const empOk = enabledEmployeeIds.value.length === 0 ||
        !e.employeeId || enabledEmployeeIds.value.includes(e.employeeId)
      return typeOk && empOk
    })
  })

  const fetchEventTypes = async () => {
    const res = await request.get<any, Result<EventType[]>>('/api/calendar/events/event-types')
    if (res.code === 200) {
      eventTypes.value = res.data
      enabledEventTypes.value = res.data.map((t: EventType) => t.value)
    }
  }

  const fetchEmployees = async () => {
    const res = await request.get<any, Result<Employee[]>>('/api/employees')
    if (res.code === 200) {
      employees.value = res.data
    }
  }

  const fetchEvents = async (startTime?: string, endTime?: string) => {
    const date = currentDate.value
    let start: Date, end: Date
    if (startTime && endTime) {
      start = new Date(startTime)
      end = new Date(endTime)
    } else if (viewMode.value === 'month') {
      start = new Date(date.getFullYear(), date.getMonth(), 1)
      end = new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59)
    } else {
      const day = date.getDay()
      const diff = date.getDate() - day + (day === 0 ? -6 : 1)
      start = new Date(date.getFullYear(), date.getMonth(), diff, 0, 0, 0)
      end = new Date(start)
      end.setDate(end.getDate() + 6)
      end.setHours(23, 59, 59)
    }
    const res = await request.post<any, Result<CalendarEvent[]>>('/api/calendar/events/query', {
      startTime: formatDateTime(start),
      endTime: formatDateTime(end),
      eventTypes: enabledEventTypes.value,
      employeeIds: enabledEmployeeIds.value
    })
    if (res.code === 200) {
      events.value = res.data
    }
  }

  const fetchDateEvents = async (dateStr: string) => {
    const res = await request.get<any, Result<CalendarEvent[]>>(`/api/calendar/events/date/${dateStr}`)
    if (res.code === 200) {
      selectedDateEvents.value = res.data
      selectedDate.value = dateStr
    }
  }

  const fetchSubscriptions = async () => {
    const res = await request.get<any, Result<CalendarSubscription[]>>(`/api/calendar/events/subscriptions/${currentUserId.value}`)
    if (res.code === 200) {
      subscriptions.value = res.data
    }
  }

  const addSubscription = async (targetEmployeeId: number, targetEmployeeName: string) => {
    const res = await request.post<any, Result<unknown>>('/api/calendar/events/subscriptions', {
      subscriberId: currentUserId.value,
      subscriberName: currentUserName.value,
      targetEmployeeId,
      targetEmployeeName
    })
    if (res.code === 200) {
      await fetchSubscriptions()
    }
    return res
  }

  const removeSubscription = async (targetEmployeeId: number) => {
    const res = await request.delete<any, Result<unknown>>(
      `/api/calendar/events/subscriptions/${currentUserId.value}/${targetEmployeeId}`
    )
    if (res.code === 200) {
      await fetchSubscriptions()
    }
    return res
  }

  const createEvent = async (event: CalendarEvent) => {
    const res = await request.post<any, Result<unknown>>('/api/calendar/events', event)
    if (res.code === 200) {
      await fetchEvents()
      if (selectedDate.value) {
        await fetchDateEvents(selectedDate.value)
      }
    }
    return res
  }

  const updateEvent = async (event: CalendarEvent) => {
    const res = await request.put<any, Result<unknown>>('/api/calendar/events', event)
    if (res.code === 200) {
      await fetchEvents()
      if (selectedDate.value) {
        await fetchDateEvents(selectedDate.value)
      }
    }
    return res
  }

  const deleteEvent = async (id: number) => {
    const res = await request.delete<any, Result<unknown>>(`/api/calendar/events/${id}`)
    if (res.code === 200) {
      await fetchEvents()
      if (selectedDate.value) {
        await fetchDateEvents(selectedDate.value)
      }
    }
    return res
  }

  const exportICal = async (params: {
    startTime?: string
    endTime?: string
    eventTypes?: string[]
    employeeIds?: number[]
    calendarName?: string
  }) => {
    const date = currentDate.value
    const start = new Date(date.getFullYear(), date.getMonth(), 1)
    const end = new Date(date.getFullYear(), date.getMonth() + 1, 0, 23, 59, 59)
    const payload = {
      startTime: params.startTime || formatDateTime(start),
      endTime: params.endTime || formatDateTime(end),
      eventTypes: params.eventTypes || enabledEventTypes.value,
      employeeIds: params.employeeIds || enabledEmployeeIds.value,
      calendarName: params.calendarName || '团队日历'
    }
    const blob = await request.post<any, Blob>('/api/calendar/events/export', payload, {
      responseType: 'blob'
    })
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${payload.calendarName}.ics`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    window.URL.revokeObjectURL(url)
  }

  const formatDateTime = (date: Date): string => {
    const pad = (n: number) => n.toString().padStart(2, '0')
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
  }

  const goToPrev = () => {
    const d = new Date(currentDate.value)
    if (viewMode.value === 'month') {
      d.setMonth(d.getMonth() - 1)
    } else {
      d.setDate(d.getDate() - 7)
    }
    currentDate.value = d
    fetchEvents()
  }

  const goToNext = () => {
    const d = new Date(currentDate.value)
    if (viewMode.value === 'month') {
      d.setMonth(d.getMonth() + 1)
    } else {
      d.setDate(d.getDate() + 7)
    }
    currentDate.value = d
    fetchEvents()
  }

  const goToToday = () => {
    currentDate.value = new Date()
    fetchEvents()
  }

  const setViewMode = (mode: ViewMode) => {
    viewMode.value = mode
    fetchEvents()
  }

  const toggleEventType = (type: string) => {
    const idx = enabledEventTypes.value.indexOf(type)
    if (idx >= 0) {
      enabledEventTypes.value.splice(idx, 1)
    } else {
      enabledEventTypes.value.push(type)
    }
    fetchEvents()
  }

  const toggleEmployeeFilter = (empId: number) => {
    const idx = enabledEmployeeIds.value.indexOf(empId)
    if (idx >= 0) {
      enabledEmployeeIds.value.splice(idx, 1)
    } else {
      enabledEmployeeIds.value.push(empId)
    }
    fetchEvents()
  }

  return {
    currentDate,
    viewMode,
    events,
    selectedDateEvents,
    selectedDate,
    eventTypes,
    enabledEventTypes,
    subscriptions,
    employees,
    enabledEmployeeIds,
    currentUserId,
    currentUserName,
    filteredEvents,
    getDefaultColor,
    getEventTypeLabel,
    fetchEventTypes,
    fetchEmployees,
    fetchEvents,
    fetchDateEvents,
    fetchSubscriptions,
    addSubscription,
    removeSubscription,
    createEvent,
    updateEvent,
    deleteEvent,
    exportICal,
    formatDateTime,
    goToPrev,
    goToNext,
    goToToday,
    setViewMode,
    toggleEventType,
    toggleEmployeeFilter
  }
})
