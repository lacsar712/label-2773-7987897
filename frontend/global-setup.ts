import { chromium, type FullConfig } from '@playwright/test';
import { TEST_USER } from './e2e/helpers';

async function globalSetup(config: FullConfig) {
  const { baseURL, storageState } = config.projects[0].use;
  const browser = await chromium.launch();
  const page = await browser.newPage();

  await page.goto(baseURL + '/login');
  await page.waitForLoadState('networkidle');
  await page.getByTestId('login-username').fill(TEST_USER.username);
  await page.getByTestId('login-password').fill(TEST_USER.password);
  await page.getByTestId('login-submit').click();
  await page.waitForLoadState('networkidle');

  await page.context().storageState({ path: storageState as string });
  await browser.close();
}

export default globalSetup;
