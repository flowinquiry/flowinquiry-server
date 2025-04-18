import { ContentLayout } from "@/components/admin-panel/content-layout";
import { AuthorityView } from "@/components/authorities/authority-view";
import { deobfuscateToString } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: { params: Promise<{ authorityId: string }> }) => {
  const params = await props.params;
  const authorityId = deobfuscateToString(params.authorityId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("authorities")}>
      <AuthorityView authorityId={authorityId} />
    </ContentLayout>
  );
};

export default Page;
