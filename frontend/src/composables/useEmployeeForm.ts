import { ref } from 'vue'
import { message } from 'ant-design-vue'
import { useEmployeeStore, type Employee } from '../stores/employee'

export function useEmployeeForm() {
  const store = useEmployeeStore()

  const modalVisible = ref(false)
  const modalLoading = ref(false)
  const isEdit = ref(false)
  const editingEmployee = ref<Employee | null>(null)

  const handleAdd = () => {
    isEdit.value = false
    editingEmployee.value = null
    modalVisible.value = true
  }

  const handleEdit = (record: Employee) => {
    isEdit.value = true
    editingEmployee.value = record
    modalVisible.value = true
  }

  const handleCancel = () => {
    modalVisible.value = false
    editingEmployee.value = null
  }

  const handleSubmit = async (employeeData: Employee) => {
    try {
      modalLoading.value = true
      if (isEdit.value) {
        await store.updateEmployee(employeeData)
        message.success('更新成功')
      } else {
        await store.createEmployee(employeeData)
        message.success('创建成功')
      }
      modalVisible.value = false
      editingEmployee.value = null
    } catch (e) {
      console.error(e)
    } finally {
      modalLoading.value = false
    }
  }

  return {
    modalVisible,
    modalLoading,
    isEdit,
    editingEmployee,
    handleAdd,
    handleEdit,
    handleCancel,
    handleSubmit
  }
}
