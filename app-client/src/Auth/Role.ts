import { AuthState } from "Auth/AuthProvider";

export function isNoneRole(auth: AuthState){
  return auth.session.payload.role.toLowerCase().trim() === "none";
}