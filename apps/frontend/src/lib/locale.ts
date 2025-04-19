"use server";

import { auth } from "@/auth";
import { defaultLocale } from "@/i18n/config";

// In this example the locale is read from a cookie. You could alternatively
// also read it from a database, backend service, or any other source.
const COOKIE_NAME = "NEXT_LOCALE";

export async function getUserLocale() {
  const session = await auth();
  return session?.user?.langKey ?? defaultLocale;
}
