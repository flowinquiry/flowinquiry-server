"use client";

import { redirect } from "next/navigation";
import { getSession } from "next-auth/react";

export const Home = async () => {
  const session = await getSession();
  if (!session) {
    redirect("/login");
  } else {
    redirect("/portal");
  }
};
