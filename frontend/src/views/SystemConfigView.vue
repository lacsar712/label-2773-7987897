<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  Form,
  Input,
  InputNumber,
  Switch,
  Button,
  Tabs,
  Table,
  Space,
  Modal,
  message,
  Upload,
  Tag,
  Descriptions,
  Divider,
  Popconfirm
} from 'ant-design-vue';
import {
  SettingOutlined,
  HistoryOutlined,
  ExportOutlined,
  ImportOutlined,
  SaveOutlined,
  ReloadOutlined,
  ArrowLeftOutlined
} from '@ant-design/icons-vue';
import { systemConfigApi } from '../api/systemConfig';
import type { ConfigGroupVO, ConfigHistoryVO, SystemConfigDTO } from '../types/systemConfig';
import type { UploadProps } from 'ant-design-vue';

const groups = ref<ConfigGroupVO[]>([]);
const loading = ref(false);
const activeTab = ref('COMPANY_INFO');
const router = useRouter();

const goBack = () => {
  router.push('/');
};

const historyModalVisible = ref(false);
const historyLoading = ref(false);
const historyList = ref<ConfigHistoryVO[]>([]);
const historyTotal = ref(0);
const historyPage = ref(1);
const historyPageSize = ref(10);
const historyGroupFilter = ref<string>('');

const getFormData = (groupCode: string) => {
  const group = groups.value.find(g => g.configGroup === groupCode);
  if (!group) return {};
  const formData: Record<string, any> = {};
  group.configs.forEach(config => {
    switch (config.valueType) {
      case 'INTEGER':
      case 'NUMBER':
        formData[config.configKey] = config.configValue ? Number(config.configValue) : null;
        break;
      case 'BOOLEAN':
        formData[config.configKey] = config.configValue === 'true';
        break;
      default:
        formData[config.configKey] = config.configValue;
    }
  });
  return formData;
};

const formDataMap = ref<Record<string, Record<string, any>>>({});

const currentGroup = computed(() => {
  return groups.value.find(g => g.configGroup === activeTab.value);
});

const loadGroups = async () => {
  loading.value = true;
  try {
    const res = await systemConfigApi.getAllGroups();
    groups.value = (res as any).data;
    formDataMap.value = {};
    groups.value.forEach(group => {
      formDataMap.value[group.configGroup] = getFormData(group.configGroup);
    });
  } catch (error) {
    message.error('加载配置失败');
  } finally {
    loading.value = false;
  }
};

const saveGroup = async (groupCode: string) => {
  const group = groups.value.find(g => g.configGroup === groupCode);
  if (!group) return;

  const configs: SystemConfigDTO[] = group.configs.map(config => {
    let value: string;
    const formValue = formDataMap.value[groupCode]?.[config.configKey];
    switch (config.valueType) {
      case 'INTEGER':
      case 'NUMBER':
        value = String(formValue ?? config.configValue);
        break;
      case 'BOOLEAN':
        value = String(Boolean(formValue));
        break;
      default:
        value = formValue ?? config.configValue;
    }
    return {
      configGroup: groupCode,
      configKey: config.configKey,
      configValue: value,
      valueType: config.valueType,
      displayName: config.displayName,
      description: config.description,
      sortOrder: config.sortOrder,
      updatedBy: 'admin'
    };
  });

  try {
    const res = await systemConfigApi.updateGroup({
      configGroup: groupCode,
      configs,
      updatedBy: 'admin'
    });
    if ((res as any).data) {
      message.success('保存成功，配置已即时生效');
      await loadGroups();
    } else {
      message.error('保存失败');
    }
  } catch (error) {
    message.error('保存失败');
  }
};

const resetGroup = (groupCode: string) => {
  const group = groups.value.find(g => g.configGroup === groupCode);
  if (group) {
    formDataMap.value[groupCode] = getFormData(groupCode);
    message.info('已重置为当前保存的值');
  }
};

const exportGroup = async (groupCode: string) => {
  try {
    const blob = await systemConfigApi.exportGroup(groupCode) as unknown as Blob;
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${groupCode}_config.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    message.success('导出成功');
  } catch (error) {
    message.error('导出失败');
  }
};

const beforeImportUpload: UploadProps['beforeUpload'] = (file) => {
  const isJson = file.type === 'application/json' || file.name.endsWith('.json');
  if (!isJson) {
    message.error('只能上传 JSON 文件！');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('文件大小不能超过 2MB！');
    return false;
  }
  return true;
};

