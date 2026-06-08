<template>
  <div class="employee-management">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <TeamOutlined /> 员工管理
        </h2>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="handleAdd" data-testid="add-employee-btn">
          <PlusOutlined /> 新增员工
        </a-button>
      </div>
    </div>

    <div class="search-panel">
      <a-form :model="searchForm" layout="inline" @finish="handleSearch">
        <a-form-item label="姓名">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入员工姓名"
            allow-clear
            data-testid="search-name"
          />
        </a-form-item>
        <a-form-item label="部门">
          <a-select
            v-model:value="searchForm.department"
            placeholder="请选择部门"
            allow-clear
            :options="departmentOptions"
            data-testid="search-department"
          />
        </a-form-item>
        <a-form-item label="职位">
          <a-input
            v-model:value="searchForm.role"
            placeholder="请输入职位"
            allow-clear
            data-testid="search-role"
          />
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit" data-testid="search-submit">
            <SearchOutlined /> 查询
          </a-button>
          <a-button style="margin-left: 8px" @click="handleResetSearch" data-testid="search-reset">
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="filteredEmployees"
        :loading="store.loading"
        :pagination="pagination"
        @change="handleTableChange"
        row-key="id"
        data-testid="employee-table"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-button type="link" size="small" @click="handleEdit(record)" :data-testid="`edit-btn-${record.id}`">
              编辑
            </a-button>
            <a-popconfirm
              title="确定要删除该员工吗？"
              ok-text="确定"
              cancel-text="取消"
              @confirm="handleDelete(record.id!)"
            >
              <a-button type="link" size="small" danger :data-testid="`delete-btn-${record.id}`">
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

    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑员工' : '新增员工'"
      @ok="handleSubmit"
      @cancel="handleCancel"
      :confirm-loading="modalLoading"
      :ok-text="isEdit ? '保存' : '创建'"
      cancel-text="取消"
      data-testid="employee-modal"
    >
      <a-form
        :model="employeeForm"
        layout="vertical"
        ref="formRef"
      >
        <a-form-item
          label="姓名"
          name="name"
          :rules="[{ required: true, message: '请输入姓名' }]"
        >
          <a-input v-model:value="employeeForm.name" placeholder="请输入姓名" data-testid="form-name" />
        </a-form-item>
        <a-form-item
          label="邮箱"
          name="email"
          :rules="[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '请输入有效的邮箱地址' }
          ]"
        >
          <a-input v-model:value="employeeForm.email" placeholder="请输入邮箱" data-testid="form-email" />
        </a-form-item>
        <a-form-item
          label="部门"
          name="department"
          :rules="[{ required: true, message: '请选择部门' }]"
        >
          <a-select
            v-model:value="employeeForm.department"
            placeholder="请选择部门"
            :options="departmentOptions"
            data-testid="form-department"
          />
        </a-form-item>
        <a-form-item
          label="职位"
          name="role"
          :rules="[{ required: true, message: '请输入职位' }]"
        >
          <a-input v-model:value="employeeForm.role" placeholder="请输入职位" data-testid="form-role" />
        </a-form-item>
        <a-form-item label="入职日期" name="hireDate">
          <a-date-picker
            v-model:value="employeeForm.hireDate"
            style="width: 100%"
            format="YYYY-MM-DD"
            data-testid="form-hireDate"
          />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="employeeForm.phone" placeholder="请输入手机号" data-testid="form-phone" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { message } from 'ant-design-vue'
