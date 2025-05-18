"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import { Breadcrumbs } from "@/components/breadcrumbs";
import { Heading } from "@/components/heading";
import RichTextEditor from "@/components/shared/rich-text-editor";
import { TeamRequestPrioritySelect } from "@/components/teams/team-requests-priority-select";
import TicketChannelSelectField from "@/components/teams/team-ticket-channel-select";
import TeamUserSelectField from "@/components/teams/team-users-select-field";
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
import {
  findRequestById,
  updateTeamRequest,
} from "@/lib/actions/teams-request.action";
import { obfuscate } from "@/lib/endecode";
import { randomPair } from "@/lib/utils";
import { validateForm } from "@/lib/validator";
import { useError } from "@/providers/error-provider";
import {
  TeamRequestDTO,
  TeamRequestDTOSchema,
  TeamRequestPriority,
} from "@/types/team-requests";

export const TeamRequestForm = ({
  teamRequestId,
}: {
  teamRequestId: number;
}) => {
  const router = useRouter();

  const [teamRequest, setTeamRequest] = useState<TeamRequestDTO | undefined>(
    undefined,
  );
  const { setError } = useError();
  const [loading, setLoading] = useState(true);
  const t = useAppClientTranslations();

  const form = useForm<TeamRequestDTO>({
    resolver: zodResolver(TeamRequestDTOSchema),
    defaultValues: undefined,
    mode: "onChange",
  });

  useEffect(() => {
    const fetchTeamRequest = async () => {
      setLoading(true);
      try {
        const data = await findRequestById(teamRequestId, setError);
        setTeamRequest(data);
        console.log("Fetch tickets", data);
      } finally {
        setLoading(false);
      }
    };
    fetchTeamRequest();
  }, [teamRequestId]);

  useEffect(() => {
    if (teamRequest) {
      form.reset(teamRequest);
    } else {
      form.reset(undefined);
    }
  }, [teamRequest]);

  async function onSubmit(formValues: TeamRequestDTO) {
    if (validateForm(formValues, TeamRequestDTOSchema, form)) {
      await updateTeamRequest(formValues.id!, formValues, setError);
      router.push(
        `/portal/teams/${obfuscate(formValues.teamId)}/requests/${obfuscate(
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

  if (!teamRequest) {
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
      title: teamRequest.teamName!,
      link: `/portal/teams/${obfuscate(teamRequest.teamId!)}`,
    },
    ...(teamRequest.projectId
      ? [
          {
            title: t.common.navigation("projects"),
            link: `/portal/teams/${obfuscate(teamRequest.teamId!)}/projects`,
          },
          {
            title: teamRequest.projectName!,
            link: `/portal/teams/${obfuscate(teamRequest.teamId!)}/projects/${obfuscate(teamRequest.projectId!)}`,
          },
          {
            title: teamRequest.requestTitle!,
            link: `/portal/teams/${obfuscate(teamRequest.teamId!)}/projects/${obfuscate(teamRequest.projectId!)}/${obfuscate(teamRequest.id!)}`,
          },
          { title: t.common.buttons("edit"), link: "#" },
        ]
      : [
          {
            title: t.common.navigation("tickets"),
            link: `/portal/teams/${obfuscate(teamRequest.teamId!)}/requests`,
          },
          {
            title: teamRequest.requestTitle!,
            link: `/portal/teams/${obfuscate(teamRequest.teamId!)}/requests/${obfuscate(teamRequest.id!)}`,
          },
          { title: t.common.buttons("edit"), link: "#" },
        ]),
  ];

  return (
    <div className="py-4">
      <Breadcrumbs items={breadcrumbItems} />
      <div className="flex items-center justify-between mb-4 mt-4">
        <Heading
          title={`${teamRequest?.workflowRequestName}: ${t.teams.tickets.form.base("edit_ticket_title")}`}
          description={t.teams.tickets.form.base("edit_ticket_description")}
        />
      </div>

      <Form {...form}>
        <form
          className="grid grid-cols-1 gap-4 sm:grid-cols-2 max-w-[72rem]"
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
            teamId={teamRequest.teamId!}
          />

          <FormField
            control={form.control}
            name="priority"
            render={({ field }) => (
              <FormItem>
                <FormLabel>{t.teams.tickets.form.base("priority")}</FormLabel>
                <FormControl>
                  <TeamRequestPrioritySelect
                    value={field.value || ("Medium" as TeamRequestPriority)}
                    onChange={(value: TeamRequestPriority) =>
                      field.onChange(value)
                    }
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
            workflowId={teamRequest.workflowId!}
            workflowStateId={teamRequest.currentStateId!}
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
