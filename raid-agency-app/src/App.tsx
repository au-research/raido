import { SnackbarProvider } from "@/components/snackbar";
import { ReactErrorBoundary } from "@/error/ReactErrorBoundary";
import {
  Box,
  createTheme,
  CssBaseline,
  ThemeProvider,
  useMediaQuery,
} from "@mui/material";
import { grey } from "@mui/material/colors";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useMemo } from "react";
import { Outlet } from "react-router-dom";

import { MappingProvider } from "@/mapping";
import { ErrorDialogProvider } from "./components/error-dialog";
import Keycloak from "keycloak-js";

export function App() {
  const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");

  const keycloak = new Keycloak({
    url: import.meta.env.VITE_KEYCLOAK_URL,
    realm: import.meta.env.VITE_KEYCLOAK_REALM,
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
  });

  const theme = useMemo(
    () =>
      createTheme({
        typography: {
          fontFamily: `Figtree, sans-serif`,
          fontSize: 14,
          fontWeightLight: 300,
          fontWeightRegular: 400,
          fontWeightMedium: 500,
        },
        palette: {
          mode: prefersDarkMode ? "dark" : "light",
          primary: {
            main: "#4183CE",
          },
          secondary: {
            main: "#DC8333",
          },
          background: {
            default: prefersDarkMode ? "#000" : grey[50],
          },
        },
      }),
    [prefersDarkMode]
  );

  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
        refetchOnWindowFocus: false,
      },
    },
  });

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ErrorDialogProvider>
        <MappingProvider>
          <SnackbarProvider>
            <ReactKeycloakProvider
              authClient={keycloak}
              initOptions={{
                pkceMethod: "S256",
              }}
            >
              <QueryClientProvider client={queryClient}>
                <ReactErrorBoundary>
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <Box sx={{ pt: 3 }}></Box>
                    <Outlet />
                  </LocalizationProvider>
                </ReactErrorBoundary>
              </QueryClientProvider>
            </ReactKeycloakProvider>
          </SnackbarProvider>
        </MappingProvider>
      </ErrorDialogProvider>
    </ThemeProvider>
  );
}
