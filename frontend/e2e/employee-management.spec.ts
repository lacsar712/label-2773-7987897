import { test, expect } from '@playwright/test';
import {
  login,
  goToEmployeePage,
  generateUniqueEmployee,
  createEmployee,
  verifyEmployeeInTable
} from './helpers';

test.describe('员工管理功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await login(page);
    await goToEmployeePage(page);
  });

  test.describe('员工列表页面', () => {
    test('员工管理页面正确显示', async ({ page }) => {
      await expect(page.getByText('员工管理')).toBeVisible();
      await expect(page.getByTestId('add-employee-btn')).toBeVisible();
      await expect(page.getByTestId('employee-table')).toBeVisible();
      await expect(page.getByTestId('search-name')).toBeVisible();
      await expect(page.getByTestId('search-department')).toBeVisible();
      await expect(page.getByTestId('search-role')).toBeVisible();
    });

    test('搜索和重置功能', async ({ page }) => {
      await page.getByTestId('search-name').fill('测试');
      await page.getByTestId('search-submit').click();
      await page.waitForTimeout(500);

      await page.getByTestId('search-reset').click();
      await expect(page.getByTestId('search-name')).toHaveValue('');
      await expect(page.getByTestId('search-role')).toHaveValue('');
    });
  });

  test.describe('新增员工', () => {
    test('新增员工弹窗正确显示', async ({ page }) => {
      await page.getByTestId('add-employee-btn').click();
      await expect(page.getByTestId('employee-modal')).toBeVisible();
      await expect(page.getByText('新增员工')).toBeVisible();
      await expect(page.getByTestId('form-name')).toBeVisible();
      await expect(page.getByTestId('form-email')).toBeVisible();
      await expect(page.getByTestId('form-department')).toBeVisible();
      await expect(page.getByTestId('form-role')).toBeVisible();
    });

    test('成功创建新员工', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);
      await expect(page.getByText('创建成功')).toBeVisible();
      await verifyEmployeeInTable(page, employee.name);
    });

    test('表单校验 - 必填字段为空时提示错误', async ({ page }) => {
      await page.getByTestId('add-employee-btn').click();
      await expect(page.getByTestId('employee-modal')).toBeVisible();
      await page.getByRole('button', { name: '创建' }).click();
      await expect(page.getByText('请输入姓名')).toBeVisible();
      await expect(page.getByText('请输入邮箱')).toBeVisible();
      await expect(page.getByText('请选择部门')).toBeVisible();
      await expect(page.getByText('请输入职位')).toBeVisible();
    });

    test('表单校验 - 邮箱格式错误时提示', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await page.getByTestId('add-employee-btn').click();
      await expect(page.getByTestId('employee-modal')).toBeVisible();

      await page.getByTestId('form-name').fill(employee.name);
      await page.getByTestId('form-email').fill('invalid-email');
      await page.getByTestId('form-department').click();
      await page.getByText(employee.department).click();
      await page.getByTestId('form-role').fill(employee.role);

      await page.getByRole('button', { name: '创建' }).click();
      await expect(page.getByText('请输入有效的邮箱地址')).toBeVisible();
    });

    test('取消新增员工', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await page.getByTestId('add-employee-btn').click();
      await expect(page.getByTestId('employee-modal')).toBeVisible();

      await page.getByTestId('form-name').fill(employee.name);
      await page.getByTestId('form-email').fill(employee.email);

      await page.getByRole('button', { name: '取消' }).click();
      await expect(page.getByTestId('employee-modal')).not.toBeVisible();
    });
  });

  test.describe('编辑员工', () => {
    test('成功编辑员工信息', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);

      const row = await verifyEmployeeInTable(page, employee.name);
      await row.getByTestId(`edit-btn-${employee.name}`).first().click();

      const updatedName = `更新_${employee.name}`;
      const updatedRole = '资深测试工程师';

      await page.getByTestId('form-name').clear();
      await page.getByTestId('form-name').fill(updatedName);
      await page.getByTestId('form-role').clear();
      await page.getByTestId('form-role').fill(updatedRole);

      await page.getByRole('button', { name: '保存' }).click();
      await page.waitForLoadState('networkidle');

      await expect(page.getByText('更新成功')).toBeVisible();
      await verifyEmployeeInTable(page, updatedName);
    });
  });

  test.describe('搜索和筛选功能', () => {
    test('按姓名搜索员工', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);

      await page.getByTestId('search-name').fill(employee.name);
      await page.getByTestId('search-submit').click();
      await page.waitForTimeout(500);

      await verifyEmployeeInTable(page, employee.name);
    });

    test('按部门筛选员工', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);

      await page.getByTestId('search-department').click();
      await page.getByText(employee.department).click();
      await page.getByTestId('search-submit').click();
      await page.waitForTimeout(500);

      const rows = page.locator('tbody tr');
      const count = await rows.count();
      for (let i = 0; i < count; i++) {
        await expect(rows.nth(i)).toContainText(employee.department);
      }
    });

    test('按职位搜索员工', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);

      await page.getByTestId('search-role').fill(employee.role);
      await page.getByTestId('search-submit').click();
      await page.waitForTimeout(500);

      await verifyEmployeeInTable(page, employee.role);
    });

    test('组合搜索条件', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);

      await page.getByTestId('search-name').fill(employee.name);
      await page.getByTestId('search-department').click();
      await page.getByText(employee.department).click();
      await page.getByTestId('search-role').fill(employee.role);
      await page.getByTestId('search-submit').click();
      await page.waitForTimeout(500);

      await verifyEmployeeInTable(page, employee.name);
    });
  });

  test.describe('删除员工', () => {
    test('成功删除员工', async ({ page }) => {
      const employee = generateUniqueEmployee();
      await createEmployee(page, employee);
      await verifyEmployeeInTable(page, employee.name);

      page.on('dialog', async dialog => {
        await dialog.accept();
      });

      const row = await verifyEmployeeInTable(page, employee.name);
      const deleteBtn = row.getByRole('button', { name: '删除' }).first();
      await deleteBtn.click();

      await page.getByRole('button', { name: '确定' }).click();
      await page.waitForLoadState('networkidle');

      await expect(page.getByText('删除成功')).toBeVisible();
    });
  });

  test.describe('完整流程测试', () => {
    test('员工管理完整端到端流程', async ({ page }) => {
      const employee = generateUniqueEmployee();

      await test.step('1. 新增员工', async () => {
        await createEmployee(page, employee);
        await expect(page.getByText('创建成功')).toBeVisible();
        await verifyEmployeeInTable(page, employee.name);
      });

      await test.step('2. 搜索员工', async () => {
        await page.getByTestId('search-name').fill(employee.name);
        await page.getByTestId('search-submit').click();
        await page.waitForTimeout(500);
        await verifyEmployeeInTable(page, employee.name);
      });

      await test.step('3. 编辑员工信息', async () => {
        const row = await verifyEmployeeInTable(page, employee.name);
        await row.getByRole('button', { name: '编辑' }).first().click();

        const updatedRole = '技术主管';
        await page.getByTestId('form-role').clear();
        await page.getByTestId('form-role').fill(updatedRole);
        await page.getByRole('button', { name: '保存' }).click();
        await page.waitForLoadState('networkidle');

        await expect(page.getByText('更新成功')).toBeVisible();
        const updatedRow = await verifyEmployeeInTable(page, employee.name);
        await expect(updatedRow).toContainText(updatedRole);
      });

      await test.step('4. 重置搜索', async () => {
        await page.getByTestId('search-reset').click();
        await expect(page.getByTestId('search-name')).toHaveValue('');
      });

      await test.step('5. 删除员工', async () => {
        const row = await verifyEmployeeInTable(page, employee.name);
        await row.getByRole('button', { name: '删除' }).first().click();
        await page.getByRole('button', { name: '确定' }).click();
        await page.waitForLoadState('networkidle');
        await expect(page.getByText('删除成功')).toBeVisible();
      });
    });
  });
});
