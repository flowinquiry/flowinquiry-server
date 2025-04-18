import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamDashboard from "@/components/teams/team-dashboard";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamDashboard />
    </ContentLayout>
  );
};

export default Page;
