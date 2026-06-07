<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { Modal, Form, Input, Select, DatePicker, Switch, InputNumber, message } from 'ant-design-vue'
import type { CalendarEvent } from '../../types/calendar'
import { useCalendarStore } from '../../stores/calendar'
import dayjs from 'dayjs'

const store = useCalendarStore()

const props = defineProps<{
  visible: boolean
  event?: CalendarEvent | null
  defaultDateStr?: string
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
  (e: 'success'): void
}>()

const formRef = ref()
const formState = ref<Partial<CalendarEvent>>({
  title: '',
  description: '',
  eventType: 'CUSTOM',
  startTime: '',
  endTime: '',
  isAllDay: false,
  location: '',
  color: '',
  isPublic: true,
  department: '',
  employeeId: undefined,
  employeeName: ''
})

const isEdit = computed(() => !!props.event?.id)

watch(
  () => props.visible,
  (val) => {
    if (val) {
      if (props.event) {
        formState.value = { ...props.event }
      } else {
        const baseDate = props.defaultDateStr ? dayjs(props.defaultDateStr) : dayjs()
        formState.value = {
          title: '',
          description: '',
          eventType: 'CUSTOM',
          startTime: baseDate.hour(9).minute(0).format('YYYY-MM-DD HH:mm:ss'),
          endTime: baseDate.hour(10).minute(0).format('YYYY-MM-DD HH:mm:ss'),
          isAllDay: false,
          location: '',
          color: '#F759AB',
          isPublic: true,
          department: '',
          employeeId: store.currentUserId,
          employeeName: store.currentUserName
        }
      }
    }
  }
)

watch(
  () => formState.value.eventType,
  (val) => {
    if (val) {
      formState.value.color = store.getDefaultColor(val)
    }
  }
)

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (!formState.value.startTime || !formState.value.endTime) {
      message.error('请选择开始和结束时间')
      return
    }
    if (isEdit.value) {
      await store.updateEvent(formState.value as CalendarEvent)
      message.success('更新成功')
    } else {
      await store.createEvent(formState.value as CalendarEvent)
      message.success('创建成功')
    }
    emit('update:visible', false)
    emit('success')
  } catch (e) {
    console.error(e)
  }
}

const disabledStartDate = (current: any) => {
  return false
}
</script>

<template>
  <Modal
    :open="visible"
    :title="isEdit ? '编辑事件' : '新建事件'"
    @cancel="emit('update:visible', false)"
    @ok="handleSubmit"
    ok-text="保存"
    cancel-text="取消"
    width="520"
    destroy-on-close
  >
    <Form ref="formRef" layout="vertical" :model="formState">
      <Form.Item
        label="事件标题"
        name="title"
        :rules="[{ required: true, message: '请输入事件标题' }]"
      >
        <Input v-model:value="formState.title" placeholder="请输入事件标题" maxlength="200" />
      </Form.Item>

      <div style="display: flex; gap: 12px">
        <Form.Item label="事件类型" style="flex: 1">
          <Select
            v-model:value="formState.eventType"
            :options="store.eventTypes.map(t => ({ value: t.value, label: t.label }))"
          />
        </Form.Item>
        <Form.Item label="全天" style="width: 100px">
          <Switch v-model:checked="formState.isAllDay" />
        </Form.Item>
      </div>

      <div style="display: flex; gap: 12px">
        <Form.Item label="开始时间" style="flex: 1">
          <DatePicker
            v-model:value="formState.startTime"
            :show-time="!formState.isAllDay"
            :disabled-date="disabledStartDate"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            valueFormat="YYYY-MM-DD HH:mm:ss"
          />
        </Form.Item>
        <Form.Item label="结束时间" style="flex: 1">
          <DatePicker
            v-model:value="formState.endTime"
            :show-time="!formState.isAllDay"
            style="width: 100%"
            format="YYYY-MM-DD HH:mm:ss"
            valueFormat="YYYY-MM-DD HH:mm:ss"
          />
        </Form.Item>
      </div>

      <div style="display: flex; gap: 12px">
        <Form.Item label="事件颜色" style="width: 180px">
          <div style="display: flex; align-items: center; gap: 8px">
            <input
              type="color"
              v-model="formState.color"
              style="width: 32px; height: 32px; border: none; cursor: pointer; padding: 0"
            />
            <Input v-model:value="formState.color" style="flex: 1" />
          </div>
        </Form.Item>
        <Form.Item label="地点" style="flex: 1">
          <Input v-model:value="formState.location" placeholder="可选" />
        </Form.Item>
      </div>

      <div style="display: flex; gap: 12px">
        <Form.Item label="相关员工" style="flex: 1">
          <Select
            v-model:value="formState.employeeId"
            allow-clear
            :options="store.employees.map(e => ({ value: e.id, label: e.name + ' - ' + e.department }))"
            @change="(val: number) => {
              const emp = store.employees.find(e => e.id === val)
              formState.employeeName = emp?.name || ''
              formState.department = emp?.department || ''
            }"
            placeholder="选择相关员工（可选）"
          />
        </Form.Item>
        <Form.Item label="部门" style="flex: 1">
          <Input v-model:value="formState.department" placeholder="可选" />
        </Form.Item>
      </div>

      <Form.Item label="是否公开可见">
        <Switch v-model:checked="formState.isPublic" />
      </Form.Item>

      <Form.Item label="描述">
        <Input.TextArea
          v-model:value="formState.description"
          placeholder="请输入事件描述（可选）"
          :rows="3"
          maxlength="1000"
          show-count
        />
      </Form.Item>
    </Form>
  </Modal>
</template>
