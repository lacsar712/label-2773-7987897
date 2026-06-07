<script setup lang="ts">
import { useCalendarStore } from '../../stores/calendar'
import { Checkbox } from 'ant-design-vue'

const store = useCalendarStore()
</script>

<template>
  <div class="filter-panel">
    <div class="panel-section">
      <h4 class="section-title">事件类型</h4>
      <div class="legend-list">
        <div
          v-for="t in store.eventTypes"
          :key="t.value"
          class="legend-item"
          :class="{ active: store.enabledEventTypes.includes(t.value) }"
          @click="store.toggleEventType(t.value)"
        >
          <span class="color-dot" :style="{ backgroundColor: t.color }"></span>
          <span class="legend-label">{{ t.label }}</span>
          <Checkbox :checked="store.enabledEventTypes.includes(t.value)" style="margin-left: auto" />
        </div>
      </div>
    </div>

    <div class="panel-section">
      <h4 class="section-title">订阅同事日历</h4>
      <div class="subscription-list">
        <div
          v-for="sub in store.subscriptions"
          :key="sub.id"
          class="sub-item"
        >
          <span class="sub-avatar">{{ sub.targetEmployeeName?.[0] }}</span>
          <span class="sub-name">{{ sub.targetEmployeeName }}</span>
          <a
            class="sub-remove"
            @click.stop="store.removeSubscription(sub.targetEmployeeId)"
          >
            取消
          </a>
        </div>
        <div v-if="store.subscriptions.length === 0" class="empty-sub">
          暂无订阅，可在下方添加
        </div>
      </div>

      <div class="employee-filter">
        <div class="filter-title">员工筛选</div>
        <div class="emp-list">
          <div
            v-for="emp in store.employees"
            :key="emp.id"
            class="emp-item"
            @click="store.toggleEmployeeFilter(emp.id)"
            :class="{ active: store.enabledEmployeeIds.includes(emp.id) }"
          >
            <span class="emp-avatar">{{ emp.name?.[0] }}</span>
            <span class="emp-info">
              <span class="emp-name">{{ emp.name }}</span>
              <span class="emp-dept">{{ emp.department }}</span>
            </span>
            <Checkbox :checked="store.enabledEmployeeIds.includes(emp.id)" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.filter-panel {
  height: 100%;
  overflow-y: auto;
  padding: 16px;
}

.panel-section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 12px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.legend-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #f5f5f5;
  }

  &.active {
    background-color: #e6f4ff;
  }
}

.color-dot {
  width: 12px;
  height: 12px;
  border-radius: 3px;
  flex-shrink: 0;
}

.legend-label {
  font-size: 13px;
  color: #595959;
}

.subscription-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 16px;
}

.sub-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.sub-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #52c41a;
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.sub-name {
  font-size: 13px;
  color: #389e0d;
  flex: 1;
}

.sub-remove {
  font-size: 12px;
  color: #ff4d4f;
  cursor: pointer;
}

.empty-sub {
  font-size: 12px;
  color: #999;
  padding: 8px;
  text-align: center;
}

.employee-filter {
  border-top: 1px dashed #f0f0f0;
  padding-top: 12px;
}

.filter-title {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 8px;
}

.emp-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.emp-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background-color: #f5f5f5;
  }

  &.active {
    background-color: #e6f4ff;
  }
}

.emp-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #1677ff;
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.emp-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.emp-name {
  font-size: 13px;
  color: #262626;
}

.emp-dept {
  font-size: 11px;
  color: #999;
}
</style>
