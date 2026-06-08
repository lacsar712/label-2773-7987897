<template>
  <div class="table-container">
    <a-table
      :columns="columns"
      :data-source="dataSource"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
      row-key="id"
      data-testid="employee-table"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'action'">
          <a-button
            type="link"
            size="small"
            @click="$emit('edit', record)"
            :data-testid="`edit-btn-${record.id}`"
          >
            编辑
          </a-button>
          <a-popconfirm
            title="确定要删除该员工吗？"
            ok-text="确定"
            cancel-text="取消"
            @confirm="$emit('delete', record.id!)"
          >
            <a-button
              type="link"
              size="small"
              danger
              :data-testid="`delete-btn-${record.id}`"
            >
              删除
            </a-button>
          </a-popconfirm>
        </template>
        <template v-else-if="column.key === 'hireDate'">
          {{ formatDate(record.hireDate) }}
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import type { TableColumnsType, TablePaginationConfig } from 'ant-design-vue'
import dayjs from 'dayjs'
import type { Employee } from '../../stores/employee'

defineProps<{
  dataSource: Employee[]
  loading: boolean
  pagination: TablePaginationConfig
}>()

const emit = defineEmits<{
  (e: 'edit', record: Employee): void
  (e: 'delete', id: number): void
  (e: 'table-change', pag: TablePaginationConfig): void
}>()

const columns: TableColumnsType = [
  {
    title: 'ID',
    dataIndex: 'id',
    key: 'id',
    width: 80
  },
  {
    title: '姓名',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '邮箱',
    dataIndex: 'email',
    key: 'email'
  },
  {
    title: '部门',
    dataIndex: 'department',
    key: 'department'
  },
  {
    title: '职位',
    dataIndex: 'role',
    key: 'role'
  },
  {
    title: '入职日期',
    key: 'hireDate',
    dataIndex: 'hireDate'
  },
  {
    title: '操作',
    key: 'action',
    width: 150,
    fixed: 'right'
  }
]

const handleTableChange = (pag: TablePaginationConfig) => {
  emit('table-change', pag)
}

const formatDate = (date: any) => {
  if (!date) return '-'
  if (dayjs.isDayjs(date)) return date.format('YYYY-MM-DD')
  if (typeof date === 'string') return date
  return dayjs(date as string).format('YYYY-MM-DD')
}
</script>

<style lang="scss" scoped>
.table-container {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}
</style>
