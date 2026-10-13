
import api from "./api";
import type {
  ApplicationRequest,
  JobApplication,
} from "../types/Application";

function isRecord(
  value: unknown
): value is Record<string, unknown> {
  return (
    typeof value === "object" &&
    value !== null &&
    !Array.isArray(value)
  );
}

function list(data: unknown): JobApplication[] {
  if (Array.isArray(data)) {
    return data as JobApplication[];
  }

  if (!isRecord(data)) {
    return [];
  }

  // Support common Spring Boot response formats.
  for (const key of ["content", "applications", "items", "data"]) {
    const value = data[key];

    if (Array.isArray(value)) {
      return value as JobApplication[];
    }

    if (isRecord(value)) {
      const nested = list(value);

      if (nested.length > 0) {
        return nested;
      }
    }
  }

  return [];
}

// Fetch applications for the currently authenticated user.
export async function getApplications(): Promise<JobApplication[]> {
  const response = await api.get("/applications");
  return list(response.data);
}

// Fetch one application.
export async function getApplication(
  id: number
): Promise<JobApplication> {
  const response = await api.get(`/applications/${id}`);
  return response.data as JobApplication;
}

// Create an application.
export async function createApplication(
  data: ApplicationRequest
): Promise<JobApplication> {
  const response = await api.post("/applications", data);
  return response.data as JobApplication;
}

// Update an application.
export async function updateApplication(
  id: number,
  data: ApplicationRequest
): Promise<JobApplication> {
  const response = await api.put(
    `/applications/${id}`,
    data
  );

  return response.data as JobApplication;
}

// Delete an application.
export async function deleteApplication(
  id: number
): Promise<void> {
  await api.delete(`/applications/${id}`);
}
