import { Link } from "react-router-dom";

export default function PaymentFailed() {
  return (
    <div className="result-page card">
      <h1>Payment Failed</h1>
      <p>The payment could not be completed.</p>
      <Link className="button" to="/payment">Try Again</Link>
    </div>
  );
}
