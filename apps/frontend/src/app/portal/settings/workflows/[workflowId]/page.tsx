import { ContentLayout } from "@/components/admin-panel/content-layout";
import WorkflowDetailView from "@/components/workflows/workflow-detail-view";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: { params: Promise<{ workflowId: string }> }) => {
  const params = await props.params;
  const workflowId = deobfuscateToNumber(params.workflowId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("workflows")}>
      <WorkflowDetailView workflowId={workflowId} />
    </ContentLayout>
  );
};

export default Page;
