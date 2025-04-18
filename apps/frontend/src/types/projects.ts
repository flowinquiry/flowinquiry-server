import { z } from "zod";

export type ProjectStatus = "Active" | "Closed";

export const ProjectSchema = z.object({
  id: z.number().int().positive().optional(), // Nullable for cases where it's not set yet
  name: z.string().min(1).max(255),
  description: z.string().nullable(),
  teamId: z.number().int().positive(),
  status: z.enum(["Active", "Closed", "Cancelled"]),
  startDate: z.date().nullish(),
  endDate: z.date().nullish(),
  createdBy: z.number().int().positive().optional(),
  createdAt: z.string().datetime().optional(),
  modifiedBy: z.number().int().positive().optional(),
  modifiedAt: z.string().datetime().optional(),
});

export type ProjectDTO = z.infer<typeof ProjectSchema>;

export const ProjectIterationDTOSchema = z.object({
  id: z.number().optional(),
  projectId: z.number(),
  name: z.string(),
  description: z.string().optional(),
  startDate: z.date().nullish(),
  endDate: z.date().nullish(),
  totalTickets: z.number().optional(),
});

export type ProjectIterationDTO = z.infer<typeof ProjectIterationDTOSchema>;

export const ProjectEpicDTOSchema = z.object({
  id: z.number().optional(),
  projectId: z.number(), // required
  name: z.string(), // required
  description: z.string().optional(),
  startDate: z.date().nullish(),
  endDate: z.date().nullish(),
  totalTickets: z.number().optional(),
});

export type ProjectEpicDTO = z.infer<typeof ProjectEpicDTOSchema>;
