import { get } from "@/lib/actions/commons.action";
import { mapEntityToFilterOptions } from "@/lib/mappers";
import { EntityValueDefinition } from "@/types/commons";

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

export const getVersion = async () => {
  return get<{ version: string }>(`/api/versions`);
};
