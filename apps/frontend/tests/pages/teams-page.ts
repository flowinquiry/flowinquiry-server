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
    // Try multiple selectors for the "New team" button
    this.newTeamButton = page
      .locator("button, a")
      .filter({ hasText: /new team|create team|add team/i })
      .first();
    this.teamNameInput = page.getByLabel("Name");
    this.teamDescriptionInput = page.getByLabel("Description");
    // Try multiple selectors for the "Save" button
    this.saveButton = page
      .locator('button, input[type="submit"]')
      .filter({ hasText: /save|create|submit|confirm/i })
      .first();
    this.managerDialog = page.locator("div[role='dialog']");
    this.closeDialogButton = page
      .getByRole("button", { name: /close|cancel|ok/i })
      .filter({ hasText: /close|cancel|ok/i });
  }

  /**
   * Navigate to the teams page
   */
  async goto() {
    await this.page.goto("/portal/teams");
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
  }

  /**
   * Verify redirection to the new team dashboard page
   */
  async expectRedirectToTeamDashboard() {
    // Wait for navigation to complete
    await this.page.waitForURL(/\/portal\/teams\/[^/]+\/dashboard/);

    // Get the current URL and verify it matches the expected pattern
    const url = this.page.url();
    expect(url).toMatch(/\/portal\/teams\/[^/]+\/dashboard/);

    return url;
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
  }
}
