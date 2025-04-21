import { zodResolver } from "@hookform/resolvers/zod";
import { format } from "date-fns";
import { CalendarIcon } from "lucide-react";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
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
      startDate: epic?.startDate ? new Date(epic.startDate) : new Date(),
      endDate: epic?.endDate
        ? new Date(epic.endDate)
        : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000), // Default to 30 days
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
        startDate: epic?.startDate ? new Date(epic.startDate) : new Date(),
        endDate: epic?.endDate
          ? new Date(epic.endDate)
          : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
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
              <FormField
                control={form.control}
                name="startDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>
                      {t.teams.projects.epic("form.start_date")}
                    </FormLabel>
                    <Popover>
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button
                            variant="outline"
                            className="w-full pl-3 text-left font-normal"
                          >
                            {field.value ? (
                              format(field.value, "PPP")
                            ) : (
                              <span className="text-muted-foreground">
                                {t.common.misc("date_select_place_holder")}
                              </span>
                            )}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent className="w-auto p-0" align="start">
                        <Calendar
                          mode="single"
                          selected={
                            field.value instanceof Date
                              ? field.value
                              : undefined
                          }
                          onSelect={field.onChange}
                          initialFocus
                        />
                      </PopoverContent>
                    </Popover>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="endDate"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>
                      {t.teams.projects.epic("form.end_date")}
                    </FormLabel>
                    <Popover>
                      <PopoverTrigger asChild>
                        <FormControl>
                          <Button
                            variant="outline"
                            className="w-full pl-3 text-left font-normal"
                          >
                            {field.value ? (
                              format(field.value, "PPP")
                            ) : (
                              <span className="text-muted-foreground">
                                {t.common.misc("date_select_place_holder")}
                              </span>
                            )}
                            <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                          </Button>
                        </FormControl>
                      </PopoverTrigger>
                      <PopoverContent className="w-auto p-0" align="start">
                        <Calendar
                          mode="single"
                          selected={
                            field.value instanceof Date
                              ? field.value
                              : undefined
                          }
                          onSelect={field.onChange}
                          initialFocus
                        />
                      </PopoverContent>
                    </Popover>
                    <FormMessage />
                  </FormItem>
                )}
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
