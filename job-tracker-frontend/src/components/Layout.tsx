import { Outlet, useLocation } from "react-router-dom";
import Navbar from "./Navbar";
import Sidebar from "./Sidebar";
import { useAuth } from "../hooks/useAuth";

const publicPaths = new Set(["/", "/signin", "/signup"]);

export default function Layout() {
  const { user } = useAuth();
  const location = useLocation();
  const isPublicPage = publicPaths.has(location.pathname);
  const showWorkspace = Boolean(user) && !isPublicPage;

  return (
    <>
      <Navbar />
      {showWorkspace ? (
        <div className="app-layout">
          <Sidebar />
          <main className="content"><Outlet /></main>
        </div>
      ) : (
        <main className="public-content"><Outlet /></main>
      )}
    </>
  );
}
