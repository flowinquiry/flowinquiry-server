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
import { expect, type Page } from "@playwright/test";

export async function assertAuthenticated(page: Page) {
  await page.goto("/portal");

  // Wait up to 10s for either dashboard or redirect to login
  await page.waitForLoadState("networkidle");

  const url = page.url();

  if (url.includes("/login")) {
    throw new Error(
      "Authentication failed: redirected to login — storageState may be expired or invalid.",
    );
  }

  // Confirm authenticated user has access to portal
  await expect(page).toHaveURL(/\/portal/);
}
