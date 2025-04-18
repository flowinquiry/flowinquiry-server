import { SimpleContentView } from "@/components/admin-panel/simple-content-view";
import { UserForm } from "@/components/users/user-form";
import { deobfuscateToNumber } from "@/lib/endecode";
import { getAppTranslations } from "@/lib/translation";

const Page = async (props: { params: Promise<{ userId: string | "new" }> }) => {
  const params = await props.params;
  const userId =
    params.userId !== "new" ? deobfuscateToNumber(params.userId) : undefined;

  const t = await getAppTranslations();

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    { title: t.common.navigation("users"), link: "/portal/users" },
    { title: t.common.buttons("create"), link: "/portal/users/edit/new" },
  ];

  return (
    <SimpleContentView
      title={t.common.navigation("users")}
      breadcrumbItems={breadcrumbItems}
    >
      <UserForm userId={userId} />
    </SimpleContentView>
  );
};

export default Page;
