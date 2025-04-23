"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";
import { useForm } from "react-hook-form";

import { Breadcrumbs } from "@/components/breadcrumbs";
import { Heading } from "@/components/heading";
import { ImageCropper } from "@/components/image-cropper";
import { TeamAvatar } from "@/components/shared/avatar-display";
import { Button } from "@/components/ui/button";
import {
  ExtInputField,
  ExtTextAreaField,
  SubmitButton,
} from "@/components/ui/ext-form";
import { Form } from "@/components/ui/form";
import { Separator } from "@/components/ui/separator";
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { useImageCropper } from "@/hooks/use-image-cropper";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  createTeam,
  findTeamById,
  updateTeam,
} from "@/lib/actions/teams.action";
import { obfuscate } from "@/lib/endecode";
import { validateForm } from "@/lib/validator";
import { useError } from "@/providers/error-provider";
import { TeamDTO, TeamDTOSchema } from "@/types/teams";

export const TeamForm = ({ teamId }: { teamId: number | undefined }) => {
  const router = useRouter();

  const {
    selectedFile,
    setSelectedFile,
    isDialogOpen,
    setDialogOpen,
    getRootProps,
    getInputProps,
  } = useImageCropper();

  const [team, setTeam] = useState<TeamDTO | undefined>(undefined);
  const [loading, setLoading] = useState(true);
  const { setError } = useError();
  const t = useAppClientTranslations();

  const form = useForm<TeamDTO>({
    resolver: zodResolver(TeamDTOSchema),
    defaultValues: undefined,
  });

  useEffect(() => {
    const fetchTeam = async () => {
      setLoading(true);
      try {
        if (teamId) {
          const data = await findTeamById(teamId, setError);
          if (!data) {
            throw new Error("Team not found.");
          }
          setTeam(data);
          form.reset(data); // ✅ set form values after fetch
        } else {
          setTeam(undefined);
          form.reset();
        }
      } finally {
        setLoading(false);
      }
    };

    fetchTeam();
  }, [teamId]);

  // When team data is loaded, reset form with fetched data
  useEffect(() => {
    if (team) {
      form.reset(team);
    } else {
      // If no team is fetched (e.g. creation mode), reset with defaults
      form.reset(undefined);
    }
  }, [team]);

  async function onSubmit(formValues: TeamDTO) {
    if (validateForm(formValues, TeamDTOSchema, form)) {
      const formData = new FormData();
      const teamJsonBlob = new Blob([JSON.stringify(formValues)], {
        type: "application/json",
      });
      formData.append("teamDTO", teamJsonBlob);

      if (selectedFile) {
        formData.append("file", selectedFile);
      }

      let redirectTeamId;
      if (formValues.id) {
        // Edit mode
        redirectTeamId = formValues.id;
        await updateTeam(formData, setError);
      } else {
        // Create mode
        await createTeam(formData, setError).then(
          (data) => (redirectTeamId = data.id),
        );
      }

      router.push(`/portal/teams/${obfuscate(redirectTeamId)}/dashboard`);
    }
  }

  const isEdit = !!team;
  const title = isEdit ? `Edit team ${team?.name}` : "Create team";
  const description = isEdit ? "Edit team" : "Add a new team";
  const submitText = isEdit ? "Save changes" : "Create";
  const submitTextWhileLoading = isEdit ? "Saving changes ..." : "Creating ...";

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    { title: t.common.navigation("teams"), link: "/portal/teams" },
    ...(team
      ? [
          {
            title: `${team.name}`,
            link: `/portal/teams/${obfuscate(team.id)}`,
          },
          { title: t.common.buttons("edit"), link: "#" },
        ]
      : [{ title: t.common.buttons("add"), link: "#" }]),
  ];

  if (loading) {
    return (
      <div className="py-4 flex justify-center items-center">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-4">
      <Breadcrumbs items={breadcrumbItems} />
      <div className="flex items-center justify-between">
        <Heading title={title} description={description} />
      </div>
      <Separator />
      <div className="flex gap-4 py-4 flex-col md:flex-row">
        <div>
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger asChild>
                {selectedFile ? (
                  <ImageCropper
                    dialogOpen={isDialogOpen}
                    setDialogOpen={setDialogOpen}
                    selectedFile={selectedFile}
                    setSelectedFile={setSelectedFile}
                  />
                ) : (
                  <>
                    <input {...getInputProps()} />
                    <TeamAvatar
                      {...getRootProps()}
                      imageUrl={team?.logoUrl}
                      size="w-36 h-36"
                      className="cursor-pointer ring-offset-2 ring-2 ring-slate-200"
                    />
                  </>
                )}
              </TooltipTrigger>
              <TooltipContent>
                <p>Update Team Logo</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        </div>
        <Form {...form}>
          <form
            className="grid grid-cols-1 gap-4 w-[28rem]"
            onSubmit={form.handleSubmit(onSubmit)}
          >
            <ExtInputField
              form={form}
              required
              fieldName="name"
              label="Name"
              placeholder="Team Name"
            />
            <ExtTextAreaField form={form} fieldName="slogan" label="Slogan" />
            <ExtTextAreaField
              form={form}
              fieldName="description"
              label="Description"
            />
            <div className="md:col-span-2 flex flex-row gap-4">
              <SubmitButton
                label={submitText}
                labelWhileLoading={submitTextWhileLoading}
              />
              <Button
                variant="secondary"
                type="button"
                onClick={() => router.back()}
              >
                {t.common.buttons("discard")}
              </Button>
            </div>
          </form>
        </Form>
      </div>
    </div>
  );
};
