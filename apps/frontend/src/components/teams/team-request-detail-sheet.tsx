"use client";

import {
  AlertCircle,
  CheckCircle,
  Clock,
  Eye,
  FileText,
  Paperclip,
  User,
} from "lucide-react";
import Link from "next/link";
import React, { useState } from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";

import AttachmentView from "@/components/shared/attachment-view";
import { UserAvatar } from "@/components/shared/avatar-display";
import CommentsView from "@/components/shared/comments-view";
import EntityWatchers from "@/components/shared/entity-watchers";
import RichTextEditor from "@/components/shared/rich-text-editor";
import TeamRequestHealthLevel from "@/components/teams/team-requests-health-level";
import { PriorityDisplay } from "@/components/teams/team-requests-priority-display";
import { TeamRequestPrioritySelect } from "@/components/teams/team-requests-priority-select";
import TicketChannelSelectField from "@/components/teams/team-ticket-channel-select";
import TeamUserSelectField from "@/components/teams/team-users-select-field";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import WorkflowStateSelectField from "@/components/workflows/workflow-state-select-field";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { updateTeamRequest } from "@/lib/actions/teams-request.action";
import { obfuscate } from "@/lib/endecode";
import { cn, getSpecifiedColor } from "@/lib/utils";
import { useError } from "@/providers/error-provider";
import { TeamRequestDTO } from "@/types/team-requests";

const EditableSection = ({
  children,
  onEdit,
  editableClassName,
}: {
  children: React.ReactNode;
  onEdit: () => void;
  editableClassName?: string;
}) => {
  const [isHovered, setIsHovered] = useState(false);
  const t = useAppClientTranslations();

  const handleClick = (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    onEdit();
  };

  return (
    <div
      className={cn(
        "group relative",
        isHovered
          ? "border border-dashed border-gray-500 rounded-lg bg-gray-50 dark:bg-gray-800"
          : "",
        editableClassName,
      )}
      onClick={handleClick}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <TooltipProvider delayDuration={300}>
        <Tooltip>
          <TooltipTrigger className="absolute inset-0 z-10 cursor-pointer" />
          <TooltipContent side="bottom">
            <p>{t.teams.tickets.detail("click_to_edit")}</p>
          </TooltipContent>
        </Tooltip>
      </TooltipProvider>
      {children}
    </div>
  );
};

type RequestDetailsProps = {
  open: boolean;
  onClose: () => void;
  request: TeamRequestDTO;
};

