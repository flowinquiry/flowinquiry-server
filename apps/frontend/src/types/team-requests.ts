import { z } from "zod";

export type TeamRequestPriority =
  | "Critical"
  | "High"
  | "Medium"
  | "Low"
  | "Trivial";

// Define the TicketHealthLevel enum
export enum TicketHealthLevel {
  Excellent = "Excellent",
  Good = "Good",
  Fair = "Fair",
  Poor = "Poor",
  Critical = "Critical",
}

export const TeamRequestConversationHealthDTOSchema = z.object({
  id: z.number().optional(),
  teamRequestId: z.number().optional(),
  conversationHealth: z.number().optional(),
  cumulativeSentiment: z.number().optional(),
  totalMessages: z.number().optional(),
  totalQuestions: z.number().optional(),
  resolvedQuestions: z.number().optional(),
  healthLevel: z.nativeEnum(TicketHealthLevel).optional(),
});

export const TeamRequestDTOSchema = z.object({
  id: z.number().optional(),
  teamId: z.number().optional(),
  teamName: z.string().optional(),
  workflowId: z.number().optional(),
  workflowName: z.string().nullish(),
  workflowRequestName: z.string().nullish(),
  projectId: z.number().nullish(),
  projectName: z.string().nullish(),
  priority: z.enum(["Critical", "High", "Medium", "Low", "Trivial"]),
  requestUserId: z.number().optional(),
  requestUserName: z.string().nullish(),
  requestUserImageUrl: z.string().nullish(),
  assignUserId: z.number().nullish(),
  assignUserName: z.string().nullish(),
  assignUserImageUrl: z.string().nullish(),
  currentStateId: z.number().nullish(),
  currentStateName: z.string().nullish(),
  iterationId: z.number().optional(),
  iterationName: z.string().optional(),
  epicId: z.number().optional(),
  epicName: z.string().optional(),
  requestTitle: z.string(),
  requestDescription: z.string(),
  isNew: z.boolean().optional(),
  isCompleted: z.boolean().optional(),
  createdAt: z.string().optional(),
  modifiedAt: z.date().optional(),
  estimatedCompletionDate: z.date().nullish(),
  actualCompletionDate: z.date().nullish(),
  channel: z.string().nullish(),
  numberAttachments: z.number().optional(),
  numberWatchers: z.number().optional(),
  conversationHealth: TeamRequestConversationHealthDTOSchema.nullish(),
});

export type TeamRequestDTO = z.infer<typeof TeamRequestDTOSchema>;

export type TeamRequestConversationHealthDTO = z.infer<
  typeof TeamRequestConversationHealthDTOSchema
>;

export type TicketChannel =
  | "email"
  | "phone"
  | "web_portal"
  | "chat"
  | "social_media"
  | "in_person"
  | "mobile_app"
  | "api"
  | "system_generated"
  | "internal";

export const ticketChannels = [
  "email",
  "phone",
  "web_portal",
  "chat",
  "social_media",
  "in_person",
  "mobile_app",
  "api",
  "system_generated",
  "internal",
] as const;

export type TicketDistributionDTO = {
  userId: number | null; // Null for unassigned tickets
  userName: string | null; // Null for unassigned tickets
  ticketCount: number;
};

export type PriorityDistributionDTO = {
  priority: TeamRequestPriority;
  ticketCount: number;
};

export interface TicketStatisticsDTO {
  totalTickets: number;
  pendingTickets: number;
  completedTickets: number;
}
