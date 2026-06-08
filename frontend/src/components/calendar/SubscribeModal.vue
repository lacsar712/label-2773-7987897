<script setup lang="ts">
import { ref, computed } from 'vue'
import { Modal, Select, message } from 'ant-design-vue'
import { useCalendarStore } from '../../stores/calendar'

const store = useCalendarStore()

const props = defineProps<{
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', v: boolean): void
}>()

const selectedId = ref<number | undefined>(undefined)

const availableEmployees = computed(() => {
  const subscribedIds = new Set(store.subscriptions.map(s => s.targetEmployeeId))
  return store.employees.filter(
    e => e.id !== store.currentUserId && !subscribedIds.has(e.id)
  )
})

const handleOk = async () => {
  if (!selectedId.value) {
    message.warning('请选择要订阅的同事')
    return
  }
  const emp = store.employees.find(e => e.id === selectedId.value)
  if (emp) {
    await store.addSubscription(emp.id, emp.name)
    message.success(`已订阅 ${emp.name} 的日历`)
    selectedId.value = undefined
    emit('update:visible', false)
  }
}
</script>

<template>
  <Modal
    :open="visible"
    title="订阅同事日历"
    @cancel="emit('update:visible', false)"
    @ok="handleOk"
    ok-text="订阅"
    cancel-text="取消"
    width="420"
  >
    <div style="margin-bottom: 8px">选择同事以订阅其公开日程：</div>
    <Select
      v-model:value="selectedId"
      :options="availableEmployees.map(e => ({ value: e.id, label: e.name + ' - ' + e.department + ' / ' + e.role }))"
      placeholder="选择同事"
      style="width: 100%"
      size="large"
      show-search
      :filter-option="(input: string, option: any) =>
        (option.label || '').toLowerCase().includes(input.toLowerCase())
      "
    />
    <div v-if="availableEmployees.length === 0" style="margin-top: 12px; color: #999; font-size: 13px">
      没有更多可订阅的同事。
    </div>
  </Modal>
</template>
