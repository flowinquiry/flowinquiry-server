import { defineConfig, devices } from "@playwright/test";

/**
 * Read environment variables from file.
 * https://github.com/motdotla/dotenv
 */
// import dotenv from 'dotenv';
// import path from 'path';
// dotenv.config({ path: path.resolve(__dirname, '.env') });

/**
 * See https://playwright.dev/docs/test-configuration.
 */
export default defineConfig({
  testDir: "./tests",
  globalSetup: "./global-setup.ts",
  /* Run tests sequentially to avoid race conditions */
  fullyParallel: false,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on both CI and local to handle flaky tests */
  retries: process.env.CI ? 3 : 1,
  /* Run tests sequentially with a single worker to prevent navigation interference */
  workers: 1,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: "html",
  /* Set consistent timeouts */
  timeout: 60000,
  expect: {
    timeout: 10000,
  },

  use: {
    /* Base URL to use in actions like `await page.goto('/')`. */
    baseURL: "http://localhost:3000",

    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: "on-first-retry",

    /* Only capture screenshots on test failures */
    screenshot: "only-on-failure",

    /* Disable browser caching to prevent cache-related issues */
    ignoreHTTPSErrors: true,

    /* Ensure each test gets a fresh browser context */
    acceptDownloads: true,
    bypassCSP: true,

    /* Create a new context for each test to isolate browser state */
    contextOptions: {
      ignoreHTTPSErrors: true,
      viewport: { width: 1280, height: 720 },
      /* Clear storage state between tests to prevent login persistence */
      storageState: undefined,
    },

    /* Set navigation timeout */
    navigationTimeout: 6000, // Reduced to less than 3 seconds
  },

  /* Configure projects for major browsers */
  projects: [
    {
      name: "chromium",
      use: {
        ...devices["Desktop Chrome"],
        // Disable cache for Chromium
        launchOptions: {
          args: ["--disable-cache"],
        },
      },
    },
  ],

  /* Run your local dev server before starting the tests */
  webServer: {
    command: "pnpm run dev",
    url: "http://localhost:3000",
    reuseExistingServer: true,
    timeout: 60000,
  },
});
