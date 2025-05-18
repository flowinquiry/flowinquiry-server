import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import TicketDetailView from "@/components/teams/ticket-detail-view";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

interface ProjectTaskDetailProps {
  params: Promise<{ teamId: string; projectId: string; taskId: string }>;
}

const ProjectTaskDetailPage = async (props: ProjectTaskDetailProps) => {
  const params = await props.params;
  const taskId = deobfuscateToNumber(params.taskId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TicketDetailView ticketId={taskId} />
    </ContentLayout>
  );
};

export default ProjectTaskDetailPage;
