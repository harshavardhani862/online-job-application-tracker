export type PaymentStatus = "CREATED" | "SUCCESS" | "FAILED";

export interface CreateOrderResponse {
  orderId: string;
  amount: number;
  currency: string;
  keyId?: string;
  message?: string;
}

export interface VerifyPaymentRequest {
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
}

export interface Payment {
  id?: number;
  userEmail?: string;
  razorpayOrderId: string;
  razorpayPaymentId?: string;
  razorpaySignature?: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  createdAt?: string;
}
