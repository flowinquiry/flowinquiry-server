"use client";

import { formatDistanceToNow } from "date-fns";
import { motion } from "framer-motion";
import React from "react";

import { UserAvatar } from "@/components/shared/avatar-display";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { TicketDTO } from "@/types/tickets";

type TaskBlockProps = {
  task: TicketDTO;
  isDragging?: boolean;
};

const TaskBlock: React.FC<TaskBlockProps> = ({ task, isDragging = false }) => {
  return (
    <Tooltip>
      <TooltipTrigger asChild>
        <motion.div
          className="w-full p-3 rounded-lg shadow-md mb-2 border bg-white dark:bg-gray-800"
          animate={{
            opacity: isDragging ? 0.5 : 1,
            scale: isDragging ? 0.95 : 1,
          }}
          transition={{ type: "spring", stiffness: 300, damping: 20 }}
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.97 }}
        >
          <div className="flex flex-col gap-1">
            <h3 className="font-medium truncate">{task.requestTitle}</h3>

            {/* Task description - limited to 2 lines */}
            {task.requestDescription && (
              <div
                className="text-xs text-gray-600 dark:text-gray-400 line-clamp-2 mt-1"
                dangerouslySetInnerHTML={{ __html: task.requestDescription }}
              />
            )}

            {/* Additional task details */}
            <div className="flex items-center gap-2 mt-2 text-xs">
              {task.priority && (
                <span
                  className={`px-2 py-0.5 rounded-full text-xs font-medium ${getPriorityClass(task.priority)}`}
                >
                  {task.priority}
                </span>
              )}

              {task.assignUserName && (
                <span className="flex items-center gap-1 text-gray-500 dark:text-gray-400">
                  <span className="w-8 h-8 rounded-full bg-gray-200 dark:bg-gray-700 flex items-center justify-center overflow-hidden">
                    <UserAvatar imageUrl={task.assignUserImageUrl} />
                  </span>
                  <span className="truncate max-w-[80px]">
                    {task.assignUserName}
                  </span>
                </span>
              )}

              {task.modifiedAt && (
                <span className="text-gray-500 dark:text-gray-400 ml-auto">
                  {formatDistanceToNow(task.modifiedAt, { addSuffix: true })}
                </span>
              )}
            </div>
          </div>
        </motion.div>
      </TooltipTrigger>
      <TooltipContent side="top" align="center" className="text-sm max-w-xs">
        <div className="font-medium">{task.requestTitle}</div>
        {task.requestDescription && (
          <div
            className="mt-1 text-xs text-gray-500 dark:text-gray-400 max-h-40 overflow-y-auto"
            dangerouslySetInnerHTML={{ __html: task.requestDescription }}
          />
        )}
        <div className="mt-2 text-xs grid grid-cols-2 gap-x-4 gap-y-1">
          <span className="text-gray-500 dark:text-gray-400">Priority:</span>
          <span
            className={`font-medium ${getTextPriorityClass(task.priority)}`}
          >
            {task.priority}
          </span>

          {task.assignUserName && (
            <>
              <span className="text-gray-500 dark:text-gray-400">
                Assigned to:
              </span>
              <span>{task.assignUserName}</span>
            </>
          )}

          {task.requestUserName && (
            <>
              <span className="text-gray-500 dark:text-gray-400">
                Requester:
              </span>
              <span>{task.requestUserName}</span>
            </>
          )}
        </div>
      </TooltipContent>
    </Tooltip>
  );
};

// Helper function to determine priority badge class
const getPriorityClass = (priority: string): string => {
  switch (priority) {
    case "Critical":
      return "bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300";
    case "High":
      return "bg-orange-100 text-orange-800 dark:bg-orange-900/30 dark:text-orange-300";
    case "Medium":
      return "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/30 dark:text-yellow-300";
    case "Low":
      return "bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300";
    case "Trivial":
      return "bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300";
    default:
      return "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-300";
  }
};

// Helper function for text color in tooltip
const getTextPriorityClass = (priority: string): string => {
  switch (priority) {
    case "Critical":
      return "text-red-600 dark:text-red-400";
    case "High":
      return "text-orange-600 dark:text-orange-400";
    case "Medium":
      return "text-yellow-600 dark:text-yellow-400";
    case "Low":
      return "text-green-600 dark:text-green-400";
    case "Trivial":
      return "text-blue-600 dark:text-blue-400";
    default:
      return "";
  }
};

export default TaskBlock;
