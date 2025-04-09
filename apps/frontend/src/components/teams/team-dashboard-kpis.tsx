"use client";

import { useRouter } from "next/navigation";
import React from "react";
import useSWR from "swr";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  getCountOverdueTicketsByTeamId,
  getTicketStatisticsByTeamId,
} from "@/lib/actions/teams-request.action";
import { useError } from "@/providers/error-provider";
import { useTimeRange } from "@/providers/time-range-provider";

const TeamDashboardTopSection = ({ teamId }: { teamId: number }) => {
  const router = useRouter();
  const { setError } = useError();
  const { timeRange, customDates } = useTimeRange();
  const t = useAppClientTranslations();

  const dateParams =
    timeRange === "custom"
      ? { from: customDates?.from, to: customDates?.to }
      : { range: timeRange };

  // Fetch ticket statistics using time range
  const { data: ticketStats } = useSWR(
    ["fetchTicketStatisticsByTeamId", teamId, dateParams],
    async () => getTicketStatisticsByTeamId(teamId, dateParams, setError),
  );

  // Fetch overdue ticket count using time range
  const { data: overdueTickets } = useSWR(
    ["fetchOverdueTicketsCountByTeamId", teamId, dateParams],
    async () => getCountOverdueTicketsByTeamId(teamId, dateParams, setError),
  );

  // Metrics configuration
  const metrics = [
    {
      title: t.teams.dashboard("summary.total.title"),
      description: t.teams.dashboard("summary.total.description"),
      value: ticketStats?.totalTickets ?? 0,
      color: "text-gray-700 dark:text-gray-300",
      link: "#",
      tooltip: t.teams.dashboard("summary.total.tooltip"),
    },
    {
      title: t.teams.dashboard("summary.pending.title"),
      description: t.teams.dashboard("summary.pending.description"),
      value: ticketStats?.pendingTickets ?? 0,
      color: "text-yellow-500",
      link: "#",
      tooltip: t.teams.dashboard("summary.pending.tooltip"),
    },
    {
      title: t.teams.dashboard("summary.completed.title"),
      description: t.teams.dashboard("summary.completed.description"),
      value: ticketStats?.completedTickets ?? 0,
      color: "text-green-500",
      link: "#",
      tooltip: t.teams.dashboard("summary.completed.tooltip"),
    },
    {
      title: t.teams.dashboard("summary.overdue.title"),
      description: t.teams.dashboard("summary.overdue.description"),
      value: overdueTickets ?? 0,
      color: "text-red-500",
      link: "#",
      tooltip: t.teams.dashboard("summary.overdue.tooltip"),
    },
  ];

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      {metrics.map((metric, index) => (
        <Tooltip key={index}>
          <TooltipTrigger asChild>
            <Card
              onClick={() => router.push(metric.link)}
              className="cursor-pointer hover:shadow-lg transition-shadow"
            >
              <CardHeader>
                <CardTitle>{metric.title}</CardTitle>
                <CardDescription>{metric.description}</CardDescription>
              </CardHeader>
              <CardContent className={`text-3xl font-bold ${metric.color}`}>
                {metric.value !== undefined ? (
                  metric.value
                ) : (
                  <Skeleton className="h-8 w-16" />
                )}
              </CardContent>
            </Card>
          </TooltipTrigger>
          <TooltipContent>{metric.tooltip}</TooltipContent>
        </Tooltip>
      ))}
    </div>
  );
};

export default TeamDashboardTopSection;
