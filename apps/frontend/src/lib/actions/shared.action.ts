import { get } from "@/lib/actions/commons.action";
import { HttpError } from "@/lib/errors";
import { mapEntityToFilterOptions } from "@/lib/mappers";
import { EntityValueDefinition, VersionCheckResponse } from "@/types/commons";

export interface TimezoneInfo {
  zoneId: string;
  offset: string;
}

export const getTimezones = async (): Promise<Array<TimezoneInfo>> => {
  return get<Array<TimezoneInfo>>(`/api/timezones`);
};

export const findEntitiesFilterOptions = async (
  findEntitiesFn: () => Promise<Array<EntityValueDefinition>>,
) => {
  const data = await findEntitiesFn();
  return mapEntityToFilterOptions(data);
};

let versionCache: { version: string; edition: string } | null = null;

export const getVersion = async () => {
  if (versionCache) return versionCache;
  versionCache = await get<{ version: string; edition: string }>(
    `/api/versions`,
  );
  return versionCache;
};

export const checkVersion = async (
  setError?: (error: HttpError | string | null) => void,
) => {
  return get<VersionCheckResponse>(`/api/versions/check`);
};
