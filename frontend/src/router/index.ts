import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import SystemConfigView from '../views/SystemConfigView.vue'
import ScheduleManagementView from '../views/ScheduleManagementView.vue'
import LoginView from '../views/LoginView.vue'
import EmployeeManagementView from '../views/EmployeeManagementView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/employees',
      name: 'employees',
      component: EmployeeManagementView,
      meta: { requiresAuth: true }
    },
    {
      path: '/schedule',
      name: 'schedule',
      component: ScheduleManagementView,
      meta: { requiresAuth: true }
    },
    {
      path: '/system/config',
      name: 'system-config',
      component: SystemConfigView,
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.path === '/login' && isLoggedIn) {
    next({ path: '/' })
  } else {
    next()
  }
})

export default router
