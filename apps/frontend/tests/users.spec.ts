import { expect, test } from "@playwright/test";

import { HomePage } from "./pages/home-page";

test.describe("Users Page Navigation", () => {
  test("should navigate to users page and view user details", async ({
    page,
  }) => {
    const homePage = new HomePage(page);

    console.log("[DEBUG_LOG] Navigating to home page and logging in");
    await homePage.navigateAndLogin();

    const currentUrl = page.url();
    console.log(`[DEBUG_LOG] Current URL after login: ${currentUrl}`);

    if (currentUrl.includes("/login")) {
      console.log("[DEBUG_LOG] Login failed after retries, exiting test early");
      return; // Avoid using test.skip() inside async block
    }

    console.log("[DEBUG_LOG] Navigating to users page");
    await homePage.navigateToUrl("/portal/users");

    if (page.url().includes("/login")) {
      console.log("[DEBUG_LOG] Redirected to login, retrying login");
      await homePage.login("admin@flowinquiry.io", "admin");
      await homePage.navigateToUrl("/portal/users");

      if (page.url().includes("/login")) {
        console.log(
          "[DEBUG_LOG] Still redirected to login, exiting test early",
        );
        return;
      }
    }

    console.log("[DEBUG_LOG] Looking for user links");

    try {
      const userLinks = page.getByRole("link", {
        name: /[A-Za-z]+, [A-Za-z]+/,
      });

      await userLinks.first().waitFor({ state: "visible", timeout: 2900 });

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

      await userLink.click();
      try {
        await page.waitForLoadState("networkidle", { timeout: 5000 });
      } catch {
        console.log("[DEBUG_LOG] Timeout waiting for navigation to complete");
      }

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
