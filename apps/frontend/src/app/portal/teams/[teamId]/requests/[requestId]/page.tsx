import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamRequestDetailView from "@/components/teams/ticket-detail";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

interface RequestDetailsProps {
  params: Promise<{ teamId: string; requestId: string }>;
}

const RequestDetailsPage = async (props: RequestDetailsProps) => {
  const params = await props.params;
  const teamRequestId = deobfuscateToNumber(params.requestId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamRequestDetailView teamRequestId={teamRequestId} />
    </ContentLayout>
  );
};

export default RequestDetailsPage;
