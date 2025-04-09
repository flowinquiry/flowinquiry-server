import { get, put } from "@/lib/actions/commons.action";
import { HttpError } from "@/lib/errors";
import { AppSettingDTO } from "@/types/commons";

export const findAppSettingsByGroup = async (
  group: string,
  setError?: (error: HttpError | string | null) => void,
) => {
  return get<Array<AppSettingDTO>>(`/api/settings?group=${group}`, setError);
};

export const updateAppSettings = (
  appSettings: Array<AppSettingDTO>,
  setError?: (error: HttpError | string | null) => void,
) => {
  return put<Array<AppSettingDTO>, void>(
    `/api/settings`,
    appSettings,
    setError,
  );
};
