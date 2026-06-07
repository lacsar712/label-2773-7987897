<script setup lang="ts">import { computed } from 'vue';
import { Drawer, Button, Tag, Popconfirm, Empty, Tooltip } from 'ant-design-vue';
import { DeleteOutlined, EditOutlined, ClockCircleOutlined, EnvironmentOutlined, UserOutlined, TeamOutlined } from '@ant-design/icons-vue';
import { useCalendarStore } from '../../stores/calendar';
import type { CalendarEvent } from '../../types/calendar';
const store = useCalendarStore();
const emit = defineEmits<{
 (e: 'edit', event: CalendarEvent): void;
 (e: 'create', dateStr: string): void;
}>();
const visible = computed(() => !!store.selectedDate);
const close = () => {
 store.selectedDate = null;
 store.selectedDateEvents = [];
};
const formatDateDisplay = computed(() => {
 if (!store.selectedDate)
 return '';
 const d = new Date(store.selectedDate);
 const weekdays = ['日', '一', '二', '三', '四', '五', '六'];
 return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${weekdays[d.getDay()]}`;
});
const groupByHour = computed(() => {
 const groups: Record<string, CalendarEvent[]> = {};
 const allDayEvents: CalendarEvent[] = [];
 store.selectedDateEvents.forEach(ev => {
 if (ev.isAllDay) {
 allDayEvents.push(ev);
 }
 else {
 const h = new Date(ev.startTime).getHours();
 const key = h.toString();
 if (!groups[key])
 groups[key] = [];
 groups[key].push(ev);
 }
 });
 return { allDayEvents, groups };
});
const hours = Array.from({ length: 24 }, (_, i) => i.toString());
const formatHour = (h: string) => `${h.padStart(2, '0')}:00`;
const formatEventTimeRange = (ev: CalendarEvent) => {
 const s = new Date(ev.startTime);
 const e = new Date(ev.endTime);
 const pad = (n: number) => n.toString().padStart(2, '0');
 return `${pad(s.getHours())}:${pad(s.getMinutes())} - ${pad(e.getHours())}:${pad(e.getMinutes())}`;
};
const deleteEvent = async (id: number) => {
 await store.deleteEvent(id);
};
</script>

<template>
  <Drawer
    v-model:open="visible"
    :title="formatDateDisplay"
    width="420"
    @close="close"
  >
    <template #extra>
      <Button type="primary" size="small" @click="emit('create', store.selectedDate || '')">
        新建事件
      </Button>
    </template>
    <div v-if="store.selectedDateEvents.length === 0" class="empty-wrap">
      <Empty description="今日暂无安排" />
      <Button type="primary" size="large" @click="emit('create', store.selectedDate || '')" style="margin-top: 16px">
        添加事件
      </Button>
    </div>

    <div v-else class="timeline">
      <div v-if="groupByHour.allDayEvents.length > 0" class="timeline-block">
        <div class="time-label all-day-label">全天</div>
        <div class="events-list">
          <div
            v-for="ev in groupByHour.allDayEvents"
            :key="ev.id"
            class="event-card"
            :style="{ borderLeftColor: ev.color }"
          >
            <div class="event-head">
              <Tag :color="ev.color" class="ev-type-tag">
                {{ store.getEventTypeLabel(ev.eventType) }}
              </Tag>
              <div class="ev-actions">
                <Tooltip title="编辑">
                  <Button type="text" size="small" @click="emit('edit', ev)">
                    <EditOutlined />
                  </Button>
                </Tooltip>
                <Popconfirm title="确定删除该事件？" @confirm="deleteEvent(ev.id!)">
                  <Button type="text" size="small" danger>
                    <DeleteOutlined />
                  </Button>
                </Popconfirm>
              </div>
            </div>
            <div class="ev-title">{{ ev.title }}</div>
            <div v-if="ev.description" class="ev-desc">{{ ev.description }}</div>
            <div class="ev-meta">
              <span v-if="ev.employeeName">
                <UserOutlined /> {{ ev.employeeName }}
              </span>
              <span v-if="ev.department">
                <TeamOutlined /> {{ ev.department }}
              </span>
              <span v-if="ev.location">
                <EnvironmentOutlined /> {{ ev.location }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-for="h in hours" :key="h" class="timeline-block">
        <div class="time-label">{{ formatHour(h) }}</div>
        <div class="events-list">
          <div
            v-for="ev in groupByHour.groups[h] || []"
            :key="ev.id"
            class="event-card"
            :style="{ borderLeftColor: ev.color }"
          >
            <div class="event-head">
              <Tag :color="ev.color" class="ev-type-tag">
                {{ store.getEventTypeLabel(ev.eventType) }}
              </Tag>
              <div class="ev-actions">
                <Tooltip title="编辑">
                  <Button type="text" size="small" @click="emit('edit', ev)">
                    <EditOutlined />
                  </Button>
                </Tooltip>
                <Popconfirm title="确定删除该事件？" @confirm="deleteEvent(ev.id!)">
                  <Button type="text" size="small" danger>
                    <DeleteOutlined />
                  </Button>
                </Popconfirm>
              </div>
            </div>
            <div class="ev-title">{{ ev.title }}</div>
            <div class="ev-time-range">
              <ClockCircleOutlined /> {{ formatEventTimeRange(ev) }}
            </div>
            <div v-if="ev.description" class="ev-desc">{{ ev.description }}</div>
            <div class="ev-meta">
              <span v-if="ev.employeeName">
                <UserOutlined /> {{ ev.employeeName }}
              </span>
              <span v-if="ev.department">
                <TeamOutlined /> {{ ev.department }}
              </span>
              <span v-if="ev.location">
                <EnvironmentOutlined /> {{ ev.location }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Drawer>
</template>

<style lang="scss" scoped>
.empty-wrap {
  text-align: center;
  padding: 40px 0;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.timeline-block {
  display: flex;
  gap: 12px;
}

.time-label {
  width: 60px;
  flex-shrink: 0;
  font-size: 13px;
  font-weight: 500;
  color: #666;
  padding-top: 4px;

  &.all-day-label {
    color: #1677ff;
    font-weight: 600;
  }
}

.events-list {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.event-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-left: 4px solid;
  border-radius: 8px;
  padding: 12px;
  transition: box-shadow 0.2s;

  &:hover {
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  }
}

.event-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.ev-type-tag {
  margin: 0;
}

.ev-actions {
  display: flex;
  gap: 2px;
}

.ev-title {
  font-size: 15px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 4px;
}

.ev-time-range {
  font-size: 12px;
  color: #8c8c8c;
  margin-bottom: 6px;
}

.ev-desc {
  font-size: 13px;
  color: #595959;
  margin-bottom: 8px;
  line-height: 1.5;
}

.ev-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #8c8c8c;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}
</style>
