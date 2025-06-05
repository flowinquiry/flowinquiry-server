import { zodResolver } from "@hookform/resolvers/zod";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { DatePickerField } from "@/components/ui/ext-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  createProjectIteration,
  updateProjectIteration,
} from "@/lib/actions/project-iteration.action";
import { useError } from "@/providers/error-provider";
import {
  ProjectIterationDTO,
  ProjectIterationDTOSchema,
} from "@/types/projects";

interface ProjectIterationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave?: (iteration: ProjectIterationDTO) => void;
  onCancel?: () => void;
  projectId: number;
  iteration?: ProjectIterationDTO | null; // Optional iteration for edit mode
}

export function ProjectIterationDialog({
  open,
  onOpenChange,
  onSave,
  onCancel,
  projectId,
  iteration,
}: ProjectIterationDialogProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { setError } = useError();
  const t = useAppClientTranslations();

  // Determine if we're in edit mode
  const isEditMode = !!iteration?.id;

  // Initialize form with the ProjectIterationDTOSchema
  const form = useForm<ProjectIterationDTO>({
    resolver: zodResolver(ProjectIterationDTOSchema),
    defaultValues: {
      id: iteration?.id,
      projectId: projectId,
      name: iteration?.name || "",
      description: iteration?.description || "",
      startDate: iteration?.startDate,
      endDate: iteration?.endDate,
      totalTickets: iteration?.totalTickets || 0,
    },
  });

  // Reset form when dialog opens/closes or iteration changes
  useEffect(() => {
    if (open) {
      form.reset({
        id: iteration?.id,
        projectId: projectId,
        name: iteration?.name || "",
        description: iteration?.description || "",
        startDate: iteration?.startDate,
        endDate: iteration?.endDate,
        totalTickets: iteration?.totalTickets || 0,
      });
    }
  }, [open, iteration, projectId, form]);

  const handleSubmit = async (values: ProjectIterationDTO) => {
    setIsSubmitting(true);
    try {
      let result: ProjectIterationDTO;

      if (isEditMode && iteration?.id) {
        // Update existing iteration
        result = await updateProjectIteration(iteration.id, values, setError);
      } else {
        // Create new iteration
        result = await createProjectIteration(values, setError);
      }

      onOpenChange(false);

      if (onSave) {
        onSave(result);
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-240">
        <DialogHeader>
          <DialogTitle>
            {isEditMode
              ? t.teams.projects.iteration("edit_dialog_title")
              : t.teams.projects.iteration("create_dialog_title")}
          </DialogTitle>
          <DialogDescription>
            {isEditMode
              ? t.teams.projects.iteration("edit_dialog_description")
              : t.teams.projects.iteration("create_dialog_description")}
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form
            onSubmit={form.handleSubmit(handleSubmit)}
            className="space-y-6"
          >
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    {t.teams.projects.iteration("form.name")}
                  </FormLabel>
                  <FormControl>
                    <Input
                      placeholder={t.teams.projects.iteration(
                        "form.name_place_holder",
                      )}
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <DatePickerField
                form={form}
                fieldName="startDate"
                label={t.teams.projects.iteration("form.start_date")}
                placeholder={t.common.misc("date_select_place_holder")}
              />

              <DatePickerField
                form={form}
                fieldName="endDate"
                label={t.teams.projects.iteration("form.end_date")}
                placeholder={t.common.misc("date_select_place_holder")}
              />
            </div>

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    {t.teams.projects.iteration("form.description")}
                  </FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder={t.teams.projects.iteration(
                        "form.description_place_holder",
                      )}
                      {...field}
                      rows={3}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter>
              <Button
                type="button"
                variant="outline"
                onClick={onCancel}
                disabled={isSubmitting}
              >
                {t.common.buttons("cancel")}
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting
                  ? isEditMode
                    ? t.common.buttons("saving")
                    : t.common.buttons("creating")
                  : isEditMode
                    ? t.common.buttons("save_changes")
                    : t.teams.projects.iteration("form.create_iteration")}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export default ProjectIterationDialog;
