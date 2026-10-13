import { useCallback, useEffect, useState } from "react";
import type { JobApplication } from "../types/Application";
import { deleteApplication, getApplications } from "../services/applicationService";

export function useApplications() {
  const [applications, setApplications] = useState<JobApplication[]>([]);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    try { setApplications(await getApplications()); }
    finally { setLoading(false); }
  }, []);

  useEffect(() => { load(); }, [load]);

  const remove = async (id: number) => {
    await deleteApplication(id);
    await load();
  };

  return { applications, loading, reload: load, remove };
}
