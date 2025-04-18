import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamNavLayout from "@/components/teams/team-nav";
import { TeamRequestForm } from "@/components/teams/team-requests-form";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: {
  params: Promise<{ teamId: string; requestId: string }>;
}) => {
  const params = await props.params;
  const teamRequestId = deobfuscateToNumber(params.requestId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamNavLayout teamId={teamRequestId}>
        <TeamRequestForm teamRequestId={teamRequestId} />
      </TeamNavLayout>
    </ContentLayout>
  );
};

export default Page;
