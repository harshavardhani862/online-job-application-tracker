import { useEffect, useState } from "react";
import PaymentCard from "../components/PaymentCard";
import { getMyPayments } from "../services/paymentService";
import type { Payment } from "../types/Payment";

export default function PaymentHistory() {
  const [payments, setPayments] = useState<Payment[]>([]);
  useEffect(() => { getMyPayments().then(setPayments).catch(() => setPayments([])); }, []);

  return (
    <div>
      <h1>Payment History</h1>
      {payments.length === 0
        ? <div className="card empty">No payments found.</div>
        : <div className="grid">{payments.map((p, i) => <PaymentCard key={p.id ?? i} payment={p} />)}</div>}
    </div>
  );
}
