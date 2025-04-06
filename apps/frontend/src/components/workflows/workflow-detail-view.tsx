"use client";

import { Edit, Trash } from "lucide-react";
import { useRouter } from "next/navigation";
import React, { useEffect, useState } from "react";

import { Breadcrumbs } from "@/components/breadcrumbs";
import { Heading } from "@/components/heading";
import { Button } from "@/components/ui/button";
import { Spinner } from "@/components/ui/spinner";
import { WorkflowDiagram } from "@/components/workflows/workflow-diagram-view";
import WorkflowEditForm from "@/components/workflows/workflow-editor-form";
import { usePagePermission } from "@/hooks/use-page-permission";
import { useAppClientTranslations } from "@/hooks/use-translations";
import {
  deleteWorkflow,
  getWorkflowDetail,
  updateWorkflowDetail,
} from "@/lib/actions/workflows.action";
import { useError } from "@/providers/error-provider";
import { PermissionUtils } from "@/types/resources";
import { WorkflowDetailDTO } from "@/types/workflows";

const GlobalWorkflowDetailView = ({ workflowId }: { workflowId: number }) => {
  const router = useRouter();
  const t = useAppClientTranslations();

  const permissionLevel = usePagePermission();
  const [workflowDetail, setWorkflowDetail] =
    useState<WorkflowDetailDTO | null>(null);
  const [previewWorkflowDetail, setPreviewWorkflowDetail] =
    useState<WorkflowDetailDTO | null>(null); // Separate state for preview
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const { setError } = useError();

  useEffect(() => {
    async function fetchWorkflowDetail() {
      setLoading(true);
      getWorkflowDetail(workflowId, setError)
        .then((data) => {
          setWorkflowDetail(data);
          setPreviewWorkflowDetail(data); // Initialize preview with the original workflow
        })
        .finally(() => setLoading(false));
    }

    fetchWorkflowDetail();
  }, [workflowId]);

  const handleSave = (updatedWorkflow: WorkflowDetailDTO) => {
    updateWorkflowDetail(updatedWorkflow.id!, updatedWorkflow, setError).then(
      (data) => {
        setWorkflowDetail(data); // Update the main workflow detail
        setPreviewWorkflowDetail(data); // Sync preview with saved workflow
        setIsEditing(false);
      },
    );
  };

  const removeWorkflow = async (workflow: WorkflowDetailDTO) => {
    await deleteWorkflow(workflow.id!, setError);
    router.push("/portal/settings/workflows");
  };

  if (!workflowDetail) {
    return <div>Error loading workflow detail.</div>;
  }

  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    {
      title: t.common.navigation("workflows"),
      link: `/portal/settings/workflows`,
    },
    { title: workflowDetail.name, link: "#" },
  ];

  return (
    <div className="grid grid-cols-1 gap-4">
      <Breadcrumbs items={breadcrumbItems} />
      {/* Header Section */}
      <div className="flex flex-col gap-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <Heading
              title={workflowDetail.name}
              description={workflowDetail.description ?? ""}
            />
          </div>
        </div>

        <div className="text-sm text-muted-foreground">
          Ticket type: {workflowDetail.requestName}
        </div>
        {/* Buttons Row */}
        <div className="flex justify-end space-x-4">
          {PermissionUtils.canWrite(permissionLevel) && (
            <Button onClick={() => setIsEditing(!isEditing)}>
              {isEditing ? "Cancel Edit" : <Edit />} Customize Workflow
            </Button>
          )}
          {PermissionUtils.canAccess(permissionLevel) &&
            !workflowDetail.useForProject && (
              <Button
                variant="destructive"
                onClick={() => removeWorkflow(workflowDetail)}
              >
                <Trash /> Delete
              </Button>
            )}
        </div>
      </div>

      {/* Spinner When Loading */}
      {loading && (
        <div className="flex items-center justify-center py-4">
          <Spinner>
            <span>{t.common.misc("loading_data")}</span>
          </Spinner>
        </div>
      )}

      {/* Workflow Editor Form */}
      {isEditing && workflowDetail && !loading && (
        <WorkflowEditForm
          workflowDetail={workflowDetail}
          onCancel={() => setIsEditing(false)}
          onSave={handleSave}
          onPreviewChange={setPreviewWorkflowDetail} // Update preview in real-time
        />
      )}

      {/* Workflow Diagram */}
      {previewWorkflowDetail && !loading && (
        <div className="w-full">
          <WorkflowDiagram workflowDetails={previewWorkflowDetail} />
        </div>
      )}
    </div>
  );
};

export default GlobalWorkflowDetailView;
