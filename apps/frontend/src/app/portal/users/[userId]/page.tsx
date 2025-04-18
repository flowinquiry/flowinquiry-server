import React from "react";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import { UserView } from "@/components/users/user-view";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: { params: Promise<{ userId: string }> }) => {
  const params = await props.params;
  const userId = deobfuscateToNumber(params.userId);
  const t = await getAppTranslations();

  return (
    <ContentLayout title={t.common.navigation("users")}>
      <UserView userId={userId} />
    </ContentLayout>
  );
};

export default Page;
