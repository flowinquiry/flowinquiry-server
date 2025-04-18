import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamWorkflowDetailView from "@/components/teams/team-workflow-detail-view";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: {
  params: Promise<{ teamId: string; workflowId: string }>;
}) => {
  const params = await props.params;
  const t = await getAppTranslations();
  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamWorkflowDetailView
        workflowId={deobfuscateToNumber(params.workflowId)}
      />
    </ContentLayout>
  );
};

export default Page;
