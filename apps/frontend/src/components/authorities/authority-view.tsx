"use client";

import { Edit, Ellipsis, Info, Plus, Trash } from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";

import AddUserToAuthorityDialog from "@/components/authorities/authority-add-user-dialog";
import { Breadcrumbs } from "@/components/breadcrumbs";
import { Heading } from "@/components/heading";
import { UserAvatar } from "@/components/shared/avatar-display";
import PaginationExt from "@/components/shared/pagination-ext";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Spinner } from "@/components/ui/spinner";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { usePagePermission } from "@/hooks/use-page-permission";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  deleteUserFromAuthority,
  findAuthorityByName,
  findPermissionsByAuthorityName,
  getUsersByAuthority,
} from "@/lib/actions/authorities.action";
import { obfuscate } from "@/lib/endecode";
import { useError } from "@/providers/error-provider";
import {
  AuthorityDTO,
  AuthorityResourcePermissionDTO,
} from "@/types/authorities";
import { PermissionUtils } from "@/types/resources";
import { UserDTO } from "@/types/users";

export const AuthorityView = ({ authorityId }: { authorityId: string }) => {
  const permissionLevel = usePagePermission();
  const [open, setOpen] = useState(false);
  const [users, setUsers] = useState<Array<UserDTO>>();
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [authority, setAuthority] = useState<AuthorityDTO | undefined>(
    undefined,
  );
  const [resourcePermissions, setResourcePermissions] =
    useState<Array<AuthorityResourcePermissionDTO>>();
  const [loadingAuthority, setLoadingAuthority] = useState(false);
  const [loadingUsers, setLoadingUsers] = useState(false);
  const { setError } = useError();
  const t = useAppClientTranslations();

  const router = useRouter();

  async function fetchUsers() {
    setLoadingUsers(true);
    try {
      const pageableResult = await getUsersByAuthority(authority!.name, {
        page: currentPage,
        size: 10,
      });
      setUsers(pageableResult.content);
      setTotalElements(pageableResult.totalElements);
      setTotalPages(pageableResult.totalPages);
    } finally {
      setLoadingUsers(false);
    }
  }

  useEffect(() => {
    const fetchData = async () => {
      setLoadingAuthority(true);
      try {
        const authorityData = await findAuthorityByName(authorityId, setError);
        setAuthority(authorityData);

        const resourcePermissionsResult = await findPermissionsByAuthorityName(
          authorityData.name,
          setError,
        );
        setResourcePermissions(resourcePermissionsResult);
      } finally {
        setLoadingAuthority(false);
      }
    };
    fetchData();
  }, [authorityId]);

  useEffect(() => {
    if (authority) {
      fetchUsers();
    }
  }, [currentPage, authority]);

  async function removeUserOutAuthority(user: UserDTO) {
    await deleteUserFromAuthority(authority!.name, user.id!);
    await fetchUsers();
  }

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    {
      title: t.common.navigation("authorities"),
      link: "/portal/settings/authorities",
    },
    { title: `${authority?.descriptiveName ?? ""}`, link: "#" },
  ];

  return (
    <div className="grid grid-cols-1 gap-4 py-4">
      <Breadcrumbs items={breadcrumbItems} />
      <div className="flex flex-row justify-between">
        {loadingAuthority ? (
          <Spinner>Loading data...</Spinner>
        ) : (
          <Heading
            title={`${authority?.descriptiveName ?? ""} (${totalElements})`}
            description={authority?.description ?? ""}
          />
        )}
        {PermissionUtils.canWrite(permissionLevel) && authority && (
          <div className="flex space-x-4">
            <Button onClick={() => setOpen(true)}>
              <Plus /> Add User
            </Button>
            <AddUserToAuthorityDialog
              open={open}
              setOpen={setOpen}
              authorityEntity={authority}
              onSaveSuccess={() => fetchUsers()}
            />
            <Button
              onClick={() =>
                router.push(
                  `/portal/settings/authorities/${obfuscate(authority.name)}/edit`,
                )
              }
            >
              <Edit /> {t.common.buttons("edit")}
            </Button>
          </div>
        )}
      </div>
      <div className="flex flex-col md:flex-row md:space-x-4 items-start">
        <div className="md:flex-1 flex flex-row flex-wrap w-full">
          <div className="md:flex-1 flex flex-row flex-wrap gap-4 w-full">
            {loadingUsers ? (
              <Spinner size="large" />
            ) : users && users.length > 0 ? (
              users.map((user: UserDTO) => (
                <div
                  className="w-full md:w-[24rem] flex flex-row gap-4 border border-gray-200 px-4 py-4 rounded-2xl relative"
                  key={user.id}
                >
                  <div className="relative w-24 h-24">
                    <UserAvatar imageUrl={user.imageUrl} size="w-24 h-24" />
                    {user.status !== "ACTIVE" && (
                      <div className="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center">
                        <span className="text-white text-xs font-bold">
                          Not Activated
                        </span>
                      </div>
                    )}
                  </div>
                  <div>
                    <div className="text-xl">
                      <Button variant="link" asChild className="px-0">
                        <Link href={`/portal/users/${obfuscate(user.id)}`}>
                          {user.firstName}, {user.lastName}
                        </Link>
                      </Button>
                    </div>
                    <div>
                      Email:{" "}
                      <Link href={`mailto:${user.email}`}>{user.email}</Link>
                    </div>
                    <div>Title: {user.title}</div>
                  </div>
                  {PermissionUtils.canWrite(permissionLevel) && (
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Ellipsis className="cursor-pointer absolute top-2 right-2 text-gray-400" />
                      </DropdownMenuTrigger>
                      <DropdownMenuContent className="w-[14rem]">
                        <TooltipProvider>
                          <Tooltip>
                            <TooltipTrigger>
                              <DropdownMenuItem
                                className="cursor-pointer"
                                onClick={() => removeUserOutAuthority(user)}
                              >
                                <Trash /> Remove user
                              </DropdownMenuItem>
                            </TooltipTrigger>
                            <TooltipContent>
                              <p>
                                This action will revoke the selected user’s
                                access and permissions associated with this
                                authority
                              </p>
                            </TooltipContent>
                          </Tooltip>
                        </TooltipProvider>
                      </DropdownMenuContent>
                    </DropdownMenu>
                  )}
                </div>
              ))
            ) : (
              <div className="w-full text-left py-8 flex items-start gap-2">
                <Info className="h-6 w-6" />
                <p className="text-lg">
                  No users have been assigned to this authority yet. Please add
                  users to this authority to manage permissions.
                </p>
              </div>
            )}
            {users && users.length > 0 && (
              <PaginationExt
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={(page) => setCurrentPage(page)}
              />
            )}
          </div>
        </div>
        <Card className="w-full md:w-[28rem] mt-4 md:mt-0">
          <CardHeader>Resource Permissions</CardHeader>
          <CardContent>
            <div>
              {loadingAuthority ? (
                <Spinner size="large" />
              ) : resourcePermissions ? (
                resourcePermissions.map((perm, index) => (
                  <div key={index} className="p-4 rounded shadow">
                    <p>
                      <strong>Resource:</strong> {perm.resourceName}
                    </p>
                    <p>
                      <strong>Permission:</strong> {perm.permission}
                    </p>
                  </div>
                ))
              ) : (
                <p>No permissions available</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
