"use client";

import React from "react";

import { Breadcrumbs } from "@/components/breadcrumbs";
import { Heading } from "@/components/heading";
import NewWorkflowFromScratch from "@/components/workflows/workflow-create-from-scratch";
import { useAppClientTranslations } from "@/hooks/use-translations";

const WorkflowNew = () => {
  const t = useAppClientTranslations();
  const breadcrumbItems = [
    { title: t.common.navigation("dashboard"), link: "/portal" },
    {
      title: t.common.navigation("workflows"),
      link: `/portal/settings/workflows`,
    },
    { title: t.common.buttons("create"), link: "#" },
  ];

  return (
    <div className="grid grid-cols-1 gap-4">
      <Breadcrumbs items={breadcrumbItems} />
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Heading
            title="Create New Workflow"
            description="Select how you want to create the workflow"
          />
        </div>
      </div>
      <NewWorkflowFromScratch />
    </div>
  );
};

export default WorkflowNew;
