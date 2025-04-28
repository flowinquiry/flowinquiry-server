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

  // Don't set cookie here, just get from session
  const session = await auth();
  const sessionLocale = session?.user?.langKey;

  if (isValidLocale(sessionLocale)) {
    return sessionLocale;
  }

  return defaultLocale;
}

// Separate function for initializing locale with cookie au
export async function initializeUserLocale(): Promise<Locale> {
  "use server";

  const cookieStore = await cookies();
  const cookieLocale = cookieStore.get(COOKIE_NAME)?.value;

  if (isValidLocale(cookieLocale)) {
    return cookieLocale;
  }

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
  return { success: true };
}