const handleImport = async (info: any) => {
  if (info.file.status === 'done') {
    message.success('导入成功');
    await loadGroups();
  } else if (info.file.status === 'error') {
    message.error('导入失败');
  }
};

const openHistoryModal = (groupCode?: string) => {
  historyGroupFilter.value = groupCode || '';
  historyPage.value = 1;
  historyModalVisible.value = true;
  loadHistory();
};

const loadHistory = async () => {
  historyLoading.value = true;
  try {
    const res = await systemConfigApi.queryHistory(
      historyGroupFilter.value || undefined,
      undefined,
      historyPage.value,
      historyPageSize.value
    );
    const data = (res as any).data;
    historyList.value = data.records;
    historyTotal.value = data.total;
  } catch (error) {
    message.error('加载历史记录失败');
  } finally {
    historyLoading.value = false;
  }
};

const groupDisplayNameMap: Record<string, string> = {
  COMPANY_INFO: '公司信息',
  BUSINESS_RULES: '业务规则',
  SECURITY_POLICY: '安全策略',
  FEATURE_TOGGLE: '功能开关'
};

const historyColumns = [
  {
    title: '配置分组',
    dataIndex: 'configGroup',
    key: 'configGroup',
    width: 120,
    customRender: ({ record }: any) => (
      <Tag color="blue">{groupDisplayNameMap[record.configGroup] || record.configGroup}</Tag>
    )
  },
  {
    title: '配置项',
    dataIndex: 'displayName',
    key: 'displayName',
    width: 150
  },
  {
    title: '变更前',
    dataIndex: 'oldValue',
    key: 'oldValue',
    width: 180,
    customRender: ({ record }: any) => (
      <span style="color: #ff4d4f;">{record.oldValue || '(空)'}</span>
    )
  },
  {
    title: '变更后',
    dataIndex: 'newValue',
    key: 'newValue',
    width: 180,
    customRender: ({ record }: any) => (
      <span style="color: #52c41a;">{record.newValue || '(空)'}</span>
    )
  },
  {
    title: '变更人',
    dataIndex: 'changedBy',
    key: 'changedBy',
    width: 100
  },
  {
    title: '变更时间',
    dataIndex: 'changedAt',
    key: 'changedAt',
    width: 180
  }
];

onMounted(() => {
  loadGroups();
});
</script>

