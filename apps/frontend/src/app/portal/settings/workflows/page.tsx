import React from "react";

import { SimpleContentView } from "@/components/admin-panel/simple-content-view";
import WorkflowsView from "@/components/workflows/workflow-list";
import { useAppClientTranslations } from "@/hooks/use-translations";

const Page = () => {
  const t = useAppClientTranslations();

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    { title: t.common.navigation("settings"), link: "/portal/settings" },
    {
      title: t.common.navigation("workflows"),
      link: "/portal/settings/workflows",
    },
  ];

  return (
    <SimpleContentView
      title={t.common.navigation("workflows")}
      breadcrumbItems={breadcrumbItems}
    >
      <WorkflowsView />
    </SimpleContentView>
  );
};

export default Page;
