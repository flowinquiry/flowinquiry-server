import { useLocale, useTranslations } from "next-intl";

import LocaleSwitcherSelect from "@/components/shared/locale-switcher-select";

export default function LocaleSwitcher() {
  const t = useTranslations("locale_switcher");
  const locale = useLocale();

  return (
    <LocaleSwitcherSelect
      defaultValue={locale}
      items={[
        {
          value: "en",
          label: t("en"),
        },
        {
          value: "fr",
          label: t("fr"),
        },
      ]}
      label={t("label")}
    />
  );
}
