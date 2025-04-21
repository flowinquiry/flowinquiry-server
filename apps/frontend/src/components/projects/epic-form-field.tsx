import React, { useEffect,useState } from "react";

import {
  FormControl,
  FormDescription,
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
import { useAppClientTranslations } from "@/hooks/use-translations";
import { findEpicsByProjectId } from "@/lib/actions/project-epic.action";
import { useError } from "@/providers/error-provider";
import { ProjectEpicDTO } from "@/types/projects";

interface EpicFormFieldProps {
  form: any;
  projectId: number;
  name: string;
  label?: string;
  description?: string;
  required?: boolean;
}

export function EpicFormField({
  form,
  projectId,
  name,
  label = "Epic",
  description,
  required = false,
}: EpicFormFieldProps) {
  const t = useAppClientTranslations();
  const [epics, setEpics] = useState<ProjectEpicDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const { setError } = useError();

  useEffect(() => {
    async function loadEpics() {
      if (!projectId) return;

      setLoading(true);

      try {
        const epicsList = await findEpicsByProjectId(projectId, setError);
        setEpics(epicsList || []);
      } finally {
        setLoading(false);
      }
    }

    loadEpics();
  }, [projectId]);

  return (
    <FormField
      control={form.control}
      name={name}
      render={({ field }) => (
        <FormItem>
          <FormLabel>
            {label}
            {required && <span className="text-destructive"> *</span>}
          </FormLabel>
          <FormControl>
            <Select
              disabled={loading || epics.length === 0}
              onValueChange={field.onChange}
              value={field.value?.toString()}
              defaultValue={field.value?.toString()}
            >
              <SelectTrigger className="w-full">
                <SelectValue
                  placeholder={
                    loading ? t.common.misc("loading_data") : "Select an epic"
                  }
                />
              </SelectTrigger>
              <SelectContent>
                {epics.map((epic) => (
                  <SelectItem key={epic.id} value={epic.id!.toString()}>
                    {epic.name}
                  </SelectItem>
                ))}
                {epics.length === 0 && !loading && (
                  <SelectItem value="none" disabled>
                    No epics found for this project
                  </SelectItem>
                )}
              </SelectContent>
            </Select>
          </FormControl>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  );
}
