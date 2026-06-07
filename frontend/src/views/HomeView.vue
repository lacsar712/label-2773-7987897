<template>
  <div class="page-container">
    <div class="content-wrapper">
      <a-card class="main-card" :bordered="false">
        <div class="header-section">
          <div class="title-group">
            <h1 class="page-title">员工管理系统</h1>
            <p class="subtitle">Employee Management System</p>
          </div>
          <a-button type="primary" class="add-btn" @click="showModal()">
            <template #icon><plus-outlined /></template>
            添加员工
          </a-button>
        </div>

        <a-table 
          :columns="columns" 
          :data-source="store.employees" 
          :loading="store.loading" 
          row-key="id"
          :pagination="{ pageSize: 10 }"
          class="modern-table"
          :scroll="{ x: 800 }"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'name'">
              <div class="user-info">
                <a-avatar :style="{ backgroundColor: '#7265e6', verticalAlign: 'middle' }" size="small">
                  {{ record.name.charAt(0) }}
                </a-avatar>
                <span class="user-name">{{ record.name }}</span>
              </div>
            </template>
            
            <template v-if="column.key === 'role'">
              <a-tag :color="getRoleColor(record.role)">{{ record.role }}</a-tag>
            </template>

            <template v-if="column.key === 'action'">
              <a-space size="small">
                <a-button type="link" class="action-btn edit" @click="showModal(record)">编辑</a-button>
                <a-divider type="vertical" />
                <a-popconfirm 
                  title="确定要删除吗?" 
                  @confirm="handleDelete(record.id)" 
                  ok-text="确定" 
                  cancel-text="取消"
                  placement="topRight"
                >
                  <a-button type="link" danger class="action-btn delete">删除</a-button>
                </a-popconfirm>
              </a-space>
            </template>
          </template>
        </a-table>
      </a-card>

      <a-modal
        v-model:open="visible"
        :title="isEdit ? '编辑员工' : '添加员工'"
        @ok="handleOk"
        ok-text="保存"
        cancel-text="取消"
        centered
        :mask-style="{ backdropFilter: 'blur(4px)' }"
        class="modern-modal"
      >
        <a-form ref="formRef" :model="formState" layout="vertical" class="modern-form">
          <a-form-item label="姓名" name="name" :rules="[{ required: true, message: '请输入姓名!' }]">
            <a-input v-model:value="formState.name" placeholder="请输入员工姓名" />
          </a-form-item>
          <a-form-item label="邮箱" name="email" :rules="[{ required: true, message: '请输入邮箱!', type: 'email' }]">
            <a-input v-model:value="formState.email" placeholder="example@company.com" />
          </a-form-item>
          <a-form-item label="部门" name="department" :rules="[{ required: true, message: '请输入部门!' }]">
            <a-input v-model:value="formState.department" placeholder="例如：技术部" />
          </a-form-item>
          <a-form-item label="职位" name="role" :rules="[{ required: true, message: '请输入职位!' }]">
            <a-input v-model:value="formState.role" placeholder="例如：高级工程师" />
          </a-form-item>
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, reactive } from 'vue';
import { useEmployeeStore, type Employee } from '../stores/employee';
import { PlusOutlined } from '@ant-design/icons-vue';

const store = useEmployeeStore();
const visible = ref(false);
const isEdit = ref(false);
const formRef = ref();

const formState = reactive<Employee>({
  name: '',
  email: '',
  department: '',
  role: '',
});

const columns = [
  { title: '姓名', dataIndex: 'name', key: 'name', width: '20%' },
  { title: '邮箱', dataIndex: 'email', key: 'email', width: '25%' },
  { title: '部门', dataIndex: 'department', key: 'department', width: '20%' },
  { title: '职位', dataIndex: 'role', key: 'role', width: '20%' },
  { title: '操作', key: 'action', width: '15%', align: 'center' },
];

const getRoleColor = (role: string) => {
  const colors = ['blue', 'cyan', 'green', 'purple', 'geekblue', 'magenta'];
  let hash = 0;
  for (let i = 0; i < role.length; i++) {
    hash = role.charCodeAt(i) + ((hash << 5) - hash);
  }
  return colors[Math.abs(hash) % colors.length];
};

onMounted(() => {
  store.fetchEmployees();
});

const showModal = (record?: Employee) => {
  if (record) {
    isEdit.value = true;
    Object.assign(formState, record);
  } else {
    isEdit.value = false;
    formState.id = undefined;
    formState.name = '';
    formState.email = '';
    formState.department = '';
    formState.role = '';
  }
  visible.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate();
    if (isEdit.value) {
      await store.updateEmployee(formState);
    } else {
      await store.createEmployee(formState);
    }
    visible.value = false;
  } catch (error) {
    // validation failed
  }
};

const handleDelete = async (id: number) => {
  await store.deleteEmployee(id);
};
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  padding: 40px 24px;
  display: flex;
  justify-content: center;
  align-items: flex-start;
}

.content-wrapper {
  width: 100%;
  max-width: 1200px;
}

.main-card {
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
}

.main-card:hover {
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
}

.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding: 0 8px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  background: linear-gradient(135deg, #2c3e50 0%, #3498db 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.subtitle {
  color: #8c8c8c;
  margin: 4px 0 0 0;
  font-size: 14px;
}

.add-btn {
  height: 40px;
  padding: 0 24px;
  border-radius: 20px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 15px rgba(118, 75, 162, 0.3);
  transition: all 0.3s ease;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(118, 75, 162, 0.4);
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
}

.add-btn:active {
  transform: translateY(0);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  font-weight: 500;
  color: #2c3e50;
}

.action-btn {
  padding: 0;
  font-weight: 500;
}

.action-btn.edit {
  color: #3498db;
}

.action-btn.delete {
  color: #ff4d4f;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .page-container {
    padding: 20px 16px;
  }
  
  .header-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .add-btn {
    width: 100%;
  }
}
</style>
