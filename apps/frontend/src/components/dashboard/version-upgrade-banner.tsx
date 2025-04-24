"use client";

import { X } from "lucide-react";
import { useEffect, useState } from "react";

import { checkVersion } from "@/lib/actions/shared.action";
import { useError } from "@/providers/error-provider";

type UpdateInfo = {
  latestVersion: string;
  releaseDate: string;
  releaseNotes: string;
  instruction_link: string;
};

export const VersionUpgradeBanner = () => {
  const { setError } = useError();
  const [visible, setVisible] = useState(true);
  const [updateInfo, setUpdateInfo] = useState<UpdateInfo | null>(null);

  useEffect(() => {
    const runCheck = async () => {
      try {
        const result = await checkVersion(setError);
        if (result.isOutdated) {
          setUpdateInfo({
            latestVersion: result.latestVersion,
            releaseDate: result.releaseDate,
            releaseNotes: result.releaseNotes,
            instruction_link: result.instruction_link,
          });
        }
      } catch (err) {
        console.error("Version check failed", err);
      }
    };

    runCheck();
    const interval = setInterval(runCheck, 3600000);
    return () => clearInterval(interval);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (!updateInfo || !visible) return null;

  return (
    <div className="w-full bg-amber-100 border-b border-amber-200 text-amber-900 text-sm py-2 px-4 flex justify-between items-center shadow-sm">
      <div className="flex flex-col sm:flex-row sm:items-center gap-1 sm:gap-3">
        <span>
          🚀 New version <strong>{updateInfo.latestVersion}</strong> released on{" "}
          {updateInfo.releaseDate}
        </span>
        <a
          href={updateInfo.instruction_link}
          target="_blank"
          rel="noopener noreferrer"
          className="underline text-blue-600 font-medium"
        >
          Upgrade instructions
        </a>
        <a
          href={updateInfo.releaseNotes}
          target="_blank"
          rel="noopener noreferrer"
          className="underline text-muted-foreground"
        >
          Release notes
        </a>
      </div>
      <button
        onClick={() => setVisible(false)}
        className="ml-4 text-amber-900 hover:text-amber-700"
      >
        <X className="w-4 h-4" />
      </button>
    </div>
  );
};
