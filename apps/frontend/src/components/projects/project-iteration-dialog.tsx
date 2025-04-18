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
  FormDescription,
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
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Create New Iteration</DialogTitle>
          <DialogDescription>
            Add a new iteration to organize your tasks into sprints or phases.
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
                  <FormLabel>Name</FormLabel>
                  <FormControl>
                    <Input placeholder="Sprint 1" {...field} />
                  </FormControl>
                  <FormDescription>
                    A short, descriptive name for this iteration.
                  </FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              <DatePickerField
                form={form}
                fieldName="startDate"
                label="Start date"
                placeholder={t.common.misc("date_select_place_holder")}
              />

              <DatePickerField
                form={form}
                fieldName="endDate"
                label="End date"
                placeholder={t.common.misc("date_select_place_holder")}
              />
            </div>

            <FormField
              control={form.control}
              name="description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Description</FormLabel>
                  <FormControl>
                    <Textarea
                      placeholder="Brief description of this iteration's goals"
                      {...field}
                      rows={3}
                    />
                  </FormControl>
                  <FormDescription>
                    Optional: Provide additional context for this iteration.
                  </FormDescription>
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
                Cancel
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Creating..." : "Create Iteration"}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}

export default CreateIterationDialog;
