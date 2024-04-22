import { KeycloakProvider } from "@/providers/KeycloakProvider";
import {
  Box,
  createTheme,
  CssBaseline,
  ThemeProvider,
  useMediaQuery,
} from "@mui/material";
import { amber, blue, grey } from "@mui/material/colors";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { ReactKeycloakProvider } from "@react-keycloak/web";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import Keycloak from "keycloak-js";
import React from "react";
import { Outlet } from "react-router-dom";
import { SnackbarProvider } from "./components/Snackbar/SnackbarProvider";
import { ReactErrorBoundary } from "./error/ReactErrorBoundary";
import { DialogProvider } from "./providers/DialogProvider";
import { RaidApiProvider } from "./providers/RaidApiProvider";

const url = import.meta.env.VITE_KEYCLOAK_URL;
const realm = import.meta.env.VITE_KEYCLOAK_REALM;

export function App() {
  const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");

  const mainurl = new URL(window.location.href);
  const tempClientId = mainurl.searchParams.get("client_id");

  React.useEffect(() => {
    if (tempClientId) {
      localStorage.setItem("client_id", tempClientId);
    }
  }, [tempClientId]);

  const theme = React.useMemo(
    () =>
      createTheme({
        palette: {
          mode: prefersDarkMode ? "dark" : "light",
          primary: {
            main: blue[500],
          },
          secondary: {
            main: amber[600],
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
      },
    },
  });

  return (
    // <ConfigProvider>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <SnackbarProvider>
        <RaidApiProvider>
          <ReactKeycloakProvider
            authClient={
              new Keycloak({
                url,
                realm,
                clientId:
                  tempClientId ||
                  localStorage.getItem("client_id") ||
                  "raid-api",
              })
            }
          >
            <KeycloakProvider>
              <QueryClientProvider client={queryClient}>
                <DialogProvider>
                  <ReactErrorBoundary>
                    <LocalizationProvider dateAdapter={AdapterDayjs}>
                      <Box sx={{ pt: 3 }}></Box>
                      <Outlet />
                    </LocalizationProvider>
                  </ReactErrorBoundary>
                </DialogProvider>
              </QueryClientProvider>
            </KeycloakProvider>
          </ReactKeycloakProvider>
        </RaidApiProvider>
      </SnackbarProvider>
    </ThemeProvider>
    // </ConfigProvider>
  );
}
