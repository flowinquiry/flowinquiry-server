import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TicketDetailView from "@/components/teams/ticket-detail-view";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

interface PageProps {
  params: Promise<{ teamId: string; ticketId: string }>;
}

const Page = async (props: PageProps) => {
  const params = await props.params;
  const ticketId = deobfuscateToNumber(params.ticketId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TicketDetailView ticketId={ticketId} />
    </ContentLayout>
  );
};

export default Page;