<template>
  <div class="system-config-container">
    <div class="page-header">
      <div class="header-left">
        <Button icon={<ArrowLeftOutlined />} @click="goBack" style="margin-right: 12px;">
          返回
        </Button>
        <SettingOutlined class="header-icon" />
        <h2>系统设置</h2>
      </div>
      <Space>
        <Button icon={<HistoryOutlined />} @click="openHistoryModal()">
          变更历史
        </Button>
      </Space>
    </div>

    <div class="config-content">
      <Tabs
        v-model:activeKey="activeTab"
        type="card"
        size="large"
        class="config-tabs"
      >
        <template v-for="group in groups" :key="group.configGroup">
          <Tabs.TabPane :key="group.configGroup">
            <template #tab>
              <span class="tab-label">{{ group.displayName }}</span>
            </template>

            <div class="tab-content">
              <div class="group-description">
                <Descriptions :column="1" size="small" bordered>
                  <Descriptions.Item :label="group.displayName">
                    {{ group.description }}
                  </Descriptions.Item>
                </Descriptions>
              </div>

              <Divider />

              <Form
                layout="vertical"
                :model="formDataMap[group.configGroup] || {}"
                class="config-form"
              >
                <template v-for="config in group.configs" :key="config.configKey">
                  <Form.Item
                    :label="config.displayName"
                    :name="config.configKey"
                    :tooltip="config.description"
                  >
                    <template v-if="config.valueType === 'INTEGER'">
                      <InputNumber
                        v-model:value="formDataMap[group.configGroup]?.[config.configKey]"
                        :min="0"
                        style="width: 200px"
                        :placeholder="`请输入${config.displayName}`"
                      />
                    </template>
                    <template v-else-if="config.valueType === 'NUMBER'">
                      <InputNumber
                        v-model:value="formDataMap[group.configGroup]?.[config.configKey]"
                        :min="0"
                        :step="0.01"
                        style="width: 200px"
                        :placeholder="`请输入${config.displayName}`"
                      />
                    </template>
                    <template v-else-if="config.valueType === 'BOOLEAN'">
                      <Switch
                        v-model:checked="formDataMap[group.configGroup]?.[config.configKey]"
                        checked-children="开启"
                        un-checked-children="关闭"
                      />
                    </template>
                    <template v-else>
                      <Input
                        v-model:value="formDataMap[group.configGroup]?.[config.configKey]"
                        :placeholder="`请输入${config.displayName}`"
                      />
                    </template>
                  </Form.Item>
                </template>
              </Form>

              <Divider />

              <div class="action-bar">
                <Space>
                  <Popconfirm
                    title="确定保存此分组的配置？"
                    description="保存后配置将即时生效"
                    @confirm="saveGroup(group.configGroup)"
                  >
                    <Button type="primary" icon={<SaveOutlined />}>
                      保存配置
                    </Button>
                  </Popconfirm>
                  <Button icon={<ReloadOutlined />} @click="resetGroup(group.configGroup)">
                    重置
                  </Button>
                </Space>
                <Space>
                  <Button icon={<HistoryOutlined />} @click="openHistoryModal(group.configGroup)">
                    查看历史
                  </Button>
                  <Button icon={<ExportOutlined />} @click="exportGroup(group.configGroup)">
                    导出 JSON
                  </Button>
                  <Upload
                    name="file"
                    :showUploadList="false"
                    :action="'http://localhost:8080/api/system/config/import'"
                    :data="{ updatedBy: 'admin' }"
                    :beforeUpload="beforeImportUpload"
                    @change="handleImport"
                  >
                    <Button icon={<ImportOutlined />}>
                      导入 JSON
                    </Button>
                  </Upload>
                </Space>
              </div>

              <div class="updated-info" v-if="currentGroup && currentGroup.configs.length > 0">
                <Tag color="default">
                  最后更新: {{ currentGroup.configs.reduce((latest, c) => 
                    (!latest || (c.updatedAt && c.updatedAt > latest)) ? c.updatedAt : latest
                  , '') }}
                </Tag>
                <Tag color="default">
                  更新人: {{ currentGroup.configs[0].updatedBy }}
                </Tag>
              </div>
            </div>
          </Tabs.TabPane>
        </template>
      </Tabs>
    </div>

    <Modal
      v-model:open="historyModalVisible"
      title="配置变更历史"
      width="1000px"
      :footer="null"
      @cancel="historyModalVisible = false"
    >
      <div class="history-toolbar">
        <Space>
          <span>分组筛选：</span>
          <select
            v-model="historyGroupFilter"
            class="filter-select"
            @change="loadHistory()"
          >
            <option value="">全部分组</option>
            <option v-for="g in groups" :key="g.configGroup" :value="g.configGroup">
              {{ g.displayName }}
            </option>
          </select>
          <Button @click="loadHistory()">刷新</Button>
        </Space>
      </div>
      <Table
        :columns="historyColumns"
        :dataSource="historyList"
        :loading="historyLoading"
        :pagination="{
          current: historyPage,
          pageSize: historyPageSize,
          total: historyTotal,
          showSizeChanger: true,
          showQuickJumper: true,
          showTotal: (total: number) => `共 ${total} 条记录`,
          onChange: (page: number, pageSize: number) => {
            historyPage = page;
            historyPageSize = pageSize;
            loadHistory();
          }
        }"
        rowKey="id"
        scroll="{ y: 400 }"
      />
    </Modal>
  </div>
</template>

<style scoped lang="scss">
.system-config-container {
  padding: 24px;
  background: #f0f2f5;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .header-icon {
      font-size: 28px;
      color: #1890ff;
    }

    h2 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
    }
  }
}

.config-content {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.config-tabs {
  :deep(.ant-tabs-nav) {
    margin-bottom: 24px;
  }

  :deep(.ant-tabs-tab) {
    font-size: 15px;
    font-weight: 500;
  }
}

.tab-label {
  font-size: 15px;
}

.tab-content {
  padding: 0 8px;
}

.group-description {
  margin-bottom: 16px;
}

.config-form {
  max-width: 600px;

  :deep(.ant-form-item-label) {
    font-weight: 500;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 24px 0;
  padding: 16px 0;
}

.updated-info {
  display: flex;
  gap: 8px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #e8e8e8;
}

.history-toolbar {
  margin-bottom: 16px;

  .filter-select {
    height: 32px;
    padding: 0 12px;
    border: 1px solid #d9d9d9;
    border-radius: 6px;
    background: #fff;
    cursor: pointer;

    &:hover {
      border-color: #40a9ff;
    }

    &:focus {
      outline: none;
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }
}
</style>
