"use client";

import { useEffect, useState } from "react";

import {
  Timeline,
  TimelineConnector,
  TimelineContent,
  TimelineDescription,
  TimelineHeader,
  TimelineIcon,
  TimelineItem,
  TimelineTitle,
} from "@/components/ui/timeline";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { getTeamRequestStateChangesHistory } from "@/lib/actions/teams.action";
import { formatDateTime, formatDateTimeDistanceToNow } from "@/lib/datetime";
import { useError } from "@/providers/error-provider";
import { TransitionItemCollectionDTO } from "@/types/teams";

const TicketTimelineHistory = ({ teamId }: { teamId: number }) => {
  const t = useAppClientTranslations();
  const [transitionItemCollection, setTransitionItemCollection] =
    useState<TransitionItemCollectionDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const { setError } = useError();

  useEffect(() => {
    const fetchStatesHistory = async () => {
      setLoading(true);
      getTeamRequestStateChangesHistory(teamId, setError)
        .then((data) => setTransitionItemCollection(data))
        .finally(() => setLoading(false));
    };
    fetchStatesHistory();
  }, [teamId]);

  if (loading) {
    return (
      <div className="text-center py-4">{t.common.misc("loading_data")}</div>
    );
  }

  if (
    !transitionItemCollection ||
    !transitionItemCollection.transitions ||
    transitionItemCollection.transitions.length === 0
  ) {
    return (
      <div className="py-4 text-left">
        {t.teams.tickets.timeline("no_data")}
      </div>
    );
  }

  return (
    <div>
      <Timeline className="py-4">
        {transitionItemCollection.transitions.map((transition, index) => (
          <TimelineItem key={index}>
            <TimelineConnector />
            <TimelineHeader>
              <TimelineIcon />
              <TimelineTitle>
                {transition.eventName}
                <Tooltip>
                  <TooltipTrigger asChild>
                    <span
                      className="text-sm ml-2 cursor-pointer"
                      title={transition.transitionDate}
                    >
                      (
                      {formatDateTimeDistanceToNow(
                        new Date(transition.transitionDate),
                      )}
                      )
                    </span>
                  </TooltipTrigger>
                  <TooltipContent>
                    {formatDateTime(new Date(transition.transitionDate))}
                  </TooltipContent>
                </Tooltip>
              </TimelineTitle>
            </TimelineHeader>
            <TimelineContent>
              <TimelineDescription>
                <strong>{transition.fromState}</strong> →{" "}
                <strong>{transition.toState}</strong>
              </TimelineDescription>

              {/* Show SLA if present */}
              {transition.slaDueDate && (
                <div className="mt-1 text-sm text-gray-600 dark:text-gray-400">
                  {t.teams.tickets.timeline("sla_due")}:{" "}
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <span className="cursor-pointer text-red-500 dark:text-red-400">
                        {formatDateTime(new Date(transition.slaDueDate))}
                      </span>
                    </TooltipTrigger>
                    <TooltipContent>
                      {t.teams.tickets.timeline("sla_deadline")}:{" "}
                      {formatDateTime(new Date(transition.slaDueDate))}
                    </TooltipContent>
                  </Tooltip>
                </div>
              )}

              {/* Show Status Only for Specific Cases */}
              {["Completed", "Overdue", "Escalated"].includes(
                transition.status,
              ) && (
                <div className="mt-1 text-sm font-medium">
                  Status:{" "}
                  <span
                    className={
                      transition.status === "Escalated"
                        ? "text-orange-500 dark:text-orange-400"
                        : transition.status === "Completed"
                          ? "text-green-500 dark:text-green-400"
                          : transition.status === "Overdue"
                            ? "text-red-500 dark:text-red-400"
                            : "text-gray-500 dark:text-gray-400"
                    }
                  >
                    {transition.status}
                  </span>
                </div>
              )}
            </TimelineContent>
          </TimelineItem>
        ))}
      </Timeline>
    </div>
  );
};

export default TicketTimelineHistory;
