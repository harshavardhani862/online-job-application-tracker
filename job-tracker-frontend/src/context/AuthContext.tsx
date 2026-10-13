import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import type { LoginRequest, SignupRequest, User } from "../types/User";
import { signin, signup } from "../services/authService";

interface AuthContextValue {
  user: User | null;
  token: string | null;
  loading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  register: (data: SignupRequest) => Promise<void>;
  logout: () => void;
  refreshUser: (user: User) => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem("user");
    return saved ? JSON.parse(saved) : null;
  });
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (token) localStorage.setItem("token", token);
  }, [token]);

  const login = async (data: LoginRequest) => {
    setLoading(true);
    try {
      const result = await signin(data);
      const nextUser = result.user ?? {
        id: result.userId,
        userId: result.userId,
        name: result.name || "",
        email: result.email || data.email,
        subscriptionStatus: result.subscriptionStatus as User["subscriptionStatus"]
      };
      localStorage.setItem("token", result.token);
      localStorage.setItem("user", JSON.stringify(nextUser));
      setToken(result.token);
      setUser(nextUser);
    } finally {
      setLoading(false);
    }
  };

  const register = async (data: SignupRequest) => {
    setLoading(true);
    try {
      await signup(data);
      await login({ email: data.email, password: data.password });
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setToken(null);
    setUser(null);
  };

  const refreshUser = (next: User) => {
    setUser(next);
    localStorage.setItem("user", JSON.stringify(next));
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error("useAuth must be used inside AuthProvider");
  return value;
}
