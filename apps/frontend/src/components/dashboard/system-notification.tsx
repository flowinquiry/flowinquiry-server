"use client";

import { useEffect, useState } from "react";

import { useToast } from "@/components/ui/use-toast";
import { checkVersion } from "@/lib/actions/shared.action";
import { useError } from "@/providers/error-provider";

type UpdateInfo = {
  latestVersion: string;
  releaseDate: string;
  releaseNotes: string;
  instruction_link: string;
};

export const VersionUpdateBanner = () => {
  const [updateInfo, setUpdateInfo] = useState<UpdateInfo | null>(null);

  const { setError } = useError();
  const { toast } = useToast();

  useEffect(() => {
    const runCheck = async () => {
      try {
        const result = await checkVersion(setError);

        if (result.isOutdated) {
          toast({
            title: `🚀 New version available: v${result.latestVersion}`,
            description: (
              <div className="flex flex-col gap-1">
                <span>Released on {result.releaseDate}</span>
                <a
                  href={result.instruction_link}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="underline text-sm text-blue-600"
                >
                  Upgrade instructions
                </a>
                <a
                  href={result.releaseNotes}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="underline text-sm text-muted-foreground"
                >
                  Release notes
                </a>
              </div>
            ),
            duration: 10000, // 10 seconds
          });
        }
      } catch (err) {
        console.error("Version check failed", err);
      }
    };

    runCheck();
  }, []);

  if (!updateInfo) return null;

  return null;
};
