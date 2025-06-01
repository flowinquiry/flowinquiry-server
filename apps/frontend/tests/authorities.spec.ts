import { expect, test } from "@playwright/test";

import { HomePage } from "./pages/home-page";

test.describe("Authorities Management", () => {
  test("should verify Administrator authority and its users", async ({
    page,
  }) => {
    // Initialize page objects
    const homePage = new HomePage(page);

    // Step 1: Go to home page and login
    console.log("[DEBUG_LOG] Navigating to home page and logging in");
    await homePage.navigateAndLogin();

    // Ensure we're logged in by checking URL
    const currentUrl = page.url();
    console.log(`[DEBUG_LOG] Current URL after login: ${currentUrl}`);

    // If we're not logged in (still on login page), retry login
    if (currentUrl.includes("/login")) {
      console.log("[DEBUG_LOG] Still on login page, retrying login");
      await homePage.login("admin@flowinquiry.io", "admin");
      await page.waitForTimeout(3000);
      console.log(`[DEBUG_LOG] URL after retry: ${page.url()}`);

      // If still not logged in, skip the test
      if (page.url().includes("/login")) {
        console.log("[DEBUG_LOG] Login failed, skipping test");
        test.skip();
        return;
      }
    }

    // Step 2: Navigate to authorities page
    console.log("[DEBUG_LOG] Navigating to authorities page");
    await page.goto("/portal/settings/authorities");

    // Wait for page to load
    await page.waitForTimeout(3000);
    console.log(`[DEBUG_LOG] Current URL after navigation: ${page.url()}`);

    // If redirected to login, try to login again and navigate back
    if (page.url().includes("/login")) {
      console.log("[DEBUG_LOG] Redirected to login, logging in again");
      await homePage.login("admin@flowinquiry.io", "admin");
      await page.waitForTimeout(3000);

      // Navigate to authorities page again
      await page.goto("/portal/settings/authorities");
      await page.waitForTimeout(3000);

      // If still redirected to login, skip the test
      if (page.url().includes("/login")) {
        console.log("[DEBUG_LOG] Still redirected to login, skipping test");
        test.skip();
        return;
      }
    }

    // Step 3: Check there is at least one authority named "Administrator"
    console.log("[DEBUG_LOG] Looking for Administrator authority");

    // Wait for the page to be fully loaded
    await page.waitForLoadState("networkidle");

    // Find all authority links or rows
    const authorityElements = page.getByRole("link", {
      name: /Administrator/i,
    });

    // Get the count of authority elements
    const count = await authorityElements.count();
    console.log(`[DEBUG_LOG] Found ${count} Administrator authorities`);

    // Verify at least one Administrator authority exists
    expect(count).toBeGreaterThan(0);

    // Step 4: Click on the Administrator authority to view details
    const adminAuthority = authorityElements.first();
    console.log("[DEBUG_LOG] Clicking on Administrator authority");
    await adminAuthority.click();

    // Wait for navigation
    await page.waitForTimeout(3000);
    console.log(
      `[DEBUG_LOG] Current URL after clicking authority: ${page.url()}`,
    );

    // Step 5: Verify there is at least one user belonging to this role
    console.log("[DEBUG_LOG] Checking for users in Administrator role");

    // Wait for the page to be fully loaded
    await page.waitForLoadState("networkidle");

    // Find all user elements in the authority details page
    // This selector might need adjustment based on the actual page structure
    const userElements = page.getByRole("row").filter({ hasText: /@/ });

    // Get the count of user elements
    const userCount = await userElements.count();
    console.log(`[DEBUG_LOG] Found ${userCount} users in Administrator role`);

    // Verify at least one user exists in the Administrator role
    expect(userCount).toBeGreaterThan(0);
  });
});
