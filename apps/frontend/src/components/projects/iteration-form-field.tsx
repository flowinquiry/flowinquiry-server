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
import { findIterationsByProjectId } from "@/lib/actions/project-iteration.action";
import { useError } from "@/providers/error-provider";
import { ProjectIterationDTO } from "@/types/projects";

interface IterationFormFieldProps {
  form: any;
  projectId: number;
  name: string;
  label?: string;
  description?: string;
  required?: boolean;
}

export function IterationFormField({
  form,
  projectId,
  name,
  label = "Iteration",
  description,
  required = false,
}: IterationFormFieldProps) {
  const [iterations, setIterations] = useState<ProjectIterationDTO[]>([]);
  const [loading, setLoading] = useState(false);
  const { setError } = useError();

  useEffect(() => {
    async function loadIterations() {
      if (!projectId) return;

      setLoading(true);

      try {
        const iterationsList = await findIterationsByProjectId(
          projectId,
          setError,
        );
        setIterations(iterationsList || []);
      } finally {
        setLoading(false);
      }
    }

    loadIterations();
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
              disabled={loading || iterations.length === 0}
              onValueChange={field.onChange}
              value={field.value?.toString()}
              defaultValue={field.value?.toString()}
            >
              <SelectTrigger className="w-full">
                <SelectValue
                  placeholder={
                    loading ? "Loading iterations..." : "Select an iteration"
                  }
                />
              </SelectTrigger>
              <SelectContent>
                {iterations.map((iteration) => (
                  <SelectItem
                    key={iteration.id}
                    value={iteration.id!.toString()}
                  >
                    {iteration.name}
                  </SelectItem>
                ))}
                {iterations.length === 0 && !loading && (
                  <SelectItem value="none" disabled>
                    No iterations found for this project
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
