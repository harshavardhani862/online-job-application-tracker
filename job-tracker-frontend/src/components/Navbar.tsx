import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth";

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const initials = (user?.name || user?.email || "U").trim().charAt(0).toUpperCase();

  return (
    <header className="navbar professional-navbar">
      <Link className="brand professional-brand" to={user ? "/dashboard" : "/"} aria-label="JobTrack home">
        <span className="brand-mark">J</span>
        <span>JobTrack<span className="brand-period">.</span></span>
      </Link>
      <nav aria-label="Main navigation">
        {user ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/applications">Applications</Link>
            <Link to="/payment">Premium</Link>
            <Link className="navbar-profile" to="/profile" title="Your profile">
              <span className="nav-user-avatar">{initials}</span>
              <span className="navbar-user-name">{user.name || "My account"}</span>
            </Link>
            <button className="nav-logout" onClick={() => { logout(); navigate("/signin"); }}>Log out</button>
          </>
        ) : (
          <>
            <Link className="navbar-public-link" to="/">Home</Link>
            <Link className="navbar-public-link" to="/signin">Sign in</Link>
            <Link className="nav-button" to="/signup">Get started <span aria-hidden="true">↗</span></Link>
          </>
        )}
      </nav>
    </header>
  );
}
