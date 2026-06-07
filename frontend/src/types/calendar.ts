export interface CalendarEvent {
  id?: number
  title: string
  description?: string
  eventType: string
  startTime: string
  endTime: string
  isAllDay?: boolean
  color?: string
  location?: string
  employeeId?: number
  employeeName?: string
  department?: string
  sourceModule?: string
  sourceId?: string
  isPublic?: boolean
  createdBy?: number
  createdAt?: string
  updatedAt?: string
}

export interface EventType {
  value: string
  label: string
  color: string
}

export interface CalendarSubscription {
  id?: number
  subscriberId: number
  subscriberName: string
  targetEmployeeId: number
  targetEmployeeName: string
  createdAt?: string
}

export interface Employee {
  id: number
  name: string
  email: string
  department: string
  role: string
  hireDate?: string
  isPublicCalendar?: boolean
}

export type ViewMode = 'month' | 'week'
