import { useTranslations } from "next-intl";

import { ContentLayout } from "@/components/admin-panel/content-layout";
import RecentUserTeamActivities from "@/components/dashboard/global-dashboard-recent-activities";
import UserNotifications from "@/components/dashboard/notifications-user";
import TeamUnresolvedTicketsPriorityDistributionChart from "@/components/dashboard/team-unresolved-tickets-priority-distribution";
import UserTeamsOverdueTickets from "@/components/dashboard/user-requests-overdue";

const Page = () => {
  const navT = useTranslations("common.navigation");

  return (
    <ContentLayout title="Dashboard">
      <h1 className="text-2xl mb-4">{navT("dashboard")}</h1>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 pb-4">
        <div className="flex flex-col">
          <RecentUserTeamActivities />
        </div>
        <div className="flex flex-col">
          <UserNotifications />
        </div>
      </div>
      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="flex flex-col">
          <TeamUnresolvedTicketsPriorityDistributionChart />
        </div>
        <div className="flex flex-col">
          <UserTeamsOverdueTickets />
        </div>
      </div>
    </ContentLayout>
  );
};

export default Page;
