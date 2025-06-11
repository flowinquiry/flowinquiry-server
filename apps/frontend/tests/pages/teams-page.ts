import { expect, Locator, Page } from "@playwright/test";

/**
 * Page Object Model for the Teams page
 * This class encapsulates the selectors and actions for the teams page
 */
export class TeamsPage {
  readonly page: Page;
  readonly newTeamButton: Locator;
  readonly teamNameInput: Locator;
  readonly teamDescriptionInput: Locator;
  readonly saveButton: Locator;
  readonly managerDialog: Locator;
  readonly closeDialogButton: Locator;

  constructor(page: Page) {
    this.page = page;
    // Use data-testid for the "New team" button
    this.newTeamButton = page.getByTestId("team-list-new-team");
    // Keep using label selectors for form inputs as they don't have data-testid attributes
    this.teamNameInput = page.getByLabel("Name");
    this.teamDescriptionInput = page.getByLabel("Description");
    // Keep using text filtering for the "Save" button as it doesn't have a data-testid attribute
    this.saveButton = page
      .locator('button, input[type="submit"]')
      .filter({ hasText: /save|create|submit|confirm/i })
      .first();
    // Keep using role-based selector for the manager dialog as it's not the delete dialog
    this.managerDialog = page.locator("div[role='dialog']");
    // Keep using role and text filtering for the close button as it doesn't have a data-testid attribute
    this.closeDialogButton = page
      .getByRole("button", { name: /close|cancel|ok/i })
      .filter({ hasText: /close|cancel|ok/i });
  }

  /**
   * Navigate to the teams page
   */
  async goto() {
    await this.page.goto("/portal/teams");
    // Wait for the page to be fully loaded
    await this.page.waitForLoadState("networkidle");
  }

  /**
   * Verify that the teams page has loaded correctly
   */
  async expectPageLoaded() {
    await expect(this.page).toHaveURL("/portal/teams");
    // Wait for the page to be fully loaded
    await this.page.waitForLoadState("networkidle");
  }

  /**
   * Click on the New Team button
   */
  async clickNewTeamButton() {
    await this.newTeamButton.click();
    // Wait for navigation to complete after clicking
    await this.page.waitForLoadState("networkidle").catch(() => {
      console.log(
        "[DEBUG_LOG] Navigation did not complete after clicking New Team button",
      );
    });
  }

  /**
   * Verify that we're on the new team edit page
   */
  async expectNewTeamEditPage() {
    await expect(this.page).toHaveURL("/portal/teams/new/edit");
  }

  /**
   * Fill in the team details
   * @param name The name of the team
   * @param description The description of the team
   */
  async fillTeamDetails(name: string, description: string) {
    await this.teamNameInput.fill(name);
    await this.teamDescriptionInput.fill(description);
    await this.saveButton.click();
    // Wait for navigation to complete after saving
    await this.page.waitForLoadState("networkidle").catch(() => {
      console.log(
        "[DEBUG_LOG] Navigation did not complete after saving team details",
      );
    });
  }

  /**
   * Verify redirection to the new team dashboard page
   */
  async expectRedirectToTeamDashboard(): Promise<string> {
    const pattern = /\/portal\/teams\/[^/]+\/dashboard/;

    // Wait and assert using Playwright's expect
    await expect(this.page).toHaveURL(pattern);

    // Return URL if needed
    return this.page.url();
  }

  /**
   * Check if the manager dialog is displayed
   */
  async expectManagerDialogDisplayed() {
    await expect(this.managerDialog).toBeVisible();
  }

  /**
   * Close the manager dialog
   */
  async closeManagerDialog() {
    await this.closeDialogButton.click();
    // Wait for the dialog to disappear
    await expect(this.managerDialog)
      .not.toBeVisible()
      .catch(() => {
        console.log(
          "[DEBUG_LOG] Dialog did not disappear after clicking close button",
        );
      });
  }
}
