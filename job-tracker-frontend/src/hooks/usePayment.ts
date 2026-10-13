import { useState } from "react";
import { createOrder, verifyPayment } from "../services/paymentService";

export function usePayment() {
  const [loading, setLoading] = useState(false);

  const startPayment = async (
    user: { name: string; email: string },
    onSuccess: () => void,
    onFailure: (message: string) => void
  ) => {
    setLoading(true);
    try {
      const order = await createOrder(99900);
      const key = order.keyId || import.meta.env.VITE_RAZORPAY_KEY_ID;

      if (!key || key === "YOUR_RAZORPAY_KEY_ID") {
        throw new Error("Configure VITE_RAZORPAY_KEY_ID in .env");
      }

      const razorpay = new window.Razorpay({
        key,
        amount: order.amount,
        currency: order.currency || "INR",
        name: "JobTrack",
        description: "Premium Subscription",
        order_id: order.orderId,
        prefill: { name: user.name, email: user.email },
        theme: { color: "#2563eb" },
        handler: async (response: Record<string, string>) => {
          try {
            await verifyPayment({
              razorpayOrderId: response.razorpay_order_id,
              razorpayPaymentId: response.razorpay_payment_id,
              razorpaySignature: response.razorpay_signature
            });
            onSuccess();
          } catch (error) {
            onFailure(error instanceof Error ? error.message : "Payment verification failed");
          }
        },
        modal: { ondismiss: () => onFailure("Payment window was closed.") }
      });

      razorpay.open();
    } catch (error) {
      onFailure(error instanceof Error ? error.message : "Unable to start payment");
    } finally {
      setLoading(false);
    }
  };

  return { loading, startPayment };
}
