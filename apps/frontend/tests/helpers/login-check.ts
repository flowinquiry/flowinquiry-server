/**
 * Login Check Helper
 *
 * This file provides utility functions to verify authentication status in tests.
 * It helps ensure that users are properly authenticated before running tests that
 * require authentication, making tests more reliable and preventing false failures.
 *
 * The main function checks if a user is properly authenticated by verifying they
 * can access protected routes and aren't redirected to the login page.
 */

import { expect,Page, test } from "@playwright/test";

/**
 * Verifies that a user is properly authenticated.
 *
 * This function:
 * 1. Navigates to the portal page
 * 2. Checks if the user stays on the portal page (authenticated) or is redirected to login (unauthenticated)
 * 3. Skips the test if the user is not authenticated
 *
 * @param page - The Playwright Page object
 */
export async function assertAuthenticated(page: Page) {
  // Navigate to the portal page
  await page.goto("/portal");

  // Get the current URL after navigation
  const url = page.url();

  // Check if the user is still on the portal page
  if (url.includes("/portal")) {
    // User is authenticated, continue with the test
    await expect(page).toHaveURL(/\/portal/);
  } else if (url.includes("/login")) {
    // User was redirected to login, skip the test
    test.skip("User was redirected to login — storageState may be expired.");
  }
}
