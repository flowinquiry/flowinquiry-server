"use client";

import {
  ArrowLeft,
  ArrowRight,
  Calendar,
  Clock,
  Edit,
  Eye,
  FileText,
  Loader2,
  MessageSquarePlus,
  Users,
} from "lucide-react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import React, { useEffect, useRef, useState } from "react";

import AttachmentView from "@/components/shared/attachment-view";
import AuditLogView from "@/components/shared/audit-log-view";
import { UserAvatar } from "@/components/shared/avatar-display";
import CommentsView from "@/components/shared/comments-view";
import EntityWatchers from "@/components/shared/entity-watchers";
import TeamNavLayout from "@/components/teams/team-nav";
import TeamRequestHealthLevel from "@/components/teams/team-requests-health-level";
import { PriorityDisplay } from "@/components/teams/team-requests-priority-display";
import TicketTimelineHistory from "@/components/teams/ticket-timeline-history";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { usePagePermission } from "@/hooks/use-page-permission";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  findNextTeamRequest,
  findPreviousTeamRequest,
  findRequestById,
  updateTeamRequest,
} from "@/lib/actions/teams-request.action";
import { getValidTargetStates } from "@/lib/actions/workflows.action";
import { formatDateTimeDistanceToNow } from "@/lib/datetime";
import { obfuscate } from "@/lib/endecode";
import { getSpecifiedColor, randomPair } from "@/lib/utils";
import { BreadcrumbProvider } from "@/providers/breadcrumb-provider";
import { useError } from "@/providers/error-provider";
import { useUserTeamRole } from "@/providers/user-team-role-provider";
import { PermissionUtils } from "@/types/resources";
import { TeamRequestDTO } from "@/types/team-requests";
import { WorkflowStateDTO } from "@/types/workflows";

import WorkflowReviewDialog from "../workflows/workflow-review-dialog";

