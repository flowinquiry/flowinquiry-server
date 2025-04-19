"use client";

import Link from "next/link";
import React, { useEffect, useState } from "react";

import { UserAvatar } from "@/components/shared/avatar-display";
import PaginationExt from "@/components/shared/pagination-ext";
import { Button } from "@/components/ui/button";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { getActivityLogs } from "@/lib/actions/activity-logs.action";
import { formatDateTimeDistanceToNow } from "@/lib/datetime";
import { obfuscate } from "@/lib/endecode";
import { cn } from "@/lib/utils";
import { useError } from "@/providers/error-provider";
import { ActivityLogDTO } from "@/types/activity-logs";
import { EntityType } from "@/types/commons";

type AuditLogViewProps = {
  entityType: EntityType;
  entityId: number;
};

const AuditLogView: React.FC<AuditLogViewProps> = ({
  entityType,
  entityId,
}) => {
  const [activityLogs, setActivityLogs] = useState<ActivityLogDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);
  const { setError } = useError();
  const t = useAppClientTranslations();

  useEffect(() => {
    const fetchAuditLogs = async () => {
      setLoading(true);
      getActivityLogs("Team_Request", entityId, currentPage, 10, setError)
        .then((data) => {
          setTotalPages(data.totalPages);
          setActivityLogs(data.content);
        })
        .finally(() => setLoading(false));
    };
    fetchAuditLogs();
  }, [entityType, entityId, currentPage]);

  if (loading) {
    return <div>{t.common.misc("loading_data")}</div>;
  }

  if (activityLogs.length === 0) {
    return <div>No history available.</div>;
  }

  const decodeHTML = (html: string) => {
    const txt = document.createElement("textarea");
    txt.innerHTML = html;
    return txt.value;
  };

  return (
    <div>
      {activityLogs.map((activityLog, index) => (
        <div
          key={index}
          className={cn(
            "relative border-b pl-2 py-2 gap-2 transition-colors",
            "odd:bg-[hsl(var(--card))] odd:text-[hsl(var(--card-foreground))]",
            "even:bg-[hsl(var(--secondary))] even:text-[hsl(var(--secondary-foreground))]",
          )}
        >
          <div className="flex items-center gap-2">
            <UserAvatar
              imageUrl={activityLog.createdByImageUrl}
              size="w-6 h-6"
            />
            <Button variant="link" className="px-0">
              <Link
                href={`/portal/users/${obfuscate(activityLog.createdById)}`}
              >
                {activityLog.createdByName}
              </Link>
            </Button>
            <span>made some changes</span>
          </div>
          <div
            className="prose max-w-none table-consistent-width"
            dangerouslySetInnerHTML={{
              __html: decodeHTML(activityLog.content!),
            }}
          />
          <small>
            Updated:{" "}
            <Tooltip>
              <TooltipTrigger asChild>
                <span className="cursor-pointer">
                  {formatDateTimeDistanceToNow(new Date(activityLog.createdAt))}
                </span>
              </TooltipTrigger>
              <TooltipContent>
                {new Date(activityLog.createdAt).toLocaleString()}{" "}
              </TooltipContent>
            </Tooltip>
          </small>
        </div>
      ))}
      <PaginationExt
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={(page) => setCurrentPage(page)}
        className="pt-2"
      />
    </div>
  );
};

export default AuditLogView;
