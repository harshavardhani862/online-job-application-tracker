export type SubscriptionStatus = "FREE" | "PREMIUM";

export interface User {
  id?: number;
  userId?: number;
  name: string;
  email: string;
  phone?: string;
  subscriptionStatus?: SubscriptionStatus;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
  phone?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user?: User;
  userId?: number;
  name?: string;
  email?: string;
  subscriptionStatus?: SubscriptionStatus;
  message?: string;
}
