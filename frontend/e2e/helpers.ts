import { Page, expect } from '@playwright/test';

export const TEST_USER = {
  username: 'admin',
  password: 'admin123'
};

export const TEST_EMPLOYEE = {
  name: '测试员工',
  email: 'test@example.com',
  department: '技术部',
  role: '高级工程师',
  phone: '13800138000'
};

export async function login(page: Page, username: string = TEST_USER.username, password: string = TEST_USER.password) {
  await page.goto('/login');
  await page.waitForLoadState('networkidle');
  await page.getByTestId('login-username').fill(username);
  await page.getByTestId('login-password').fill(password);
  await page.getByTestId('login-submit').click();
  await page.waitForLoadState('networkidle');
  await expect(page).toHaveURL(/.*\/(employees)?$/);
}

export async function logout(page: Page) {
  await page.getByTestId('user-menu').click();
  await page.getByText('退出登录').click();
  await expect(page).toHaveURL(/.*\/login$/);
}

export async function goToEmployeePage(page: Page) {
  await page.goto('/employees');
  await page.waitForLoadState('networkidle');
}

export function generateUniqueEmployee() {
  const timestamp = Date.now();
  return {
    name: `测试员工_${timestamp}`,
    email: `test_${timestamp}@example.com`,
    department: '技术部',
    role: '测试工程师',
    phone: `139${timestamp.toString().slice(-8)}`
  };
}

export async function createEmployee(page: Page, employee: {
  name: string;
  email: string;
  department: string;
  role: string;
  phone?: string;
}) {
  await page.getByTestId('add-employee-btn').click();
  await expect(page.getByTestId('employee-modal')).toBeVisible();

  await page.getByTestId('form-name').fill(employee.name);
  await page.getByTestId('form-email').fill(employee.email);
  await page.getByTestId('form-department').click();
  await page.getByText(employee.department).click();
  await page.getByTestId('form-role').fill(employee.role);

  if (employee.phone) {
    await page.getByTestId('form-phone').fill(employee.phone);
  }

  await page.getByRole('button', { name: '创建' }).click();
  await page.waitForLoadState('networkidle');
}

export async function verifyEmployeeInTable(page: Page, employeeName: string) {
  const row = page.locator('tr', { hasText: employeeName }).first();
  await expect(row).toBeVisible();
  return row;
}
