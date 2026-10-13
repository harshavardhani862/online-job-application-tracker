import api from "./api";
import type { DashboardStatistics } from "../types/Dashboard";

export async function getStatistics(): Promise<DashboardStatistics> {
  const response = await api.get<DashboardStatistics>("/dashboard/statistics", {
    headers: { "Cache-Control": "no-cache", Pragma: "no-cache" },
  });
  return response.data;
}
