import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamWorkflowsView from "@/components/teams/team-workflows";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamWorkflowsView />
    </ContentLayout>
  );
};

export default Page;
