import { useNavigate } from "react-router-dom";
import PremiumBadge from "../components/PremiumBadge";
import { useAuth } from "../hooks/useAuth";
import { usePayment } from "../hooks/usePayment";

export default function Payment() {
  const { user } = useAuth();
  const { loading, startPayment } = usePayment();
  const navigate = useNavigate();

  if (!user) return null;

  const pay = () => startPayment(
    { name: user.name, email: user.email },
    () => navigate("/payment-success"),
    (message) => alert(message)
  );

  return (
    <div>
      <h1>Upgrade to Premium</h1>
      <div className="premium-card">
        <PremiumBadge />
        <h2>Premium Plan</h2>
        <h1>₹999</h1>
        <p>Unlock advanced analytics, enhanced interview insights, advanced reminders and unlimited tracking.</p>
        {user.subscriptionStatus === "PREMIUM" ? (
          <div className="success-box">You are already a Premium user.</div>
        ) : (
          <button className="button" onClick={pay} disabled={loading}>
            {loading ? "Starting Payment..." : "Pay ₹999 with Razorpay"}
          </button>
        )}
      </div>
    </div>
  );
}
