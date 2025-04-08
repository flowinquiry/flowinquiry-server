import { useTranslations } from "next-intl";

export function useAppClientTranslations() {
  return {
    teams: {
      list: useTranslations("teams.list"),
    },
    users: {
      list: useTranslations("users.list"),
      detail: useTranslations("users.detail"),
      common: useTranslations("users.common"),
      form: useTranslations("users.form"),
      org_chart_view: useTranslations("users.org_chart_view"),
    },
    common: {
      buttons: useTranslations("common.buttons"),
      misc: useTranslations("common.misc"),
      navigation: useTranslations("common.navigation"),
    },
    authorities: {
      common: useTranslations("authorities.common"),
      list: useTranslations("authorities.list"),
    },
  };
}
