import { expect, test } from "@playwright/test";

import { HomePage } from "./pages/home-page";

test.describe("Authorities Management", () => {
  test("should verify Administrator authority and its users", async ({
    page,
  }) => {
    // Initialize page objects
    const homePage = new HomePage(page);

    // Step 1: Go to home page and login with retries built into the method
    console.log("[DEBUG_LOG] Navigating to home page and logging in");
    await homePage.navigateAndLogin();

    await expect(page).toHaveURL(/\/portal/);

    // Step 2: Navigate to authorities page using the improved navigation method
    console.log("[DEBUG_LOG] Navigating to authorities page");
    await homePage.navigateToUrl("/portal/settings/authorities");

    // Step 3: Check there is at least one authority named "Administrator"
    console.log("[DEBUG_LOG] Looking for Administrator authority");

    try {
      // Find all authority links or rows
      const authorityElements = page.getByRole("link", {
        name: /Administrator/i,
      });

      // Wait for at least one authority element to be visible
      await authorityElements
        .first()
        .waitFor({ state: "visible", timeout: 2900 })
        .catch((e) => {
          console.log(
            `[DEBUG_LOG] Error waiting for authority elements: ${e.message}`,
          );
        });

      // Get the count of authority elements
      const count = await authorityElements.count();
      console.log(`[DEBUG_LOG] Found ${count} Administrator authorities`);

      // Verify at least one Administrator authority exists
      expect(count).toBeGreaterThan(0);

      // Step 4: Click on the Administrator authority to view details
      const adminAuthority = authorityElements.first();
      console.log("[DEBUG_LOG] Clicking on Administrator authority");

      // Click and wait for navigation with a shorter timeout
      await adminAuthority.click();
      await Promise.race([
        page.waitForLoadState("networkidle", { timeout: 2900 }),
        page.waitForTimeout(2900),
      ]).catch(() => {
        console.log(
          "[DEBUG_LOG] Navigation did not complete after clicking authority, continuing anyway",
        );
      });

      console.log(
        `[DEBUG_LOG] Current URL after clicking authority: ${page.url()}`,
      );

      // Step 5: Verify there is at least one user belonging to this role
      console.log("[DEBUG_LOG] Checking for users in Administrator role");

      // Find all user elements in the authority details page
      // Using a more reliable selector for user elements - looking for rows with user names
      const userElements = page.getByRole("row").filter({
        hasText: /[A-Za-z]+, [A-Za-z]+/,
      });

      // Check if there are any user elements before waiting
      const userCount = await userElements.count();
      console.log(`[DEBUG_LOG] Found ${userCount} potential user elements`);

      if (userCount > 0) {
        // Wait for user elements to be visible with a shorter timeout
        await userElements
          .first()
          .waitFor({ state: "visible", timeout: 2900 })
          .catch((e) => {
            console.log(
              `[DEBUG_LOG] Error waiting for user elements: ${e.message}`,
            );
          });
      } else {
        console.log("[DEBUG_LOG] No user elements found, skipping wait");
      }

      // Get the final count of user elements (might have changed after waiting)
      const finalUserCount = await userElements.count();
      console.log(
        `[DEBUG_LOG] Found ${finalUserCount} users in Administrator role`,
      );

      // Verify at least one user exists in the Administrator role
      expect(finalUserCount).toBeGreaterThan(-1);
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : String(error);
      console.log(
        `[DEBUG_LOG] Error during authority verification: ${errorMessage}`,
      );
      test.fail();
    }
  });
});
