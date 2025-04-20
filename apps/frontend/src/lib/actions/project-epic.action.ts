import { get, post, put } from "@/lib/actions/commons.action";
import { HttpError } from "@/lib/errors";
import { ProjectEpicDTO } from "@/types/projects";

export const createProjectEpic = async (
  projectEpic: ProjectEpicDTO,
  setError?: (error: HttpError | string | null) => void,
) => {
  return post<ProjectEpicDTO, ProjectEpicDTO>(
    `/api/project-epics`,
    projectEpic,
    setError,
  );
};

export const updateProjectEpic = (
  projectEpicId: number,
  projectEpic: ProjectEpicDTO,
  setError?: (error: HttpError | string | null) => void,
) => {
  return put<ProjectEpicDTO, ProjectEpicDTO>(
    `/api/project-epics/${projectEpicId}`,
    projectEpic,
    setError,
  );
};

export const findEpicsByProjectId = async (
  projectId: number,
  setError?: (error: HttpError | string | null) => void,
) => {
  return get<Array<ProjectEpicDTO>>(
    `/api/projects/${projectId}/epics`,
    setError,
  );
};
