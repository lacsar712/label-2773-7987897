import { request } from '../utils/request'
import type {
  ShiftDefinition,
  ShiftDefinitionDTO,
  ScheduleWeekMatrix,
  EmployeeSchedule,
  ScheduleUpdateDTO,
  ScheduleBatchUpdateDTO,
  ScheduleCopyDTO,
  ScheduleConfirmDTO,
  ScheduleAlert,
  ScheduleChangeLog,
  AttendanceCompare,
  AttendancePunchDTO
} from '../types/schedule'

export const scheduleApi = {
  getShifts: (department?: string) =>
    request.get<ShiftDefinition[]>('/api/schedule/shifts', { params: { department } }),

  getShiftById: (id: number) =>
    request.get<ShiftDefinition>(`/api/schedule/shifts/${id}`),

  createShift: (data: ShiftDefinitionDTO) =>
    request.post<ShiftDefinition>('/api/schedule/shifts', data),

  updateShift: (id: number, data: ShiftDefinitionDTO) =>
    request.put<ShiftDefinition>(`/api/schedule/shifts/${id}`, data),

  deleteShift: (id: number) =>
    request.delete<boolean>(`/api/schedule/shifts/${id}`),

  getWeekMatrix: (params: { department?: string; scheduleWeek?: string; teamGroup?: string }) =>
    request.get<ScheduleWeekMatrix>('/api/schedule/week-matrix', { params }),

  querySchedules: (params: Record<string, any>) =>
    request.get<EmployeeSchedule[]>('/api/schedule/list', { params }),

  updateSingleSchedule: (data: ScheduleUpdateDTO) =>
    request.put<EmployeeSchedule>('/api/schedule/update-single', data),

  batchUpdateSchedules: (data: ScheduleBatchUpdateDTO) =>
    request.put<number>('/api/schedule/batch-update', data),

  copyWeekSchedule: (data: ScheduleCopyDTO) =>
    request.post<number>('/api/schedule/copy-week', data),

  confirmSchedules: (data: ScheduleConfirmDTO) =>
    request.post<number>('/api/schedule/confirm', data),

  lockSchedules: (params: { scheduleWeek?: string; department?: string }) =>
    request.post<number>('/api/schedule/lock', null, { params }),

  getChangeLogs: (params: {
    employeeId?: number
    startDate?: string
    endDate?: string
    operatorId?: number
  }) =>
    request.get<ScheduleChangeLog[]>('/api/schedule/change-logs', { params }),

  exportExcel: (params: { department?: string; scheduleWeek?: string }) =>
    request.get('/api/schedule/export/excel', {
      params,
      responseType: 'blob'
    }),

  getAlerts: (params: {
    department?: string
    startDate?: string
    endDate?: string
    isResolved?: boolean
  }) =>
    request.get<ScheduleAlert[]>('/api/schedule/alerts', { params }),

  resolveAlert: (id: number, note: string) =>
    request.post<boolean>(`/api/schedule/alerts/${id}/resolve`, null, {
      params: { note }
    }),

  punchInOut: (data: AttendancePunchDTO) =>
    request.post('/api/schedule/attendance/punch', data),

  queryAttendance: (params: Record<string, any>) =>
    request.get<AttendanceCompare[]>('/api/schedule/attendance/list', { params })
}
