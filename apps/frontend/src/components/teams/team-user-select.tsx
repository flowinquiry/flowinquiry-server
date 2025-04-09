"use client";

import { Check, ChevronsUpDown } from "lucide-react";
import React, { useEffect, useState } from "react";

import { UserAvatar } from "@/components/shared/avatar-display";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { findMembersByTeamId } from "@/lib/actions/teams.action";
import { cn } from "@/lib/utils";
import { useError } from "@/providers/error-provider";
import { UserWithTeamRoleDTO } from "@/types/teams";

type TeamUserSelectProps = {
  teamId: number;
  currentUserId?: number | null;
  onUserChange: (user: UserWithTeamRoleDTO | null) => void;
  disabled?: boolean;
};

/**
 * A component for selecting team members
 *
 * This component loads team members and provides a select dropdown
 * with user avatars and names.
 */
const TeamUserSelect: React.FC<TeamUserSelectProps> = ({
  teamId,
  currentUserId,
  onUserChange,
  disabled = false,
}) => {
  const t = useAppClientTranslations();
  const [users, setUsers] = useState<UserWithTeamRoleDTO[]>([]);
  const [open, setOpen] = useState(false);
  const { setError } = useError();

  useEffect(() => {
    async function fetchUsers() {
      try {
        const data = await findMembersByTeamId(teamId, setError);
        setUsers(data);
      } catch (error) {
        console.error("Failed to load team members:", error);
      }
    }

    if (teamId) {
      fetchUsers();
    }
  }, [teamId, setError]);

  const selectedUser = users.find((user) => user.id === currentUserId);

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button
          variant="outline"
          role="combobox"
          className={cn(
            "w-full justify-between",
            !currentUserId && "text-muted-foreground",
          )}
          disabled={disabled}
        >
          {selectedUser ? (
            <div className="flex items-center gap-2">
              <UserAvatar imageUrl={selectedUser.imageUrl} />
              <span>{`${selectedUser.firstName} ${selectedUser.lastName}`}</span>
            </div>
          ) : (
            t.teams.tickets.form.base("user_select_unassigned")
          )}
          <ChevronsUpDown className="opacity-50 h-4 w-4" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-[18rem] p-0">
        <Command>
          <CommandInput
            placeholder={t.teams.tickets.form.base("user_select_place_holder")}
            className="h-9"
          />
          <CommandList>
            <CommandEmpty>
              {t.teams.tickets.form.base("user_select_no_data")}
            </CommandEmpty>
            <CommandGroup>
              {/* Option to unassign a user */}
              <CommandItem
                value="none"
                onSelect={() => {
                  onUserChange(null);
                  setOpen(false);
                }}
                className="gap-2 text-gray-500"
              >
                {t.teams.tickets.form.base("user_select_no_choose")}
                <Check
                  className={cn(
                    "ml-auto",
                    !currentUserId ? "opacity-100" : "opacity-0",
                  )}
                />
              </CommandItem>

              {/* Render user options */}
              {users.map((user) => (
                <CommandItem
                  value={user.firstName!}
                  key={user.id}
                  onSelect={() => {
                    onUserChange(user);
                    setOpen(false);
                  }}
                  className="gap-2"
                >
                  <UserAvatar imageUrl={user.imageUrl} />
                  {user.firstName} {user.lastName} ({user.teamRole})
                  <Check
                    className={cn(
                      "ml-auto",
                      user.id === currentUserId ? "opacity-100" : "opacity-0",
                    )}
                  />
                </CommandItem>
              ))}
            </CommandGroup>
          </CommandList>
        </Command>
      </PopoverContent>
    </Popover>
  );
};

export default TeamUserSelect;
