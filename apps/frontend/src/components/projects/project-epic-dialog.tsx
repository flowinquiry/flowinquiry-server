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
  createProjectEpic,
  updateProjectEpic,
} from "@/lib/actions/project-epic.action";
import { useError } from "@/providers/error-provider";
import { ProjectEpicDTO, ProjectEpicDTOSchema } from "@/types/projects";

interface ProjectEpicDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave?: (epic: ProjectEpicDTO) => void;
  onCancel?: () => void;
  projectId: number;
  epic?: ProjectEpicDTO | null; // Optional epic for edit mode
}

export function ProjectEpicDialog({
  open,
  onOpenChange,
  onSave,
  onCancel,
  projectId,
  epic,
}: ProjectEpicDialogProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { setError } = useError();
  const t = useAppClientTranslations();

  // Determine if we're in edit mode
  const isEditMode = !!epic?.id;

  // Initialize form with the ProjectEpicDTOSchema
  const form = useForm<ProjectEpicDTO>({
    resolver: zodResolver(ProjectEpicDTOSchema),
    defaultValues: {
      id: epic?.id,
      projectId,
      name: epic?.name || "",
      description: epic?.description || "",
      startDate: epic?.startDate ? new Date(epic.startDate) : undefined,
      endDate: epic?.endDate ? new Date(epic.endDate) : undefined,
      totalTickets: epic?.totalTickets || 0,
    },
  });

  // Reset form when dialog opens/closes or epic changes
  useEffect(() => {
    if (open) {
      form.reset({
        id: epic?.id,
        projectId,
        name: epic?.name || "",
        description: epic?.description || "",
        startDate: epic?.startDate ? new Date(epic.startDate) : undefined,
        endDate: epic?.endDate ? new Date(epic.endDate) : undefined,
        totalTickets: epic?.totalTickets || 0,
      });
    }
  }, [open, epic, projectId, form]);

  const handleSubmit = async (values: ProjectEpicDTO) => {
    setIsSubmitting(true);
    try {
      let result: ProjectEpicDTO;

      if (isEditMode && epic?.id) {
        // Update existing epic
        result = await updateProjectEpic(epic.id, values, setError);
      } else {
        // Create new epic
        result = await createProjectEpic(values, setError);
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
      <DialogContent className="sm:max-w-[60rem]">
        <DialogHeader>
          <DialogTitle>
            {isEditMode
              ? t.teams.projects.epic("edit_dialog_title")
              : t.teams.projects.epic("create_dialog_title")}
          </DialogTitle>
          <DialogDescription>
            {isEditMode
              ? t.teams.projects.epic("edit_dialog_description")
              : t.teams.projects.epic("create_dialog_description")}
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
                  <FormLabel>{t.teams.projects.epic("form.name")}</FormLabel>
                  <FormControl>
                    <Input placeholder="User Authentication" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              {/* Use DatePickerField for startDate */}
              <DatePickerField
                form={form}
                fieldName="startDate"
                label={t.teams.projects.epic("form.start_date")}
                placeholder={t.common.misc("date_select_place_holder")}
                dateSelectionMode="any"
                required={false}
              />

              {/* Use DatePickerField for endDate */}
              <DatePickerField
                form={form}
                fieldName="endDate"
                label={t.teams.projects.epic("form.end_date")}
                placeholder={t.common.misc("date_select_place_holder")}
                dateSelectionMode="any"
                required={false}
              />
            </div>

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    {t.teams.projects.epic("form.description")}
                  </FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder={t.teams.projects.epic(
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
                    : t.teams.projects.epic("form.create_epic")}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export default ProjectEpicDialog;
