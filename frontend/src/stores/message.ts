import { defineStore } from 'pinia'
import request from '../utils/request'
import { message } from 'ant-design-vue'
import type {
  MessagePreviewVO,
  MessageVO,
  MessagePreferenceVO,
  MessageQueryDTO,
  MessageBatchDTO,
  MessagePreferenceDTO,
  PageResult
} from '../types/message'

const API_BASE = '/api/messages'

interface Result<T> {
  code: number
  message: string
  data: T
}

export const useMessageStore = defineStore('message', {
  state: () => ({
    preview: {
      unreadCount: 0,
      latestMessages: [] as MessageVO[]
    } as MessagePreviewVO,
    messages: [] as MessageVO[],
    total: 0,
    loading: false,
    preferences: [] as MessagePreferenceVO[],
    currentEmployeeId: 1
  }),
  actions: {
    async fetchPreview(employeeId?: number) {
      const empId = employeeId ?? this.currentEmployeeId
      try {
        const res = await request.get<any, Result<MessagePreviewVO>>(`${API_BASE}/preview`, {
          params: { employeeId: empId }
        })
        this.preview = res.data
      } catch (error) {
        // Error handled by interceptor
      }
    },
    async fetchMessages(query: MessageQueryDTO) {
      this.loading = true
      try {
        const params = { ...query, employeeId: query.employeeId ?? this.currentEmployeeId }
        const res = await request.get<any, Result<PageResult<MessageVO>>>(`${API_BASE}/list`, {
          params
        })
        this.messages = res.data.records
        this.total = res.data.total
      } catch (error) {
        // Error handled by interceptor
      } finally {
        this.loading = false
      }
    },
    async markAsRead(id: number, employeeId?: number) {
      const empId = employeeId ?? this.currentEmployeeId
      try {
        await request.post<any, Result<boolean>>(`${API_BASE}/${id}/read`, null, {
          params: { employeeId: empId }
        })
        await this.fetchPreview()
        return true
      } catch (error) {
        return false
      }
    },
    async batchMarkAsRead(messageIds?: number[], employeeId?: number) {
      const empId = employeeId ?? this.currentEmployeeId
      try {
        const dto: MessageBatchDTO = { employeeId: empId, messageIds }
        const res = await request.post<any, Result<number>>(`${API_BASE}/batch-read`, dto)
        message.success(`已标记 ${res.data} 条消息为已读`)
        await this.fetchPreview()
        return true
      } catch (error) {
        return false
      }
    },
    async batchClear(messageIds?: number[], employeeId?: number) {
      const empId = employeeId ?? this.currentEmployeeId
      try {
        const dto: MessageBatchDTO = { employeeId: empId, messageIds }
        const res = await request.post<any, Result<number>>(`${API_BASE}/batch-clear`, dto)
        message.success(`已清除 ${res.data} 条消息`)
        await this.fetchPreview()
        return true
      } catch (error) {
        return false
      }
    },
    async fetchPreferences(employeeId?: number) {
      const empId = employeeId ?? this.currentEmployeeId
      try {
        const res = await request.get<any, Result<MessagePreferenceVO[]>>(`${API_BASE}/preferences`, {
          params: { employeeId: empId }
        })
        this.preferences = res.data
      } catch (error) {
        // Error handled by interceptor
      }
    },
    async updatePreference(dto: MessagePreferenceDTO) {
      try {
        const data = { ...dto, employeeId: dto.employeeId ?? this.currentEmployeeId }
        await request.post<any, Result<boolean>>(`${API_BASE}/preferences`, data)
        message.success('设置已更新')
        await this.fetchPreferences()
        return true
      } catch (error) {
        return false
      }
    },
    setCurrentEmployee(id: number) {
      this.currentEmployeeId = id
    }
  }
})
