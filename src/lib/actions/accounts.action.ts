"use server";

import { redirect } from "next/navigation";

import { auth } from "@/auth";
import { fetchData } from "@/lib/actions/commons.action";
import { BACKEND_API } from "@/lib/constants";
import { accountSchema, AccountType } from "@/types/accounts";
import { ActionResult, PageableResult } from "@/types/commons";

export const getAccounts = async (): Promise<ActionResult> => {
  const session = await auth();

  const res = await fetch(`${BACKEND_API}/api/crm/accounts`, {
    headers: {
      "Content-Type": "application/json",
      "Access-Control-Allow-Origin": "*",
      Authorization: `Bearer ${session?.user?.accessToken}`,
    },
  });
  if (res.ok) {
    return {
      ok: true,
      status: "success",
      data: (await res.json()) as PageableResult<AccountType>,
    };
  } else {
    return {
      ok: false,
      status: "user_error",
      message: `Can not get the users ${res.status}`,
    };
  }
};

export const saveOrUpdateAccount = async (
  prevState: ActionResult,
  isEdit: boolean,
  account: AccountType,
): Promise<ActionResult> => {
  const validation = accountSchema.safeParse(account);

  if (validation.success) {
    let response;
    const session = await auth();
    if (isEdit) {
      response = await fetch(`${BACKEND_API}/api/crm/accounts/${account.id}`, {
        method: "PUT",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
          Authorization: `Bearer ${session?.user?.accessToken}`,
        },
        body: JSON.stringify(account),
      });
    } else {
      response = await fetch(`${BACKEND_API}/api/crm/accounts`, {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
          "Access-Control-Allow-Origin": "*",
          Authorization: `Bearer ${session?.user?.accessToken}`,
        },
        body: JSON.stringify(account),
      });
    }

    if (response.ok) {
      redirect("/portal/accounts");
    } else {
      return { ok: true, status: "system_error", message: response.statusText };
    }
  } else {
    return { ok: false, status: "user_error" };
  }
};

export const findAccount = async (accountId: number) => {
  return fetchData<AccountType>(`${BACKEND_API}/api/crm/accounts/${accountId}`);
};

export const findPreviousAccount = async (accountId: number) => {
  return fetchData<AccountType>(
    `${BACKEND_API}/api/crm/accounts/previous/${accountId}`,
  );
};

export const findNextAccount = async (accountId: number) => {
  return fetchData<AccountType>(
    `${BACKEND_API}/api/crm/accounts/next/${accountId}`,
  );
};
