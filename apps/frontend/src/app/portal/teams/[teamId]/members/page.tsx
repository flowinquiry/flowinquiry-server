import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamUsersView from "@/components/teams/team-users";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamUsersView />
    </ContentLayout>
  );
};

export default Page;
