"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import React, { useEffect, useRef } from "react";
import { useFieldArray, useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { ExtInputField, ExtTextAreaField } from "@/components/ui/ext-form";
import { Form } from "@/components/ui/form";
import WorkflowStatesSelectField from "@/components/workflows/workflow-states-select-field";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { WorkflowDetailDTO, WorkflowDetailSchema } from "@/types/workflows";

let temporaryIdCounter = -1;

const WorkflowEditForm = ({
  workflowDetail,
  onCancel,
  onSave,
  onPreviewChange,
}: {
  workflowDetail: WorkflowDetailDTO;
  onCancel: () => void;
  onSave: (values: WorkflowDetailDTO) => void;
  onPreviewChange: (values: WorkflowDetailDTO) => void; // New prop for live preview
}) => {
  const t = useAppClientTranslations();
  const form = useForm<WorkflowDetailDTO>({
    resolver: zodResolver(WorkflowDetailSchema),
    defaultValues: workflowDetail,
    mode: "onChange",
  });

  const {
    fields: stateFields,
    append: appendState,
    remove: removeState,
  } = useFieldArray({ control: form.control, name: "states" });

  const {
    fields: transitionFields,
    append: appendTransition,
    remove: removeTransition,
  } = useFieldArray({ control: form.control, name: "transitions" });

  const watchedValues = form.watch();

  // Debounce preview updates
  const debounceRef = useRef<NodeJS.Timeout | null>(null);

  useEffect(() => {
    if (debounceRef.current) {
      clearTimeout(debounceRef.current);
    }
    debounceRef.current = setTimeout(() => {
      onPreviewChange(watchedValues); // Update the preview with debounced values
    }, 300);
    return () => {
      if (debounceRef.current) clearTimeout(debounceRef.current);
    };
  }, [watchedValues, onPreviewChange]);

  const handleSubmit = (values: WorkflowDetailDTO) => {
    onSave(values);
  };

  return (
    <>
      <h2 className="text-lg font-bold mb-4">
        {t.workflows.add("edit_workflow")}
      </h2>
      <Form {...form}>
        <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
          <div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <ExtInputField
                form={form}
                fieldName="name"
                label={t.workflows.add("name")}
                required
              />
              <ExtInputField
                form={form}
                fieldName="requestName"
                label={t.workflows.add("ticket_type")}
                required
              />
            </div>
            <ExtTextAreaField
              form={form}
              fieldName="description"
              label={t.workflows.add("field_description")}
            />
          </div>
          {/* States Section */}
          <div>
            <h3 className="text-md font-semibold mb-4">
              {t.workflows.add("states")}
            </h3>
            {stateFields.map((state, index) => (
              <div
                key={state.id || index}
                className="flex items-center gap-4 mb-4"
              >
                <div className="flex-1">
                  <ExtInputField
                    form={form}
                    fieldName={`states.${index}.stateName`}
                    label={t.workflows.add("state_name")}
                    required
                  />
                </div>
                <div className="flex items-center gap-2">
                  <label className="text-sm">
                    {t.workflows.add("initial")}
                  </label>
                  <Checkbox
                    checked={form.watch(`states.${index}.isInitial`)}
                    onCheckedChange={(value) =>
                      form.setValue(`states.${index}.isInitial`, Boolean(value))
                    }
                  />
                </div>
                <div className="flex items-center gap-2">
                  <label className="text-sm">{t.workflows.add("final")}</label>
                  <Checkbox
                    checked={form.watch(`states.${index}.isFinal`)}
                    onCheckedChange={(value) =>
                      form.setValue(`states.${index}.isFinal`, Boolean(value))
                    }
                  />
                </div>
                <Button
                  variant="destructive"
                  onClick={() => removeState(index)}
                  className="h-10"
                >
                  {t.common.buttons("remove")}
                </Button>
              </div>
            ))}
            <Button
              type="button"
              onClick={() =>
                appendState({
                  stateName: "",
                  isInitial: false,
                  isFinal: false,
                  id: temporaryIdCounter--,
                  workflowId: workflowDetail.id!,
                })
              }
              variant="secondary"
            >
              {t.workflows.add("add_state")}
            </Button>
          </div>

          {/* Transitions Section */}
          <div>
            <h3 className="text-md font-semibold mb-4">
              {t.workflows.add("transitions")}
            </h3>
            {transitionFields.map((transition, index) => (
              <div
                key={transition.id || index}
                className="flex items-center gap-4 mb-4"
              >
                <div className="flex-1">
                  <WorkflowStatesSelectField
                    fieldName={`transitions.${index}.sourceStateId`}
                    form={form}
                    label={t.workflows.add("source_state")}
                    placeholder={t.workflows.add("source_state_place_holder")}
                    options={watchedValues.states.map((state) => ({
                      label: state.stateName,
                      value: state.id!,
                    }))}
                    required
                  />
                </div>
                <div className="flex-1">
                  <WorkflowStatesSelectField
                    fieldName={`transitions.${index}.targetStateId`}
                    form={form}
                    label={t.workflows.add("target_state")}
                    placeholder={t.workflows.add("target_state_place_holder")}
                    options={watchedValues.states.map((state) => ({
                      label: state.stateName,
                      value: state.id!,
                    }))}
                    required
                  />
                </div>
                <div className="flex-1">
                  <ExtInputField
                    form={form}
                    fieldName={`transitions.${index}.eventName`}
                    label={t.workflows.add("event_name")}
                    required
                  />
                </div>
                <div className="flex-1">
                  <ExtInputField
                    form={form}
                    fieldName={`transitions.${index}.slaDuration`}
                    label={t.workflows.add("sla_duration")}
                    type="number"
                  />
                </div>
                <div className="flex items-center gap-2">
                  <label className="text-sm">
                    {t.workflows.add("escalate")}
                  </label>
                  <Checkbox
                    checked={form.watch(
                      `transitions.${index}.escalateOnViolation`,
                    )}
                    onCheckedChange={(value) =>
                      form.setValue(
                        `transitions.${index}.escalateOnViolation`,
                        Boolean(value),
                      )
                    }
                  />
                </div>
                <Button
                  variant="destructive"
                  onClick={() => removeTransition(index)}
                  className="h-10"
                >
                  {t.common.buttons("remove")}
                </Button>
              </div>
            ))}
            <Button
              type="button"
              onClick={() =>
                appendTransition({
                  sourceStateId: null,
                  targetStateId: null,
                  eventName: "",
                  slaDuration: null,
                  escalateOnViolation: false,
                  workflowId: workflowDetail.id!,
                })
              }
              variant="secondary"
            >
              {t.workflows.add("add_transition")}
            </Button>
          </div>

          <div className="flex justify-start space-x-4">
            <Button type="submit">{t.common.buttons("save")}</Button>
            <Button type="button" variant="secondary" onClick={onCancel}>
              {t.common.buttons("discard")}
            </Button>
          </div>
        </form>
      </Form>
    </>
  );
};

export default WorkflowEditForm;
