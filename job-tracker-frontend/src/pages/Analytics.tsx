import { useEffect, useState } from "react";
import { BarChart, Bar, CartesianGrid, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";
import { getStatistics } from "../services/dashboardService";
import type { DashboardStatistics } from "../types/Dashboard";

export default function Analytics() {
  const [stats, setStats] = useState<DashboardStatistics | null>(null);
  useEffect(() => { getStatistics().then(setStats); }, []);

  const data = stats ? [
    { name: "Applied", value: stats.applied },
    { name: "Shortlisted", value: stats.shortlisted },
    { name: "Interviews", value: stats.interviews },
    { name: "Selected", value: stats.selected },
    { name: "Rejected", value: stats.rejected }
  ] : [];

  return (
    <div>
      <h1>Analytics</h1>
      <div className="card chart">
        <ResponsiveContainer width="100%" height={350}>
          <BarChart data={data}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis allowDecimals={false} />
            <Tooltip />
            <Bar dataKey="value" />
          </BarChart>
        </ResponsiveContainer>
      </div>
      <div className="card">
        <h3>Success Rate</h3>
        <p>{stats && stats.totalApplications > 0
          ? `${((stats.selected / stats.totalApplications) * 100).toFixed(2)}%`
          : "0%"}</p>
      </div>
    </div>
  );
}
