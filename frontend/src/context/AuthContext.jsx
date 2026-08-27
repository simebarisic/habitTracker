import { createContext, useContext, useState, useCallback } from "react";
import client from "../api/client";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [username, setUsername] = useState(
    () => localStorage.getItem("habit_tracker_username") || null
  );

  const login = useCallback(async (usernameInput, password) => {
    const { data } = await client.post("/auth/login", {
      username: usernameInput,
      password,
    });
    localStorage.setItem("habit_tracker_token", data.token);
    localStorage.setItem("habit_tracker_username", data.username);
    setUsername(data.username);
  }, []);

  const register = useCallback(async (usernameInput, email, password) => {
    const { data } = await client.post("/auth/register", {
      username: usernameInput,
      email,
      password,
    });
    localStorage.setItem("habit_tracker_token", data.token);
    localStorage.setItem("habit_tracker_username", data.username);
    setUsername(data.username);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("habit_tracker_token");
    localStorage.removeItem("habit_tracker_username");
    setUsername(null);
  }, []);

  const updateProfile = useCallback(
    async ({ currentPassword, newUsername, newEmail, newPassword }) => {
      const { data } = await client.put("/users/me", {
        currentPassword,
        newUsername: newUsername || null,
        newEmail: newEmail || null,
        newPassword: newPassword || null,
      });
      localStorage.setItem("habit_tracker_token", data.token);
      localStorage.setItem("habit_tracker_username", data.username);
      setUsername(data.username);
      return data;
    },
    []
  );

  const value = {
    username,
    isAuthenticated: Boolean(username),
    login,
    register,
    logout,
    updateProfile,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
