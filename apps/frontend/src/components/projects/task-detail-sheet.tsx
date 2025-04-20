"use client";

import { formatDistanceToNow } from "date-fns";
import { Edit2, MessageSquarePlus } from "lucide-react";
import React, { useEffect, useRef, useState } from "react";

import AttachmentView from "@/components/shared/attachment-view";
import AuditLogView from "@/components/shared/audit-log-view";
import { UserAvatar } from "@/components/shared/avatar-display";
import CommentsView from "@/components/shared/comments-view";
import EntityWatchers from "@/components/shared/entity-watchers";
import RichTextEditor from "@/components/shared/rich-text-editor"; // Import RichTextEditor
import TeamUserSelect from "@/components/teams/team-user-select";
import TicketTimelineHistory from "@/components/teams/ticket-timeline-history";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Separator } from "@/components/ui/separator";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import WorkflowStateSelect from "@/components/workflows/workflow-state-select";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  PRIORITIES_ORDERED,
  PRIORITY_CONFIG,
} from "@/lib/constants/ticket-priorities";
import { TeamRequestDTO, TeamRequestPriority } from "@/types/team-requests";
import { UserWithTeamRoleDTO } from "@/types/teams";

type TaskDetailSheetProps = {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  task: TeamRequestDTO | null;
  onTaskUpdate?: (updatedTask: TeamRequestDTO) => Promise<void> | void;
};

