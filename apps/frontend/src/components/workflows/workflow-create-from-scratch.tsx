"use client";

import { useRouter } from "next/navigation";
import React, { useState } from "react";

import { WorkflowDiagram } from "@/components/workflows/workflow-diagram-view";
import WorkflowEditForm from "@/components/workflows/workflow-editor-form";
import { saveWorkflowDetail } from "@/lib/actions/workflows.action";
import { obfuscate } from "@/lib/endecode";
import { useError } from "@/providers/error-provider";
import { WorkflowDetailDTO } from "@/types/workflows";

const defaultWorkflow: WorkflowDetailDTO = {
  id: undefined,
  requestName: "",
  name: "",
  description: "",
  states: [],
  transitions: [],
  ownerId: null,
  ownerName: "",
};

const NewWorkflowFromScratch = ({
  teamId = undefined,
}: {
  teamId?: number;
}) => {
  const [workflowDetail, setWorkflowDetail] =
    useState<WorkflowDetailDTO>(defaultWorkflow);
  const [previewWorkflowDetail, setPreviewWorkflowDetail] =
    useState<WorkflowDetailDTO>(defaultWorkflow);
  const router = useRouter();
  const { setError } = useError();

  const handleSave = async (updatedWorkflow: WorkflowDetailDTO) => {
    // Ensure the team ID is correctly assigned to the workflow
    const workflowToSave = {
      ...updatedWorkflow,
      visibility: teamId
        ? ("PRIVATE" as "PRIVATE" | "PUBLIC" | "TEAM")
        : ("PUBLIC" as "PRIVATE" | "PUBLIC" | "TEAM"),
      ownerId: teamId,
    };

    const workflow = await saveWorkflowDetail(workflowToSave, setError);

    if (workflow?.id) {
      if (teamId) {
        router.push(
          `/portal/teams/${obfuscate(teamId)}/workflows/${obfuscate(workflow.id)}`,
        );
      } else {
        router.push(`/portal/settings/workflows/${obfuscate(workflow.id)}`);
      }
    } else {
      console.error("Workflow save failed: Missing workflow ID.");
    }
  };

  const handleCancel = () => {
    router.push(`/portal/teams/${obfuscate(teamId)}/workflows`);
  };

  return (
    <div className="flex flex-col gap-6">
      {/* Workflow Form */}
      <div className="border p-4 rounded shadow-xs">
        <WorkflowEditForm
          workflowDetail={workflowDetail}
          onCancel={handleCancel}
          onSave={handleSave}
          onPreviewChange={setPreviewWorkflowDetail}
        />
      </div>

      {/* Workflow Preview */}
      <div className="border p-4 rounded shadow-xs">
        <WorkflowDiagram workflowDetails={previewWorkflowDetail} />
      </div>
    </div>
  );
};

export default NewWorkflowFromScratch;
