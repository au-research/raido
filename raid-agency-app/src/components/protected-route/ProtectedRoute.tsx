import { AppNavBar } from "@/components/app-nav-bar";
import { useKeycloakContext } from "@/keycloak";
import { Loading } from "@/pages/loading";
import { Box, Container } from "@mui/material";
import { memo } from "react";
import { Navigate, Outlet } from "react-router-dom";

export const ProtectedRoute = memo(() => {
  const { keycloak, initialized } = useKeycloakContext();

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