const TaskDetailSheet: React.FC<TaskDetailSheetProps> = ({
  isOpen,
  setIsOpen,
  task: initialTask,
  onTaskUpdate,
}) => {
  // Keep a local copy of the task for UI updates
  const [task, setTask] = useState<TeamRequestDTO | null>(initialTask);
  const t = useAppClientTranslations();

  // Editing states
  const [isEditingTitle, setIsEditingTitle] = useState(false);
  const [isEditingDescription, setIsEditingDescription] = useState(false);
  const [isEditingPriority, setIsEditingPriority] = useState(false);
  const [isEditingState, setIsEditingState] = useState(false);
  const [isEditingAssignee, setIsEditingAssignee] = useState(false);
  const [selectedTab, setSelectedTab] = useState("comments");
  const commentsViewRef = useRef<HTMLDivElement | null>(null);

  // Form values
  const [editedTitle, setEditedTitle] = useState("");
  const [editedDescription, setEditedDescription] = useState("");

  const [isSaving, setIsSaving] = useState(false);

  // Refs for input elements
  const titleInputRef = useRef<HTMLInputElement>(null);
  const descriptionContainerRef = useRef<HTMLDivElement>(null);

  // Update local task when prop changes
  useEffect(() => {
    setTask(initialTask);
  }, [initialTask]);

  // Start editing title
  const handleEditTitle = () => {
    if (task) {
      setEditedTitle(task.requestTitle);
      setIsEditingTitle(true);
      // Focus will be set by useEffect below
    }
  };

  // Focus input element when entering edit mode
  useEffect(() => {
    if (isEditingTitle && titleInputRef.current) {
      titleInputRef.current.focus();
    }
  }, [isEditingTitle]);

  // Start editing description
  const handleEditDescription = () => {
    if (task) {
      setEditedDescription(task.requestDescription || "");
      setIsEditingDescription(true);
    }
  };

  // Handle blur for title (save on blur)
  const handleTitleBlur = async () => {
    if (!task) return;

    // Create updated task with new title
    const updatedTask = {
      ...task,
      requestTitle: editedTitle,
    };

    // Update local state first for immediate feedback
    setTask(updatedTask);

    // Exit edit mode
    setIsEditingTitle(false);

    // Then call API if handler exists
    if (onTaskUpdate) {
      setIsSaving(true);
      try {
        await onTaskUpdate(updatedTask);
      } catch (error) {
        // If API fails, revert to the previous state
        console.error("Failed to update task title:", error);
        setTask(task);
      } finally {
        setIsSaving(false);
      }
    }
  };

  // Handle save for description
  const handleDescriptionSave = async () => {
    if (!task) return;

    // Create updated task with new description
    const updatedTask = {
      ...task,
      requestDescription: editedDescription,
    };

    // Update local state first for immediate feedback
    setTask(updatedTask);

    // Exit edit mode
    setIsEditingDescription(false);

    // Then call API if handler exists
    if (onTaskUpdate) {
      setIsSaving(true);
      try {
        await onTaskUpdate(updatedTask);
      } catch (error) {
        // If API fails, revert to the previous state
        console.error("Failed to update task description:", error);
        setTask(task);
      } finally {
        setIsSaving(false);
      }
    }
  };

  // Handler for RichTextEditor's onChange
  const handleDescriptionChange = (content: string) => {
    setEditedDescription(content);
  };

  // Handle priority change
  const handlePriorityChange = async (
    selectedPriority: TeamRequestPriority,
  ) => {
    if (!task) return;

    // Create updated task with new priority
    const updatedTask = {
      ...task,
      priority: selectedPriority,
    };

    // Update local state first for immediate feedback
    setTask(updatedTask);

    // Exit edit mode
    setIsEditingPriority(false);

    // Then call API if handler exists
    if (onTaskUpdate) {
      setIsSaving(true);
      try {
        await onTaskUpdate(updatedTask);
      } catch (error) {
        // If API fails, revert to the previous state
        console.error("Failed to update task priority:", error);
        setTask(task);
      } finally {
        setIsSaving(false);
      }
    }
  };

  // Handle state change
  const handleStateChange = async (
    newStateId: number,
    newStateName: string,
  ) => {
    if (!task) return;

    // Create updated task with new state
    const updatedTask = {
      ...task,
      currentStateId: newStateId,
      currentStateName: newStateName,
    };

    // Update local state first for immediate feedback
    setTask(updatedTask);

    // Exit edit mode
    setIsEditingState(false);

    // Then call API if handler exists
    if (onTaskUpdate) {
      setIsSaving(true);
      try {
        await onTaskUpdate(updatedTask);
      } catch (error) {
        // If API fails, revert to the previous state
        console.error("Failed to update task state:", error);
        setTask(task);
      } finally {
        setIsSaving(false);
      }
    }
  };

  // Handle assignee change
  const handleAssigneeChange = async (
    selectedUser: UserWithTeamRoleDTO | null,
  ) => {
    if (!task) return;

    // Create updated task with new assignee information
    const updatedTask = {
      ...task,
      assignUserId: selectedUser?.id || null,
      assignUserName: selectedUser
        ? `${selectedUser.firstName} ${selectedUser.lastName}`
        : null,
      assignUserImageUrl: selectedUser?.imageUrl || null,
    };

    // Update local state first for immediate feedback
    setTask(updatedTask);

    // Exit edit mode
    setIsEditingAssignee(false);

    // Then call API if handler exists
    if (onTaskUpdate) {
      setIsSaving(true);
      try {
        await onTaskUpdate(updatedTask);
      } catch (error) {
        // If API fails, revert to the previous state
        console.error("Failed to update task assignee:", error);
        setTask(task);
      } finally {
        setIsSaving(false);
      }
    }
  };

  // Reset editing states when sheet opens/closes
  useEffect(() => {
    if (!isOpen) {
      setIsEditingTitle(false);
      setIsEditingDescription(false);
      setIsEditingPriority(false);
      setIsEditingState(false);
      setIsEditingAssignee(false);
    }
  }, [isOpen]);

  const handleTabChange = (value: string) => {
    setSelectedTab(value);
  };

  const handleFocusComments = () => {
    setSelectedTab("comments"); // Ensure the Comments tab is active

    // Delay scrolling slightly to allow UI to update first
    setTimeout(() => {
      if (commentsViewRef.current) {
        commentsViewRef.current.scrollIntoView({
          behavior: "smooth",
          block: "start",
        });
      }
    }, 100);
  };

  if (!task) return null; // Don't render if no task is selected

  const canEdit = !task.isCompleted;

  // CSS class for editable content hover effect
  const editableClass = canEdit
    ? "group cursor-pointer transition-colors border border-transparent hover:border-dashed hover:border-gray-400 dark:hover:border-gray-600 rounded-md"
    : "";

  return (
    <Sheet open={isOpen} onOpenChange={setIsOpen}>
      <SheetContent side="right" className="w-[90vw] sm:w-[42rem] lg:w-[56rem]">
        <SheetHeader>
          <SheetTitle>
            <div className="flex items-center justify-between">
              <div className="flex-1">
                {isEditingTitle ? (
                  <div>
                    <Input
                      ref={titleInputRef}
                      value={editedTitle}
                      onChange={(e) => setEditedTitle(e.target.value)}
                      onBlur={handleTitleBlur}
                      onKeyDown={(e) => {
                        if (e.key === "Enter") {
                          handleTitleBlur();
                        }
                      }}
                      className="text-xl font-semibold"
                    />
                  </div>
                ) : (
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <h2
                          className={`text-xl font-semibold px-1 py-1 ${editableClass}`}
                          onClick={canEdit ? handleEditTitle : undefined}
                        >
                          {task.requestTitle}
                        </h2>
                      </TooltipTrigger>
                      {canEdit && (
                        <TooltipContent>
                          <p>Click to edit title</p>
                        </TooltipContent>
                      )}
                    </Tooltip>
                  </TooltipProvider>
                )}
              </div>
              <Button
                variant="ghost"
                size="sm"
                className="flex items-center gap-1"
                onClick={handleFocusComments}
              >
                <MessageSquarePlus className="h-4 w-4" />{" "}
                {t.common.buttons("add_comment")}
              </Button>
            </div>
          </SheetTitle>
        </SheetHeader>

        <ScrollArea className="h-[calc(100vh-200px)] mt-6">
          <div className="space-y-6 pr-4">
            {/* Status and Priority Section */}
            <div className="grid grid-cols-2 gap-4">
              {/* Priority Section */}
              <div>
                <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  {t.teams.tickets.form.base("priority")}
                </h3>

                {isEditingPriority ? (
                  <div className="flex flex-col gap-2">
                    <Select
                      defaultValue={task.priority as string}
                      onValueChange={(value: TeamRequestPriority) =>
                        handlePriorityChange(value)
                      }
                    >
                      <SelectTrigger className="w-full">
                        <SelectValue placeholder="Select priority">
                          <div className="flex items-center gap-2">
                            <span
                              className={
                                PRIORITY_CONFIG[
                                  task.priority as TeamRequestPriority
                                ].iconColor
                              }
                            >
                              {
                                PRIORITY_CONFIG[
                                  task.priority as TeamRequestPriority
                                ].icon
                              }
                            </span>
                            <span>{task.priority}</span>
                          </div>
                        </SelectValue>
                      </SelectTrigger>
                      <SelectContent>
                        {PRIORITIES_ORDERED.map(
                          (priority: TeamRequestPriority) => (
                            <SelectItem key={priority} value={priority}>
                              <div className="flex items-center gap-2">
                                <span
                                  className={
                                    PRIORITY_CONFIG[priority].iconColor
                                  }
                                >
                                  {PRIORITY_CONFIG[priority].icon}
                                </span>
                                <span>{priority}</span>
                              </div>
                            </SelectItem>
                          ),
                        )}
                      </SelectContent>
                    </Select>
                  </div>
                ) : (
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <div
                          className={`flex items-center p-2 rounded-md bg-gray-50 dark:bg-gray-800 ${editableClass}`}
                          onClick={
                            canEdit
                              ? () => setIsEditingPriority(true)
                              : undefined
                          }
                        >
                          <div className="flex items-center gap-2">
                            <span
                              className={
                                PRIORITY_CONFIG[
                                  task.priority as TeamRequestPriority
                                ].iconColor
                              }
                            >
                              {
                                PRIORITY_CONFIG[
                                  task.priority as TeamRequestPriority
                                ].icon
                              }
                            </span>
                            <span
                              className={
                                PRIORITY_CONFIG[
                                  task.priority as TeamRequestPriority
                                ].textColor
                              }
                            >
                              {task.priority}
                            </span>
                          </div>
                          {canEdit && (
                            <Edit2 className="h-3 w-3 ml-auto opacity-0 group-hover:opacity-100 text-gray-400" />
                          )}
                        </div>
                      </TooltipTrigger>
                      {canEdit && (
                        <TooltipContent>
                          <p>Click to change priority</p>
                        </TooltipContent>
                      )}
                    </Tooltip>
                  </TooltipProvider>
                )}
              </div>

              {/* Status Section */}
              <div>
                <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                  {t.teams.tickets.form.base("state")}
                </h3>

                {isEditingState && task.workflowId ? (
                  <div className="flex flex-col gap-2">
                    <WorkflowStateSelect
                      workflowId={task.workflowId}
                      currentStateId={task.currentStateId!}
                      onChange={handleStateChange}
                    />
                  </div>
                ) : (
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <div
                          className={`flex items-center p-2 rounded-md bg-gray-50 dark:bg-gray-800 ${editableClass}`}
                          onClick={
                            canEdit ? () => setIsEditingState(true) : undefined
                          }
                        >
                          <span>{task.currentStateName || "Not Set"}</span>
                          {canEdit && (
                            <Edit2 className="h-3 w-3 ml-auto opacity-0 group-hover:opacity-100 text-gray-400" />
                          )}
                        </div>
                      </TooltipTrigger>
                      {canEdit && (
                        <TooltipContent>
                          <p>Click to change state</p>
                        </TooltipContent>
                      )}
                    </Tooltip>
                  </TooltipProvider>
                )}
              </div>
            </div>

            {/* Description Section with Click-to-Edit - REPLACED TEXTAREA WITH RICHTEXTEDITOR */}
            <div>
              <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Description
              </h3>

              {isEditingDescription ? (
                <div ref={descriptionContainerRef}>
                  <RichTextEditor
                    value={editedDescription}
                    onChange={handleDescriptionChange}
                  />
                  <div className="flex justify-end mt-2 gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => setIsEditingDescription(false)}
                    >
                      {t.common.buttons("cancel")}
                    </Button>
                    <Button
                      size="sm"
                      onClick={handleDescriptionSave}
                      disabled={isSaving}
                    >
                      {isSaving
                        ? t.common.buttons("saving")
                        : t.common.buttons("save")}
                    </Button>
                  </div>
                </div>
              ) : (
                <TooltipProvider>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <div
                        className={`text-sm bg-gray-50 dark:bg-gray-800 p-3 rounded-md min-h-[100px] ${editableClass}`}
                        onClick={canEdit ? handleEditDescription : undefined}
                      >
                        <div
                          dangerouslySetInnerHTML={{
                            __html:
                              task.requestDescription ||
                              "No description provided. Click to add one.",
                          }}
                        />
                        {canEdit && (
                          <Edit2 className="h-3 w-3 float-right mt-1 opacity-0 group-hover:opacity-100 text-gray-400" />
                        )}
                      </div>
                    </TooltipTrigger>
                    {canEdit && (
                      <TooltipContent>
                        <p>Click to edit description</p>
                      </TooltipContent>
                    )}
                  </Tooltip>
                </TooltipProvider>
              )}
            </div>

            <div className="col-span-1 sm:col-span-2 text-sm font-medium flex items-start gap-4">
              <span className="pt-1">
                {t.teams.tickets.detail("attachments")}
              </span>
              <AttachmentView entityType="Team_Request" entityId={task.id!} />
            </div>

            <div className="col-span-1 sm:col-span-2 text-sm font-medium flex items-start gap-4">
              <span className="pt-1">{t.teams.tickets.detail("watchers")}</span>
              <EntityWatchers entityType="Team_Request" entityId={task.id!} />
            </div>

            <Separator />

            {/* Assignment & People Section */}
            <div>
              <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">
                {t.teams.tickets.detail("people")}
              </h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {t.teams.tickets.form.base("requester")}
                  </p>
                  <div className="flex items-center mt-1 gap-2">
                    <UserAvatar imageUrl={task.requestUserImageUrl} />
                    <span className="text-sm">
                      {task.requestUserName || "Not specified"}
                    </span>
                  </div>
                </div>

                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {t.teams.tickets.form.base("assignee")}
                  </p>

                  {isEditingAssignee ? (
                    <div className="mt-1">
                      <TeamUserSelect
                        teamId={task.teamId!}
                        currentUserId={task.assignUserId}
                        onUserChange={handleAssigneeChange}
                      />
                    </div>
                  ) : (
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <div
                            className={`flex items-center mt-1 p-2 rounded-md bg-gray-50 dark:bg-gray-800 gap-2 ${editableClass}`}
                            onClick={
                              canEdit
                                ? () => setIsEditingAssignee(true)
                                : undefined
                            }
                          >
                            <UserAvatar imageUrl={task.assignUserImageUrl} />
                            <span className="text-sm">
                              {task.assignUserName || "Unassigned"}
                            </span>
                            {canEdit && (
                              <Edit2 className="h-3 w-3 ml-auto opacity-0 group-hover:opacity-100 text-gray-400" />
                            )}
                          </div>
                        </TooltipTrigger>
                        {canEdit && (
                          <TooltipContent>
                            <p>Click to change assignee</p>
                          </TooltipContent>
                        )}
                      </Tooltip>
                    </TooltipProvider>
                  )}
                </div>
              </div>
            </div>

            <Separator />

            {/* Dates Section */}
            <div>
              <h3 className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-3">
                {t.teams.tickets.detail("date_time")}
              </h3>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {t.teams.tickets.form.base("created_at")}
                  </p>
                  <p className="text-sm mt-1">
                    {task.createdAt
                      ? new Date(task.createdAt).toLocaleDateString()
                      : "Not available"}
                    {task.createdAt && (
                      <span className="text-xs text-gray-500 ml-2">
                        (
                        {formatDistanceToNow(task.createdAt, {
                          addSuffix: true,
                        })}
                        )
                      </span>
                    )}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    {t.teams.tickets.form.base("last_modified_at")}
                  </p>
                  <p className="text-sm mt-1">
                    {task.modifiedAt
                      ? new Date(task.modifiedAt).toLocaleDateString()
                      : "Not modified"}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    Due Date
                  </p>
                  <p className="text-sm mt-1">
                    {task.estimatedCompletionDate
                      ? new Date(
                          task.estimatedCompletionDate,
                        ).toLocaleDateString()
                      : t.teams.tickets.detail("not_set")}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-gray-500 dark:text-gray-400">
                    Completed
                  </p>
                  <p className="text-sm mt-1">
                    {task.actualCompletionDate
                      ? new Date(task.actualCompletionDate).toLocaleDateString()
                      : task.isCompleted
                        ? "Completed (date not specified)"
                        : "Not completed"}
                  </p>
                </div>
              </div>
            </div>

            {/* Project & Workflow Section */}
            <Separator />
            <Tabs
              defaultValue="comments"
              value={selectedTab}
              onValueChange={handleTabChange}
            >
              <TabsList className="grid w-full grid-cols-3">
                <TabsTrigger value="comments">
                  {t.common.misc("comments")}
                </TabsTrigger>
                <TabsTrigger value="changes-history">
                  {t.teams.tickets.detail("changes_history")}
                </TabsTrigger>
                <TabsTrigger value="timeline-history">
                  {t.teams.tickets.detail("timeline")}
                </TabsTrigger>
              </TabsList>
              <TabsContent value="comments">
                {selectedTab === "comments" && (
                  <div ref={commentsViewRef}>
                    <CommentsView
                      entityType="Team_Request"
                      entityId={task.id!}
                    />
                  </div>
                )}
              </TabsContent>
              <TabsContent value="changes-history">
                {selectedTab === "changes-history" && (
                  <AuditLogView entityType="Team_Request" entityId={task.id!} />
                )}
              </TabsContent>
              <TabsContent value="timeline-history">
                {selectedTab === "timeline-history" && (
                  <TicketTimelineHistory teamId={task.id!} />
                )}
              </TabsContent>
            </Tabs>
          </div>
        </ScrollArea>

        <div className="mt-6 flex justify-end space-x-2">
          <Button variant="outline" onClick={() => setIsOpen(false)}>
            {t.common.buttons("close")}
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  );
};

export default TaskDetailSheet;
