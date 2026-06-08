<template>
  <div>
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">
          <TeamOutlined /> 员工管理
        </h2>
      </div>
      <div class="header-right">
        <a-button type="primary" @click="$emit('add')" data-testid="add-employee-btn">
          <PlusOutlined /> 新增员工
        </a-button>
      </div>
    </div>

    <div class="search-panel">
      <a-form :model="searchForm" layout="inline" @finish="$emit('search')">
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
          <a-button style="margin-left: 8px" @click="$emit('reset-search')" data-testid="search-reset">
            重置
          </a-button>
        </a-form-item>
      </a-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  TeamOutlined,
  PlusOutlined,
  SearchOutlined
} from '@ant-design/icons-vue'
import type { SearchForm } from '../../composables/useEmployeeTable'

defineProps<{
  searchForm: SearchForm
}>()

defineEmits<{
  (e: 'add'): void
  (e: 'search'): void
  (e: 'reset-search'): void
}>()

const departmentOptions = [
  { label: '技术部', value: '技术部' },
  { label: '产品部', value: '产品部' },
  { label: '运营部', value: '运营部' },
  { label: '市场部', value: '市场部' },
  { label: '人力资源部', value: '人力资源部' },
  { label: '财务部', value: '财务部' }
]
</script>

<style lang="scss" scoped>
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
</style>
