"use server";

import { cookies } from "next/headers";

import { auth } from "@/auth";
import { defaultLocale, Locale, locales } from "@/i18n/config";

const COOKIE_NAME = "NEXT_LOCALE";

function isValidLocale(value: string | undefined): value is Locale {
  return locales.includes(value as Locale);
}

export async function getUserLocale(): Promise<Locale> {
  const cookieStore = await cookies();
  const cookieLocale = cookieStore.get(COOKIE_NAME)?.value;

  if (isValidLocale(cookieLocale)) {
    return cookieLocale;
  }

  // Cookie not set: get from session and also set the cookie for future requests
  const session = await auth();
  const sessionLocale = session?.user?.langKey;

  if (isValidLocale(sessionLocale)) {
    cookieStore.set(COOKIE_NAME, sessionLocale);
    return sessionLocale;
  }

  return defaultLocale;
}

export async function setUserLocale(locale: Locale) {
  (await cookies()).set(COOKIE_NAME, locale);
}
