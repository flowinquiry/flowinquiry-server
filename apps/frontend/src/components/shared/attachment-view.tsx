"use client";

import { Download, FilePlus, Paperclip, Trash } from "lucide-react";
import React, { useRef, useState } from "react";
import useSWR from "swr";

import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Spinner } from "@/components/ui/spinner";
import {
  Tooltip,
  TooltipContent,
  TooltipTrigger,
} from "@/components/ui/tooltip";
import { usePagePermission } from "@/hooks/use-page-permission";
import { useAppClientTranslations } from "@/hooks/use-translations";
import { getSecureBlobResource } from "@/lib/actions/commons.action";
import {
  deleteEntityAttachment,
  getEntityAttachments,
  uploadAttachmentsForEntity, // ✅ Multi-file upload
} from "@/lib/actions/entity-attachments.action";
import { useError } from "@/providers/error-provider";
import { useUserTeamRole } from "@/providers/user-team-role-provider";
import { EntityAttachmentDTO, EntityType } from "@/types/commons";
import { PermissionUtils } from "@/types/resources";

interface AttachmentViewProps {
  entityType: EntityType;
  entityId: number;
}

// Format file size into readable string
const formatFileSize = (size: number | null) => {
  if (!size) return "Unknown size";
  const KB = 1024,
    MB = KB * 1024;
  return size < MB
    ? `${(size / KB).toFixed(2)} KB`
    : `${(size / MB).toFixed(2)} MB`;
};

