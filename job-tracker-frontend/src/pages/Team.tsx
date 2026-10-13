import { useEffect, useState, type FormEvent } from "react";
import { createTeamInvitation, getTeam } from "../services/recruitingService";
import type { Team, TeamRole } from "../types/Recruiting";
import { getErrorMessage } from "../utils/apiError";

function initials(name: string) {
  return name.split(/\s+/).filter(Boolean).slice(0, 2).map((part) => part[0]).join("").toUpperCase();
}

export default function Team() {
  const [team, setTeam] = useState<Team | null>(null);
  const [email, setEmail] = useState("");
  const [role, setRole] = useState<Exclude<TeamRole, "OWNER">>("RECRUITER");
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [notice, setNotice] = useState("");
  const [inviteLink, setInviteLink] = useState("");

  const refresh = async () => {
    setLoading(true);
    try { setTeam(await getTeam()); setError(""); }
    catch (e) { setError(getErrorMessage(e, "Unable to load your team.")); }
    finally { setLoading(false); }
  };
  useEffect(() => { void refresh(); }, []);

  const addMember = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    setNotice("");
    try {
      const invitation = await createTeamInvitation(email, role);
      setEmail("");
      setShowForm(false);
      setInviteLink(`${window.location.origin}/signup?invite=${encodeURIComponent(invitation.token)}`);
      setNotice("Invitation created. Share the secure signup link with your teammate.");
      await refresh();
    } catch (e) { setError(getErrorMessage(e, "Could not add this teammate.")); }
    finally { setSaving(false); }
  };

  return (
    <div className="ats-page">
      <div className="ats-page-topline"><span className="workspace-crumb"><span className="crumb-mark">t</span> {team?.organizationName || "Workspace"} <span> / </span> Team</span><span className="workspace-status"><i /> Workspace access</span></div>
      <header className="ats-page-header"><div><span className="eyebrow">BETTER TOGETHER</span><h1>Your team</h1><p>Bring the people behind every hiring decision into one workspace.</p></div><button className="button" type="button" onClick={() => { setShowForm((value) => !value); setError(""); setNotice(""); }}>＋ Add a teammate</button></header>
      {error && <div className="ats-alert" role="alert">{error}<button type="button" onClick={() => void refresh()}>Refresh</button></div>}
      {notice && <div className="ats-success" role="status">{notice}</div>}
      {inviteLink && <div className="invite-link-card"><label htmlFor="team-invite-link">Team signup link</label><input id="team-invite-link" readOnly value={inviteLink} /><button type="button" className="button small" onClick={() => { void navigator.clipboard.writeText(inviteLink).then(() => setNotice("Invitation link copied.")).catch(() => setError("Could not copy automatically. Select and copy the invitation link.")); }}>Copy link</button></div>}
      <section className="team-welcome-card"><div className="team-emblem">⌘</div><div><span className="eyebrow">YOUR ORGANIZATION</span><h2>{team?.organizationName || (loading ? "Loading workspace…" : "Team workspace")}</h2><p>One shared place for roles, candidates, and hiring conversations.</p></div><span className="team-private"><i /> Private workspace</span></section>
      {showForm && <form className="ats-form-panel team-form" onSubmit={addMember}>      <div className="ats-form-heading"><div><h2>Invite a teammate</h2><p>We’ll create a secure signup link for this email.</p></div><button className="icon-close" aria-label="Close form" type="button" onClick={() => setShowForm(false)}>×</button></div><div className="ats-form-grid team-form-grid"><label>Work email<input required type="email" autoComplete="email" placeholder="teammate@company.com" value={email} onChange={(e) => setEmail(e.target.value)} /></label><label>Workspace role<select value={role} onChange={(e) => setRole(e.target.value as Exclude<TeamRole, "OWNER">)}><option value="RECRUITER">Recruiter</option><option value="ADMIN">Admin</option></select></label></div><div className="team-permission-note"><span>ⓘ</span> Recruiters can manage the candidate pipeline. Admins can also invite teammates. Links expire after seven days.</div><div className="ats-form-actions"><button className="button secondary" type="button" onClick={() => setShowForm(false)}>Cancel</button><button className="button" disabled={saving}>{saving ? "Creating invite…" : "Create invitation"}</button></div></form>}
      <section className="ats-list-panel">
        <div className="ats-list-toolbar"><div><h2>Workspace members <span className="count-badge">{team?.members.length ?? 0}</span></h2><p>Only members of this organization can see its hiring data.</p></div></div>
        {loading ? <div className="ats-empty">Loading workspace members…</div> : team?.members.length ? <div className="team-members-list">{team.members.map((member) => <article className="team-member-row" key={member.id}><i className="team-member-avatar">{initials(member.name)}</i><span className="team-member-identity"><b>{member.name}</b><small>{member.email}</small></span><span className="team-role">{member.role === "OWNER" ? "Workspace owner" : member.role === "ADMIN" ? "Admin" : "Recruiter"}</span><span className="team-joined">Joined {new Date(member.joinedAt).toLocaleDateString(undefined, { month: "short", year: "numeric" })}</span></article>)}</div> : <div className="ats-empty">No team members yet.</div>}
        {team?.pendingInvitations.length ? <div className="pending-invitations"><h3>Pending invitations</h3>{team.pendingInvitations.map((invitation) => <div className="pending-invitation-row" key={invitation.id}><span><b>{invitation.email}</b><small>{invitation.role.toLowerCase()} · expires {new Date(invitation.expiresAt).toLocaleDateString(undefined, { month: "short", day: "numeric" })}</small></span><button type="button" className="button small secondary" onClick={() => { const link = `${window.location.origin}/signup?invite=${encodeURIComponent(invitation.token)}`; void navigator.clipboard.writeText(link).then(() => setNotice("Invitation link copied.")).catch(() => { setInviteLink(link); setError("Could not copy automatically. Use the link field above."); }); }}>Copy invite link</button></div>)}</div> : null}
        <div className="ats-list-footer">Team data is isolated by organization <span>Workspace owner and admin roles are protected by the server</span></div>
      </section>
      <p className="team-invite-footnote">Invite links are single-use, email-specific, and expire after seven days.</p>
    </div>
  );
}
