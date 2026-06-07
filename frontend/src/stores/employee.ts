import { defineStore } from 'pinia';
import request from '../utils/request';
import { message } from 'ant-design-vue';

// Set API endpoint (relative to baseURL in request.ts)
const API_URL = '/employees';

export interface Employee {
  id?: number;
  name: string;
  email: string;
  department: string;
  role: string;
}

interface Result<T> {
  code: number;
  message: string;
  data: T;
}

export const useEmployeeStore = defineStore('employee', {
  state: () => ({
    employees: [] as Employee[],
    loading: false,
  }),
  actions: {
    async fetchEmployees() {
      this.loading = true;
      try {
        const res = await request.get<any, Result<Employee[]>>(API_URL);
        this.employees = res.data;
      } catch (error) {
        // Error is handled by interceptor
      } finally {
        this.loading = false;
      }
    },
    async createEmployee(employee: Employee) {
      try {
        await request.post<any, Result<boolean>>(API_URL, employee);
        message.success('创建成功'); // Keep success message here or move to interceptor? Usually keep distinct success messages in business logic.
        await this.fetchEmployees();
      } catch (error) {
         // Error handled by interceptor
      }
    },
    async updateEmployee(employee: Employee) {
      try {
        await request.put<any, Result<boolean>>(API_URL, employee);
        message.success('更新成功');
        await this.fetchEmployees();
      } catch (error) {
        // Error handled by interceptor
      }
    },
    async deleteEmployee(id: number) {
      try {
        await request.delete<any, Result<boolean>>(`${API_URL}/${id}`);
        message.success('删除成功');
        await this.fetchEmployees();
      } catch (error) {
        // Error handled by interceptor
      }
    },
  },
});
