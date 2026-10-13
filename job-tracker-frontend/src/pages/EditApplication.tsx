import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import JobForm from "../components/JobForm";
import { getApplication, updateApplication } from "../services/applicationService";
import type { JobApplication, ApplicationRequest } from "../types/Application";

export default function EditApplication() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [application, setApplication] = useState<JobApplication | null>(null);

  useEffect(() => {
    if (id) getApplication(Number(id)).then(setApplication);
  }, [id]);

  if (!application) return <p>Loading...</p>;

  const save = async (data: ApplicationRequest) => {
    await updateApplication(Number(id), data);
    navigate(`/applications/${id}`);
  };

  return <div><h1>Edit Application</h1><JobForm initial={application} onSubmit={save} submitText="Update Application" /></div>;
}
