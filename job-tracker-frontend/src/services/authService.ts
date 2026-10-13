import api from "./api";
import type { LoginRequest, LoginResponse, SignupRequest } from "../types/User";

export async function signup(data: SignupRequest) {
  const response = await api.post("/auth/signup", data);
  return response.data;
}

export async function signin(data: LoginRequest): Promise<LoginResponse> {
  const response = await api.post<LoginResponse>("/auth/signin", data);
  return response.data;
}
