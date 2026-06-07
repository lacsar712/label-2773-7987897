export interface ShiftDefinition {
  id?: number
  shiftCode: string
  shiftName: string
  department: string
  startTime: string
  endTime: string
  isCrossDay?: boolean
  color?: string
  sortOrder?: number
  isActive?: boolean
  description?: string
  createdBy?: number
  createdByName?: string
  createdAt?: string
  updatedAt?: string
}

export interface EmployeeSchedule {
  id?: number
  scheduleWeek?: string
  department?: string
  employeeId: number
  employeeName?: string
  teamGroup?: string
  scheduleDate: string
  shiftId: number
  shiftCode: string
  shiftName: string
  startTime?: string
  endTime?: string
  isCrossDay?: boolean
  effectiveStartDate?: string
  effectiveEndDate?: string
  status?: 'DRAFT' | 'CONFIRMED' | 'LOCKED'
  confirmedBy?: number
  confirmedByName?: string
  confirmedAt?: string
  remark?: string
  createdBy?: number
  createdByName?: string
  createdAt?: string
  updatedAt?: string
}

export interface ScheduleCell {
  scheduleId?: number
  scheduleDate: string
  shiftId?: number
  shiftCode?: string
  shiftName?: string
  color?: string
  startTime?: string
  endTime?: string
  isCrossDay?: boolean
  status?: string
  remark?: string
  hasAlert?: boolean
}

export interface ScheduleEmployeeRow {
  employeeId: number
  employeeName: string
  department: string
  teamGroup?: string
  scheduleCells: Record<string, ScheduleCell>
}

export interface ScheduleAlert {
  id: number
  alertType: 'MULTI_SHIFT' | 'CONSECUTIVE_NIGHT' | 'REST_INTERVAL'
  severity: 'INFO' | 'WARNING' | 'ERROR'
  department?: string
  employeeId: number
  employeeName: string
  alertDate?: string
  alertStartDate?: string
  alertEndDate?: string
  message: string
  detail?: string
  isResolved?: boolean
}

export interface ScheduleWeekMatrix {
  scheduleWeek: string
  department?: string
  weekStartDate: string
  weekEndDate: string
  weekDates: string[]
  employeeRows: ScheduleEmployeeRow[]
  alerts: ScheduleAlert[]
  status: string
}

export interface ScheduleChangeLog {
  id: number
  scheduleId?: number
  employeeId: number
  employeeName: string
  scheduleDate: string
  changeType: 'CREATE' | 'UPDATE' | 'DELETE' | 'BATCH_UPDATE' | 'COPY'
  oldShiftId?: number
  oldShiftCode?: string
  oldShiftName?: string
  newShiftId?: number
  newShiftCode?: string
  newShiftName?: string
  changeReason: string
  operatorId: number
  operatorName: string
  changedAt: string
}

export interface AttendanceCompare {
  attendanceId: number
  employeeId: number
  employeeName: string
  department: string
  attendanceDate: string
  scheduleId?: number
  shiftId?: number
  shiftCode?: string
  shiftName?: string
  scheduledStartTime?: string
  scheduledEndTime?: string
  punchInTime?: string
  punchOutTime?: string
  workHours?: number
  isLate?: boolean
  lateMinutes?: number
  isEarlyLeave?: boolean
  earlyLeaveMinutes?: number
  isAbsent?: boolean
  isAbnormal?: boolean
  abnormalType?: string
  abnormalReason?: string
}

export interface ShiftDefinitionDTO {
  id?: number
  shiftCode: string
  shiftName: string
  department: string
  startTime: string
  endTime: string
  isCrossDay?: boolean
  color?: string
  sortOrder?: number
  description?: string
}

export interface ScheduleUpdateDTO {
  changeReason: string
  employeeId: number
  employeeName?: string
  scheduleDate: string
  shiftId: number
  shiftCode?: string
  shiftName?: string
  startTime?: string
  endTime?: string
  isCrossDay?: boolean
  remark?: string
}

export interface ScheduleBatchUpdateDTO {
  changeReason: string
  department?: string
  teamGroup?: string
  employeeIds?: number[]
  startDate?: string
  endDate?: string
  dates?: string[]
  shiftId: number
  shiftCode?: string
  shiftName?: string
}

export interface ScheduleCopyDTO {
  changeReason: string
  sourceWeek: string
  targetWeek: string
  department?: string
  teamGroup?: string
}

export interface ScheduleConfirmDTO {
  scheduleIds?: number[]
  scheduleWeek?: string
  department?: string
  remark: string
}

export interface AttendancePunchDTO {
  employeeId: number
  employeeName?: string
  department?: string
  punchTime: string
  punchType?: 'IN' | 'OUT'
}
