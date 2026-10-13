import { Link } from "react-router-dom";

export default function PaymentSuccess() {
  return (
    <div className="result-page card">
      <h1>🎉 Payment Successful</h1>
      <p>Your payment was verified. Your premium subscription should now be active.</p>
      <Link className="button" to="/dashboard">Go to Dashboard</Link>
    </div>
  );
}
