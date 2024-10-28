"use server";

import { unstable_noStore as noStore } from "next/dist/server/web/spec-extension/unstable-no-store";
import { redirect } from "next/navigation";

import { doAdvanceSearch, post } from "@/lib/actions/commons.action";
import { BACKEND_API } from "@/lib/constants";
import { userSchema, UserSearchParams, UserType } from "@/types/users";

export async function searchUsers(input: UserSearchParams) {
  noStore();
  return doAdvanceSearch<UserType>(`${BACKEND_API}/api/users/search`);
}

export const createUser = async (user: UserType) => {
  const validation = userSchema.safeParse(user);
  if (validation.success) {
    await post(`${BACKEND_API}/api/admin/users`, user);
    redirect("/portal/users");
  }
};

export const passwordReset = async (key: string, password: string) => {
  await post(
    `${BACKEND_API}/api/account/reset-password/finish`,
    { key: key, newPassword: password },
    false,
  );
};
