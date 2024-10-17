import AppNavBar from "@/design/AppNavBar";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { Box } from "@mui/material";
import { Navigate, Outlet, useLocation } from "react-router-dom";

export default function ProtectedRoute() {
  const location = useLocation();
  const { keycloak, initialized } = useCustomKeycloak();
  const queryParams = new URLSearchParams(location.search);

  if (!initialized) {
    return <LoadingPage />;
  }

  const redirectUri = `${window.location.origin}${location.pathname || ""}${
    queryParams.toString() ? `?${queryParams.toString()}` : ""
  }`;

  return keycloak?.authenticated ? (
    <>
      <AppNavBar />
      <Box sx={{ height: "3em" }} />
      <Outlet />
    </>
  ) : (
    <Navigate to={`/login?from=${btoa(redirectUri)}`} replace />
  );
}
