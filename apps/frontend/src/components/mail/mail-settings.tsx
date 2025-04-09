"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { EyeIcon, EyeOffIcon } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { Button } from "@/components/ui/button";
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useToast } from "@/hooks/use-toast";

const emailSettingsSchema = z.object({
  "mail.host": z.string().min(1, "SMTP host is required"),
  "mail.port": z.string().min(1, "SMTP port is required"),
  "mail.username": z.string(),
  "mail.password": z.string(),
  "mail.protocol": z.string(),
  "mail.from": z.string(),
  "mail.fromName": z.string(),
  "mail.smtp.auth": z.enum(["true", "false"]),
  "mail.smtp.starttls.enable": z.enum(["true", "false"]),
  "mail.smtp.ssl.enable": z.enum(["true", "false"]),
  "mail.debug": z.enum(["true", "false"]),
});

const FIELD_META: Record<
  keyof z.infer<typeof emailSettingsSchema>,
  {
    label: string;
    type: "string" | "boolean" | "password";
    description?: string;
  }
> = {
  "mail.host": { label: "SMTP Host", type: "string" },
  "mail.port": { label: "SMTP Port", type: "string" },
  "mail.username": { label: "Username", type: "string" },
  "mail.password": {
    label: "Password",
    type: "password",
    description: "Encrypted using secret:aes256",
  },
  "mail.protocol": { label: "Protocol", type: "string" },
  "mail.from": { label: "From Address", type: "string" },
  "mail.fromName": { label: "Sender Name", type: "string" },
  "mail.smtp.auth": { label: "SMTP Auth", type: "boolean" },
  "mail.smtp.starttls.enable": { label: "STARTTLS", type: "boolean" },
  "mail.smtp.ssl.enable": { label: "SSL", type: "boolean" },
  "mail.debug": { label: "Debug Logging", type: "boolean" },
};

const FIELD_GROUPS: Record<string, (keyof typeof FIELD_META)[]> = {
  "SMTP Server": ["mail.host", "mail.port", "mail.protocol"],
  Authentication: ["mail.username", "mail.password", "mail.smtp.auth"],
  "Sender Info": ["mail.from", "mail.fromName"],
  "Advanced Options": [
    "mail.smtp.starttls.enable",
    "mail.smtp.ssl.enable",
    "mail.debug",
  ],
};

export function MailSettings() {
  const form = useForm<z.infer<typeof emailSettingsSchema>>({
    resolver: zodResolver(emailSettingsSchema),
    defaultValues: {
      "mail.host": "",
      "mail.port": "",
      "mail.username": "",
      "mail.password": "",
      "mail.protocol": "smtp",
      "mail.from": "",
      "mail.fromName": "",
      "mail.smtp.auth": "true",
      "mail.smtp.starttls.enable": "true",
      "mail.smtp.ssl.enable": "false",
      "mail.debug": "false",
    },
  });

  const { toast } = useToast();
  const [loading, setLoading] = useState(true);
  const [showPassword, setShowPassword] = useState(false);

  useEffect(() => {
    axios
      .get("/api/settings?group=email")
      .then((res) => {
        const settings: Record<string, string> = {};
        res.data.forEach((setting: any) => {
          settings[setting.key] = setting.value;
        });
        form.reset(settings);
      })
      .finally(() => setLoading(false));
  }, [form]);

  const onSubmit = async (values: z.infer<typeof emailSettingsSchema>) => {
    const payload = Object.entries(values).map(([key, value]) => ({
      key,
      value,
      type: key === "mail.password" ? "secret:aes256" : "string",
      group: "email",
      description:
        FIELD_META[key as keyof typeof FIELD_META]?.description ?? "",
    }));

    try {
      await axios.put("/api/settings", payload);
    } catch (err) {}
  };

  if (loading)
    return <p className="text-muted-foreground text-sm">Loading settings...</p>;

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(onSubmit)}
        className="space-y-6 max-w-xl"
      >
        {Object.entries(FIELD_GROUPS).map(([groupLabel, keys]) => (
          <div key={groupLabel} className="space-y-4 border p-4 rounded-lg">
            <h3 className="text-lg font-semibold mb-2">{groupLabel}</h3>
            {keys.map((key) => {
              const meta = FIELD_META[key];
              return (
                <FormField
                  key={key}
                  control={form.control}
                  name={key as keyof EmailSettingsFormValues}
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>{meta.label}</FormLabel>
                      <FormControl>
                        {meta.type === "boolean" ? (
                          <Select
                            value={field.value}
                            onValueChange={(val) => field.onChange(val)}
                          >
                            <SelectTrigger>
                              <SelectValue placeholder="Select true or false" />
                            </SelectTrigger>
                            <SelectContent>
                              <SelectItem value="true">True</SelectItem>
                              <SelectItem value="false">False</SelectItem>
                            </SelectContent>
                          </Select>
                        ) : meta.type === "password" ? (
                          <div className="relative">
                            <Input
                              type={showPassword ? "text" : "password"}
                              {...field}
                              className="pr-10"
                            />
                            <button
                              type="button"
                              onClick={() => setShowPassword((v) => !v)}
                              className="absolute right-2 top-2 text-muted-foreground hover:text-foreground"
                            >
                              {showPassword ? (
                                <EyeOffIcon className="w-4 h-4" />
                              ) : (
                                <EyeIcon className="w-4 h-4" />
                              )}
                            </button>
                          </div>
                        ) : (
                          <Input {...field} />
                        )}
                      </FormControl>
                      {meta.description && (
                        <FormDescription>{meta.description}</FormDescription>
                      )}
                      <FormMessage />
                    </FormItem>
                  )}
                />
              );
            })}
          </div>
        ))}
        <Button type="submit">Save Settings</Button>
      </form>
    </Form>
  );
}
