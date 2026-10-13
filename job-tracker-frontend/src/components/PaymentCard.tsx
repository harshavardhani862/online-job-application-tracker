import type { Payment } from "../types/Payment";
import { formatCurrency } from "../utils/paymentUtils";

export default function PaymentCard({ payment }: { payment: Payment }) {
  return (
    <div className="card">
      <h3>{formatCurrency(payment.amount, payment.currency)}</h3>
      <p>Status: <b>{payment.status}</b></p>
      <p>Order: {payment.razorpayOrderId}</p>
      <p>Payment: {payment.razorpayPaymentId || "-"}</p>
      <p>{payment.createdAt ? new Date(payment.createdAt).toLocaleString() : ""}</p>
    </div>
  );
}
