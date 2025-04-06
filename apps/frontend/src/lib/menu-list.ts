import {
  Layers,
  LayoutGrid,
  LucideIcon,
  ShieldCheck,
  Shuffle,
  Users,
} from "lucide-react";
import { createTranslator, Messages } from "next-intl";

import { Permission } from "@/providers/permissions-provider";

type Submenu = {
  href: string;
  label: string;
  resource: string;
  active?: boolean;
};

type Menu = {
  href: string;
  label: string;
  resource: string;
  active?: boolean;
  icon: LucideIcon;
  submenus?: Submenu[];
};

type Group = {
  groupLabel: string;
  menus: Menu[];
};

type Translator = ReturnType<
  typeof createTranslator<Messages, "common.navigation">
>;

export function getMenuList(
  pathname: string,
  permissions: Permission[],
  comT: Translator,
): Group[] {
  // Helper function to check if a menu or submenu is enabled
  const isMenuEnabled = (menuResource: string): boolean => {
    if (menuResource === "Dashboard") {
      return true; // Always enable "Dashboard"
    }
    const permission = permissions.find(
      (perm) => perm.resourceName === menuResource,
    );
    return permission ? permission.permission !== "NONE" : false;
  };

  // Helper function to filter menus recursively
  const filterMenus = (menus: Menu[]): Menu[] => {
    return menus
      .map((menu) => {
        // Recursively filter submenus
        const filteredSubmenus = menu.submenus
          ? menu.submenus.filter((submenu) => isMenuEnabled(submenu.resource))
          : [];

        // Check if the menu itself or its submenus are enabled
        if (isMenuEnabled(menu.resource) || filteredSubmenus.length > 0) {
          return {
            ...menu,
            submenus: filteredSubmenus,
          };
        }

        return null; // Exclude the menu if it's not enabled
      })
      .filter(Boolean) as Menu[]; // Remove null values
  };

  // Define the full menu structure
  const groups: Group[] = [
    {
      groupLabel: "",
      menus: [
        {
          href: "/portal",
          label: comT("dashboard"),
          resource: "Dashboard",
          icon: LayoutGrid,
          submenus: [],
        },
      ],
    },
    {
      groupLabel: "",
      menus: [
        {
          href: "/portal/teams",
          label: comT("teams"),
          resource: "Teams",
          icon: Layers,
        },
      ],
    },
    {
      groupLabel: comT("settings"),
      menus: [
        {
          href: "/portal/users",
          label: comT("users"),
          resource: "Users",
          icon: Users,
        },
        {
          href: "/portal/settings/authorities",
          label: comT("authorities"),
          resource: "Authorities",
          icon: ShieldCheck,
        },
        {
          href: "/portal/settings/workflows",
          label: comT("workflows"),
          resource: "Workflows",
          icon: Shuffle,
        },
      ],
    },
  ];

  // Filter the groups and their menus
  return groups
    .map((group) => {
      const filteredMenus = filterMenus(group.menus);
      if (filteredMenus.length > 0) {
        return {
          ...group,
          menus: filteredMenus,
        };
      }
      return null; // Exclude the group if it has no enabled menus
    })
    .filter(Boolean) as Group[]; // Remove null groups
}