const TeamRequestDetailView = ({
  teamRequestId,
}: {
  teamRequestId: number;
}) => {
  const permissionLevel = usePagePermission();
  const teamRole = useUserTeamRole().role;
  const router = useRouter();

  const [selectedTab, setSelectedTab] = useState("comments");
  const [teamRequest, setTeamRequest] = useState<TeamRequestDTO>(
    {} as TeamRequestDTO,
  );
  const [loading, setLoading] = useState(true);
  const { setError } = useError();
  const [workflowStates, setWorkflowStates] = useState<WorkflowStateDTO[]>([]);
  const [currentRequestState, setCurrentRequestState] = useState<String>("");
  const [isWorkflowDialogOpen, setWorkflowDialogOpen] = useState(false);
  const t = useAppClientTranslations();

  const commentsViewRef = useRef<HTMLDivElement | null>(null);

  const canEdit =
    PermissionUtils.canWrite(permissionLevel) ||
    teamRole === "Manager" ||
    teamRole === "Member";
  const canComment = canEdit || teamRole === "Guest";

  useEffect(() => {
    const fetchRequest = async () => {
      setLoading(true);
      try {
        const data = await findRequestById(teamRequestId, setError);
        if (!data) {
          throw new Error("Could not find the specified team request.");
        }
        setTeamRequest(data);
        setCurrentRequestState(data.currentStateName!);
      } finally {
        setLoading(false);
      }
    };
    fetchRequest();
  }, [teamRequestId, setError]);

  useEffect(() => {
    const loadWorkflowStates = async () => {
      if (teamRequest?.workflowId && teamRequest?.currentStateId) {
        const data = await getValidTargetStates(
          teamRequest.workflowId,
          teamRequest.currentStateId,
          true,
          setError,
        );
        setWorkflowStates(data);
      }
    };

    loadWorkflowStates();
  }, [teamRequest?.workflowId, teamRequest?.currentStateId, setError]);

  const handleViewWorkflow = () => {
    setWorkflowDialogOpen(true);
  };

  const handleTabChange = (value: string) => {
    setSelectedTab(value);
  };

  const navigateToPreviousRecord = async () => {
    if (!teamRequest) return;
    const previousTeamRequest = await findPreviousTeamRequest(
      teamRequest.id!,
      teamRequest.projectId,
      setError,
    );
    setTeamRequest(previousTeamRequest);
  };

  const navigateToNextRecord = async () => {
    if (!teamRequest) return;
    const nextTeamRequest = await findNextTeamRequest(
      teamRequest.id!,
      teamRequest.projectId,
      setError,
    );
    setTeamRequest(nextTeamRequest);
  };

  const handleFocusComments = () => {
    setSelectedTab("comments");
    setTimeout(() => {
      if (commentsViewRef.current) {
        commentsViewRef.current.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
      }
    }, 100);
  };

  const handleStateChangeRequest = async (state: WorkflowStateDTO) => {
    const updatedRequest = {
      ...teamRequest,
      currentStateId: state.id,
      currentStateName: state.stateName,
    };
    await updateTeamRequest(updatedRequest.id!, updatedRequest, setError);
    setTeamRequest(updatedRequest);
    setCurrentRequestState(state.stateName);
  };

  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
        <span className="ml-2 text-lg font-medium">
          {t.common.misc("loading_data")}
        </span>
      </div>
    );
  }

  if (!teamRequest) {
    return (
      <Card className="my-4">
        <CardHeader>
          <CardTitle className="text-2xl font-bold text-red-500">
            Ticket Not Found
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="mb-4">The requested ticket could not be found.</p>
          <Button variant="secondary" onClick={() => router.back()}>
            <ArrowLeft className="mr-2 h-4 w-4" /> Go Back
          </Button>
        </CardContent>
      </Card>
    );
  }

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    { title: t.common.navigation("teams"), link: "/portal/teams" },
    {
      title: teamRequest.teamName!,
      link: `/portal/teams/${obfuscate(teamRequest.teamId)}`,
    },
    ...(teamRequest.projectId
      ? [
          {
            title: t.common.navigation("projects"),
            link: `/portal/teams/${obfuscate(teamRequest.teamId)}/projects`,
          },
          {
            title: teamRequest.projectName!,
            link: `/portal/teams/${obfuscate(teamRequest.teamId)}/projects/${obfuscate(teamRequest.projectId)}`,
          },
          {
            title: teamRequest.requestTitle!,
            link: "#",
          },
        ]
      : [
          {
            title: t.common.navigation("tickets"),
            link: `/portal/teams/${obfuscate(teamRequest.teamId)}/requests`,
          },
          { title: teamRequest.requestTitle!, link: "#" },
        ]),
  ];

  const workflowColor = getSpecifiedColor(teamRequest.workflowRequestName!);

  return (
    <BreadcrumbProvider items={breadcrumbItems}>
      <TeamNavLayout teamId={teamRequest.teamId!}>
        <div className="space-y-4">
          {/* Header with Title and Navigation */}
          <div className="flex items-start justify-between gap-4">
            <div className="flex items-center">
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={navigateToPreviousRecord}
                      className="mr-1"
                    >
                      <ArrowLeft className="h-5 w-5" />
                    </Button>
                  </TooltipTrigger>
                  <TooltipContent>Previous ticket</TooltipContent>
                </Tooltip>
              </TooltipProvider>

              <div className="flex flex-col">
                <div className="flex items-center space-x-2">
                  <span
                    className="inline-block rounded-md px-2 py-1 text-xs font-semibold"
                    style={{
                      backgroundColor: workflowColor.background,
                      color: workflowColor.text,
                    }}
                  >
                    {teamRequest.workflowRequestName}
                  </span>

                  <Badge
                    variant={teamRequest.isCompleted ? "secondary" : "outline"}
                    className="font-normal"
                  >
                    {currentRequestState}
                  </Badge>

                  <PriorityDisplay priority={teamRequest.priority} />
                </div>

                <h1
                  className={`mt-1 text-2xl font-semibold ${
                    teamRequest.isCompleted
                      ? "line-through text-muted-foreground"
                      : ""
                  }`}
                >
                  {teamRequest.requestTitle}
                </h1>
              </div>
            </div>

            <div className="flex items-center">
              <TooltipProvider>
                <Tooltip>
                  <TooltipTrigger asChild>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={navigateToNextRecord}
                      className="ml-1"
                    >
                      <ArrowRight className="h-5 w-5" />
                    </Button>
                  </TooltipTrigger>
                  <TooltipContent>Next ticket</TooltipContent>
                </Tooltip>
              </TooltipProvider>
            </div>
          </div>

          <div className="flex flex-wrap items-center justify-end gap-3">
            {canEdit && (
              <Button
                variant="outline"
                onClick={() =>
                  router.push(
                    `/portal/teams/${obfuscate(teamRequest.teamId)}/requests/${obfuscate(teamRequest.id)}/edit?${randomPair()}`,
                  )
                }
              >
                <Edit className="mr-2 h-4 w-4" /> Edit Ticket
              </Button>
            )}

            {canComment && (
              <Button variant="outline" onClick={handleFocusComments}>
                <MessageSquarePlus className="mr-2 h-4 w-4" /> Add Comment
              </Button>
            )}

            {canEdit && (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button className="flex items-center gap-2">
                    Change Status <ArrowRight className="ml-1 h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56">
                  <DropdownMenuGroup>
                    {workflowStates
                      .filter(
                        (state) => state.id !== teamRequest.currentStateId,
                      )
                      .map((state) => (
                        <DropdownMenuItem
                          key={state.id}
                          onClick={() => handleStateChangeRequest(state)}
                          className="cursor-pointer"
                        >
                          {state.stateName}
                        </DropdownMenuItem>
                      ))}

                    {workflowStates.filter(
                      (state) => state.id !== teamRequest.currentStateId,
                    ).length === 0 && (
                      <DropdownMenuItem disabled>
                        No available states
                      </DropdownMenuItem>
                    )}

                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      onClick={handleViewWorkflow}
                      className="cursor-pointer"
                    >
                      <Eye className="mr-2 h-4 w-4" /> View Workflow
                    </DropdownMenuItem>
                  </DropdownMenuGroup>
                </DropdownMenuContent>
              </DropdownMenu>
            )}

            {!canEdit && canComment && (
              <Button variant="outline" onClick={handleViewWorkflow}>
                <Eye className="mr-2 h-4 w-4" /> View Workflow
              </Button>
            )}
          </div>

          {/* Health Level Indicator */}
          {teamRequest.conversationHealth?.healthLevel && (
            <TeamRequestHealthLevel
              currentLevel={teamRequest.conversationHealth.healthLevel}
            />
          )}

          <Card className="overflow-hidden">
            <CardContent className="p-0">
              <div className="p-6 space-y-6">
                <div>
                  <h3 className="text-lg font-medium mb-2 flex items-center">
                    <FileText className="mr-2 h-5 w-5" /> Description
                  </h3>
                  <div
                    className="prose max-w-none text-muted-foreground"
                    dangerouslySetInnerHTML={{
                      __html: teamRequest.requestDescription!,
                    }}
                  />
                </div>

                <Separator />

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div className="space-y-4">
                    <h3 className="text-sm font-medium mb-2 flex items-center">
                      <Users className="mr-2 h-4 w-4" /> People
                    </h3>

                    <div className="space-y-3">
                      <div>
                        <p className="text-xs text-muted-foreground mb-1">
                          Requester
                        </p>
                        <div className="flex items-center gap-2">
                          <UserAvatar
                            imageUrl={teamRequest.requestUserImageUrl}
                            size="w-6 h-6"
                          />
                          <Link
                            href={`/portal/users/${obfuscate(teamRequest.requestUserId)}`}
                            className="text-sm hover:underline"
                          >
                            {teamRequest.requestUserName}
                          </Link>
                        </div>
                      </div>

                      <div>
                        <p className="text-xs text-muted-foreground mb-1">
                          Assigned To
                        </p>
                        {teamRequest.assignUserId ? (
                          <div className="flex items-center gap-2">
                            <UserAvatar
                              imageUrl={teamRequest.assignUserImageUrl}
                              size="w-6 h-6"
                            />
                            <Link
                              href={`/portal/users/${obfuscate(teamRequest.assignUserId)}`}
                              className="text-sm hover:underline"
                            >
                              {teamRequest.assignUserName}
                            </Link>
                          </div>
                        ) : (
                          <span className="text-sm text-muted-foreground">
                            Unassigned
                          </span>
                        )}
                      </div>
                    </div>
                  </div>

                  <div className="space-y-4">
                    <h3 className="text-sm font-medium mb-2 flex items-center">
                      <FileText className="mr-2 h-4 w-4" /> Ticket Details
                    </h3>

                    <div className="grid grid-cols-2 gap-3">
                      <div>
                        <p className="text-xs text-muted-foreground mb-1">
                          Type
                        </p>
                        <Badge variant="outline" className="font-normal">
                          {teamRequest.workflowRequestName}
                        </Badge>
                      </div>

                      <div>
                        <p className="text-xs text-muted-foreground mb-1">
                          Status
                        </p>
                        <Badge variant="outline" className="font-normal">
                          {teamRequest.currentStateName}
                        </Badge>
                      </div>

                      <div>
                        <p className="text-xs text-muted-foreground mb-1">
                          Priority
                        </p>
                        <PriorityDisplay priority={teamRequest.priority} />
                      </div>

                      {teamRequest.channel && (
                        <div>
                          <p className="text-xs text-muted-foreground mb-1">
                            Channel
                          </p>
                          <Badge variant="outline" className="font-normal">
                            {teamRequest.channel}
                          </Badge>
                        </div>
                      )}
                    </div>
                  </div>
                </div>

                <div className="mt-6">
                  <h3 className="text-sm font-medium mb-2 flex items-center">
                    <Calendar className="mr-2 h-4 w-4" /> Important Dates
                  </h3>

                  <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                    <div>
                      <p className="text-xs text-muted-foreground mb-1">
                        Created
                      </p>
                      <TooltipProvider>
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <p className="text-sm">
                              {formatDateTimeDistanceToNow(
                                new Date(teamRequest.createdAt!),
                              )}
                            </p>
                          </TooltipTrigger>
                          <TooltipContent>
                            {new Date(teamRequest.createdAt!).toLocaleString()}
                          </TooltipContent>
                        </Tooltip>
                      </TooltipProvider>
                    </div>

                    <div>
                      <p className="text-xs text-muted-foreground mb-1">
                        Last Modified
                      </p>
                      <TooltipProvider>
                        <Tooltip>
                          <TooltipTrigger asChild>
                            <p className="text-sm">
                              {formatDateTimeDistanceToNow(
                                new Date(teamRequest.modifiedAt!),
                              )}
                            </p>
                          </TooltipTrigger>
                          <TooltipContent>
                            {new Date(teamRequest.modifiedAt!).toLocaleString()}
                          </TooltipContent>
                        </Tooltip>
                      </TooltipProvider>
                    </div>

                    <div>
                      <p className="text-xs text-muted-foreground mb-1">
                        Target Completion
                      </p>
                      <p className="text-sm">
                        {teamRequest.estimatedCompletionDate
                          ? new Date(
                              teamRequest.estimatedCompletionDate,
                            ).toDateString()
                          : "Not set"}
                      </p>
                    </div>

                    <div>
                      <p className="text-xs text-muted-foreground mb-1">
                        Actual Completion
                      </p>
                      <p className="text-sm">
                        {teamRequest.actualCompletionDate
                          ? new Date(
                              teamRequest.actualCompletionDate,
                            ).toDateString()
                          : "Not completed"}
                      </p>
                    </div>
                  </div>
                </div>

                <div className="space-y-4 mt-6"></div>

                <div className="space-y-4 mt-6">
                  <div>
                    <h3 className="text-sm font-medium mb-2 flex items-center">
                      <FileText className="mr-2 h-4 w-4" /> Attachments
                    </h3>
                    <AttachmentView
                      entityType="Team_Request"
                      entityId={teamRequest.id!}
                    />
                  </div>

                  <div>
                    <h3 className="text-sm font-medium mb-2 flex items-center">
                      <Users className="mr-2 h-4 w-4" /> Watchers
                    </h3>
                    <EntityWatchers
                      entityType="Team_Request"
                      entityId={teamRequest.id!}
                    />
                  </div>
                </div>
              </div>

              <Separator />

              <div className="pb-6">
                <Tabs
                  defaultValue="comments"
                  value={selectedTab}
                  onValueChange={handleTabChange}
                  className="w-full"
                >
                  <TabsList className="mx-6 mt-6 grid w-full grid-cols-3 bg-muted/50">
                    <TabsTrigger
                      value="comments"
                      className="data-[state=active]:bg-background data-[state=active]:shadow-sm"
                    >
                      <MessageSquarePlus className="mr-2 h-4 w-4" /> Comments
                    </TabsTrigger>
                    <TabsTrigger
                      value="changes-history"
                      className="data-[state=active]:bg-background data-[state=active]:shadow-sm"
                    >
                      <Clock className="mr-2 h-4 w-4" /> Changes History
                    </TabsTrigger>
                    <TabsTrigger
                      value="timeline-history"
                      className="data-[state=active]:bg-background data-[state=active]:shadow-sm"
                    >
                      <FileText className="mr-2 h-4 w-4" /> Timeline
                    </TabsTrigger>
                  </TabsList>

                  <div className="mx-6 mt-6">
                    <TabsContent value="comments">
                      {selectedTab === "comments" && (
                        <div ref={commentsViewRef}>
                          <CommentsView
                            entityType="Team_Request"
                            entityId={teamRequest.id!}
                          />
                        </div>
                      )}
                    </TabsContent>
                    <TabsContent value="changes-history">
                      {selectedTab === "changes-history" && (
                        <AuditLogView
                          entityType="Team_Request"
                          entityId={teamRequest.id!}
                        />
                      )}
                    </TabsContent>
                    <TabsContent value="timeline-history">
                      {selectedTab === "timeline-history" && (
                        <TicketTimelineHistory teamId={teamRequest.id!} />
                      )}
                    </TabsContent>
                  </div>
                </Tabs>
              </div>
            </CardContent>
          </Card>
        </div>

        <WorkflowReviewDialog
          workflowId={teamRequest.workflowId!}
          open={isWorkflowDialogOpen}
          onClose={() => setWorkflowDialogOpen(false)}
        />
      </TeamNavLayout>
    </BreadcrumbProvider>
  );
};

export default TeamRequestDetailView;
