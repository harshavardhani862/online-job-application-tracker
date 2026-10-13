import { useForm } from "react-hook-form";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";
import type { LoginRequest } from "../types/User";

export default function Signin() {
  const { register, handleSubmit, formState: { errors } } = useForm<LoginRequest>();
  const { login, loading } = useAuth();
  const navigate = useNavigate();

  const onSubmit = async (data: LoginRequest) => {
    try { await login(data); navigate("/dashboard"); }
    catch (e) { alert(e instanceof Error ? e.message : "Login failed"); }
  };

  return (
    <div className="auth-page">
      <form className="form card auth-card" onSubmit={handleSubmit(onSubmit)}>
        <h1>Welcome Back</h1>
        <label>Email<input type="email" {...register("email", { required: "Email is required" })} /></label>
        {errors.email && <small className="error">{errors.email.message}</small>}
        <label>Password<input type="password" {...register("password", { required: "Password is required" })} /></label>
        {errors.password && <small className="error">{errors.password.message}</small>}
        <button className="button" disabled={loading}>{loading ? "Signing in..." : "Sign In"}</button>
        <p>New user? <Link to="/signup">Create an account</Link></p>
      </form>
    </div>
  );
}