import {
  TeamOutlined,
  PlusOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import dayjs, { Dayjs } from 'dayjs'
import type { TableColumnsType, TablePaginationConfig } from 'ant-design-vue'
import type { FormInstance } from 'ant-design-vue'
import { useEmployeeStore, type Employee } from '../stores/employee'

const store = useEmployeeStore()

const searchForm = reactive({
  name: '',
  department: undefined as string | undefined,
  role: ''
})

const pagination = reactive<TablePaginationConfig>({
  current: 1,
  pageSize: 10,
  total: 0
})

const filteredEmployees = computed(() => {
  let list = store.employees
  if (searchForm.name) {
    list = list.filter((e: Employee) => e.name.includes(searchForm.name))
  }
  if (searchForm.department) {
    list = list.filter((e: Employee) => e.department === searchForm.department)
  }
  if (searchForm.role) {
    list = list.filter((e: Employee) => e.role.includes(searchForm.role))
  }
  pagination.total = list.length
  const start = (pagination.current - 1) * pagination.pageSize
  return list.slice(start, start + pagination.pageSize)
})

watch(filteredEmployees, () => {
  pagination.total = store.employees.length
})

const departmentOptions = [
  { label: '技术部', value: '技术部' },
  { label: '产品部', value: '产品部' },
  { label: '运营部', value: '运营部' },
  { label: '市场部', value: '市场部' },
  { label: '人力资源部', value: '人力资源部' },
  { label: '财务部', value: '财务部' }
]

interface EmployeeFormData {
  id?: number
  name: string
  email: string
  department: string | undefined
  role: string
  hireDate: Dayjs | undefined
  phone: string | undefined
}

const modalVisible = ref(false)
const modalLoading = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const employeeForm = reactive<EmployeeFormData>({
  name: '',
  email: '',
  department: undefined,
  role: '',
  hireDate: undefined,
  phone: undefined
})

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
    key: 'name',
    dataIndex: 'name'
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

const handleAdd = () => {
  isEdit.value = false
  editingId.value = null
  resetForm()
  modalVisible.value = true
}

const handleEdit = (record: Employee) => {
  isEdit.value = true
  editingId.value = record.id as number
  employeeForm.id = record.id as number
  employeeForm.name = record.name
  employeeForm.email = record.email
  employeeForm.department = record.department
  employeeForm.role = record.role
  employeeForm.hireDate = record.hireDate ? dayjs(record.hireDate as string) : undefined
  employeeForm.phone = record.phone
  modalVisible.value = true
}

const handleDelete = async (id: number) => {
  try {
    await store.deleteEmployee(id)
    message.success('删除成功')
  } catch (e) {
    console.error(e)
  }
}

const handleSearch = () => {
  pagination.current = 1
}

const handleResetSearch = () => {
  searchForm.name = ''
  searchForm.department = undefined
  searchForm.role = ''
  pagination.current = 1
}

const handleTableChange = (pag: TablePaginationConfig) => {
  pagination.current = pag.current || 1
  pagination.pageSize = pag.pageSize || 10
}

const resetForm = () => {
  employeeForm.id = undefined
  employeeForm.name = ''
  employeeForm.email = ''
  employeeForm.department = undefined
  employeeForm.role = ''
  employeeForm.hireDate = undefined
  employeeForm.phone = undefined
  formRef.value?.resetFields()
}

const handleCancel = () => {
  modalVisible.value = false
  resetForm()
}

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    modalLoading.value = true
    const employeeData: Employee = {
      name: employeeForm.name,
      email: employeeForm.email,
      department: employeeForm.department as string,
      role: employeeForm.role,
      phone: employeeForm.phone,
      hireDate: employeeForm.hireDate ? employeeForm.hireDate.format('YYYY-MM-DD') as any : undefined
    }
    if (isEdit.value && editingId.value) {
      employeeData.id = editingId.value
      await store.updateEmployee(employeeData)
      message.success('更新成功')
    } else {
      await store.createEmployee(employeeData)
      message.success('创建成功')
    }
    modalVisible.value = false
    resetForm()
  } catch (e) {
    console.error(e)
  } finally {
    modalLoading.value = false
  }
}

const formatDate = (date: any) => {
  if (!date) return '-'
  if (dayjs.isDayjs(date)) return date.format('YYYY-MM-DD')
  if (typeof date === 'string') return date
  return dayjs(date as string).format('YYYY-MM-DD')
}

onMounted(async () => {
  await store.fetchEmployees()
})
</script>

<style lang="scss" scoped>
.employee-management {
  padding: 24px;
  min-height: 100vh;
  background: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0;
  background: transparent;
  border: none;
}

.header-left {
  .page-title {
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: #1a1a1a;
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.search-panel {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}

.table-container {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
}
</style>
