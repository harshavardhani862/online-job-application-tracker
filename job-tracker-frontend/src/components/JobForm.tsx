import { useForm } from "react-hook-form";
import type { ApplicationRequest } from "../types/Application";
import { APPLICATION_STATUSES } from "../utils/constants";

export default function JobForm({
  initial,
  onSubmit,
  submitText = "Save Application"
}: {
  initial?: Partial<ApplicationRequest>;
  onSubmit: (data: ApplicationRequest) => Promise<void> | void;
  submitText?: string;
}) {
  const { register, handleSubmit, formState: { errors, isSubmitting } } =
    useForm<ApplicationRequest>({
      defaultValues: {
        companyName: initial?.companyName ?? "",
        jobTitle: initial?.jobTitle ?? "",
        jobUrl: initial?.jobUrl ?? "",
        location: initial?.location ?? "",
        salary: initial?.salary ?? "",
        applicationDate: initial?.applicationDate ?? new Date().toISOString().slice(0, 10),
        status: initial?.status ?? "APPLIED",
        notes: initial?.notes ?? ""
      }
    });

  return (
    <form className="form card" onSubmit={handleSubmit(onSubmit)}>
      <label>Company Name<input {...register("companyName", { required: true })} /></label>
      {errors.companyName && <small className="error">Company is required</small>}
      <label>Job Title<input {...register("jobTitle", { required: true })} /></label>
      {errors.jobTitle && <small className="error">Job title is required</small>}
      <label>Job URL<input {...register("jobUrl")} placeholder="https://..." /></label>
      <label>Location<input {...register("location")} /></label>
      <label>Salary<input {...register("salary")} placeholder="e.g. ₹8 LPA" /></label>
      <label>Applied Date<input type="date" {...register("applicationDate", { required: true })} /></label>
      <label>Status
        <select {...register("status")}>
          {APPLICATION_STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
        </select>
      </label>
      <label>Notes<textarea rows={5} {...register("notes")} /></label>
      <button className="button" disabled={isSubmitting}>{isSubmitting ? "Saving..." : submitText}</button>
    </form>
  );
}
