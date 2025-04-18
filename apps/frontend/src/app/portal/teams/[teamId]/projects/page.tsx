import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import ProjectListView from "@/components/projects/project-list-view";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <ProjectListView />
    </ContentLayout>
  );
};

export default Page;
