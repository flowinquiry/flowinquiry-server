import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamNavLayout from "@/components/teams/team-nav";
import { TicketForm } from "@/components/teams/ticket-form";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: {
  params: Promise<{ teamId: string; ticketId: string }>;
}) => {
  const params = await props.params;
  const ticketId = deobfuscateToNumber(params.ticketId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamNavLayout teamId={ticketId}>
        <TicketForm ticketId={ticketId} />
      </TeamNavLayout>
    </ContentLayout>
  );
};

export default Page;
