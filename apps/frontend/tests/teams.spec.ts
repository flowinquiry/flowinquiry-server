import { test } from "@playwright/test";

import { assertAuthenticated } from "./helpers/login-check";
import { createTeam } from "./helpers/team-fixtures";

test.describe("Teams Page", () => {
  test.use({ storageState: "./playwright/.auth/admin.json" });
  test("should create a new team and handle manager dialog", async ({
    page,
  }) => {
    // Check if the user is authenticated
    await assertAuthenticated(page);

    // Create a new team using the fixture
    const teamUrl = await createTeam(page, "Team ANC", "Team description");
    console.log(`[DEBUG_LOG] Created team with dashboard URL: ${teamUrl}`);
  });
});