// Format uploaded date
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr);
  return date.toLocaleDateString(undefined, {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
};

const AttachmentView = ({ entityType, entityId }: AttachmentViewProps) => {
  const { setError } = useError();
  const [deleting, setDeleting] = useState<number | null>(null);
  const [hovered, setHovered] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const permissionLevel = usePagePermission();
  const teamRole = useUserTeamRole().role;
  const t = useAppClientTranslations();

  // ✅ Define permissions
  const canView =
    PermissionUtils.canRead(permissionLevel) ||
    teamRole === "manager" ||
    teamRole === "member" ||
    teamRole === "guest";

  const canWrite =
    PermissionUtils.canWrite(permissionLevel) ||
    teamRole === "manager" ||
    teamRole === "member";

  // Fetch attachments using SWR
  const {
    data: attachments,
    mutate,
    isValidating,
  } = useSWR<EntityAttachmentDTO[]>(
    ["getEntityAttachments", entityType, entityId],
    () => getEntityAttachments(entityType, entityId, setError),
  );

  // Handle file selection via input dialog
  const handleFileSelect = async (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    if (!event.target.files) return;
    await uploadFiles(Array.from(event.target.files));
  };

  // Handle file upload via drag & drop
  const handleDrop = async (event: React.DragEvent<HTMLDivElement>) => {
    event.preventDefault();
    await uploadFiles(Array.from(event.dataTransfer.files));
  };

  // Upload selected files
  const uploadFiles = async (files: File[]) => {
    if (!canWrite || files.length === 0) return;
    await uploadAttachmentsForEntity(entityType, entityId, files, setError);
    mutate(); // Refresh the attachment list
  };

  // Handle file download
  const handleDownload = async (
    fileUrl: string,
    fileName: string,
    event: React.MouseEvent,
  ) => {
    event.stopPropagation(); // ✅ Prevent upload dialog from opening
    if (!canView) return;

    const blob = await getSecureBlobResource(fileUrl, setError);
    if (blob) {
      const objectURL = URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = objectURL;
      link.download = fileName;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      // Revoke the object URL to free up memory
      URL.revokeObjectURL(objectURL);
    }
  };

  // Handle file deletion
  const handleDelete = async (
    attachmentId: number,
    event: React.MouseEvent,
  ) => {
    event.stopPropagation(); // ✅ Prevent upload dialog from opening
    if (!canWrite) return;

    setDeleting(attachmentId);
    try {
      await deleteEntityAttachment(attachmentId, setError);
      mutate(); // Refresh the attachment list
    } finally {
      setDeleting(null);
    }
  };

  return (
    <div
      className={`w-full rounded-md transition-all duration-200 ${
        hovered ? "border border-dotted border-gray-400" : ""
      } space-y-2 flex flex-col justify-center`} // ✅ Ensures component has a minimum height
      onClick={(event) => {
        if (
          event.target instanceof HTMLButtonElement ||
          event.target instanceof SVGElement
        )
          return; // ✅ Prevent triggering upload on button/icon click
        if (canWrite) fileInputRef.current?.click();
      }}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      onDragOver={(event) => event.preventDefault()}
      onDrop={handleDrop}
    >
      {isValidating ? (
        <Skeleton className="h-10 w-full" />
      ) : attachments?.length === 0 ? (
        // Placeholder when no attachments exist
        canWrite && (
          <div className="flex items-center justify-start gap-2 text-gray-500 cursor-pointer">
            <FilePlus className="w-5 h-5" />
            <span>{t.common.misc("attachment_place_holder")}</span>
          </div>
        )
      ) : (
        <div className="flex flex-col">
          {attachments
            ?.sort(
              (a, b) =>
                new Date(b.uploadedAt).getTime() -
                new Date(a.uploadedAt).getTime(),
            ) // Ensure newest file is on top
            .map((attachment, index) => (
              <div
                key={attachment.id}
                className={`flex items-center transition-all duration-200 cursor-pointer ${
                  index === 0 ? "p-0" : "mt-1" // ✅ First file has no padding
                }`}
              >
                {/* Attachment Icon */}
                <Paperclip className="w-4 h-4 text-gray-500 mr-2" />

                {/* File Name with Tooltip */}
                <Tooltip>
                  <TooltipTrigger asChild>
                    <span className="truncate font-medium">
                      {attachment.fileName}
                    </span>
                  </TooltipTrigger>
                  <TooltipContent>
                    <div className="text-sm">
                      <p>
                        <strong>
                          {t.common.misc("attachment_file_name")}:
                        </strong>{" "}
                        {attachment.fileName}
                      </p>
                      <p>
                        <strong>{t.common.misc("attachment_type")}:</strong>{" "}
                        {attachment.fileType ?? "Unknown"}
                      </p>
                      <p>
                        <strong>{t.common.misc("attachment_size")}:</strong>{" "}
                        {formatFileSize(attachment.fileSize)}
                      </p>
                      <p>
                        <strong>Uploaded:</strong>{" "}
                        {formatDate(attachment.uploadedAt)}
                      </p>
                    </div>
                  </TooltipContent>
                </Tooltip>

                {/* Action Buttons (Download, Delete) */}
                <div className="flex items-center ml-2">
                  {canView && (
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={(event) =>
                            handleDownload(
                              attachment.fileUrl,
                              attachment.fileName,
                              event,
                            )
                          }
                        >
                          <Download className="w-4 h-4" />
                        </Button>
                      </TooltipTrigger>
                      <TooltipContent>
                        {t.common.misc("download")}
                      </TooltipContent>
                    </Tooltip>
                  )}

                  {canWrite && (
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <Button
                          variant="ghost"
                          size="icon"
                          onClick={(event) =>
                            handleDelete(attachment.id, event)
                          }
                          disabled={deleting === attachment.id}
                          className="text-red-500 hover:text-red-700"
                        >
                          {deleting === attachment.id ? (
                            <Spinner className="h-4 w-4" />
                          ) : (
                            <Trash className="w-4 h-4" />
                          )}
                        </Button>
                      </TooltipTrigger>
                      <TooltipContent>{t.common.misc("delete")}</TooltipContent>
                    </Tooltip>
                  )}
                </div>
              </div>
            ))}
        </div>
      )}
      <input
        type="file"
        ref={fileInputRef}
        className="hidden"
        multiple
        onChange={handleFileSelect}
      />
    </div>
  );
};

export default AttachmentView;
