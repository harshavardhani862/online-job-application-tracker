import api from "./api";
import type { CreateOrderResponse, Payment, VerifyPaymentRequest } from "../types/Payment";

export async function createOrder(amount = 99900): Promise<CreateOrderResponse> {
  const response = await api.post("/payments/create-order", { amount });
  return response.data;
}

export async function verifyPayment(data: VerifyPaymentRequest) {
  const response = await api.post("/payments/verify", data);
  return response.data;
}

export async function getMyPayments(): Promise<Payment[]> {
  const response = await api.get("/payments/my-payments");
  return Array.isArray(response.data) ? response.data : response.data?.content ?? [];
}
