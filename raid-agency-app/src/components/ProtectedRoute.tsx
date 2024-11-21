import AppNavBar from "@/design/AppNavBar";
import { useCustomKeycloak } from "@/hooks/useCustomKeycloak";
import LoadingPage from "@/pages/LoadingPage";
import { Box } from "@mui/material";
import { memo } from "react";
import { Navigate, Outlet } from "react-router-dom";

const ProtectedRoute = memo(() => {
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
    <Navigate to="/login" replace />
  );
});

ProtectedRoute.displayName = "ProtectedRoute";
export default ProtectedRoute;
