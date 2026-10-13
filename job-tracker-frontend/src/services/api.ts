
import axios from "axios";

const api = axios.create({
  baseURL:
    import.meta.env.VITE_API_BASE_URL ||
    "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use(
  (config) => {
    const url = config.url ?? "";

    // Public authentication endpoints do not require a token.
    if (!url.includes("/auth/")) {
      const token = localStorage.getItem("token");

      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }

    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const url = error.config?.url ?? "";

    console.error("API request failed:", {
      url,
      status,
      message: error.message,
      response: error.response?.data,
    });

    if (status === 401 && !url.includes("/auth/")) {
      localStorage.removeItem("token");
      localStorage.removeItem("user");

      if (window.location.pathname !== "/signin") {
        window.location.href = "/signin";
      }
    }

    return Promise.reject(error);
  }
);

export default api;
