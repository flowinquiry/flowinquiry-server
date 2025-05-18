import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import MyTicketsView from "@/components/my/my-tickets";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();
  return (
    <ContentLayout title={t.common.navigation("my_tickets")}>
      <MyTicketsView />
    </ContentLayout>
  );
};

export default Page;
