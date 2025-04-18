import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import MyTeamRequestsView from "@/components/my/my-team-requests";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();
  return (
    <ContentLayout title={t.common.navigation("my_tickets")}>
      <MyTeamRequestsView />
    </ContentLayout>
  );
};

export default Page;
