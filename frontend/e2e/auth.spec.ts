import { test, expect } from '@playwright/test';
import { login, logout, TEST_USER } from './helpers';

test.describe('认证功能测试', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
  });

  test('登录页面正确显示', async ({ page }) => {
    await expect(page).toHaveTitle(/HR/);
    await expect(page.getByText('HR 管理系统')).toBeVisible();
    await expect(page.getByTestId('login-username')).toBeVisible();
    await expect(page.getByTestId('login-password')).toBeVisible();
    await expect(page.getByTestId('login-submit')).toBeVisible();
  });

  test('使用正确的凭据登录成功', async ({ page }) => {
    await login(page);
    await expect(page.getByTestId('user-menu')).toBeVisible();
    await expect(page.getByText(TEST_USER.username)).toBeVisible();
  });

  test('使用错误的用户名登录失败', async ({ page }) => {
    await page.getByTestId('login-username').fill('wronguser');
    await page.getByTestId('login-password').fill(TEST_USER.password);
    await page.getByTestId('login-submit').click();
    await expect(page.getByText('用户名或密码错误')).toBeVisible();
    await expect(page).toHaveURL(/.*\/login$/);
  });

  test('使用错误的密码登录失败', async ({ page }) => {
    await page.getByTestId('login-username').fill(TEST_USER.username);
    await page.getByTestId('login-password').fill('wrongpassword');
    await page.getByTestId('login-submit').click();
    await expect(page.getByText('用户名或密码错误')).toBeVisible();
    await expect(page).toHaveURL(/.*\/login$/);
  });

  test('空表单提交显示校验错误', async ({ page }) => {
    await page.getByTestId('login-submit').click();
    await expect(page.getByText('请输入用户名')).toBeVisible();
    await expect(page.getByText('请输入密码')).toBeVisible();
  });

  test('登录成功后可以退出登录', async ({ page }) => {
    await login(page);
    await logout(page);
    await expect(page.getByTestId('login-username')).toBeVisible();
  });

  test('未登录访问受保护页面重定向到登录页', async ({ page }) => {
    await page.goto('/employees');
    await expect(page).toHaveURL(/.*\/login$/);
  });

  test('已登录用户访问登录页重定向到首页', async ({ page }) => {
    await login(page);
    await page.goto('/login');
    await expect(page).not.toHaveURL(/.*\/login$/);
  });
});
