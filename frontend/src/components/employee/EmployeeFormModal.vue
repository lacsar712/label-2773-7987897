<template>
  <a-modal
    v-model:open="localVisible"
    :title="isEdit ? '编辑员工' : '新增员工'"
    @ok="handleSubmit"
    @cancel="handleCancel"
    :confirm-loading="confirmLoading"
    :ok-text="isEdit ? '保存' : '创建'"
    cancel-text="取消"
    data-testid="employee-modal"
  >
    <a-form
      :model="form"
      layout="vertical"
      ref="formRef"
    >
      <a-form-item
        label="姓名"
        name="name"
        :rules="[{ required: true, message: '请输入姓名' }]"
      >
        <a-input
          v-model:value="form.name"
          placeholder="请输入姓名"
          data-testid="form-name"
        />
      </a-form-item>
      <a-form-item
        label="邮箱"
        name="email"
        :rules="[
          { required: true, message: '请输入邮箱' },
          { type: 'email', message: '请输入有效的邮箱地址' }
        ]"
      >
        <a-input
          v-model:value="form.email"
          placeholder="请输入邮箱"
          data-testid="form-email"
        />
      </a-form-item>
      <a-form-item
        label="部门"
        name="department"
        :rules="[{ required: true, message: '请选择部门' }]"
      >
        <a-select
          v-model:value="form.department"
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
        <a-input
          v-model:value="form.role"
          placeholder="请输入职位"
          data-testid="form-role"
        />
      </a-form-item>
      <a-form-item label="入职日期" name="hireDate">
        <a-date-picker
          v-model:value="form.hireDate"
          style="width: 100%"
          format="YYYY-MM-DD"
          data-testid="form-hireDate"
        />
      </a-form-item>
      <a-form-item label="手机号" name="phone">
        <a-input
          v-model:value="form.phone"
          placeholder="请输入手机号"
          data-testid="form-phone"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import dayjs, { Dayjs } from 'dayjs'
import type { FormInstance } from 'ant-design-vue'
import type { Employee } from '../../stores/employee'

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  confirmLoading: boolean
  editingEmployee?: Employee | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'submit', data: Employee): void
  (e: 'cancel'): void
}>()

interface EmployeeFormData {
  id?: number
  name: string
  email: string
  department: string | undefined
  role: string
  hireDate: Dayjs | undefined
  phone: string | undefined
}

const createEmptyForm = (): EmployeeFormData => ({
  id: undefined,
  name: '',
  email: '',
  department: undefined,
  role: '',
  hireDate: undefined,
  phone: undefined
})

const localVisible = ref(props.visible)
const formRef = ref<FormInstance>()
const form = reactive<EmployeeFormData>(createEmptyForm())

watch(
  () => props.visible,
  (val) => {
    localVisible.value = val
  }
)

watch(localVisible, (val) => {
  emit('update:visible', val)
})

watch(
  () => props.editingEmployee,
  (emp) => {
    if (emp) {
      form.id = emp.id as number
      form.name = emp.name
      form.email = emp.email
      form.department = emp.department
      form.role = emp.role
      form.hireDate = emp.hireDate ? dayjs(emp.hireDate as string) : undefined
      form.phone = emp.phone
    } else {
      Object.assign(form, createEmptyForm())
      formRef.value?.resetFields()
    }
  },
  { immediate: true }
)

const departmentOptions = [
  { label: '技术部', value: '技术部' },
  { label: '产品部', value: '产品部' },
  { label: '运营部', value: '运营部' },
  { label: '市场部', value: '市场部' },
  { label: '人力资源部', value: '人力资源部' },
  { label: '财务部', value: '财务部' }
]

const handleSubmit = async () => {
  try {
    await formRef.value?.validate()
    const employeeData: Employee = {
      name: form.name,
      email: form.email,
      department: form.department as string,
      role: form.role,
      phone: form.phone,
      hireDate: form.hireDate
        ? (form.hireDate.format('YYYY-MM-DD') as any)
        : undefined
    }
    if (props.isEdit && form.id) {
      employeeData.id = form.id
    }
    emit('submit', employeeData)
  } catch (e) {
    console.error(e)
  }
}

const handleCancel = () => {
  localVisible.value = false
  Object.assign(form, createEmptyForm())
  formRef.value?.resetFields()
  emit('cancel')
}
</script>
