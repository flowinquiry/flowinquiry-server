"use client";

import { GlobeIcon } from "lucide-react";
import { useTransition } from "react";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Locale } from "@/i18n/config";
import { setLocale } from "@/lib/actions/users.action";
import { useError } from "@/providers/error-provider";

type Props = {
  defaultValue: string;
  items: Array<{ value: string; label: string }>;
  label: string;
};

export default function LocaleSwitcherSelect({
  defaultValue,
  items,
  label,
}: Props) {
  const [isPending, startTransition] = useTransition();
  const { setError } = useError();

  function onChange(value: string) {
    const locale = value as Locale;
    startTransition(() => {
      setLocale(locale as string, setError);
    });
  }

  return (
    <Select defaultValue={defaultValue} onValueChange={onChange}>
      <SelectTrigger
        aria-label={label}
        disabled={isPending}
        className="w-[5rem]"
      >
        <GlobeIcon className="h-4 w-4" />
        <SelectValue className="sr-only">{label}</SelectValue>
      </SelectTrigger>
      <SelectContent align="end" className="w-[120px]">
        {items.map((item) => (
          <SelectItem key={item.value} value={item.value}>
            {item.label}
          </SelectItem>
        ))}
      </SelectContent>
    </Select>
  );
}
