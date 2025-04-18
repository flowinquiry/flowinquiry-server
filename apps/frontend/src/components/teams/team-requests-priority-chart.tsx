"use client";

import { ChevronDown, ChevronRight } from "lucide-react";
import React, { useState } from "react";
import {
  Cell,
  Legend,
  Pie,
  PieChart,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import useSWR from "swr";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Spinner } from "@/components/ui/spinner";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { getTicketsPriorityDistributionByTeam } from "@/lib/actions/teams-request.action";
import { useError } from "@/providers/error-provider";
import { useTimeRange } from "@/providers/time-range-provider";
import { TeamRequestPriority } from "@/types/team-requests";

const TicketPriorityPieChart = ({ teamId }: { teamId: number }) => {
  const [collapsed, setCollapsed] = useState(false);
  const { setError } = useError();
  const { timeRange, customDates } = useTimeRange();
  const t = useAppClientTranslations();

  // Generate date parameters
  const dateParams =
    timeRange === "custom"
      ? { from: customDates?.from, to: customDates?.to }
      : { range: timeRange };

  const { data: priorityData = [], isValidating } = useSWR(
    ["fetchTicketsPriorityDistributionByTeam", teamId, dateParams],
    () => getTicketsPriorityDistributionByTeam(teamId, dateParams, setError),
  );

  // Define colors for the pie chart
  const COLORS: Record<TeamRequestPriority, string> = {
    Critical: "#DC2626", // text-red-600
    High: "#F97316", // text-orange-500
    Medium: "#F59E0B", // text-yellow-500
    Low: "#16A34A", // text-green-500
    Trivial: "#9CA3AF", // text-gray-400
  };

  return (
    <Card className="w-full mx-auto">
      {/* Header with Chevron Icon and Title */}
      <CardHeader>
        <div className="flex items-center gap-2">
          <button
            onClick={() => setCollapsed((prev) => !prev)}
            className="flex items-center p-0"
          >
            {collapsed ? (
              <ChevronRight className="w-5 h-5" />
            ) : (
              <ChevronDown className="w-5 h-5" />
            )}
          </button>
          <CardTitle>
            {t.teams.dashboard("priority_tickets_distribution.title")}
          </CardTitle>
        </div>
      </CardHeader>

      {/* Collapsible Content */}
      {!collapsed && (
        <CardContent className="p-4">
          {isValidating ? (
            <div className="flex flex-col items-center justify-center h-64">
              <Spinner className="h-8 w-8 mb-4" />
              <span>{t.common.misc("loading_data")}</span>
            </div>
          ) : priorityData.length === 0 ? (
            <p className="text-center">{t.common.misc("no_data_available")}</p>
          ) : (
            <div className="w-full h-64 md:h-96">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={priorityData}
                    dataKey="ticketCount"
                    nameKey="priority"
                    cx="50%"
                    cy="50%"
                    outerRadius="80%"
                    fill="#8884d8"
                    label
                  >
                    {priorityData.map((entry) => (
                      <Cell
                        key={`cell-${entry.priority}`}
                        fill={
                          COLORS[entry.priority as TeamRequestPriority] ||
                          "#D3D3D3"
                        }
                      />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          )}
        </CardContent>
      )}
    </Card>
  );
};

export default TicketPriorityPieChart;
