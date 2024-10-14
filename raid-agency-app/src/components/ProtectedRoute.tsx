import AppNavBar from "@/design/AppNavBar";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { Box } from "@mui/material";
import { Navigate, Outlet, useLocation } from "react-router-dom";

export default function ProtectedRoute() {
  const location = useLocation();
  const { keycloak, initialized } = useCustomKeycloak();

  if (!initialized) {
    return <LoadingPage />;
  }

  return keycloak?.authenticated ? (
    <>
      <AppNavBar />
      <Box sx={{ height: "3em" }} />
      <Outlet />
    </>
  ) : (
    <Navigate to={`/login?from=${btoa(location.pathname)}`} replace />
  );
}
