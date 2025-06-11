import { chromium } from "@playwright/test";
import fs from "fs/promises";

import config from "./playwright.config";
import { HomePage } from "./tests/pages/home-page";

// Extract the webServer URL from the configuration
// Handle both single webServer and array of webServers
let webServerUrl = "http://localhost:3000";
if (config.webServer) {
  if (Array.isArray(config.webServer)) {
    // If it's an array, use the first element's URL
    webServerUrl = config.webServer[0]?.url || webServerUrl;
  } else {
    // If it's a single object, use its URL
    webServerUrl = config.webServer.url || webServerUrl;
  }
}

/**
 * Helper function to authenticate a user and save the authentication state
 * @param email User email
 * @param password User password
 * @param filename Filename to save the authentication state
 */
async function authenticateUser(
  email: string,
  password: string,
  filename: string,
) {
  console.log(`[DEBUG_LOG] Authenticating user: ${email}`);

  const browser = await chromium.launch();
  const context = await browser.newContext({
    baseURL: webServerUrl,
  });
  const page = await context.newPage();
  const homePage = new HomePage(page);

  // Navigate to login page and authenticate
  await page.goto("/login");
  await homePage.login(email, password);

  // Wait for navigation to complete and check if we're on the portal page
  await page.waitForLoadState("domcontentloaded");
  const url = page.url();
  if (!url.includes("/portal")) {
    console.log(
      `[DEBUG_LOG] Login might have failed for ${email} - current URL: ${url}`,
    );
  } else {
    console.log(`[DEBUG_LOG] Login successful for ${email}`);
  }

  // Save authentication state
  await fs.mkdir("playwright/.auth", { recursive: true });
  await fs.writeFile(
    `playwright/.auth/${filename}`,
    JSON.stringify(await page.context().storageState()),
  );

  await browser.close();
}

export default async function globalSetup() {
  // Create authentication files for admin and regular user
  await authenticateUser("admin@flowinquiry.io", "admin", "admin.json");
  await authenticateUser("user@flowinquiry.io", "user1234", "user.json");

  // Create unauthenticated state file (optional but explicit)
  await fs.writeFile(
    "playwright/.auth/unauthenticated.json",
    JSON.stringify({ cookies: [], origins: [] }),
  );
}
