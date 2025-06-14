"use client";

import { Ellipsis, Plus, Shield, Trash } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";

import { Heading } from "@/components/heading";
import PaginationExt from "@/components/shared/pagination-ext";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Separator } from "@/components/ui/separator";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { usePagePermission } from "@/hooks/use-page-permission";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  deleteAuthority,
  getAuthorities,
} from "@/lib/actions/authorities.action";
import { obfuscate } from "@/lib/endecode";
import { useError } from "@/providers/error-provider";
import { AuthorityDTO } from "@/types/authorities";
import { PermissionUtils } from "@/types/resources";

export function AuthoritiesView() {
  const router = useRouter();
  const t = useAppClientTranslations();
  const permissionLevel = usePagePermission();

  const [authorities, setAuthorities] = useState<Array<AuthorityDTO>>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedAuthority, setSelectedAuthority] =
    useState<AuthorityDTO | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const { setError } = useError();

  const fetchAuthorities = async () => {
    getAuthorities(currentPage, setError).then((pageableResult) => {
      setAuthorities(pageableResult.content);
      setTotalPages(pageableResult.totalPages);
      setTotalElements(pageableResult.totalElements);
    });
  };

  useEffect(() => {
    fetchAuthorities();
  }, [currentPage]);

  function handleDeleteClick(authority: AuthorityDTO) {
    setSelectedAuthority(authority);
    setIsDialogOpen(true);
  }

  async function confirmDeleteAuthority() {
    if (selectedAuthority) {
      await deleteAuthority(selectedAuthority.name, setError);
      setSelectedAuthority(null);
      fetchAuthorities();
    }
    setIsDialogOpen(false);
    setSelectedAuthority(null);
  }

  return (
    <div
      className="grid grid-cols-1 gap-4"
      data-testid="authority-list-container"
    >
      <div className="flex flex-row justify-between">
        <Heading
          title={t.authorities.list("title", { totalElements })}
          description={t.authorities.list("description")}
          data-testid="authority-list-heading"
        />
        {PermissionUtils.canWrite(permissionLevel) && (
          <Button
            onClick={() => router.push("/portal/settings/authorities/new/edit")}
            testId="authority-list-new-button"
          >
            <Plus />
            {t.authorities.list("new_authority")}
          </Button>
        )}
      </div>
      <Separator />
      <div
        className="flex flex-col md:flex-row md:space-x-4 items-start"
        data-testid="authority-list-content"
      >
        <div
          className="md:flex-1 flex flex-row flex-wrap w-full gap-4 pt-2"
          data-testid="authority-list-items"
        >
          {authorities?.map((authority) => (
            <div
              className={`w-full md:w-[24rem] flex flex-row gap-4 px-4 py-4 rounded-2xl relative 
    ${
      authority.systemRole
        ? "bg-gray-100 border-gray-300 dark:bg-gray-800 dark:border-gray-700"
        : "bg-gray-50 border-gray-200 dark:bg-gray-900 dark:border-gray-600"
    }`}
              key={authority.name}
              data-testid={`authority-list-item-${authority.name}`}
            >
              <div
                className="flex flex-col"
                data-testid={`authority-list-item-content-${authority.name}`}
              >
                <div
                  className="flex items-center gap-2"
                  data-testid={`authority-list-item-header-${authority.name}`}
                >
                  <Button
                    variant="link"
                    className="px-0 text-xl"
                    testId={`authority-list-item-link-${authority.name}`}
                  >
                    <Link
                      href={`/portal/settings/authorities/${obfuscate(authority.name)}`}
                    >
                      {authority.descriptiveName} ({authority.usersCount})
                    </Link>
                  </Button>
                  {authority.systemRole && (
                    <Badge
                      variant="outline"
                      className="text-blue-500 border-blue-500 dark:text-blue-300 dark:border-blue-600"
                      data-testid={`authority-list-item-system-badge-${authority.name}`}
                    >
                      <Shield className="w-4 h-4 mr-1" />
                      {t.authorities.common("system_role")}
                    </Badge>
                  )}
                </div>
                <div
                  data-testid={`authority-list-item-description-${authority.name}`}
                >
                  {authority.description}
                </div>
              </div>

              {PermissionUtils.canAccess(permissionLevel) &&
                !authority.systemRole && (
                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Ellipsis
                        className="cursor-pointer absolute top-2 right-2 text-gray-400"
                        data-testid={`authority-list-item-actions-${authority.name}`}
                      />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent className="w-56">
                      <TooltipProvider>
                        <Tooltip>
                          <TooltipTrigger>
                            <DropdownMenuItem
                              className="cursor-pointer"
                              onClick={() => handleDeleteClick(authority)}
                              data-testid={`authority-list-item-delete-${authority.name}`}
                            >
                              <Trash /> {t.authorities.list("remove_authority")}
                            </DropdownMenuItem>
                          </TooltipTrigger>
                          <TooltipContent>
                            <p>
                              {t.authorities.list("remove_authority_tooltip")}
                            </p>
                          </TooltipContent>
                        </Tooltip>
                      </TooltipProvider>
                    </DropdownMenuContent>
                  </DropdownMenu>
                )}
            </div>
          ))}
          <div data-testid="authority-list-pagination">
            <PaginationExt
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={(page) => setCurrentPage(page)}
            />
          </div>
        </div>

        <div data-testid="authority-list-delete-dialog">
          <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
            <DialogContent>
              <DialogHeader>
                <DialogTitle data-testid="authority-list-delete-dialog-title">
                  {t.authorities.list("remove_dialog_title")}
                </DialogTitle>
              </DialogHeader>
              <p data-testid="authority-list-delete-dialog-content">
                {t.authorities.list.rich("remove_dialog_content", {
                  name: selectedAuthority?.descriptiveName ?? "",
                  strong: (chunks) => <strong>{chunks}</strong>,
                })}
              </p>
              <DialogFooter>
                <Button
                  variant="secondary"
                  onClick={() => setIsDialogOpen(false)}
                  testId="authority-list-delete-dialog-cancel"
                >
                  {t.common.buttons("cancel")}
                </Button>
                <Button
                  variant="destructive"
                  onClick={confirmDeleteAuthority}
                  testId="authority-list-delete-dialog-confirm"
                >
                  {t.common.buttons("delete")}
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>
    </div>
  );
}
