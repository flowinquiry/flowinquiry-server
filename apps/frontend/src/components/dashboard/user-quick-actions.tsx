"use client";

import { CheckCircle, ClipboardList, FileText } from "lucide-react";
import { useRouter } from "next/navigation";
import { useTranslations } from "next-intl";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

export function UserQuickAction() {
  const router = useRouter();
  const compT = useTranslations("header.my_tickets");

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          className="flex items-center gap-2 cursor-pointer"
        >
          <ClipboardList className="w-5 h-5" />
          <span>{compT("title")}</span>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent align="end">
        <DropdownMenuItem
          className="cursor-pointer flex items-center gap-2"
          onSelect={() => router.push("/portal/my/tickets?ticketType=reported")}
        >
          <FileText className="w-4 h-4" /> {compT("reported_tickets")}
        </DropdownMenuItem>
        <DropdownMenuItem
          className="cursor-pointer flex items-center gap-2"
          onSelect={() => router.push("/portal/my/tickets?ticketType=assigned")}
        >
          <CheckCircle className="w-4 h-4" /> {compT("assigned_tickets")}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
