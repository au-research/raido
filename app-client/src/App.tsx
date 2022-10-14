import React from 'react';
import './App.css';
import { RaidoTheme } from "Design/RaidoTheme";
import { CssBaseline } from "@mui/material";
import { ReactErrorBoundary } from "Error/ReactErrorBoundary";
import { ErrorDialogProvider } from "Error/ErrorDialog";
import { LocationPathnameProvider } from "Util/Hook/LocationPathname";
import { NavigationProvider } from "Design/NavigationProvider";
import { AppNavBar } from "Design/AppNavBar";
import { HomePage } from "Page/HomePage";
import { AuthProvider } from "Auth/AuthProvider";
import { isPrivacyPagePath, PrivacyPage } from "Page/Unauth/PrivacyPage";
import {
  isUsageTermsPagePath,
  UsageTermsPage
} from "Page/Unauth/UsageTermsPage";
import { AboutAppPage } from "Page/AboutAppPage";
import { AboutRaidPage, isAboutRaidPagePath } from "Page/Unauth/AboutRaidPage";
import { AuthApiProvider } from "Api/AuthApi";
import { AdminAuthzRequestPage } from "Page/Admin/AdminAuthzRequestPage";
import { AuthzRespondPage } from "Page/Admin/AuthzRespondPage";
import { ListServicePointPage } from "Page/Admin/ListServicePointPage";
import { ServicePointPage } from "Page/Admin/ServicePointPage";
import { ListAppUserPage } from "Page/Admin/ListAppUserPage";
import { LocationSearchProvider } from "Util/Hook/LocationSearch";
import { AppUserPage } from "Page/Admin/AppUserPage";
import { ListApiKeyPage } from "Page/Admin/ListApiKeyPage";
import { ApiKeyPage } from "Page/Admin/ApiKeyPage";
import { MintRaidPage } from "Page/MintRaidPage";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { LocalizationProvider } from "@mui/x-date-pickers";
import { EditRaidPage } from "Page/EditRaidPage";

export function App(){
  /* theme defines the basic color palette and styling, etc. */
  return <RaidoTheme>
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
                isPrivacyPagePath, isUsageTermsPagePath, isAboutRaidPagePath
              ]}>
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
                    <MintRaidPage/>
                    <EditRaidPage/>

                  </NavigationProvider>
                </AuthApiProvider>
              </AuthProvider>

              {/* unauthenticated pages, self-routed */}
              <PrivacyPage/>
              <UsageTermsPage/>
              <AboutRaidPage/>

            </LocationSearchProvider>
          </LocationPathnameProvider>
        </LocalizationProvider>
      </ErrorDialogProvider>
    </ReactErrorBoundary>
  </RaidoTheme>;
}
