import {
  ArrowDownCircle,
  ArrowRightCircle,
  ArrowUpCircle,
  Circle,
  Flame,
} from "lucide-react";
import React from "react";

import { TicketPriority } from "@/types/tickets";

// Mapping to backend enum codes
export const PRIORITY_CODES: Record<TicketPriority, number> = {
  Critical: 0,
  High: 1,
  Medium: 2,
  Low: 3,
  Trivial: 4,
};

export const PRIORITY_CONFIG: Record<
  TicketPriority,
  {
    icon: React.ReactNode;
    iconColor: string; // For icon color
    color: string; // For badge/background color
    textColor: string; // For text color
    darkColor: string; // For dark mode background
    darkTextColor: string; // For dark mode text
    borderColor: string; // For borders
    hoverColor: string; // For hover states
  }
> = {
  Critical: {
    icon: <Flame size={16} />,
    iconColor: "text-red-500",
    color: "bg-red-100",
    textColor: "text-red-800",
    darkColor: "dark:bg-red-900",
    darkTextColor: "dark:text-red-300",
    borderColor: "border-red-200",
    hoverColor: "hover:bg-red-200",
  },
  High: {
    icon: <ArrowUpCircle size={16} />,
    iconColor: "text-orange-500",
    color: "bg-orange-100",
    textColor: "text-orange-800",
    darkColor: "dark:bg-orange-900",
    darkTextColor: "dark:text-orange-300",
    borderColor: "border-orange-200",
    hoverColor: "hover:bg-orange-200",
  },
  Medium: {
    icon: <ArrowRightCircle size={16} />,
    iconColor: "text-yellow-500",
    color: "bg-yellow-100",
    textColor: "text-yellow-800",
    darkColor: "dark:bg-yellow-900",
    darkTextColor: "dark:text-yellow-300",
    borderColor: "border-yellow-200",
    hoverColor: "hover:bg-yellow-200",
  },
  Low: {
    icon: <ArrowDownCircle size={16} />,
    iconColor: "text-blue-500",
    color: "bg-blue-100",
    textColor: "text-blue-800",
    darkColor: "dark:bg-blue-900",
    darkTextColor: "dark:text-blue-300",
    borderColor: "border-blue-200",
    hoverColor: "hover:bg-blue-200",
  },
  Trivial: {
    icon: <Circle size={16} />,
    iconColor: "text-gray-500",
    color: "bg-gray-100",
    textColor: "text-gray-800",
    darkColor: "dark:bg-gray-800",
    darkTextColor: "dark:text-gray-300",
    borderColor: "border-gray-200",
    hoverColor: "hover:bg-gray-200",
  },
};

// Helper function to get a combined class string for a priority
export const getPriorityClassNames = (
  priority: TicketPriority,
  variant: "badge" | "row" | "cell" = "badge",
): string => {
  const config = PRIORITY_CONFIG[priority];

  switch (variant) {
    case "badge":
      // For badges
      return `${config.color} ${config.textColor} ${config.darkColor} ${config.darkTextColor} ${config.borderColor}`;
    case "row":
      // For table rows
      return `${config.color} ${config.textColor} ${config.darkColor} ${config.darkTextColor} ${config.hoverColor}`;
    case "cell":
      // For table cells
      return `${config.color} ${config.textColor} ${config.darkColor} ${config.darkTextColor} border-l-4 ${config.borderColor}`;
    default:
      return `${config.color} ${config.textColor}`;
  }
};

// Helper component for priority display
export const PriorityBadge: React.FC<{ priority: TicketPriority }> = ({
  priority,
}) => {
  const config = PRIORITY_CONFIG[priority];

  return (
    <span
      className={`inline-flex items-center gap-1 px-2 py-1 rounded-full text-xs font-medium ${getPriorityClassNames(priority)}`}
    >
      <span className={config.iconColor}>{config.icon}</span>
      {priority}
    </span>
  );
};

export const getPriorityFromCode = (code: number): TicketPriority => {
  const entry = Object.entries(PRIORITY_CODES).find(
    ([_, value]) => value === code,
  );
  if (!entry) {
    return "Medium"; // Default fallback
  }
  return entry[0] as TicketPriority;
};

export const PRIORITIES_ORDERED: TicketPriority[] = [
  "Critical",
  "High",
  "Medium",
  "Low",
  "Trivial",
];
