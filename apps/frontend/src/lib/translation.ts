import { createTranslator } from "next-intl";

import { loadMessages } from "@/lib/load-locales-messages";

export async function getAppTranslations(locale: string) {
  const messages = await loadMessages(locale);
  const t = createTranslator({ locale, messages });

  return {
    users: {
      list: (key: string, values?: Record<string, any>) =>
        t(`users.list.${key}`, values),
      common: (key: string, values?: Record<string, any>) =>
        t(`users.common.${key}`, values),
    },
    common: {
      buttons: (key: string, values?: Record<string, any>) =>
        t(`common.buttons.${key}`, values),
      navigation: (key: string, values?: Record<string, any>) =>
        t(`common.navigation.${key}`, values),
    },
  };
}
