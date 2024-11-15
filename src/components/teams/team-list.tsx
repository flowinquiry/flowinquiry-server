"use client";

import { Ellipsis, Pencil, Plus, Trash } from "lucide-react";
import Link from "next/link";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import React, { useEffect, useState } from "react";

import { Heading } from "@/components/heading";
import { EntitiesDeleteDialog } from "@/components/shared/entity-delete-dialog";
import PaginationExt from "@/components/shared/pagination-ext";
import DefaultTeamLogo from "@/components/teams/team-logo";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button, buttonVariants } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useDebouncedCallback } from "@/hooks/use-debounced-callback";
import { usePagePermission } from "@/hooks/use-page-permission";
import { deleteTeams, searchTeams } from "@/lib/actions/teams.action";
import { obfuscate } from "@/lib/endecode";
import { cn } from "@/lib/utils";
import { PermissionUtils } from "@/types/resources";
import { TeamType } from "@/types/teams";

export const TeamList = () => {
  const router = useRouter();
  const [items, setItems] = useState<Array<TeamType>>([]); // Store the items
  const [teamSearchTerm, setTeamSearchTerm] = useState<string | undefined>(
    undefined,
  );
  const [currentPage, setCurrentPage] = useState(1); // Track current page
  const [totalPages, setTotalPages] = useState(0); // Total pages
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(false); // Loading state

  const [isDialogOpen, setDialogOpen] = useState(false);
  const [selectedTeam, setSelectedTeam] = useState<TeamType | null>(null);

  const searchParams = useSearchParams();
  const { replace } = useRouter();
  const pathname = usePathname();

  const permissionLevel = usePagePermission();

  const fetchData = async () => {
    setLoading(true);
    try {
      const pageResult = await searchTeams(
        teamSearchTerm
          ? [{ field: "name", operator: "lk", value: teamSearchTerm }]
          : [],
        { page: currentPage, size: 10 },
      );
      setItems(pageResult.content);
      setTotalElements(pageResult.totalElements);
      setTotalPages(pageResult.totalPages);
    } finally {
      setLoading(false);
    }
  };

  const handleSearchTeams = useDebouncedCallback((teamName: string) => {
    const params = new URLSearchParams(searchParams);
    if (teamName) {
      params.set("name", teamName);
    } else {
      params.delete("name");
    }
    setTeamSearchTerm(teamName);
    replace(`${pathname}?${params.toString()}`);
  }, 2000);

  useEffect(() => {
    fetchData();
  }, [teamSearchTerm, currentPage]);

  if (loading) return <div>Loading...</div>;

  const showDeleteTeamConfirmationDialog = (team: TeamType) => {
    setSelectedTeam(team);
    setDialogOpen(true);
  };

  const deleteTeam = async (ids: number[]) => {
    await deleteTeams(ids);
    fetchData();
  };

  return (
    <div className="grid grid-cols-1 gap-4">
      <div className="flex flex-row justify-between">
        <div className="flex-shrink-0">
          <Heading
            title={`Teams (${totalElements})`}
            description="Manage teams"
          />
        </div>
        <div className="flex space-x-4">
          <Input
            className="w-[18rem]"
            placeholder="Search teams ..."
            onChange={(e) => {
              handleSearchTeams(e.target.value);
            }}
            defaultValue={searchParams.get("name")?.toString()}
          />
          {PermissionUtils.canWrite(permissionLevel) && (
            <Link
              href={"/portal/teams/new/edit"}
              className={cn(buttonVariants({ variant: "default" }))}
            >
              <Plus className="mr-2 h-4 w-4" /> New Team
            </Link>
          )}
        </div>
      </div>
      <Separator />
      <div className="flex flex-row flex-wrap gap-4">
        {items?.map((team) => (
          <div
            key={team.id}
            className="relative w-[24rem] flex flex-row gap-4 border border-gray-200 rounded-2xl"
          >
            <div className="px-4 py-4">
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Avatar className="size-24 cursor-pointer">
                      <AvatarImage
                        src={
                          team.logoUrl
                            ? `/api/files/${team.logoUrl}`
                            : undefined
                        }
                        alt="@flexwork"
                      />
                      <AvatarFallback>
                        <DefaultTeamLogo />
                      </AvatarFallback>
                    </Avatar>
                  </TooltipTrigger>
                  <TooltipContent>{team.slogan}</TooltipContent>
                </Tooltip>
              </TooltipProvider>
            </div>
            <div>
              <Button variant="link" asChild className="px-0">
                <Link href={`/portal/teams/${obfuscate(team.id)}`}>
                  {team.name} ({team.memberCount})
                </Link>
              </Button>
              <div>{team.description}</div>
            </div>
            {PermissionUtils.canWrite(permissionLevel) && (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Ellipsis className="cursor-pointer absolute top-2 right-2 text-gray-400" />
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-[14rem]">
                  <DropdownMenuItem
                    className="cursor-pointer"
                    onClick={() =>
                      router.push(`/portal/teams/${obfuscate(team.id)}/edit`)
                    }
                  >
                    <Pencil />
                    Edit
                  </DropdownMenuItem>
                  {PermissionUtils.canAccess(permissionLevel) && (
                    <DropdownMenuItem
                      className="cursor-pointer"
                      onClick={() => showDeleteTeamConfirmationDialog(team)}
                    >
                      <Trash /> Delete
                    </DropdownMenuItem>
                  )}
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>
        ))}
      </div>
      <PaginationExt
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={(page) => {
          setCurrentPage(page);
        }}
      />
      {isDialogOpen && selectedTeam && (
        <EntitiesDeleteDialog
          entities={[selectedTeam]}
          entityName="Team"
          deleteEntitiesFn={deleteTeam}
          isOpen={isDialogOpen}
          onOpenChange={setDialogOpen}
          onSuccess={() => {
            setDialogOpen(false);
          }}
          onClose={() => setDialogOpen(false)}
        />
      )}
    </div>
  );
};
