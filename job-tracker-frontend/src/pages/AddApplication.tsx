import { useNavigate } from "react-router-dom";
import JobForm from "../components/JobForm";
import { createApplication } from "../services/applicationService";
import type { ApplicationRequest } from "../types/Application";

export default function AddApplication() {
  const navigate = useNavigate();
  const save = async (data: ApplicationRequest) => {
    try { await createApplication(data); navigate("/applications"); }
    catch (e) { alert(e instanceof Error ? e.message : "Unable to create application"); }
  };
  return <div><h1>Add Application</h1><JobForm onSubmit={save} /></div>;
}
