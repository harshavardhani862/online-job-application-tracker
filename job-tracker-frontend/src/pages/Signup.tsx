import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import type { SignupRequest } from "../types/User";
import { useState } from "react";
import { getErrorMessage } from "../utils/apiError";

export default function Signup() {
  const { register, handleSubmit, formState: { errors } } = useForm<SignupRequest>();
  const { register: signup, loading } = useAuth();
  const navigate = useNavigate();
  const [submitError, setSubmitError] = useState("");

  const onSubmit = async (data: SignupRequest) => {
    setSubmitError("");
    try {
      await signup({
        ...data,
        name: data.name.trim(),
        email: data.email.trim().toLowerCase(),
        phone: data.phone?.trim()
      });
      navigate("/dashboard");
    } catch (error) {
      setSubmitError(getErrorMessage(error, "Unable to create your account."));
    }
  };

  return (
    <div className="auth-page">
      <form className="form card auth-card" onSubmit={handleSubmit(onSubmit)}>
        <h1>Create Account</h1>
        <label>Name<input autoComplete="name" {...register("name", { required: "Name is required", minLength: { value: 2, message: "Name must contain at least 2 characters" } })} /></label>
        {errors.name && <small className="error">{errors.name.message}</small>}
        <label>Email<input type="email" autoComplete="email" {...register("email", { required: "Email is required" })} /></label>
        {errors.email && <small className="error">{errors.email.message}</small>}
        <label>Phone<input type="tel" autoComplete="tel" {...register("phone")} /></label>
        <label>Password<input type="password" autoComplete="new-password" {...register("password", { required: "Password is required", minLength: { value: 6, message: "Minimum 6 characters" } })} /></label>
        {errors.password && <small className="error">{errors.password.message}</small>}
        {submitError && <small className="error" role="alert">{submitError}</small>}
        <button className="button" disabled={loading}>{loading ? "Creating account..." : "Create Account"}</button>
        <p>Already registered? <Link to="/signin">Sign in</Link></p>
      </form>
    </div>
  );
}
