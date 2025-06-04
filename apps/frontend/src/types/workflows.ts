import { z } from "zod";

export type WorkflowVisibility = "PUBLIC" | "PRIVATE" | "TEAM";

export const WorkflowDTOSchema = z.object({
  id: z.number().optional(),
  name: z.string().min(1),
  requestName: z.string().min(1),
  description: z.string().nullish(),
  visibility: z.enum(["PUBLIC", "PRIVATE", "TEAM"]).optional(),
  ownerId: z.number().nullish(),
  ownerName: z.string().nullish(),
  level1EscalationTimeout: z.number().nullish(),
  level2EscalationTimeout: z.number().nullish(),
  level3EscalationTimeout: z.number().nullish(),
  tags: z.string().nullish(),
  useForProject: z.oboolean(),
});

export type WorkflowDTO = z.infer<typeof WorkflowDTOSchema>;

export const WorkflowStateDTOSchema = z.object({
  id: z.number().optional(),
  workflowId: z.number().optional(),
  stateName: z.string(),
  isInitial: z.boolean(),
  isFinal: z.boolean(),
});

export type WorkflowStateDTO = z.infer<typeof WorkflowStateDTOSchema>;

export const WorkflowTransitionSchema = z.object({
  id: z.number().optional(),
  workflowId: z.number().optional(),
  sourceStateId: z.number().nullish(),
  targetStateId: z.number().nullish(),
  eventName: z.string().min(1),
  slaDuration: z.preprocess((val) => {
    if (val === "" || val === null || val === undefined) return null;
    if (typeof val === "string") {
      const num = Number(val);
      return isNaN(num) ? null : num;
    }
    return val;
  }, z.number().nullish()),
  escalateOnViolation: z.boolean(),
});

export type WorkflowTransitionDTO = z.infer<typeof WorkflowTransitionSchema>;

export const WorkflowDetailSchema = WorkflowDTOSchema.merge(
  z.object({
    states: z
      .array(WorkflowStateDTOSchema)
      .refine(
        (states) => states.filter((state) => state.isInitial).length <= 1,
        {
          message: "Only one state can be marked as initial.",
          path: ["states"], // Indicate the error relates to the `states` array
        },
      ),
    transitions: z.array(WorkflowTransitionSchema),
  }),
);

export type WorkflowDetailDTO = z.infer<typeof WorkflowDetailSchema>;
