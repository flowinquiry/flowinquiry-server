import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import ProjectView from "@/components/projects/project-view";
import { getAppTranslations } from "@/lib/translation";

interface ProjectDetailPageProps {
  params: Promise<{ teamId: string; projectId: string }>;
}

const ProjectDetailPage = async (props: ProjectDetailPageProps) => {
  const params = await props.params;
  const projectId = params.projectId;
  console.log("Project id ", projectId);
  const t = await getAppTranslations();

  return (
    <ContentLayout
      title={t.common.navigation("teams")}
      useDefaultStyles={false}
      className="h-full pt-8 pb-8 px-4 sm:px-8 bg-card"
    >
      <ProjectView projectId={projectId} />
    </ContentLayout>
  );
};

export default ProjectDetailPage;
