"use client";

import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { FormField, FormItem } from "@/components/ui/form";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";

interface WorkflowStatesSelectProps {
  fieldName: string;
  form: any;
  label: string;
  options: { label: string; value: string | number }[];
  placeholder?: string;
  required?: boolean;
}

const WorkflowStatesSelectField = ({
  fieldName,
  form,
  label,
  options,
  placeholder = "Select a state",
  required = false,
}: WorkflowStatesSelectProps) => {
  return (
    <FormField
      control={form.control}
      name={fieldName}
      render={({ field }) => {
        const selectedOption = options.find(
          (option) => option.value === field.value,
        );

        const displayText = selectedOption?.label || placeholder;
        const isTextLong = displayText.length > 30; // Adjust threshold as needed

        const DropdownButton = (
          <Button
            variant="outline"
            className="w-full text-left justify-start min-w-0"
          >
            <span className="truncate block">{displayText}</span>
          </Button>
        );

        return (
          <FormItem className="grid grid-cols-1">
            <label className="text-sm font-medium">{label}</label>
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                {isTextLong ? (
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger asChild>{DropdownButton}</TooltipTrigger>
                      <TooltipContent>
                        <p>{displayText}</p>
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                ) : (
                  DropdownButton
                )}
              </DropdownMenuTrigger>
              <DropdownMenuContent className="w-full min-w-[var(--radix-dropdown-menu-trigger-width)]">
                {options.map((option) => (
                  <DropdownMenuItem
                    key={option.value}
                    onClick={() => field.onChange(option.value)}
                    className="cursor-pointer"
                  >
                    <span className="truncate" title={option.label}>
                      {option.label}
                    </span>
                  </DropdownMenuItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>
            {form.formState.errors[fieldName]?.message && (
              <p className="text-sm text-red-500">
                {form.formState.errors[fieldName].message}
              </p>
            )}
          </FormItem>
        );
      }}
    />
  );
};

export default WorkflowStatesSelectField;
