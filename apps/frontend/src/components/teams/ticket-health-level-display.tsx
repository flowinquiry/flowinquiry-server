import React from "react";

import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { TicketHealthLevel } from "@/types/tickets";

type HealthLevelProgressProps = {
  currentLevel: TicketHealthLevel;
};

// Tooltip content for each health level
const tooltips: Record<
  TicketHealthLevel,
  { title: string; description: string }
> = {
  [TicketHealthLevel.Critical]: {
    title: "Critical",
    description:
      "Severe issues or unresolved problems requiring immediate action.",
  },
  [TicketHealthLevel.Poor]: {
    title: "Poor",
    description:
      "Significant problems with some resolutions but still needs attention.",
  },
  [TicketHealthLevel.Fair]: {
    title: "Fair",
    description:
      "Moderate health; some issues are resolved, but room for improvement.",
  },
  [TicketHealthLevel.Good]: {
    title: "Good",
    description:
      "Most issues are resolved, and the conversation is progressing well.",
  },
  [TicketHealthLevel.Excellent]: {
    title: "Excellent",
    description:
      "No significant issues; the conversation is in perfect health.",
  },
};

// Map levels to numerical values for progress calculation
const levelMap: Record<TicketHealthLevel, number> = {
  [TicketHealthLevel.Critical]: 1,
  [TicketHealthLevel.Poor]: 2,
  [TicketHealthLevel.Fair]: 3,
  [TicketHealthLevel.Good]: 4,
  [TicketHealthLevel.Excellent]: 5,
};

const TicketHealthLevelDisplay: React.FC<HealthLevelProgressProps> = ({
  currentLevel,
}) => {
  const starsCount = levelMap[currentLevel]; // Number of filled stars

  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <div className="flex items-center gap-4 cursor-pointer">
          {/* Title */}
          <h3 className="text-sm font-medium">Conversation Health:</h3>

          {/* Stars */}
          <div className="flex gap-1">
            {[1, 2, 3, 4, 5].map((index) => (
              <span
                key={index}
                className={`text-sm ${
                  index <= starsCount ? "text-yellow-500" : "text-gray-300"
                }`}
              >
                ★
              </span>
            ))}
          </div>
        </div>
      </TooltipTrigger>
      <TooltipContent className="text-sm max-w-xs">
        <strong className="block text-base">
          {tooltips[currentLevel].title}
        </strong>
        <p className="text-xs text-gray-500">
          {tooltips[currentLevel].description}
        </p>
      </TooltipContent>
    </Tooltip>
  );
};

export default TicketHealthLevelDisplay;
