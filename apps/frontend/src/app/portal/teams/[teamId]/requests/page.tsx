import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TicketListView from "@/components/teams/ticket-list-view";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TicketListView />
    </ContentLayout>
  );
};

export default Page;
