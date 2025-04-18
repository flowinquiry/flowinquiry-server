"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import React, { useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import * as z from "zod";

import RichTextEditor from "@/components/shared/rich-text-editor";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogPortal,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  DatePickerField,
  ExtInputField,
  SubmitButton,
} from "@/components/ui/ext-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { createProject, updateProject } from "@/lib/actions/project.action";
import { useError } from "@/providers/error-provider";
import { ProjectDTO, ProjectSchema, ProjectStatus } from "@/types/projects";
import { TeamDTO } from "@/types/teams";

type ProjectDialogProps = {
  open: boolean;
  setOpen: React.Dispatch<React.SetStateAction<boolean>>;
  teamEntity: TeamDTO;
  project?: ProjectDTO | null;
  onSaveSuccess: () => void;
};

const ProjectEditDialog: React.FC<ProjectDialogProps> = ({
  open,
  setOpen,
  teamEntity,
  project,
  onSaveSuccess,
}) => {
  const { setError } = useError();
  const editorMountedRef = useRef(false);

  const form = useForm<z.infer<typeof ProjectSchema>>({
    resolver: zodResolver(ProjectSchema),
    defaultValues: {
      teamId: teamEntity.id!,
      name: "",
      description: "",
      status: "Active" as ProjectStatus,
      startDate: undefined,
      endDate: undefined,
    },
  });

  // Populate form when editing an existing project
  useEffect(() => {
    if (open) {
      if (project) {
        form.reset({
          teamId: teamEntity.id!,
          name: project.name || "",
          description: project.description || "",
          status: project.status || "Active",
          startDate: project.startDate || undefined,
          endDate: project.endDate || undefined,
        });
      } else {
        form.reset({
          teamId: teamEntity.id!,
          name: "",
          description: "",
          status: "Active",
          startDate: undefined,
          endDate: undefined,
        });
      }
      editorMountedRef.current = true;
    }
  }, [project, teamEntity.id, open, form]);

  const onSubmit = async (data: ProjectDTO) => {
    if (project) {
      await updateProject(project.id!, data, setError);
    } else {
      await createProject(data, setError);
    }

    setOpen(false);
    onSaveSuccess();
  };

  // Handle dialog close - make sure to clean up any pending editor state
  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      // Allow time for any pending operations to complete
      setTimeout(() => {
        editorMountedRef.current = false;
      }, 100);
    }
    setOpen(newOpen);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogPortal>
        <DialogContent
          className="sm:max-w-[56rem] max-h-[90vh] p-4 sm:p-6 flex flex-col overflow-y-auto"
          onPointerDownOutside={(e) => {
            // Prevent closing when clicking inside editor dropdowns that may be rendered in a portal
            if (
              e.target &&
              (e.target as HTMLElement).closest(".mention-dropdown-container")
            ) {
              e.preventDefault();
            }
          }}
          onInteractOutside={(e) => {
            // Prevent closing when interacting with editor dropdowns
            if (
              e.target &&
              (e.target as HTMLElement).closest(".mention-dropdown-container")
            ) {
              e.preventDefault();
            }
          }}
        >
          <DialogHeader>
            <DialogTitle>
              {project ? "Edit Project" : "New Project"}
            </DialogTitle>
            <DialogDescription>
              {project
                ? "Modify the project details."
                : "Every great project starts with a first step. Let's begin!"}
            </DialogDescription>
          </DialogHeader>
          <Form {...form}>
            <form
              onSubmit={form.handleSubmit(onSubmit)}
              className="flex flex-col flex-1"
            >
              <div className="flex-1 overflow-y-auto space-y-6">
                {/* Grid layout for form fields */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                  {/* Name Field - Spans full width */}
                  <div className="col-span-1 sm:col-span-2">
                    <ExtInputField
                      form={form}
                      fieldName="name"
                      label="Name"
                      required
                    />
                  </div>

                  {/* Description Field - Spans full width */}
                  <div className="col-span-1 sm:col-span-2">
                    <FormField
                      control={form.control}
                      name="description"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>
                            Description{" "}
                            <span className="text-destructive">*</span>
                          </FormLabel>
                          <FormControl>
                            {editorMountedRef.current && (
                              <RichTextEditor
                                value={field.value}
                                onChange={field.onChange}
                                // This ensures the editor doesn't lose focus unexpectedly
                                onBlur={() => {
                                  // Don't trigger form blur immediately
                                }}
                                // Ensure editor is properly mounted for each dialog instance
                                key={`editor-${open ? "open" : "closed"}-${project?.id || "new"}`}
                              />
                            )}
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <div className="col-span-1">
                    <FormField
                      control={form.control}
                      name="status"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>
                            Status <span className="text-destructive">*</span>
                          </FormLabel>
                          <Select
                            onValueChange={field.onChange}
                            defaultValue={field.value}
                            value={field.value}
                          >
                            <FormControl>
                              <SelectTrigger>
                                <SelectValue placeholder="Select a status" />
                              </SelectTrigger>
                            </FormControl>
                            <SelectContent>
                              <SelectItem value="Active">Active</SelectItem>
                              <SelectItem value="Closed">Closed</SelectItem>
                            </SelectContent>
                          </Select>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <div className="col-span-1"></div>

                  <div className="col-span-1">
                    <DatePickerField
                      form={form}
                      fieldName="startDate"
                      label="Start Date"
                      placeholder="Select a date"
                    />
                  </div>
                  <div className="col-span-1">
                    <DatePickerField
                      form={form}
                      fieldName="endDate"
                      label="End Date"
                      placeholder="Select a date"
                    />
                  </div>
                </div>
              </div>

              <div className="pt-4">
                <SubmitButton
                  label={project ? "Save Changes" : "Save"}
                  labelWhileLoading="Saving ..."
                />
              </div>
            </form>
          </Form>
        </DialogContent>
      </DialogPortal>
    </Dialog>
  );
};

export default ProjectEditDialog;
