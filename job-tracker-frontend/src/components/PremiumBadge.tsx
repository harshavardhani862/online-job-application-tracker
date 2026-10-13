import { useAuth } from "../hooks/useAuth";

export default function PremiumBadge() {
  const { user } = useAuth();
  if (user?.subscriptionStatus !== "PREMIUM") return null;
  return <span className="premium-badge">⭐ PREMIUM</span>;
}
