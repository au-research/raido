import { AppNavBar } from "@/components/app-nav-bar";
import { Loading } from "@/pages/loading";
import { Box, Container } from "@mui/material";
import { useKeycloak } from "@react-keycloak/web";
import { memo } from "react";
import { Navigate, Outlet } from "react-router-dom";

export const ProtectedRoute = memo(() => {
  const { keycloak, initialized } = useKeycloak();

  if (!initialized) {
    return (
      <Container>
        <Loading />
      </Container>
    );
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
