import { expect, Locator, Page } from "@playwright/test";

/**
 * Page Object Model for the Users page
 * This class encapsulates the selectors and actions for the users page
 */
export class UsersPage {
  readonly page: Page;
  readonly userLinks: Locator;

  constructor(page: Page) {
    this.page = page;
    this.userLinks = page.getByRole("link", { name: /user-/i });
  }

  /**
   * Navigate to the users page
   */
  async goto() {
    await this.page.goto("/portal/users");
  }

  /**
   * Verify that the users page has loaded correctly
   */
  async expectPageLoaded() {
    await expect(this.page).toHaveURL("/portal/users");
    // Wait for the page to be fully loaded
    await this.page.waitForLoadState("networkidle");
  }

  /**
   * Click on the first user link in the list
   */
  async clickFirstUserLink() {
    // Wait for user links to be visible
    await this.userLinks.first().waitFor({ state: "visible" });

    // Store the href attribute to verify redirection later
    const href = await this.userLinks.first().getAttribute("href");

    // Click the first user link
    await this.userLinks.first().click();

    // Wait for navigation to complete after clicking
    await this.page.waitForLoadState("networkidle").catch(() => {
      console.log(
        "[DEBUG_LOG] Navigation did not complete after clicking user link",
      );
    });

    // Return the href for verification
    return href;
  }

  /**
   * Verify redirection to user details page
   * @param userHref The href of the user link that was clicked
   */
  async expectRedirectToUserPage(userHref: string) {
    // Extract the user ID from the href
    const userId = userHref.split("/").pop();

    // Verify we're on the correct user page
    await expect(this.page).toHaveURL(`/portal/users/${userId}`);
  }
}
