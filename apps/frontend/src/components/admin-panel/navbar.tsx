import { SheetMenu } from "@/components/admin-panel/sheet-menu";
import { UserNav } from "@/components/admin-panel/user-nav";
import NotificationsDropdown from "@/components/dashboard/notifications-dropdown";
import { VersionUpdateBanner } from "@/components/dashboard/system-notification";
import { UserQuickAction } from "@/components/dashboard/user-quick-actions";
import LocaleSwitcher from "@/components/shared/locale-switcher";

interface NavbarProps {
  title: string;
}

export function Navbar({ title }: NavbarProps) {
  return (
    <header className="sticky top-0 z-10 w-full bg-background/95 shadow backdrop-blur supports-[backdrop-filter]:bg-background/60 dark:shadow-secondary">
      <div className="mx-4 sm:mx-8 flex h-16 items-center">
        <div className="flex items-center space-x-4 lg:space-x-0">
          <SheetMenu />
          <h1 className="font-bold">{title}</h1>
        </div>
        <div className="flex flex-1 items-center gap-2 justify-end">
          <VersionUpdateBanner />
          <UserQuickAction />
          <LocaleSwitcher />
          <NotificationsDropdown />
          <UserNav />
        </div>
      </div>
    </header>
  );
}
