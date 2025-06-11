import { test } from "@playwright/test";

import { HomePage } from "./pages/home-page";

test.describe("Home Page - Authenticated", () => {
  test.use({ storageState: "playwright/.auth/admin.json" });
  test("should navigate to home page and login successfully", async ({
    page,
  }) => {
    const homePage = new HomePage(page);

    // Navigate to the home page
    await homePage.goto();

    // Since we're using the admin storage state, we should be already logged in
    // and redirected to the dashboard
    await homePage.expectPageLoaded();
  });

  test("should navigate and login in one step", async ({ page }) => {
    const homePage = new HomePage(page);

    // Navigate to home page and login with admin credentials in one step
    await homePage.navigateAndLogin();
  });
});

test.describe("Home Page - Unauthenticated", () => {
  test.use({ storageState: "playwright/.auth/unauthenticated.json" });

  test("should stay on login page when using incorrect credentials", async ({
    page,
  }) => {
    const homePage = new HomePage(page);

    await homePage.goto();
    await homePage.expectRedirectToLogin();

    await homePage.login("wrong@example.com", "wrongpassword");
    await homePage.expectLoginError();
  });
});
