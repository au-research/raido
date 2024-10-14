import { SnackbarProvider } from "@/components/Snackbar/SnackbarProvider";
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
import React from "react";
import { Outlet } from "react-router-dom";
import { ErrorDialogProvider } from "./components/ErrorDialog/ErrorDialogProvider";
import getKeycloakInstance from "./KeycloakSingleton";
import { KeycloakProvider } from "./providers/KeycloakProvider";

export function App() {
  const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");

  const theme = React.useMemo(
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
        <SnackbarProvider>
          <ReactKeycloakProvider
            authClient={getKeycloakInstance()}
            initOptions={{
              pkceMethod: "S256",
            }}
          >
            <KeycloakProvider>
              <QueryClientProvider client={queryClient}>
                <ReactErrorBoundary>
                  <LocalizationProvider dateAdapter={AdapterDayjs}>
                    <Box sx={{ pt: 3 }}></Box>
                    <Outlet />
                  </LocalizationProvider>
                </ReactErrorBoundary>
              </QueryClientProvider>
            </KeycloakProvider>
          </ReactKeycloakProvider>
        </SnackbarProvider>
      </ErrorDialogProvider>
    </ThemeProvider>
  );
}
