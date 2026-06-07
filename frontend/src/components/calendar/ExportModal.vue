<script setup lang="ts">
import { ref, computed } from 'vue'
import { Modal, Form, DatePicker, Select, Input, message } from 'ant-design-vue'
import { useCalendarStore } from '../../stores/calendar'
import dayjs, { type Dayjs } from 'dayjs'

const store = useCalendarStore()

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
}>()

const formRef = ref()

const defaultRange = computed(() => {
  const d = store.currentDate
  const start = dayjs(new Date(d.getFullYear(), d.getMonth(), 1))
  const end = dayjs(new Date(d.getFullYear(), d.getMonth() + 1, 0))
  return [start, end] as [Dayjs, Dayjs]
})

const formState = ref({
  range: defaultRange.value,
  eventTypes: [] as string[],
  employeeIds: [] as number[],
  calendarName: '团队日历'
})

const handleOk = async () => {
  try {
    if (!formState.value.range || formState.value.range.length !== 2) {
      message.error('请选择日期范围')
      return
    }
    const pad = (n: number) => n.toString().padStart(2, '0')
    const formatDate = (d: Date) =>
      `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`

    await store.exportICal({
      startTime: formatDate(formState.value.range[0].toDate()),
      endTime: formatDate(formState.value.range[1].hour(23).minute(59).second(59).toDate()),
      eventTypes: formState.value.eventTypes.length > 0 ? formState.value.eventTypes : undefined,
      employeeIds: formState.value.employeeIds.length > 0 ? formState.value.employeeIds : undefined,
      calendarName: formState.value.calendarName || '团队日历'
    })
    message.success('导出成功')
    emit('update:visible', false)
  } catch (e) {
    console.error(e)
    message.error('导出失败')
  }
}
</script>

<template>
  <Modal
    :open="visible"
    title="导出 iCal 日历"
    @cancel="emit('update:visible', false)"
    @ok="handleOk"
    ok-text="导出"
    cancel-text="取消"
    width="480"
  >
    <Form ref="formRef" layout="vertical" :model="formState">
      <Form.Item label="日历名称">
        <Input v-model:value="formState.calendarName" placeholder="团队日历" />
      </Form.Item>
      <Form.Item label="日期范围" :rules="[{ required: true, message: '请选择日期范围' }]">
        <DatePicker.RangePicker
          v-model:value="formState.range"
          style="width: 100%"
          valueFormat="YYYY-MM-DD"
        />
      </Form.Item>
      <Form.Item label="事件类型（不选则导出全部）">
        <Select
          v-model:value="formState.eventTypes"
          mode="multiple"
          :options="store.eventTypes.map(t => ({ value: t.value, label: t.label }))"
          placeholder="选择事件类型"
          style="width: 100%"
        />
      </Form.Item>
      <Form.Item label="员工（不选则导出全部）">
        <Select
          v-model:value="formState.employeeIds"
          mode="multiple"
          :options="store.employees.map(e => ({ value: e.id, label: e.name + ' - ' + e.department }))"
          placeholder="选择员工"
          style="width: 100%"
        />
      </Form.Item>
      <div style="font-size: 12px; color: #999; margin-top: -8px">
        导出格式为标准 iCal (.ics)，可导入至 Google Calendar、Apple Calendar、Outlook 等日历应用。
      </div>
    </Form>
  </Modal>
</template>
