import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SystemConfigView from '../views/SystemConfigView.vue'
import ScheduleManagementView from '../views/ScheduleManagementView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/schedule',
      name: 'schedule',
      component: ScheduleManagementView
    },
    {
      path: '/system/config',
      name: 'system-config',
      component: SystemConfigView
    }
  ]
})

export default router
