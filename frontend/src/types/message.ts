export type MessageEventType =
  | 'APPROVAL_FLOW'
  | 'ANNOUNCEMENT'
  | 'CONTRACT_EXPIRY'
  | 'ONBOARDING_OVERDUE'
  | 'ATTENDANCE_ABNORMAL'
  | 'SALARY_PAID'
  | 'PERFORMANCE_REMIND'
  | 'ATTACHMENT_EXPIRY'
  | 'SYSTEM'

export interface MessageVO {
  id: number
  eventType: MessageEventType
  eventTypeName: string
  title: string
  summary: string
  bizType: string
  bizId: string
  deepLink: string
  isRead: boolean
  readAt: string | null
  createdAt: string
}

export interface MessagePreviewVO {
  unreadCount: number
  latestMessages: MessageVO[]
}

export interface MessagePreferenceVO {
  eventType: MessageEventType
  eventTypeName: string
  pushEnabled: boolean
}

export interface MessageQueryDTO {
  employeeId: number
  status?: 'ALL' | 'UNREAD' | 'READ'
  eventType?: MessageEventType
  pageNum?: number
  pageSize?: number
}

export interface MessageBatchDTO {
  employeeId: number
  messageIds?: number[]
}

export interface MessagePreferenceDTO {
  employeeId: number
  eventType: MessageEventType
  pushEnabled: boolean
}

export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}
