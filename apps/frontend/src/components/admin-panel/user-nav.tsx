"use client";

import { BookText, CircleUserRound, Info, LogOut } from "lucide-react";
import Link from "next/link";
import { signOut, useSession } from "next-auth/react";
import { useEffect, useState } from "react";

import AppLogo from "@/components/app-logo";
import { UserAvatar } from "@/components/shared/avatar-display";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { getVersion } from "@/lib/actions/shared.action";
import { BASE_URL } from "@/lib/constants";

export function UserNav() {
  const { data: session } = useSession();
  const t = useAppClientTranslations();

  const [versionInfo, setVersionInfo] = useState<{ version: string } | null>(
    null,
  );

  useEffect(() => {
    getVersion().then((data) => {
      setVersionInfo(data);
    });
  }, []);

  return (
    <Dialog>
      <DropdownMenu>
        <TooltipProvider disableHoverableContent>
          <Tooltip delayDuration={100}>
            <TooltipTrigger asChild>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="outline"
                  className="relative h-8 w-8 rounded-full"
                >
                  <UserAvatar
                    imageUrl={session?.user?.imageUrl}
                    size="h-10 w-10"
                  />
                </Button>
              </DropdownMenuTrigger>
            </TooltipTrigger>
            <TooltipContent side="bottom">
              {session?.user?.firstName} {session?.user?.lastName}
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>

        <DropdownMenuContent className="w-56" align="end" forceMount>
          <DropdownMenuLabel className="font-normal">
            <div className="flex flex-col space-y-1">
              <p className="text-sm font-medium leading-none">
                {session?.user?.firstName} {session?.user?.lastName}
              </p>
              <p className="text-xs leading-none text-muted-foreground">
                {session?.user?.email}
              </p>
            </div>
          </DropdownMenuLabel>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem className="hover:cursor-pointer" asChild>
              <Link href="/portal/profile" className="flex items-center">
                <CircleUserRound className="w-4 h-4 mr-3 text-muted-foreground" />
                {t.header.nav("profile")}
              </Link>
            </DropdownMenuItem>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuGroup>
            <DropdownMenuItem className="hover:cursor-pointer" asChild>
              <Link
                href="https://docs.flowinquiry.io/user_guides/introduction"
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center"
              >
                <BookText className="w-4 h-4 mr-3 text-muted-foreground" />
                {t.header.nav("user_guide")}
              </Link>
            </DropdownMenuItem>
            <DialogTrigger asChild>
              <DropdownMenuItem className="hover:cursor-pointer">
                <Info className="w-4 h-4 mr-3 text-muted-foreground" />
                {t.header.nav("about")}
              </DropdownMenuItem>
            </DialogTrigger>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
          <DropdownMenuItem
            className="hover:cursor-pointer"
            onClick={() => signOut({ redirectTo: BASE_URL, redirect: true })}
          >
            <LogOut className="w-4 h-4 mr-3 text-muted-foreground" />
            {t.header.nav("logout")}
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
      <DialogContent>
        <DialogHeader className="flex items-center space-x-4">
          <div>
            <AppLogo size={100} />
          </div>

          <div>
            <DialogTitle className="text-2xl font-bold">
              FlowInquiry {versionInfo?.version}
            </DialogTitle>
            <DialogDescription className="text-gray-600 dark:text-gray-400">
              {t.header.nav("intro")}
            </DialogDescription>
          </div>
        </DialogHeader>

        {/* Footer */}
        <div className="mt-2 flex justify-between items-center border-t pt-2">
          {/* Copyright and Year */}
          <div className="text-sm text-gray-500 dark:text-gray-400">
            © {new Date().getFullYear()} FlowInquiry.{" "}
            {t.header.nav("copyright")}.
          </div>

          {/* Website Link */}
          <a
            href="https://www.flowinquiry.io"
            target="_blank"
            rel="noopener noreferrer"
            className="text-sm font-medium text-blue-600 hover:underline dark:text-blue-400"
          >
            flowinquiry.io
          </a>
        </div>
      </DialogContent>
    </Dialog>
  );
}