const TeamRequestDetailSheet: React.FC<RequestDetailsProps> = ({
  open,
  onClose,
  request,
}) => {
  const [teamRequest, setTeamRequest] = useState<TeamRequestDTO>(request);
  const workflowColor = getSpecifiedColor(request.workflowRequestName!);
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [isEditingDescription, setIsEditingDescription] = useState(false);
  const [isEditingStatus, setIsEditingStatus] = useState(false);
  const [isEditingPriority, setIsEditingPriority] = useState(false);
  const [isEditingChannel, setIsEditingChannel] = useState(false);
  const [isEditingCompletionDate, setIsEditingCompletionDate] = useState(false);
  const [isEditingAssignment, setIsEditingAssignment] = useState(false);
  const { setError } = useError();
  const t = useAppClientTranslations();

  const form = useForm<TeamRequestDTOWithStringDates>({
    defaultValues: teamRequest as unknown as TeamRequestDTOWithStringDates,
  });

  const onSubmit = async (formData: TeamRequestDTOWithStringDates) => {
    setSubmitting(true);
    try {
      // Convert to TeamRequestDTO for the backend
      const data = {
        ...formData,
        // Handle date conversions if needed
        estimatedCompletionDate: formData.estimatedCompletionDate,
      } as unknown as TeamRequestDTO;

      const updatedRequest = await updateTeamRequest(
        teamRequest.id!,
        data,
        setError,
      );

      setTeamRequest(updatedRequest);

      setIsEditingTitle(false);
      setIsEditingDescription(false);
      setIsEditingStatus(false);
      setIsEditingPriority(false);
      setIsEditingChannel(false);
      setIsEditingCompletionDate(false);
    } catch (err) {
      console.error("Failed to update request", err);
    } finally {
      setSubmitting(false);
    }
  };

  interface TeamRequestDTOWithStringDates
    extends Omit<TeamRequestDTO, "estimatedCompletionDate"> {
    estimatedCompletionDate?: string | null;
  }

  const formatDateForInput = (
    dateString: string | null | undefined,
  ): string => {
    if (!dateString) return "";
    return dateString.split("T")[0];
  };

  // Determine request status
  const currentDate = new Date();
  const estimatedCompletionDate = request.estimatedCompletionDate
    ? new Date(request.estimatedCompletionDate)
    : null;

  const getRequestStatusIcon = () => {
    if (request.isCompleted) {
      return <CheckCircle className="w-5 h-5 text-green-500" />;
    }
    if (estimatedCompletionDate && estimatedCompletionDate < currentDate) {
      return <AlertCircle className="w-5 h-5 text-red-500" />;
    }
    return <Clock className="w-5 h-5 text-blue-500" />;
  };

  return (
    <FormProvider {...form}>
      <Sheet open={open} onOpenChange={onClose}>
        <SheetContent className="w-full sm:w-[64rem] h-full">
          <ScrollArea className="h-full px-4">
            <SheetHeader className="mb-6">
              <SheetTitle>
                <div className="flex items-center gap-4 mb-2">
                  <span
                    className="inline-block px-2 py-1 text-xs font-semibold rounded-md"
                    style={{
                      backgroundColor: workflowColor.background,
                      color: workflowColor.text,
                    }}
                  >
                    {request.workflowRequestName}
                  </span>

                  {isEditingTitle ? (
                    <form
                      onSubmit={form.handleSubmit(onSubmit)}
                      className="flex items-center gap-2 flex-grow"
                    >
                      <Controller
                        name="requestTitle"
                        control={form.control}
                        render={({ field }) => (
                          <Input
                            {...field}
                            className="text-xl"
                            placeholder="Enter request title"
                            autoFocus
                          />
                        )}
                      />
                      <div className="flex gap-2">
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          onClick={() => {
                            form.handleSubmit(onSubmit)();
                            setIsEditingTitle(false);
                          }}
                        >
                          {t.common.buttons("save")}
                        </Button>
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          onClick={() => setIsEditingTitle(false)}
                        >
                          Cancel
                        </Button>
                      </div>
                    </form>
                  ) : (
                    <div className="flex-grow">
                      <Button
                        variant="link"
                        className={`px-0 text-xl flex-grow text-left ${request.isCompleted ? "line-through" : ""}`}
                        onClick={(e) => {
                          // Allow the link navigation to proceed (don't call preventDefault)
                        }}
                        onDoubleClick={(e) => {
                          e.preventDefault();
                          setIsEditingTitle(true);
                        }}
                      >
                        <Link
                          href={`/portal/teams/${obfuscate(teamRequest.teamId)}/requests/${obfuscate(
                            teamRequest.id,
                          )}`}
                          className="break-words whitespace-normal text-left"
                        >
                          {teamRequest.requestTitle || request.requestTitle}
                        </Link>
                      </Button>
                    </div>
                  )}
                </div>

                {request.conversationHealth?.healthLevel && (
                  <TeamRequestHealthLevel
                    currentLevel={request.conversationHealth.healthLevel}
                  />
                )}
              </SheetTitle>
            </SheetHeader>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <div className="md:col-span-2 space-y-6">
                <div
                  className={cn(
                    "p-4 rounded-lg border",
                    "bg-white dark:bg-gray-900",
                    "border-gray-200 dark:border-gray-700",
                  )}
                >
                  <div className="flex items-center gap-3 mb-3">
                    <FileText className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
                      {t.teams.tickets.form.base("description")}
                    </h3>
                  </div>

                  {isEditingDescription ? (
                    <div
                      className="relative z-50"
                      onClick={(e) => {
                        // Prevent the click from bubbling up and potentially closing the editor
                        e.stopPropagation();
                      }}
                    >
                      <RichTextEditor
                        key="description-editor"
                        value={teamRequest.requestDescription}
                        onChange={(content: string) => {
                          form.setValue("requestDescription", content, {
                            shouldValidate: false,
                            shouldDirty: true,
                          });
                        }}
                        onBlur={() => {
                          form.handleSubmit(onSubmit)();
                          setIsEditingDescription(false);
                        }}
                      />
                    </div>
                  ) : (
                    <EditableSection
                      onEdit={() => setIsEditingDescription(true)}
                    >
                      <div
                        className="prose dark:prose-invert max-w-none"
                        dangerouslySetInnerHTML={{
                          __html: teamRequest.requestDescription!,
                        }}
                      />
                    </EditableSection>
                  )}
                </div>

                <div
                  className={cn(
                    "p-4 rounded-lg border",
                    "bg-white dark:bg-gray-900",
                    "border-gray-200 dark:border-gray-700",
                  )}
                >
                  <div className="flex items-center gap-3 mb-3">
                    <div>{getRequestStatusIcon()}</div>
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
                      {t.teams.tickets.detail("state_priority")}
                    </h3>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 block mb-1">
                        {t.teams.tickets.form.base("state")}
                      </span>
                      {isEditingStatus ? (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="py-2"
                        >
                          <div className="w-[16rem]">
                            <WorkflowStateSelectField
                              form={form}
                              name="currentStateId"
                              label=""
                              workflowId={request.workflowId!}
                              workflowStateId={request.currentStateId!}
                              includeSelf={true}
                              required={false}
                            />
                          </div>
                          <div className="flex justify-end gap-2 mt-2">
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => {
                                form.handleSubmit(onSubmit)();
                                setIsEditingStatus(false);
                              }}
                            >
                              {t.common.buttons("save")}
                            </Button>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => setIsEditingStatus(false)}
                            >
                              {t.common.buttons("cancel")}
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <EditableSection
                          onEdit={() => setIsEditingStatus(true)}
                        >
                          <Badge variant="outline">
                            {teamRequest.currentStateName ||
                              request.currentStateName}
                          </Badge>
                        </EditableSection>
                      )}
                    </div>

                    <div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 block mb-1">
                        Priority
                      </span>
                      {isEditingPriority ? (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="py-2"
                        >
                          <Controller
                            name="priority"
                            control={form.control}
                            render={({ field }) => (
                              <TeamRequestPrioritySelect
                                value={field.value as any}
                                onChange={(value) => {
                                  field.onChange(value);
                                }}
                              />
                            )}
                          />
                          <div className="flex justify-end gap-2 mt-2">
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => {
                                form.handleSubmit(onSubmit)();
                                setIsEditingPriority(false);
                              }}
                            >
                              Save
                            </Button>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => setIsEditingPriority(false)}
                            >
                              {t.common.buttons("cancel")}
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <EditableSection
                          onEdit={() => setIsEditingPriority(true)}
                        >
                          <PriorityDisplay
                            priority={teamRequest.priority || request.priority}
                          />
                        </EditableSection>
                      )}
                    </div>

                    {/* Channel - Editable */}
                    <div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 block mb-1">
                        Channel
                      </span>
                      {isEditingChannel ? (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="py-2"
                        >
                          <div className="w-[16rem]">
                            <TicketChannelSelectField form={form} />
                          </div>
                          <div className="flex justify-end gap-2 mt-2">
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => {
                                form.handleSubmit(onSubmit)();
                                setIsEditingChannel(false);
                              }}
                            >
                              {t.common.buttons("save")}
                            </Button>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => setIsEditingChannel(false)}
                            >
                              {t.common.buttons("cancel")}
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <EditableSection
                          onEdit={() => setIsEditingChannel(true)}
                        >
                          <Badge variant="outline">
                            {teamRequest?.channel
                              ? t.teams.tickets.form.channels(
                                  teamRequest.channel,
                                )
                              : request?.channel
                                ? t.teams.tickets.form.channels(request.channel)
                                : t.teams.tickets.form.channels("internal")}
                          </Badge>
                        </EditableSection>
                      )}
                    </div>

                    {/* Target Completion - Editable */}
                    <div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 block mb-1">
                        {t.teams.tickets.form.base("target_completion_date")}
                      </span>
                      {isEditingCompletionDate ? (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="py-2"
                        >
                          <Controller
                            name="estimatedCompletionDate"
                            control={form.control}
                            render={({ field }) => (
                              <Input
                                type="date"
                                value={formatDateForInput(field.value)}
                                onChange={(e) => {
                                  const value = e.target.value || null;
                                  field.onChange(value);
                                }}
                                className="w-full"
                                autoFocus
                                onBlur={() => {
                                  form.handleSubmit(onSubmit)();
                                  setIsEditingCompletionDate(false);
                                }}
                              />
                            )}
                          />
                        </div>
                      ) : (
                        <EditableSection
                          onEdit={() => setIsEditingCompletionDate(true)}
                        >
                          <p className="text-sm p-1">
                            {teamRequest.estimatedCompletionDate
                              ? new Date(
                                  teamRequest.estimatedCompletionDate,
                                ).toLocaleDateString()
                              : "N/A"}
                          </p>
                        </EditableSection>
                      )}
                    </div>
                  </div>
                </div>

                <div
                  className={cn(
                    "p-4 rounded-lg border",
                    "bg-white dark:bg-gray-900",
                    "border-gray-200 dark:border-gray-700",
                  )}
                >
                  <div className="flex items-center gap-3 mb-3">
                    <Paperclip className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
                      {t.teams.tickets.detail("attachments")}
                    </h3>
                  </div>
                  <AttachmentView
                    entityType="Team_Request"
                    entityId={teamRequest.id!}
                  />
                </div>
              </div>

              <div className="space-y-6">
                <div
                  className={cn(
                    "p-4 rounded-lg border",
                    "bg-white dark:bg-gray-900",
                    "border-gray-200 dark:border-gray-700",
                  )}
                >
                  <div className="flex items-center gap-3 mb-3">
                    <User className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
                      {t.teams.tickets.detail("people_assignment")}
                    </h3>
                  </div>
                  <div className="space-y-4">
                    <div>
                      <span className="text-xs text-gray-500 dark:text-gray-400 block mb-1">
                        {t.teams.tickets.form.base("requester")}
                      </span>
                      <div className="flex items-center gap-2">
                        <UserAvatar
                          imageUrl={teamRequest.requestUserImageUrl}
                          size="w-8 h-8"
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
                      <span className="text-xs text-gray-500 dark:text-gray-400 block mb-1">
                        {t.teams.tickets.form.base("assignee")}
                      </span>
                      {isEditingAssignment ? (
                        <div
                          onClick={(e) => e.stopPropagation()}
                          className="py-2"
                        >
                          <TeamUserSelectField
                            form={form}
                            fieldName="assignUserId"
                            label=""
                            teamId={teamRequest.teamId!}
                          />
                          <div className="flex justify-end gap-2 mt-2">
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => {
                                form.handleSubmit(onSubmit)();
                                setIsEditingAssignment(false);
                              }}
                            >
                              {t.common.buttons("save")}
                            </Button>
                            <Button
                              type="button"
                              variant="ghost"
                              size="sm"
                              onClick={() => setIsEditingAssignment(false)}
                            >
                              {t.common.buttons("cancel")}
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <EditableSection
                          onEdit={() => setIsEditingAssignment(true)}
                        >
                          <div className="flex items-center gap-2">
                            {teamRequest.assignUserId ||
                            request.assignUserId ? (
                              <>
                                <UserAvatar
                                  imageUrl={
                                    teamRequest.assignUserImageUrl ||
                                    request.assignUserImageUrl
                                  }
                                  size="w-8 h-8"
                                />
                                <Link
                                  href={`/portal/users/${obfuscate(teamRequest.assignUserId || request.assignUserId!)}`}
                                  className="text-sm hover:underline"
                                >
                                  {teamRequest.assignUserName ||
                                    request.assignUserName}
                                </Link>
                              </>
                            ) : (
                              <span className="text-sm text-gray-500">
                                {t.teams.tickets.detail("unassigned")}
                              </span>
                            )}
                          </div>
                        </EditableSection>
                      )}
                    </div>
                  </div>
                </div>

                {/* Watchers Section */}
                <div
                  className={cn(
                    "p-4 rounded-lg border",
                    "bg-white dark:bg-gray-900",
                    "border-gray-200 dark:border-gray-700",
                  )}
                >
                  <div className="flex items-center gap-3 mb-3">
                    <Eye className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
                      {t.teams.tickets.detail("watchers")}
                    </h3>
                  </div>
                  <EntityWatchers
                    entityType="Team_Request"
                    entityId={teamRequest.id!}
                  />
                </div>
              </div>
              <div className="md:col-span-3 mt-2">
                <div className="space-y-3">
                  <div className="flex items-center gap-3">
                    <User className="w-5 h-5 text-gray-600 dark:text-gray-300" />
                    <h3 className="text-sm font-semibold text-gray-700 dark:text-gray-200">
                      {t.teams.tickets.detail("comments")}
                    </h3>
                  </div>
                  <CommentsView
                    entityType="Team_Request"
                    entityId={teamRequest.id!}
                  />
                </div>
              </div>
            </div>
          </ScrollArea>
        </SheetContent>
      </Sheet>
    </FormProvider>
  );
};

export default TeamRequestDetailSheet;
