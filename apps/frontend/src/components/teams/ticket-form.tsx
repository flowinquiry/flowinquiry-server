"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import { Breadcrumbs } from "@/components/breadcrumbs";
import { Heading } from "@/components/heading";
import RichTextEditor from "@/components/shared/rich-text-editor";
import TicketChannelSelectField from "@/components/teams/team-ticket-channel-select";
import TeamUserSelectField from "@/components/teams/team-users-select-field";
import { TicketPrioritySelect } from "@/components/teams/ticket-priority-select";
import { Button } from "@/components/ui/button";
import {
  DatePickerField,
  ExtInputField,
  SubmitButton,
} from "@/components/ui/ext-form";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Spinner } from "@/components/ui/spinner";
import WorkflowStateSelectField from "@/components/workflows/workflow-state-select-field";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { findTicketById, updateTicket } from "@/lib/actions/tickets.action";
import { obfuscate } from "@/lib/endecode";
import { randomPair } from "@/lib/utils";
import { validateForm } from "@/lib/validator";
import { useError } from "@/providers/error-provider";
import { TicketDTO, TicketDTOSchema, TicketPriority } from "@/types/tickets";

export const TicketForm = ({ ticketId }: { ticketId: number }) => {
  const router = useRouter();

  const [ticket, setTicket] = useState<TicketDTO | undefined>(undefined);
  const { setError } = useError();
  const [loading, setLoading] = useState(true);
  const t = useAppClientTranslations();

  const form = useForm<TicketDTO>({
    resolver: zodResolver(TicketDTOSchema),
    defaultValues: undefined,
    mode: "onChange",
  });

  useEffect(() => {
    const fetchTicket = async () => {
      setLoading(true);
      try {
        const data = await findTicketById(ticketId, setError);
        setTicket(data);
      } finally {
        setLoading(false);
      }
    };
    fetchTicket();
  }, [ticketId]);

  useEffect(() => {
    if (ticket) {
      form.reset(ticket);
    } else {
      form.reset(undefined);
    }
  }, [ticket]);

  async function onSubmit(formValues: TicketDTO) {
    if (validateForm(formValues, TicketDTOSchema, form)) {
      await updateTicket(formValues.id!, formValues, setError);
      router.push(
        `/portal/teams/${obfuscate(formValues.teamId)}/tickets/${obfuscate(
          formValues.id,
        )}?${randomPair()}`,
      );
    }
  }

  if (loading) {
    return (
      <div className="py-4 flex justify-center items-center">
        <Spinner>{t.common.misc("loading_data")}</Spinner>
      </div>
    );
  }

  if (!ticket) {
    return (
      <div className="py-4">
        <h1 className="text-2xl font-bold">{t.common.misc("error")}</h1>
        <p className="text-red-500 mt-4">Ticket does not exist.</p>
        <div className="mt-4">
          <Button variant="secondary" onClick={() => router.back()}>
            Go Back
          </Button>
        </div>
      </div>
    );
  }

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    { title: t.common.navigation("teams"), link: "/portal/teams" },
    {
      title: ticket.teamName!,
      link: `/portal/teams/${obfuscate(ticket.teamId!)}`,
    },
    ...(ticket.projectId
      ? [
          {
            title: t.common.navigation("projects"),
            link: `/portal/teams/${obfuscate(ticket.teamId!)}/projects`,
          },
          {
            title: ticket.projectName!,
            link: `/portal/teams/${obfuscate(ticket.teamId!)}/projects/${obfuscate(ticket.projectId!)}`,
          },
          {
            title: ticket.requestTitle!,
            link: `/portal/teams/${obfuscate(ticket.teamId!)}/projects/${obfuscate(ticket.projectId!)}/${obfuscate(ticket.id!)}`,
          },
          { title: t.common.buttons("edit"), link: "#" },
        ]
      : [
          {
            title: t.common.navigation("tickets"),
            link: `/portal/teams/${obfuscate(ticket.teamId!)}/tickets`,
          },
          {
            title: ticket.requestTitle!,
            link: `/portal/teams/${obfuscate(ticket.teamId!)}/tickets/${obfuscate(ticket.id!)}`,
          },
          { title: t.common.buttons("edit"), link: "#" },
        ]),
  ];

  return (
    <div className="py-4">
      <Breadcrumbs items={breadcrumbItems} />
      <div className="flex items-center justify-between mb-4 mt-4">
        <Heading
          title={`${ticket?.workflowRequestName}: ${t.teams.tickets.form.base("edit_ticket_title")}`}
          description={t.teams.tickets.form.base("edit_ticket_description")}
        />
      </div>

      <Form {...form}>
        <form
          className="grid grid-cols-1 gap-4 sm:grid-cols-2 max-w-6xl"
          onSubmit={form.handleSubmit(onSubmit)}
        >
          <div className="col-span-1 sm:col-span-2">
            <ExtInputField
              form={form}
              fieldName="requestTitle"
              label={t.teams.tickets.form.base("name")}
              required
            />
          </div>

          <div className="col-span-1 sm:col-span-2">
            <FormField
              control={form.control}
              name="requestDescription"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>
                    {t.teams.tickets.form.base("description")}{" "}
                    <span className="text-destructive"> *</span>
                  </FormLabel>
                  <FormControl>
                    <RichTextEditor
                      value={field.value}
                      onChange={field.onChange}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </div>

          <TeamUserSelectField
            form={form}
            fieldName="assignUserId"
            label={t.teams.tickets.form.base("assignee")}
            teamId={ticket.teamId!}
          />

          <FormField
            control={form.control}
            name="priority"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t.teams.tickets.form.base("priority")}</FormLabel>
                <FormControl>
                  <TicketPrioritySelect
                    value={field.value || ("Medium" as TicketPriority)}
                    onChange={(value: TicketPriority) => field.onChange(value)}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <DatePickerField
            form={form}
            fieldName="estimatedCompletionDate"
            label={t.teams.tickets.form.base("target_completion_date")}
            placeholder={t.common.misc("date_select_place_holder")}
          />

          <DatePickerField
            form={form}
            fieldName="actualCompletionDate"
            label={t.teams.tickets.form.base("actual_completion_date")}
            placeholder={t.common.misc("date_select_place_holder")}
          />

          <TicketChannelSelectField form={form} />
          <WorkflowStateSelectField
            form={form}
            name="currentStateId"
            label={t.teams.tickets.form.base("state")}
            required
            workflowId={ticket.workflowId!}
            workflowStateId={ticket.currentStateId!}
            includeSelf
          />

          <div className="col-span-1 sm:col-span-2 flex flex-row gap-4">
            <SubmitButton
              label={t.common.buttons("save")}
              labelWhileLoading={t.common.buttons("saving")}
            />
            <Button variant="secondary" onClick={() => router.back()}>
              {t.common.buttons("discard")}
            </Button>
          </div>
        </form>
      </Form>
    </div>
  );
};
