<template>
  <div class="schedule-management">
    <header class="app-header">
      <div class="header-left">
        <h1 class="app-title">
          <ScheduleOutlined class="title-icon" />
          排班管理
        </h1>
      </div>
      <div class="header-right">
        <a-select
          v-model:value="selectedDepartment"
          style="width: 160px"
          placeholder="选择部门"
          allow-clear
          @change="handleDeptChange"
        >
          <a-select-option v-for="d in departments" :key="d" :value="d">
            {{ d }}
          </a-select-option>
        </a-select>
        <a-date-picker
          v-model:value="currentWeekDate"
          picker="week"
          :placeholder="'选择周'"
          @change="handleWeekChange"
        />
        <a-button @click="goPrevWeek">
          <LeftOutlined />
        </a-button>
        <a-button type="primary" ghost @click="goThisWeek">本周</a-button>
        <a-button @click="goNextWeek">
          <RightOutlined />
        </a-button>
        <span class="week-label">{{ weekLabel }}</span>
      </div>
    </header>

    <a-tabs v-model:activeKey="activeTab" class="schedule-tabs">
      <a-tab-pane key="matrix" tab="周视图排班">
        <div class="matrix-toolbar">
          <div class="toolbar-left">
            <a-button @click="handleCopyLastWeek">
              <CopyOutlined /> 复制上周
            </a-button>
            <a-button @click="showBatchFillModal = true">
              <AppstoreAddOutlined /> 批量填充
            </a-button>
            <a-button type="primary" @click="handleConfirmWeek">
              <CheckCircleOutlined /> 确认本周排班
            </a-button>
            <a-button danger @click="handleLockWeek" :disabled="matrixStatus !== 'CONFIRMED'">
              <LockOutlined /> 锁定排班
            </a-button>
            <a-button @click="handleExport">
              <DownloadOutlined /> 导出 Excel
            </a-button>
            <a-button @click="showChangeLog = true">
              <HistoryOutlined /> 变更记录
            </a-button>
          </div>
          <div class="toolbar-right">
            <a-tag v-if="matrixStatus === 'DRAFT'" color="orange">草稿</a-tag>
            <a-tag v-else-if="matrixStatus === 'CONFIRMED'" color="blue">已确认</a-tag>
            <a-tag v-else-if="matrixStatus === 'LOCKED'" color="green">已锁定</a-tag>
            <a-badge
              :count="alertCount"
              :number-style="{ backgroundColor: '#ff4d4f' }"
              offset="[-2, 2]"
            >
              <a-button @click="showAlerts = true">
                <WarningOutlined /> 冲突告警
              </a-button>
            </a-badge>
          </div>
        </div>

        <div class="shift-legend">
          <span class="legend-title">班次图例：</span>
          <span
            v-for="s in shifts"
            :key="s.id"
            class="legend-item"
          >
            <span class="legend-color" :style="{ backgroundColor: s.color }"></span>
            {{ s.shiftName }}
            <span class="legend-time">{{ formatTime(s.startTime) }}-{{ formatTime(s.endTime) }}</span>
          </span>
        </div>

        <div class="matrix-container">
          <a-table
            :columns="matrixColumns"
            :data-source="matrixData"
            :pagination="false"
            size="small"
            bordered
            :scroll="{ y: 520 }"
            class="schedule-matrix"
          >
            <template #bodyCell="{ column, record, text }">
              <template v-if="column.key && column.key.startsWith('date-')">
                <div
                  class="schedule-cell"
                  :class="{
                    'has-alert': record.alerts?.[column.key],
                    'is-locked': matrixStatus === 'LOCKED'
                  }"
                  @click="handleCellClick(record, column.key)"
                >
                  <div
                    v-if="text?.shiftId"
                    class="shift-block"
                    :style="{ backgroundColor: text?.color + '30', borderLeftColor: text?.color }"
                  >
                    <div class="shift-name">{{ text?.shiftName }}</div>
                    <div class="shift-time" v-if="text?.startTime">
                      {{ formatTime(text.startTime) }}-{{ formatTime(text.endTime) }}
                    </div>
                  </div>
                  <div v-else class="empty-block" @click.stop="handleCellClick(record, column.key)">
                    <PlusOutlined />
                  </div>
                  <a-badge
                    v-if="record.alerts?.[column.key]"
                    class="alert-badge"
                    :count="0"
                    dot
                    :number-style="{ backgroundColor: '#ff4d4f', right: '4px', top: '4px' }"
                  />
                </div>
              </template>
            </template>
          </a-table>
        </div>
      </a-tab-pane>

      <a-tab-pane key="shifts" tab="班次定义">
        <div class="shift-manage">
          <div class="shift-toolbar">
            <a-select
              v-model:value="shiftDept"
              style="width: 160px"
              placeholder="选择部门"
              @change="fetchShifts"
            >
              <a-select-option v-for="d in departments" :key="d" :value="d">
                {{ d }}
              </a-select-option>
            </a-select>
            <a-button type="primary" @click="openShiftForm()">
              <PlusOutlined /> 新增班次
            </a-button>
          </div>
          <a-table :columns="shiftColumns" :data-source="shifts" :pagination="false" bordered>
            <template #bodyCell="{ column, record, text }">
              <template v-if="column.key === 'color'">
                <span class="color-demo" :style="{ backgroundColor: record.color }"></span>
                {{ record.color }}
              </template>
              <template v-else-if="column.key === 'timeRange'">
                {{ formatTime(record.startTime) }} - {{ formatTime(record.endTime) }}
                <a-tag v-if="record.isCrossDay" color="purple">跨天</a-tag>
              </template>
              <template v-else-if="column.key === 'action'">
                <a-button size="small" type="link" @click="openShiftForm(record)">编辑</a-button>
                <a-popconfirm title="确认停用该班次？" @confirm="deactivateShift(record.id)">
                  <a-button size="small" type="link" danger>停用</a-button>
                </a-popconfirm>
              </template>
            </template>
          </a-table>
        </div>
      </a-tab-pane>

      <a-tab-pane key="attendance" tab="考勤联动">
        <div class="attendance-toolbar">
          <a-range-picker v-model:value="attendanceRange" />
          <a-select
            v-model:value="attendanceDept"
            style="width: 140px"
            placeholder="部门"
            allow-clear
          >
            <a-select-option v-for="d in departments" :key="d" :value="d">
              {{ d }}
            </a-select-option>
          </a-select>
          <a-select
            v-model:value="attendanceAbnormal"
            style="width: 140px"
            placeholder="异常状态"
            allow-clear
          >
            <a-select-option value="1">仅异常</a-select-option>
            <a-select-option value="0">仅正常</a-select-option>
          </a-select>
          <a-button type="primary" @click="fetchAttendance">查询</a-button>
        </div>
        <a-table :columns="attendanceColumns" :data-source="attendanceList" :pagination="{ pageSize: 10 }" bordered>
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'status'">
              <template v-if="record.isAbnormal">
                <a-tag color="red">{{ getAbnormalLabel(record.abnormalType) }}</a-tag>
                <span v-if="record.abnormalReason" class="abnormal-reason">
                  {{ record.abnormalReason }}
                </span>
              </template>
              <a-tag v-else color="green">正常</a-tag>
            </template>
          </template>
        </a-table>
      </a-tab-pane>
    </a-tabs>

    <a-modal
      v-model:open="showShiftForm"
      :title="editingShift?.id ? '编辑班次' : '新增班次'"
      @ok="saveShift"
    >
      <a-form :model="shiftForm" layout="vertical">
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="班次编码" required>
              <a-select v-model:value="shiftForm.shiftCode" :disabled="!!editingShift?.id">
                <a-select-option value="MORNING">早班 MORNING</a-select-option>
                <a-select-option value="EVENING">晚班 EVENING</a-select-option>
                <a-select-option value="NIGHT_SHIFT">大夜班 NIGHT_SHIFT</a-select-option>
                <a-select-option value="REST">休息 REST</a-select-option>
                <a-select-option value="CUSTOM">自定义 CUSTOM</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="班次名称" required>
              <a-input v-model:value="shiftForm.shiftName" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="所属部门" required>
              <a-select v-model:value="shiftForm.department">
                <a-select-option v-for="d in departments" :key="d" :value="d">{{ d }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="显示颜色">
              <a-input v-model:value="shiftForm.color" placeholder="#1890FF" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="开始时间" required>
              <a-time-picker v-model:value="shiftForm.startTimeObj" format="HH:mm" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="结束时间" required>
              <a-time-picker v-model:value="shiftForm.endTimeObj" format="HH:mm" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="是否跨天">
              <a-switch v-model:checked="shiftForm.isCrossDay" />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="排序">
              <a-input-number v-model:value="shiftForm.sortOrder" :min="0" />
            </a-form-item>
          </a-col>
          <a-col :span="24">
            <a-form-item label="备注说明">
              <a-textarea v-model:value="shiftForm.description" :rows="2" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="showCellModal"
      title="设置排班"
      @ok="saveCellSchedule"
    >
      <a-form :model="cellForm" layout="vertical">
        <a-form-item label="员工">
          <span>{{ cellForm.employeeName }}</span>
        </a-form-item>
        <a-form-item label="日期">
          <span>{{ cellForm.scheduleDate }}</span>
        </a-form-item>
        <a-form-item label="班次" required>
          <a-select v-model:value="cellForm.shiftId" @change="onShiftSelect">
            <a-select-option v-for="s in shifts" :key="s.id" :value="s.id">
              {{ s.shiftName }} ({{ formatTime(s.startTime) }}-{{ formatTime(s.endTime) }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="变更原因" required>
          <a-textarea v-model:value="cellForm.changeReason" :rows="2" placeholder="请输入变更原因" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="cellForm.remark" :rows="2" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="showBatchFillModal"
      title="批量填充排班"
      @ok="handleBatchFill"
      :ok-text="确定填充"
    >
      <a-form :model="batchForm" layout="vertical">
        <a-form-item label="填充范围" required>
          <a-radio-group v-model:value="batchForm.rangeType">
            <a-radio value="department">全部门</a-radio>
            <a-radio value="team">指定班组</a-radio>
            <a-radio value="employees">指定员工</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="batchForm.rangeType === 'team'" label="班组">
          <a-input v-model:value="batchForm.teamGroup" placeholder="班组名称" />
        </a-form-item>
        <a-form-item v-if="batchForm.rangeType === 'employees'" label="选择员工">
          <a-select
            v-model:value="batchForm.employeeIds"
            mode="multiple"
            :max-tag-count="3"
            placeholder="请选择员工"
          >
            <a-select-option v-for="r in matrixData" :key="r.employeeId" :value="r.employeeId">
              {{ r.employeeName }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="日期范围" required>
          <a-range-picker v-model:value="batchForm.dateRange" />
        </a-form-item>
        <a-form-item label="选择班次" required>
          <a-select v-model:value="batchForm.shiftId">
            <a-select-option v-for="s in shifts" :key="s.id" :value="s.id">
              {{ s.shiftName }} ({{ formatTime(s.startTime) }}-{{ formatTime(s.endTime) }})
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="变更原因" required>
          <a-textarea v-model:value="batchForm.changeReason" :rows="2" placeholder="请输入变更原因" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="showCopyModal"
      title="复制上周排班"
      @ok="doCopyWeek"
    >
      <a-form :model="copyForm" layout="vertical">
        <a-form-item label="源周">
          <span>{{ copyForm.sourceWeek }}</span>
        </a-form-item>
        <a-form-item label="目标周">
          <span>{{ copyForm.targetWeek }}</span>
        </a-form-item>
        <a-form-item label="复制范围">
          <a-radio-group v-model:value="copyForm.scope">
            <a-radio value="department">全部门</a-radio>
            <a-radio value="team">指定班组</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item v-if="copyForm.scope === 'team'" label="班组">
          <a-input v-model:value="copyForm.teamGroup" placeholder="班组名称" />
        </a-form-item>
        <a-form-item label="变更原因" required>
          <a-textarea v-model:value="copyForm.changeReason" :rows="2" placeholder="请输入变更原因" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-drawer v-model:open="showAlerts" title="排班冲突告警" width="480">
      <a-empty v-if="!alerts || alerts.length === 0" description="暂无告警" />
      <a-list v-else item-layout="vertical">
        <a-list-item v-for="alert in alerts" :key="alert.id">
          <a-list-item-meta>
            <template #title>
              <a-tag :color="getAlertColor(alert.severity)">
                {{ getAlertTypeLabel(alert.alertType) }}
              </a-tag>
              <span v-if="alert.alertDate" class="alert-date">{{ alert.alertDate }}</span>
            </template>
            <template #description>
              <div class="alert-info">
                <span class="alert-employee">{{ alert.employeeName }}</span>
                <span v-if="alert.department">· {{ alert.department }}</span>
              </div>
              <div class="alert-message">{{ alert.message }}</div>
              <div v-if="alert.detail" class="alert-detail">详情：{{ alert.detail }}</div>
              <template v-if="!alert.isResolved">
                <a-button size="small" type="link" @click="resolveAlert(alert.id)">标记已处理</a-button>
              </template>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
    </a-drawer>

    <a-drawer v-model:open="showChangeLog" title="排班变更记录" width="560">
      <div class="changelog-toolbar">
        <a-range-picker v-model:value="changelogRange" />
        <a-button type="primary" @click="fetchChangeLogs">查询</a-button>
      </div>
      <a-timeline v-if="changeLogs.length">
        <a-timeline-item v-for="log in changeLogs" :key="log.id">
          <div class="changelog-item">
            <div class="changelog-header">
              <a-tag color="blue">{{ getChangeTypeLabel(log.changeType) }}</a-tag>
              <span class="changelog-operator">{{ log.operatorName }}</span>
              <span class="changelog-time">{{ log.changedAt }}</span>
            </div>
            <div class="changelog-body">
              <span>{{ log.employeeName }}</span>
              <span class="changelog-date">{{ log.scheduleDate }}</span>
              <template v-if="log.oldShiftName">
                <span class="changelog-arrow">{{ log.oldShiftName }}</span>
                <RightOutlined />
              </template>
              <span class="changelog-new">{{ log.newShiftName }}</span>
            </div>
            <div class="changelog-reason">原因：{{ log.changeReason }}</div>
          </div>
        </a-timeline-item>
      </a-timeline>
      <a-empty v-else description="暂无变更记录" />
    </a-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch, reactive } from 'vue'
import { message, Modal } from 'ant-design-vue'
import dayjs, { Dayjs } from 'dayjs'
import isoWeek from 'dayjs/plugin/isoWeek'
import 'dayjs/locale/zh-cn'
import {
  ScheduleOutlined,
  LeftOutlined,
  RightOutlined,
  CopyOutlined,
  AppstoreAddOutlined,
  CheckCircleOutlined,
  LockOutlined,
  DownloadOutlined,
  HistoryOutlined,
  PlusOutlined,
  WarningOutlined
} from '@ant-design/icons-vue'
import { scheduleApi } from '../api/schedule'
import type {
  ShiftDefinition,
  ScheduleWeekMatrix,
  ScheduleCell,
  ScheduleAlert,
  ScheduleChangeLog,
  AttendanceCompare,
  ShiftDefinitionDTO
} from '../types/schedule'

dayjs.extend(isoWeek)
dayjs.locale('zh-cn')

const departments = ['技术部', '产品部', '设计部', '人力资源部']
const selectedDepartment = ref<string>()
const currentWeekDate = ref<Dayjs>(dayjs())
const activeTab = ref('matrix')

const weekMatrix = ref<ScheduleWeekMatrix | null>(null)
const shifts = ref<ShiftDefinition[]>([])
const alerts = ref<ScheduleAlert[]>([])
const changeLogs = ref<ScheduleChangeLog[]>([])
const attendanceList = ref<AttendanceCompare[]>([])

const matrixStatus = computed(() => weekMatrix.value?.status || 'DRAFT')
const alertCount = computed(() => alerts.value.filter((a) => !a.isResolved).length)

const weekLabel = computed(() => {
  const start = dayjs(currentWeekDate.value).startOf('isoWeek')
  const end = dayjs(currentWeekDate.value).endOf('isoWeek')
  return `${start.format('YYYY年M月D日')} - ${end.format('M月D日')} (第${start.isoWeek()}周)`
})

const currentWeekStr = computed(() => {
  const d = dayjs(currentWeekDate.value)
  return `${d.isoWeekYear()}-${String(d.isoWeek()).padStart(2, '0')}`
})

const lastWeekStr = computed(() => {
  const d = dayjs(currentWeekDate.value).subtract(1, 'week')
  return `${d.isoWeekYear()}-${String(d.isoWeek()).padStart(2, '0')}`
})

const matrixColumns = computed(() => {
  const cols: any[] = [
    { title: '员工', dataIndex: 'employeeName', key: 'name', fixed: 'left', width: 100 },
    { title: '部门', dataIndex: 'department', key: 'dept', fixed: 'left', width: 100 }
  ]
  if (weekMatrix.value) {
    weekMatrix.value.weekDates.forEach((dateStr) => {
      const d = dayjs(dateStr)
      cols.push({
        title: () => {
          const dayNames = ['一', '二', '三', '四', '五', '六', '日']
          return [
            d.format('M/D'),
            h('div', { style: 'font-weight:normal;color:#999;font-size:12px' }, `周${dayNames[d.isoWeekday() - 1]}`)
          ]
        },
        key: `date-${dateStr}`,
        width: 140,
        align: 'center'
      })
    })
  }
  return cols
})

const matrixData = computed(() => {
  if (!weekMatrix.value) return []
  const alertMap = new Map<string, Set<string>>()
  for (const a of alerts.value) {
    if (a.isResolved) continue
    const dates = new Set<string>()
    if (a.alertDate) dates.add(a.alertDate)
    if (a.alertStartDate && a.alertEndDate) {
      let d = dayjs(a.alertStartDate)
      const end = dayjs(a.alertEndDate)
      while (d.isBefore(end) || d.isSame(end)) {
        dates.add(d.format('YYYY-MM-DD'))
        d = d.add(1, 'day')
      }
    }
    const key = String(a.employeeId)
    if (!alertMap.has(key)) alertMap.set(key, new Set())
    dates.forEach((x) => alertMap.get(key)!.add(`date-${x}`))
  }

  return weekMatrix.value.employeeRows.map((row) => {
    const data: any = {
      employeeId: row.employeeId,
      employeeName: row.employeeName,
      department: row.department,
      alerts: {} as Record<string, boolean>
    }
    for (const dateStr of weekMatrix.value!.weekDates) {
      const cell = row.scheduleCells[dateStr]
      data[`date-${dateStr}`] = cell
      if (alertMap.get(String(row.employeeId))?.has(`date-${dateStr}`)) {
        data.alerts[`date-${dateStr}`] = true
      }
    }
    return data
  })
})

const shiftColumns = [
  { title: '班次编码', dataIndex: 'shiftCode', key: 'shiftCode', width: 120 },
  { title: '班次名称', dataIndex: 'shiftName', key: 'shiftName', width: 100 },
  { title: '部门', dataIndex: 'department', key: 'department', width: 120 },
  { title: '时间段', key: 'timeRange', width: 200 },
  { title: '颜色', key: 'color', width: 140 },
  { title: '跨天', dataIndex: 'isCrossDay', key: 'isCrossDay', width: 80 },
  { title: '备注', dataIndex: 'description', key: 'description' },
  { title: '操作', key: 'action', width: 140 }
]

const attendanceColumns = [
  { title: '日期', dataIndex: 'attendanceDate', key: 'attendanceDate', width: 110 },
  { title: '员工', dataIndex: 'employeeName', key: 'employeeName', width: 90 },
  { title: '部门', dataIndex: 'department', key: 'department', width: 100 },
  { title: '班次', dataIndex: 'shiftName', key: 'shiftName', width: 80 },
  { title: '排班时间', key: 'scheduleTime', width: 140,
    customRender: ({ record }: any) =>
      record.scheduledStartTime ? `${record.scheduledStartTime?.slice(0, 5)}-${record.scheduledEndTime?.slice(0, 5)}` : '-'
  },
  { title: '上班打卡', dataIndex: 'punchInTime', key: 'punchInTime', width: 150,
    customRender: ({ text }: any) => text ? dayjs(text).format('MM-DD HH:mm') : '-'
  },
  { title: '下班打卡', dataIndex: 'punchOutTime', key: 'punchOutTime', width: 150,
    customRender: ({ text }: any) => text ? dayjs(text).format('MM-DD HH:mm') : '-'
  },
  { title: '工时(h)', dataIndex: 'workHours', key: 'workHours', width: 80 },
  { title: '状态', key: 'status' }
]

const showShiftForm = ref(false)
const editingShift = ref<ShiftDefinition | null>(null)
const shiftForm = reactive<any>({})
const shiftDept = ref<string>()

const showCellModal = ref(false)
const cellForm = reactive<any>({})

const showBatchFillModal = ref(false)
const batchForm = reactive<any>({
  rangeType: 'department',
  employeeIds: [],
  dateRange: null,
  shiftId: null,
  changeReason: ''
})

const showCopyModal = ref(false)
const copyForm = reactive<any>({
  sourceWeek: '',
  targetWeek: '',
  scope: 'department',
  teamGroup: '',
  changeReason: ''
})

const showAlerts = ref(false)
const showChangeLog = ref(false)
const changelogRange = ref<[Dayjs, Dayjs] | null>(null)

const attendanceRange = ref<[Dayjs, Dayjs] | null>(null)
const attendanceDept = ref<string>()
const attendanceAbnormal = ref<string>()

const fetchWeekMatrix = async () => {
  try {
    const data = await scheduleApi.getWeekMatrix({
      department: selectedDepartment.value,
      scheduleWeek: currentWeekStr.value
    })
    weekMatrix.value = data as any
    alerts.value = (data as any).alerts || []
  } catch (e: any) {
    console.error(e)
    message.error(e.message || '加载排班数据失败')
  }
}

const fetchShifts = async () => {
  try {
    shifts.value = (await scheduleApi.getShifts(shiftDept.value || selectedDepartment.value)) as any
  } catch (e: any) {
    console.error(e)
  }
}

const fetchAlerts = async () => {
  try {
    alerts.value = (await scheduleApi.getAlerts({
      department: selectedDepartment.value,
      isResolved: false
    })) as any
  } catch (e: any) {
    console.error(e)
  }
}

const fetchChangeLogs = async () => {
  try {
    const params: any = {}
    if (changelogRange.value) {
      params.startDate = changelogRange.value[0].format('YYYY-MM-DD')
      params.endDate = changelogRange.value[1].format('YYYY-MM-DD')
    }
    changeLogs.value = (await scheduleApi.getChangeLogs(params)) as any
  } catch (e: any) {
    console.error(e)
  }
}

const fetchAttendance = async () => {
  try {
    const params: any = {}
    if (attendanceRange.value) {
      params.startDate = attendanceRange.value[0].format('YYYY-MM-DD')
      params.endDate = attendanceRange.value[1].format('YYYY-MM-DD')
    }
    if (attendanceDept.value) params.department = attendanceDept.value
    if (attendanceAbnormal.value !== undefined) params.isAbnormal = attendanceAbnormal.value === '1'
    attendanceList.value = (await scheduleApi.queryAttendance(params)) as any
  } catch (e: any) {
    console.error(e)
  }
}

const handleDeptChange = () => {
  fetchWeekMatrix()
  fetchShifts()
}

const handleWeekChange = (val: Dayjs | null) => {
  if (val) {
    currentWeekDate.value = val
    fetchWeekMatrix()
  }
}

const goPrevWeek = () => {
  currentWeekDate.value = dayjs(currentWeekDate.value).subtract(1, 'week')
  fetchWeekMatrix()
}

const goNextWeek = () => {
  currentWeekDate.value = dayjs(currentWeekDate.value).add(1, 'week')
  fetchWeekMatrix()
}

const goThisWeek = () => {
  currentWeekDate.value = dayjs()
  fetchWeekMatrix()
}

const openShiftForm = (record?: ShiftDefinition) => {
  editingShift.value = record || null
  if (record) {
    Object.assign(shiftForm, {
      ...record,
      startTimeObj: record.startTime ? dayjs(record.startTime, 'HH:mm:ss') : null,
      endTimeObj: record.endTime ? dayjs(record.endTime, 'HH:mm:ss') : null
    })
  } else {
    Object.assign(shiftForm, {
      shiftCode: 'MORNING',
      shiftName: '',
      department: shiftDept.value || selectedDepartment.value || departments[0],
      startTimeObj: dayjs('09:00', 'HH:mm'),
      endTimeObj: dayjs('18:00', 'HH:mm'),
      isCrossDay: false,
      color: '#1890FF',
      sortOrder: 0,
      description: ''
    })
  }
  showShiftForm.value = true
}

const saveShift = async () => {
  try {
    const payload: any = {
      ...shiftForm,
      startTime: shiftForm.startTimeObj?.format('HH:mm:ss'),
      endTime: shiftForm.endTimeObj?.format('HH:mm:ss')
    }
    if (editingShift.value?.id) {
      await scheduleApi.updateShift(editingShift.value.id, payload)
      message.success('班次更新成功')
    } else {
      await scheduleApi.createShift(payload)
      message.success('班次创建成功')
    }
    showShiftForm.value = false
    fetchShifts()
  } catch (e: any) {
    message.error(e.message || '保存失败')
  }
}

const deactivateShift = async (id: number) => {
  try {
    await scheduleApi.deleteShift(id)
    message.success('已停用')
    fetchShifts()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  }
}

const handleCellClick = (record: any, colKey: string) => {
  if (matrixStatus.value === 'LOCKED') {
    message.warning('排班已锁定，无法修改')
    return
  }
  const dateStr = colKey.replace('date-', '')
  const cell = record[colKey] as ScheduleCell | undefined
  cellForm.employeeId = record.employeeId
  cellForm.employeeName = record.employeeName
  cellForm.scheduleDate = dateStr
  cellForm.shiftId = cell?.shiftId || null
  cellForm.shiftCode = cell?.shiftCode
  cellForm.shiftName = cell?.shiftName
  cellForm.remark = cell?.remark || ''
  cellForm.changeReason = ''
  showCellModal.value = true
}

const onShiftSelect = (id: number) => {
  const s = shifts.value.find((x) => x.id === id)
  if (s) {
    cellForm.shiftCode = s.shiftCode
    cellForm.shiftName = s.shiftName
  }
}

const saveCellSchedule = async () => {
  try {
    if (!cellForm.shiftId) {
      message.warning('请选择班次')
      return
    }
    if (!cellForm.changeReason) {
      message.warning('请输入变更原因')
      return
    }
    await scheduleApi.updateSingleSchedule({ ...cellForm })
    message.success('排班已更新')
    showCellModal.value = false
    fetchWeekMatrix()
    fetchAlerts()
  } catch (e: any) {
    message.error(e.message || '保存失败')
  }
}

const handleBatchFill = async () => {
  try {
    if (!batchForm.shiftId) {
      message.warning('请选择班次')
      return
    }
    if (!batchForm.dateRange || batchForm.dateRange.length < 2) {
      message.warning('请选择日期范围')
      return
    }
    if (!batchForm.changeReason) {
      message.warning('请输入变更原因')
      return
    }
    const shift = shifts.value.find((s) => s.id === batchForm.shiftId)
    const payload: any = {
      changeReason: batchForm.changeReason,
      department: batchForm.rangeType === 'department' ? selectedDepartment.value : undefined,
      teamGroup: batchForm.rangeType === 'team' ? batchForm.teamGroup : undefined,
      employeeIds: batchForm.rangeType === 'employees' ? batchForm.employeeIds : undefined,
      startDate: batchForm.dateRange[0].format('YYYY-MM-DD'),
      endDate: batchForm.dateRange[1].format('YYYY-MM-DD'),
      shiftId: batchForm.shiftId,
      shiftCode: shift?.shiftCode,
      shiftName: shift?.shiftName
    }
    const count = await scheduleApi.batchUpdateSchedules(payload)
    message.success(`已批量更新 ${count} 条排班`)
    showBatchFillModal.value = false
    fetchWeekMatrix()
    fetchAlerts()
  } catch (e: any) {
    message.error(e.message || '批量填充失败')
  }
}

const handleCopyLastWeek = () => {
  copyForm.sourceWeek = lastWeekStr.value
  copyForm.targetWeek = currentWeekStr.value
  copyForm.scope = 'department'
  copyForm.changeReason = ''
  showCopyModal.value = true
}

const doCopyWeek = async () => {
  try {
    if (!copyForm.changeReason) {
      message.warning('请输入变更原因')
      return
    }
    const count = await scheduleApi.copyWeekSchedule({
      changeReason: copyForm.changeReason,
      sourceWeek: copyForm.sourceWeek,
      targetWeek: copyForm.targetWeek,
      department: copyForm.scope === 'department' ? selectedDepartment.value : undefined,
      teamGroup: copyForm.scope === 'team' ? copyForm.teamGroup : undefined
    })
    message.success(`已复制 ${count} 条排班`)
    showCopyModal.value = false
    fetchWeekMatrix()
    fetchAlerts()
  } catch (e: any) {
    message.error(e.message || '复制失败')
  }
}

const handleConfirmWeek = () => {
  Modal.confirm({
    title: '确认本周排班',
    content: `确认后将标记为已确认状态，可继续编辑。是否确认？`,
    onOk: async () => {
      try {
        const count = await scheduleApi.confirmSchedules({
          scheduleWeek: currentWeekStr.value,
          department: selectedDepartment.value,
          remark: '本周排班已确认'
        })
        message.success(`已确认 ${count} 条排班`)
        fetchWeekMatrix()
      } catch (e: any) {
        message.error(e.message || '确认失败')
      }
    }
  })
}

const handleLockWeek = () => {
  Modal.confirm({
    title: '锁定排班',
    content: '锁定后无法再修改排班，是否继续？',
    okType: 'danger',
    onOk: async () => {
      try {
        const count = await scheduleApi.lockSchedules({
          scheduleWeek: currentWeekStr.value,
          department: selectedDepartment.value
        })
        message.success(`已锁定 ${count} 条排班`)
        fetchWeekMatrix()
      } catch (e: any) {
        message.error(e.message || '锁定失败')
      }
    }
  })
}

const handleExport = async () => {
  try {
    const blob = (await scheduleApi.exportExcel({
      department: selectedDepartment.value,
      scheduleWeek: currentWeekStr.value
    })) as any
    const url = window.URL.createObjectURL(new Blob([blob]))
    const link = document.createElement('a')
    link.href = url
    link.download = `排班表_${currentWeekStr.value}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    message.success('导出成功')
  } catch (e: any) {
    message.error(e.message || '导出失败')
  }
}

const resolveAlert = async (id: number) => {
  try {
    await scheduleApi.resolveAlert(id, '已确认处理')
    message.success('已标记为处理')
    fetchAlerts()
    fetchWeekMatrix()
  } catch (e: any) {
    message.error(e.message || '操作失败')
  }
}

const formatTime = (t: string | undefined) => {
  if (!t) return ''
  return t.slice(0, 5)
}

const getAlertColor = (severity: string) => {
  switch (severity) {
    case 'ERROR': return 'red'
    case 'WARNING': return 'orange'
    default: return 'blue'
  }
}

const getAlertTypeLabel = (type: string) => {
  switch (type) {
    case 'MULTI_SHIFT': return '同日多班次'
    case 'CONSECUTIVE_NIGHT': return '连续大夜班超限'
    case 'REST_INTERVAL': return '休息间隔不足'
    default: return type
  }
}

const getChangeTypeLabel = (type: string) => {
  switch (type) {
    case 'CREATE': return '新增'
    case 'UPDATE': return '修改'
    case 'DELETE': return '删除'
    case 'BATCH_UPDATE': return '批量更新'
    case 'COPY': return '复制'
    default: return type
  }
}

const getAbnormalLabel = (type: string) => {
  if (!type) return '异常'
  const map: Record<string, string> = {
    LATE: '迟到',
    EARLY_LEAVE: '早退',
    ABSENT: '旷工',
    MISSING_PUNCH: '缺卡',
    LEAVE: '请假',
    OVERTIME: '加班'
  }
  return type.split(',').map((t) => map[t] || t).join('、')
}

const h = (tag: string, props: any, children?: any) => ({ tag, props, children })

onMounted(() => {
  fetchWeekMatrix()
  fetchShifts()
  fetchAlerts()
  fetchChangeLogs()
})

watch(showChangeLog, (val) => {
  if (val) fetchChangeLogs()
})
</script>

<style lang="scss" scoped>
.schedule-management {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f7fa;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 24px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;

  .header-left {
    display: flex;
    align-items: center;
  }

  .app-title {
    font-size: 20px;
    font-weight: 700;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 8px;

    .title-icon {
      color: #1677ff;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 8px;

    .week-label {
      font-size: 16px;
      font-weight: 600;
      margin-left: 8px;
    }
  }
}

.schedule-tabs {
  flex: 1;
  padding: 16px 24px;
  min-height: 0;
  display: flex;
  flex-direction: column;

  :deep(.ant-tabs-content) {
    flex: 1;
    min-height: 0;
    display: flex;
  }

  :deep(.ant-tabs-tabpane) {
    flex: 1;
    min-height: 0;
    display: flex;
    flex-direction: column;
  }
}

.matrix-toolbar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;

  .toolbar-left,
  .toolbar-right {
    display: flex;
    gap: 8px;
    align-items: center;
  }
}

.shift-legend {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 10px 14px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  margin-bottom: 12px;
  overflow-x: auto;

  .legend-title {
    color: #666;
    font-size: 13px;
    flex-shrink: 0;
  }

  .legend-item {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    flex-shrink: 0;

    .legend-color {
      display: inline-block;
      width: 14px;
      height: 14px;
      border-radius: 3px;
    }

    .legend-time {
      color: #999;
      font-size: 12px;
    }
  }
}

.matrix-container {
  flex: 1;
  min-height: 0;
  background: #fff;
  border-radius: 6px;
  overflow: hidden;

  :deep(.ant-table-cell) {
    padding: 0 !important;
  }
}

.schedule-cell {
  min-height: 64px;
  padding: 4px;
  cursor: pointer;
  position: relative;
  transition: background 0.2s;

  &:hover {
    background: #f0f7ff;
  }

  &.has-alert {
    background: #fff2f0;
  }

  &.is-locked {
    cursor: not-allowed;
    opacity: 0.8;
  }
}

.shift-block {
  border-left: 3px solid;
  background: #e6f4ff;
  padding: 6px 8px;
  border-radius: 4px;
  height: 100%;

  .shift-name {
    font-weight: 600;
    font-size: 13px;
    color: #1f1f1f;
  }

  .shift-time {
    font-size: 11px;
    color: #666;
    margin-top: 2px;
  }
}

.empty-block {
  height: 100%;
  min-height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
  font-size: 16px;
  border: 1px dashed #e8e8e8;
  border-radius: 4px;

  &:hover {
    border-color: #1890ff;
    color: #1890ff;
    background: #f0f7ff;
  }
}

.shift-manage {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .shift-toolbar {
    display: flex;
    gap: 8px;
  }

  .color-demo {
    display: inline-block;
    width: 16px;
    height: 16px;
    border-radius: 3px;
    vertical-align: middle;
    margin-right: 6px;
  }
}

.attendance-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;

  .abnormal-reason {
    color: #999;
    font-size: 12px;
    margin-left: 8px;
  }
}

.changelog-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.changelog-item {
  .changelog-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 6px;

    .changelog-operator {
      color: #1677ff;
    }

    .changelog-time {
      color: #999;
      font-size: 12px;
    }
  }

  .changelog-body {
    display: flex;
    align-items: center;
    gap: 6px;
    margin-bottom: 4px;

    .changelog-date {
      color: #666;
    }

    .changelog-arrow {
      color: #999;
    }

    .changelog-new {
      color: #52c41a;
      font-weight: 500;
    }
  }

  .changelog-reason {
    color: #666;
    font-size: 12px;
  }
}

.alert-info {
  margin-bottom: 4px;

  .alert-employee {
    font-weight: 500;
  }
}

.alert-message {
  color: #333;
}

.alert-detail {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

.alert-date {
  margin-left: 8px;
  color: #999;
  font-size: 12px;
}
</style>
