import { zodResolver } from "@hookform/resolvers/zod";
import React, { useState } from "react";
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
import { useToast } from "@/components/ui/use-toast";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { createProjectIteration } from "@/lib/actions/project-iteration.action";
import { useError } from "@/providers/error-provider";
import {
  ProjectIterationDTO,
  ProjectIterationDTOSchema,
} from "@/types/projects";

interface CreateIterationDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave?: (iteration: ProjectIterationDTO) => void;
  onCancel?: () => void;
  projectId: number;
}

export function CreateIterationDialog({
  open,
  onOpenChange,
  onSave,
  onCancel,
  projectId,
}: CreateIterationDialogProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { setError } = useError();
  const { toast } = useToast();
  const t = useAppClientTranslations();

  // Initialize form with the ProjectIterationDTOSchema
  const form = useForm<ProjectIterationDTO>({
    resolver: zodResolver(ProjectIterationDTOSchema),
    defaultValues: {
      projectId,
      name: "",
      description: "",
      startDate: new Date(),
      endDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000), // Default to 2 weeks
      totalTickets: 0,
    },
  });

  // Reset form when dialog opens/closes
  useState(() => {
    if (open) {
      form.reset({
        projectId,
        name: "",
        description: "",
        startDate: new Date(),
        endDate: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000),
        totalTickets: 0,
      });
    }
  });

  const handleSubmit = async (values: ProjectIterationDTO) => {
    setIsSubmitting(true);
    try {
      const createdIteration = await createProjectIteration(values, setError);

      onOpenChange(false);

      if (onSave) {
        onSave(createdIteration);
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
            {t.teams.projects.iteration("create_dialog_title")}
          </DialogTitle>
          <DialogDescription>
            {t.teams.projects.iteration("create_dialog_description")}
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
                  ? t.common.buttons("creating")
                  : t.teams.projects.iteration("form.create_iteration")}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export default CreateIterationDialog;
