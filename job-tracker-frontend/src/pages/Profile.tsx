import { useEffect, useState } from "react";
import { getProfile, updateProfile } from "../services/userService";
import { useAuth } from "../hooks/useAuth";
import type { User } from "../types/User";

export default function Profile() {
  const { refreshUser } = useAuth();
  const [form, setForm] = useState<User>({ name: "", email: "", phone: "" });

  useEffect(() => { getProfile().then(setForm).catch(() => {}); }, []);

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const updated = await updateProfile(form);
      refreshUser(updated);
      alert("Profile updated");
    } catch { alert("Unable to update profile"); }
  };

  return (
    <div>
      <h1>Profile</h1>
      <form className="form card" onSubmit={save}>
        <label>Name<input value={form.name} onChange={e => setForm({...form, name: e.target.value})} /></label>
        <label>Email<input value={form.email} disabled /></label>
        <label>Phone<input value={form.phone || ""} onChange={e => setForm({...form, phone: e.target.value})} /></label>
        <p>Subscription: <b>{form.subscriptionStatus || "FREE"}</b></p>
        <button className="button">Save Profile</button>
      </form>
    </div>
  );
}
