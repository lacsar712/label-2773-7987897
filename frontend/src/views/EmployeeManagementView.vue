<template>
  <div class="employee-management">
    <EmployeeToolbar
      :search-form="searchForm"
      @add="form.handleAdd"
      @search="handleSearch"
      @reset-search="handleResetSearch"
    />

    <EmployeeTable
      :data-source="filteredEmployees"
      :loading="store.loading"
      :pagination="pagination"
      @edit="form.handleEdit"
      @delete="handleDelete"
      @table-change="handleTableChange"
    />

    <EmployeeFormModal
      v-model:visible="form.modalVisible.value"
      :is-edit="form.isEdit.value"
      :confirm-loading="form.modalLoading.value"
      :editing-employee="form.editingEmployee.value"
      @submit="form.handleSubmit"
      @cancel="form.handleCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import EmployeeToolbar from '../components/employee/EmployeeToolbar.vue'
import EmployeeTable from '../components/employee/EmployeeTable.vue'
import EmployeeFormModal from '../components/employee/EmployeeFormModal.vue'
import { useEmployeeTable } from '../composables/useEmployeeTable'
import { useEmployeeForm } from '../composables/useEmployeeForm'

const {
  store,
  searchForm,
  pagination,
  filteredEmployees,
  handleSearch,
  handleResetSearch,
  handleTableChange,
  handleDelete,
  fetchEmployees
} = useEmployeeTable()
const form = useEmployeeForm()

onMounted(async () => {
  await fetchEmployees()
})
</script>

<style lang="scss" scoped>
.employee-management {
  padding: 24px;
  min-height: 100vh;
  background: #f5f7fa;
}
</style>
