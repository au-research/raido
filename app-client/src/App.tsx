import { createTheme, CssBaseline, useMediaQuery } from "@mui/material";
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import { AuthApiProvider } from "Api/AuthApi";
import { AuthProvider } from "Auth/AuthProvider";
import { AppNavBar } from "Design/AppNavBar";
import { NavigationProvider } from "Design/NavigationProvider";
import { RaidoTheme } from "Design/RaidoTheme";
import { ErrorDialogProvider } from "Error/ErrorDialog";
import { ReactErrorBoundary } from "Error/ReactErrorBoundary";
import { AdminAuthzRequestPage } from "Page/Admin/AdminAuthzRequestPage";
import { ApiKeyPage } from "Page/Admin/ApiKeyPage";
import { AppUserPage } from "Page/Admin/AppUserPage";
import { AuthzRespondPage } from "Page/Admin/AuthzRespondPage";
import { ListApiKeyPage } from "Page/Admin/ListApiKeyPage";
import { ListAppUserPage } from "Page/Admin/ListAppUserPage";
import { ListServicePointPage } from "Page/Admin/ListServicePointPage";
import { ServicePointPage } from "Page/Admin/ServicePointPage";
import { EditRaidPage } from "Page/EditRaidPage";
import { HomePage } from "Page/HomePage";
import { MintRaidPage } from "Page/MintRaidPage";
import MintRaidPageNew from 'Page/MintRaidPageNew';
import { AboutAppPage } from "Page/Public/AboutAppPage";
import {
  isRaidLandingPagePath,
  RaidLandingPage
} from "Page/Public/RaidLandingPage";
import { ShowRaidPage } from "Page/ShowRaidPage";
import { AboutRaidPage, isAboutRaidPagePath } from "Page/Unauth/AboutRaidPage";
import { isPrivacyPagePath, PrivacyPage } from "Page/Unauth/PrivacyPage";
import {
  isUsageTermsPagePath,
  UsageTermsPage
} from "Page/Unauth/UsageTermsPage";
import { useMemo } from 'react';
import { LocationPathnameProvider } from "Util/Hook/LocationPathname";
import { LocationSearchProvider } from "Util/Hook/LocationSearch";
import './App.css';

export function App(){
  const prefersDarkMode = useMediaQuery("(prefers-color-scheme: dark)");
  const theme = useMemo(
    () =>
      createTheme({
        palette: {
          // mode: prefersDarkMode ? "dark" : "light",
          mode:"light",
          primary: {
            // main: "#F8B20E", // RAiD yellow
            // main: "#8E489B", // RAiD magenta
            main: "#E51875", // RAiD pink
          },
          secondary: {
            main: "#008CCF", // RAiD blue
          },
          background: {
            default: prefersDarkMode ? "#303030" : "#FAFAFA",
          },
        },
      }),
    [prefersDarkMode]
  );

  
  /* theme defines the basic color palette and styling, etc. */
  return <RaidoTheme>
          {/* <ThemeProvider theme={theme}> */}
          <LocalizationProvider dateAdapter={AdapterDayjs}>
    {/* force browser defaults for consistent display behaviour */}
    <CssBaseline/>
    {/* deal with "unhandled" errors from bad rendering logic */}
    <ReactErrorBoundary>
      {/* deal with "handled" errors as a global, generic modal dialog  */}
      <ErrorDialogProvider>
        {/* https://mui.com/x/react-date-pickers/getting-started/ */}
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          {/* manages window.location for routing */}
          <LocationPathnameProvider>
            <LocationSearchProvider>
              {/* authentication and authorization */}
              <AuthProvider unauthenticatedPaths={[
                isPrivacyPagePath, isUsageTermsPagePath, isAboutRaidPagePath,
                isRaidLandingPagePath,
              ]}>
                {/* re-bind a new ErrorDialog context where the auth context is 
                available. Any errors in components above this will call the 
                error handler on the "top" ErrorDialog where auth is not 
                available, any below will have auth available.
                By using a second instance of the *same* ErrorDialog context,
                it means that the "handeError" context function is re-bound so 
                that calling components don't need to worry about which 
                ErrorDialog context is handling their error. */}
                <ErrorDialogProvider>
                  {/* binds authentication stuff to OpenAPI stuff */}
                  <AuthApiProvider>
                    
                    {/* transition animation and delegates to location infra */}
                    <NavigationProvider>
                      {/* NavBar across the top of screen and sliding drawer */}
                      <AppNavBar/>
  
                      {/* Navigable, authenticated pages, self-routed */}
                      <HomePage/>
                      <AboutAppPage/>
                      <AdminAuthzRequestPage/>
                      <AuthzRespondPage/>
                      <ListServicePointPage/>
                      <ServicePointPage/>
                      <ListAppUserPage/>
                      <AppUserPage/>
                      <ListApiKeyPage/>
                      <ApiKeyPage/>
                      <EditRaidPage/>
                      <ShowRaidPage/>
                      <MintRaidPage/>
                      <MintRaidPageNew/>
  
                    </NavigationProvider>
                  </AuthApiProvider>
                </ErrorDialogProvider>
              </AuthProvider>

              {/* unauthenticated pages, self-routed */}
              <PrivacyPage/>
              <UsageTermsPage/>
              <AboutRaidPage/>
              <RaidLandingPage/>

            </LocationSearchProvider>
          </LocationPathnameProvider>
        </LocalizationProvider>
      </ErrorDialogProvider>
    </ReactErrorBoundary>
    {/* </ThemeProvider> */}
    </LocalizationProvider>
  </RaidoTheme>;
}
