import { ContentLayout } from "@/components/admin-panel/content-layout";
import TeamWorkflowNew from "@/components/teams/team-workflow-new";
import { getAppTranslations } from "@/lib/translation";

const Page = async () => {
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("teams")}>
      <TeamWorkflowNew />
    </ContentLayout>
  );
};
export default Page;
