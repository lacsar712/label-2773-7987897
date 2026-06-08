import { ref, reactive, computed, watch } from 'vue'
import type { TablePaginationConfig } from 'ant-design-vue'
import { useEmployeeStore, type Employee } from '../stores/employee'

export interface SearchForm {
  name: string
  department: string | undefined
  role: string
}

export function useEmployeeTable() {
  const store = useEmployeeStore()

  const searchForm = reactive<SearchForm>({
    name: '',
    department: undefined,
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

  watch(
    () => store.employees,
    () => {
      pagination.total = store.employees.length
    }
  )

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

  const handleDelete = async (id: number) => {
    try {
      await store.deleteEmployee(id)
    } catch (e) {
      console.error(e)
    }
  }

  const fetchEmployees = () => store.fetchEmployees()

  return {
    store,
    searchForm,
    pagination,
    filteredEmployees,
    handleSearch,
    handleResetSearch,
    handleTableChange,
    handleDelete,
    fetchEmployees
  }
}
