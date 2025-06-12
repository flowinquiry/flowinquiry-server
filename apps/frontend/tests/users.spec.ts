import { expect, test } from "@playwright/test";

import { assertAuthenticated } from "./helpers/login-check";
import { UsersPage } from "./pages/users-page";

test.describe("Users Page Navigation", () => {
  test.use({ storageState: "playwright/.auth/admin.json" });
  test("should navigate to users page and view user details", async ({
    page,
  }) => {
    await assertAuthenticated(page);

    const userPage = new UsersPage(page);
    await userPage.goto();
    await userPage.expectPageLoaded();

    try {
      // First find the user name containers
      const userNameContainers = page.getByTestId(/^user-list-name-/);

      await userNameContainers
        .first()
        .waitFor({ state: "visible", timeout: 2900 });

      // Then find the link elements inside them
      const userLinks = userNameContainers.locator("a");

      const count = await userLinks.count();
      console.log(`[DEBUG_LOG] Found ${count} user links`);

      if (count === 0) {
        console.log("[DEBUG_LOG] No user links found, exiting test early");
        return;
      }

      const userLink = userLinks.first();
      const userName = await userLink.textContent();
      const userHref = await userLink.getAttribute("href");
      console.log(
        `[DEBUG_LOG] Selected user: ${userName} with href ${userHref}`,
      );

      const userId = userHref?.match(/\/portal\/users\/(\w+)$/)?.[1];
      if (!userId) throw new Error("Could not extract user ID from href");

      // Click the user link and wait for the correct URL
      await Promise.all([
        userLink.click(),
        page.waitForURL(new RegExp(`/portal/users/${userId}$`), {
          timeout: 10000,
        }),
      ]);

      const finalUrl = page.url();
      console.log(`[DEBUG_LOG] Final URL: ${finalUrl}`);
      expect(finalUrl).toContain(`/portal/users/${userId}`);
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : String(error);
      console.log(
        `[DEBUG_LOG] Error during user selection or navigation: ${errorMessage}`,
      );
      throw new Error(errorMessage); // Fail the test explicitly
    }
  });
});
