"use client";

import { format } from "date-fns";
import { CalendarIcon } from "lucide-react";
import React from "react";

import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import {
  FormControl,
  FormDescription,
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
import { cn } from "@/lib/utils";
import { UiAttributes } from "@/types/ui-components";

export interface ExtInputProps {
  form: any;
  fieldName: string;
  label: string;
  placeholder?: string;
  type?: string;
  onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void;
}

export interface FormProps<Entity> {
  initialData: Entity | undefined;
}

export interface ViewProps<DValue> {
  entity: DValue;
}

export const ExtInputField = ({
  form,
  fieldName,
  label,
  placeholder,
  required = false,
  type = undefined,
  onChange,
  className = "",
  ...props
}: ExtInputProps & UiAttributes) => {
  return (
    <FormField
      control={form.control}
      name={fieldName}
      render={({ field }) => (
        <FormItem>
          <FormLabel>
            {label}
            {required && <span className="text-destructive"> *</span>}
          </FormLabel>
          <FormControl>
            {
              <Input
                type={type}
                placeholder={placeholder}
                {...field}
                className={className} // Apply custom classes here
              />
            }
          </FormControl>
          <FormMessage />
        </FormItem>
      )}
    />
  );
};

export const ExtTextAreaField = ({
  form,
  fieldName,
  label,
  placeholder,
  required,
}: ExtInputProps & UiAttributes) => {
  return (
    <div className="md:col-span-2">
      <FormField
        control={form.control}
        name={fieldName}
        render={({ field }) => (
          <FormItem>
            <FormLabel>
              {label}
              {required && <span className="text-destructive"> *</span>}
            </FormLabel>
            <FormControl>
              <Textarea placeholder={placeholder} {...field} />
            </FormControl>
            <FormMessage />
          </FormItem>
        )}
      />
    </div>
  );
};

interface SubmitButtonProps {
  label: string;
  labelWhileLoading: string;
}

export const SubmitButton = ({
  label,
  labelWhileLoading,
}: SubmitButtonProps) => {
  return <Button type="submit">{label}</Button>;
};

type DatePickerFieldProps = {
  form: any;
  fieldName: string;
  label: string;
  description?: string;
  placeholder?: string;
  allowPastDates?: boolean;
};

export const DatePickerField: React.FC<
  DatePickerFieldProps & { required?: boolean }
> = ({
  form,
  fieldName,
  label,
  description,
  placeholder = "Pick a date",
  allowPastDates = false,
  required = false,
}) => {
  const clearText = useAppClientTranslations().common.buttons("clear");
  return (
    <FormField
      control={form.control}
      name={fieldName}
      render={({ field }) => (
        <FormItem className="flex flex-col">
          <FormLabel>
            {label}
            {required && <span className="text-destructive"> *</span>}
          </FormLabel>
          <div className="flex items-center space-x-2">
            <Popover>
              <PopoverTrigger asChild>
                <FormControl>
                  <Button
                    variant={"outline"}
                    className={cn(
                      "w-[240px] pl-3 text-left font-normal",
                      !field.value && "text-muted-foreground",
                    )}
                  >
                    {field.value ? (
                      format(field.value, "PPP")
                    ) : (
                      <span>{placeholder}</span>
                    )}
                    <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                  </Button>
                </FormControl>
              </PopoverTrigger>
              <PopoverContent className="w-auto p-0" align="start">
                <Calendar
                  mode="single"
                  selected={field.value || undefined}
                  onSelect={field.onChange}
                  disabled={(date) => {
                    const today = new Date();
                    today.setHours(0, 0, 0, 0);
                    if (allowPastDates) {
                      return date > today;
                    } else {
                      return date < today;
                    }
                  }}
                  initialFocus
                />
              </PopoverContent>
            </Popover>
            {!required && field.value && (
              <Button
                variant="ghost"
                size="sm"
                onClick={() => field.onChange(undefined)}
              >
                {clearText}
              </Button>
            )}
          </div>
          {description && <FormDescription>{description}</FormDescription>}
          <FormMessage />
        </FormItem>
      )}
    />
  );
};
